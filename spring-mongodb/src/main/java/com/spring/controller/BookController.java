package com.spring.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.spring.dto.BookDto;
import com.spring.model.Book;
import com.spring.repository.BookRepository;
import com.spring.utils.DataMap;

@RestController()
@RequestMapping("book")
public class BookController {

	@Autowired
	private BookRepository bookRepository;

	public BookController(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@PostMapping("/add")
	public Book save(@RequestBody Book book) {
		Book bookSaved = null;
//		BookDto bb = transform(book);
		if (book.getBookId() != null) {
			Book b = bookRepository.findByBookId(book.getBookId()).size() > 0
					? bookRepository.findByBookId(book.getBookId()).get(0)
					: null;
			if (b != null) {
				b.setAuthorName(book.getAuthorName());
				b.setBookName(book.getBookName());
				bookSaved = bookRepository.save(b);
			} else {
				bookSaved = bookRepository.save(book);
			}
		} else {
			bookSaved = bookRepository.save(book);
		}

		return bookSaved;
	}

	@GetMapping("/allBooksInPage")
	public Map<String, Object> getAllBooksInPage(
			@RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
			@RequestParam(value = "pageSize", required = false, defaultValue = "2") int pageSize,
			@RequestParam(value = "sortBy", required = false, defaultValue = "bookId") String sortBy) {
		Map<String, Object> response = new HashMap<String, Object>();

		Sort sort = Sort.by(sortBy);
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		Page<Book> booksPage = bookRepository.findAll(pageable);
		response.put("Total No. of Elements", booksPage.getTotalElements());
		response.put("data", booksPage.getContent());
		response.put("Total No. of Page", booksPage.getTotalPages());
		response.put("Current Page No.", booksPage.getNumber());

		return response;
	}

	@PostMapping("/example")
	public List<Book> getAllByExample(@RequestBody Book book) {
//		Example<Book> example = Example.of(book, macherExample());
		Example<Book> example = Example.of(book);
		List<Book> response = bookRepository.findAll(example);

		return response;
	}

	private ExampleMatcher macherExample() {
		ExampleMatcher matcher = ExampleMatcher.matchingAny().withIgnoreCase().withMatcher("authorName",
				GenericPropertyMatcher.of(StringMatcher.ENDING));
		return matcher;
	}

	@GetMapping("/get/by-category")
	public List<Book> getByCategory(@RequestParam(value = "category", required = true) String[] category) {
		return bookRepository.findByCategories(category);
	}

	@GetMapping("/get/by-id")
	public List<Book> get(@RequestParam(value = "id", required = true) Long id) {
		return bookRepository.findByBookId(id);
	}

	@GetMapping("/get/by-test")
	public List<Book> getTest(@RequestParam(value = "id", required = false) Long id) {
		return bookRepository.findAllByBookIdIsNotNull(id);
	}

	@GetMapping("/get/by-bookId-author-null")
	public List<Book> getTest2() {
		return bookRepository.findAllByBookIdIsNotNullAndAuthorNameIsNull();
	}

	@PostMapping("/get/distinct-by-bookId-and-authorName")
	public List<Book> getDataByDistinct(@RequestBody Book book) {
		return bookRepository.findBooksDistinctByAuthorNameNotIn(book.getAuthorName());
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		boolean isDeleted = bookRepository.deleteByBookId(id) == 1 ? true : false;
		return String.format("Deleted Book by id: %d %b", id, isDeleted);
	}

	public BookDto transform(Book book) {
		BookDto b = DataMap.dataToEntityOrDto(book, BookDto.class);
		return b;
	}

	@GetMapping("/all")
	public List<Book> getBooks() {
		List<Book> findAllByOrderByBookIdAsc = bookRepository.findAllByOrderByBookIdAsc();

		return findAllByOrderByBookIdAsc;
	}

	@RequestMapping(value = "/export_data", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void downloadDataInCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
//	    if (type.equals(FileType.CSV.name())) {
		List<Book> list = bookRepository.findAllByOrderByBookIdAsc();
		int size = list.size();
		int c = size/3;
//		if (count == 3) {
//			writer.close();
//			count = 0;
//			printWriter = response.getWriter();
//			// the Unicode value for UTF-8 BOM
//			printWriter.write("\ufeff");
//			downloadCsv(request, null, books);
//		}
		downloadCsv(request, response, list);
//	    }
	}

	@GetMapping(value = "/download", produces = "text/csv; charset=UTF-8")
	public ResponseEntity<byte[]> getFile() throws IOException {
		SimpleDateFormat filenameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = filenameDateFormat.format(new Date()) + "-" + ZonedDateTime.now().toInstant().toEpochMilli()
				+ ".csv";
		String headerValue = String.format("%s; charset=UTF-8; filename=%s", "attachment", fileName);
		byte[] byteArray = IOUtils.toByteArray(new InputStreamReader(load()), StandardCharsets.UTF_8);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
				.header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(byteArray);
	}

	public ByteArrayInputStream load() {
		List<Book> list = bookRepository.findAllByOrderByBookIdAsc();

		ByteArrayInputStream in = listToCSV(list);
		return in;
	}

	private ByteArrayInputStream listToCSV(List<Book> list) {
		final CSVFormat format = CSVFormat.EXCEL.withQuote(null).withAllowMissingColumnNames();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
		// the Unicode value for UTF-8 BOM
		printWriter.write("\ufeff");
		try (CSVPrinter csvPrinter = new CSVPrinter((printWriter), format);) {
			csvPrinter.printRecord(CSV_HEADER);
			for (Book book : list) {
				String[] s = new String[] { String.valueOf(book.getBookId()), book.getBookName(),
						book.getAuthorName() };

				List<String> data = Arrays.asList(book.getBookId() != null ? book.getBookId() + "" : "",
						book.getBookName(), book.getAuthorName());

				csvPrinter.printRecord(data);
			}

			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Fail to import data to CSV file: " + e.getMessage());
		}
	}

	private ByteArrayInputStream downloadCsvFile(List<Book> list) {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				final CSVWriter writer = new CSVWriter(new PrintWriter(out), ';', CSVWriter.NO_QUOTE_CHARACTER)) {
			writer.writeNext(CSV_HEADER);

			for (Book book : list) {
				// cast/convert to String where needed
				writer.writeNext(new String[] { String.valueOf(book.getBookId() != null ? book.getBookId() : ""),
						book.getBookName(), book.getAuthorName() != null ? book.getAuthorName() : "" });
			}

			writer.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Fail to import data to CSV file: " + e.getMessage());
		}
	}

	private static final String[] CSV_HEADER = new String[] { "Book Id", "Book Name", "Author Name" };

	private int count=0;
	private void downloadCsv(HttpServletRequest request, HttpServletResponse response, List<Book> list)
			throws IOException {
			
		
		String headerKey = "Content-Disposition";
		SimpleDateFormat filenameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = filenameDateFormat.format(new Date()) + ZonedDateTime.now().toInstant().toEpochMilli()
				+ ".csv";

		String headerValue = String.format("%s; filename=%s", "attachment", fileName);
		response.setContentType("text/csv");
		response.setCharacterEncoding("UTF-8");
		response.setHeader(headerKey, headerValue);
		
		PrintWriter printWriter = response.getWriter();
		// the Unicode value for UTF-8 BOM
		printWriter.write("\ufeff");
		try (final CSVWriter writer = new CSVWriter(printWriter, ';')) {
			writer.writeNext(CSV_HEADER);

//			List<Book> books = new ArrayList<Book>(list);
			for (Book book : list) {
//				count++;
				// cast/convert to String where needed
				writer.writeNext(new String[] { book.getBookId() + "", book.getBookName(), book.getAuthorName() });

//				books.remove(book);
				
			}
			writer.close();
		}
	}

	// original
	public static String addTimestampColumnFromCsvFile(String timeStr, String inputPath, String outputPath,
			List<Book> bookList) throws JsonGenerationException, JsonMappingException, IOException {

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ObjectMapper mapper = new ObjectMapper();

		mapper.writeValue(out, bookList);

		ObjectMapper objectMapper = new ObjectMapper();
//		String[] jsonObjectAsArray = objectMapper.writeValueAsString(bookList).replace("{", "").replace("}", "").split(";\"");
		String jsonObjectAsArray = objectMapper.writeValueAsString(bookList);
//        CsvMapWriter csvWriter = getCsvWritter(fullFilePath);
//        byte[] bytee = "{ \"name\" : \"John\", \"age\" : 18 }".getBytes(StandardCharsets.UTF_8);
		byte[] blobData = jsonObjectAsArray.getBytes(StandardCharsets.UTF_8);
		InputStream is = new ByteArrayInputStream(blobData);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		File inputFile = new File(inputPath);
		File outputFile = new File(outputPath);
		CsvListReader reader = null;
		CsvListWriter writer = null;
		try {
			CsvPreference csvPreference = new CsvPreference.Builder('"', ',', "\r\n").ignoreEmptyLines(false).build();
			reader = new CsvListReader(new FileReader(inputFile), csvPreference);
			writer = new CsvListWriter(new FileWriter(outputFile), csvPreference);
			List<String> columns;
			while ((columns = reader.read()) != null) {
				columns.add(timeStr);
				writer.write(columns);
			}
		} catch (IOException e) {
//		  throw new MetatronException("Fail to transform csv file :" + e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			} catch (IOException e) {
			}
		}
		return outputFile.getAbsolutePath();
	}
}
