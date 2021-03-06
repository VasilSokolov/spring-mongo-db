package com.spring.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.beanio.StreamFactory;
import org.beanio.builder.DelimitedParserBuilder;
import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.dto.BookDto;
import com.spring.model.Address;
import com.spring.model.Book;
import com.spring.model.GNSSPosition;
import com.spring.repository.BookRepository;
import com.spring.utils.DataMap;
import com.spring.utils.FileUtils;

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
			Book b = bookRepository.findByBookId(book.getBookId()).size() > 0 ? bookRepository.findByBookId(book.getBookId()).get(0) : null;
			if (b != null) {
				b.setAuthorName(book.getAuthorName());
				b.setBookName(book.getBookName());
				bookSaved = bookRepository.save(b);
			} else {
				bookSaved = bookRepository.save(book);
			}
		}else {
			bookSaved = bookRepository.save(book);
		}
		
		return bookSaved;
	}

	
	@GetMapping("/allBooksInPage")
	public Map<String, Object> getAllBooksInPage(@RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo, 
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
		ExampleMatcher matcher = ExampleMatcher.matchingAny().withIgnoreCase().withMatcher("authorName", GenericPropertyMatcher.of(StringMatcher.ENDING));
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
		return bookRepository.findBooksDistinctByAuthorNameNotIn( book.getAuthorName());
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
		
		FileUtils<Book> book = new FileUtils<Book>();
		List<String> reportData = new ArrayList<String>();
		findAllByOrderByBookIdAsc.forEach(b -> {
			try {
				book.addDataToReport(b, reportData, 0);
			} catch (JsonProcessingException e) {
				System.out.println(e.getMessage());
			}
		});
		System.out.println(reportData);
//		convertObjToCSV(findAllByOrderByBookIdAsc);
		return findAllByOrderByBookIdAsc;
	}
		
	public void convertObjToCSV(List<Book> findAllByOrderByBookIdAsc) {
		List<Address> list= new ArrayList<>();
		findAllByOrderByBookIdAsc.forEach(item->item.getAddressList().stream().forEach(address->{
		if (address.getCity().equalsIgnoreCase("Sofia")) {
				list.add(address);
		}
		
		}));
		System.out.println(list);
		StreamFactory factory = StreamFactory.newInstance();
		StreamBuilder builder = new StreamBuilder("") // Your file
		    .format("delimited")
		    .parser(new DelimitedParserBuilder(';')) // Sign to  use as a delimiter
		    .addRecord(Address.class); // class to be mapped 

		factory.define(builder);
	}
	
//	//European countries use ";" as 
//    //CSV separator because "," is their digit separator
//    private static final String CSV_SEPARATOR = ";";
//    private static void writeToCSV(List<Book> bookList)
//    {
//    	BufferedWriter bw = null;
//        try
//        {
//            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("products.csv"), "UTF-8"));
//            for (Book book : bookList)
//            {
//                StringBuffer oneLine = new StringBuffer();
//                oneLine.append(book.getId() != null && book.getId().trim().length() == 0 ? "" : book.getId());
//                oneLine.append(CSV_SEPARATOR);
//                oneLine.append(book.getBookName() != null && book.getBookName().trim().length() == 0 ? "" : book.getBookName());
////                oneLine.append(CSV_SEPARATOR);
////                oneLine.append(product.getCostPrice() < 0 ? "" : product.getCostPrice());
////                oneLine.append(CSV_SEPARATOR);
////                oneLine.append(product.isVatApplicable() ? "Yes" : "No");
//                bw.write(oneLine.toString());
//                bw.newLine();
//            }
//            bw.flush();
//            bw.close();
//        }
//        catch (UnsupportedEncodingException e) {}
//        catch (FileNotFoundException e){}
//        catch (IOException e){}
//        
//    }
    
	public List<String> getCsvExportForDelicts(String fullFilePath, List<Book> books, Book t)
            throws IOException {

        List<String> header = new ArrayList<>();
        SimpleDateFormat filenameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fullFilePath += filenameDateFormat.format(new Date()) + ZonedDateTime.now().toInstant().toEpochMilli() + ".csv";

        ObjectMapper objectMapper = new ObjectMapper();
		String[] jsonObjectAsArray = objectMapper.writeValueAsString(t).replace("{", "").replace("}", "").split(";\"");
//        CsvMapWriter csvWriter = getCsvWritter(fullFilePath);
        byte[] bytee = "{ \"name\" : \"John\", \"age\" : 18 }".getBytes(StandardCharsets.UTF_8);
        InputStream is = new ByteArrayInputStream(bytee);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        CsvMapReader reader = new CsvMapReader(bufferedReader, CsvPreference.STANDARD_PREFERENCE);
//        CsvMapWriter csvWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        header.add("DOCUMENT NUMBER");
        header.add("LICENSE_PLATE");
        header.add("COUNTRY_CODE");
        header.add("CUSTOMER_NAME");
        header.add("CUSTOMER_ID");
        header.add("ACCOUNT_ID");
        header.add("ACCOUNT_STATUS");
        header.add("ACCOUNT_UNIT_STATUS");
        header.add("CREATED_ON(UTC)");
        header.add("NOTIFIED");
        header.add("AMOUNT_BGN");
        header.add("TOLL_SECTION_NAME");

        String[] staticHeader = new String[header.size()];
        staticHeader = header.toArray(staticHeader);

//        csvWriter.writeHeader(staticHeader);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (books.size() != 0) {
            for (Book book : books) {
                Map<String, String> csvObj = new HashMap<>();

//                AccountUnit accountUnit = accountUnitRepository.findByAccountUnitId(obj.getAccountUnitId()).orElseThrow(() ->
//                        new ResourceNotFoundException(("No account unit found for accountUnitId " + obj.getAccountUnitId())));
//
                Book b = bookRepository.findByBookId(book.getBookId()).get(0);
//                		.orElseThrow(() ->
//                        new ResourceNotFoundException("No account found for accountId " + obj.getAccountId()));

                csvObj.put("DOCUMENT NUMBER", String.valueOf(b.getAuthorName()));
                csvObj.put("LICENSE_PLATE", String.valueOf(b.getBookName()));
//                csvObj.put("COUNTRY_CODE", String.valueOf(obj.getNationality()));
//                csvObj.put("CUSTOMER_NAME", String.valueOf(accountUnit.getPartner().getName()));
//                csvObj.put("CUSTOMER_ID", String.valueOf(accountUnit.getPartner().getCustomerC5().getCustomerID()));
//                csvObj.put("ACCOUNT_ID", String.valueOf(obj.getAccountId()));
//                csvObj.put("ACCOUNT_STATUS", String.valueOf(account.getAccountStatus().getDescription()));
//                csvObj.put("ACCOUNT_UNIT_STATUS", String.valueOf(obj.getAccountUnitStatus()));
//                csvObj.put("CREATED_ON(UTC)", String.valueOf(obj.getCreatedOn()));
//                csvObj.put("NOTIFIED", String.valueOf(obj.isNotified()));
//                csvObj.put("AMOUNT_BGN", String.valueOf(obj.getAmountBGN()));
//                csvObj.put("TOLL_SECTION_NAME", String.valueOf(obj.getEvidentialRecords().size() != 0 ?
//                        obj.getEvidentialRecords().get(0).getTollSectionName() : ""));

//                csvWriter.write(csvObj, staticHeader);
            }
//            csvWriter.close();

        } else {
//            csvWriter.close();
        }
		return header;

//        String xlsFile = CsvToExcelUtil.convertCsvToXls(fullFilePath);
//        File delictsfile = new File(xlsFile);

//        return delictsfile;
    }

	public static String addTimestampColumnFromCsv(List<Book> bookList) throws JsonGenerationException, JsonMappingException, IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
	    final ObjectMapper mapper = new ObjectMapper();

	    mapper.writeValue(out, bookList);
		
		ObjectMapper objectMapper = new ObjectMapper();
//		String[] jsonObjectAsArray = objectMapper.writeValueAsString(bookList).replace("{", "").replace("}", "").split(";\"");
		String jsonObjectAsArray = objectMapper.writeValueAsString(bookList);
//        CsvMapWriter csvWriter = getCsvWritter(fullFilePath);
//        byte[] bytee = "{ \"name\" : \"John\", \"age\" : 18 }".getBytes(StandardCharsets.UTF_8);
        byte[] bytee = jsonObjectAsArray.getBytes(StandardCharsets.UTF_8);
        InputStream is = new ByteArrayInputStream(bytee);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
//        BufferedWriter bufferedWriter = new BufferedWriter(new InputStreamWr(is));
		
		
//		 File inputFile = new File(inputPath);
//		 File outputFile = new File(outputPath);
		 CsvListReader reader = null;
//		 CsvListWriter writer = null;
		 try {
		  CsvPreference csvPreference = new CsvPreference.Builder('"', ';', "\r\n")
		      .ignoreEmptyLines(false)
		      .build();
//		  reader = new CsvListReader(new FileReader(inputFile), csvPreference);
//		  writer = new CsvListWriter(new FileWriter(outputFile), csvPreference);
		  reader = new CsvListReader(bufferedReader, csvPreference);
//		  writer = new CsvListWriter(bufferedWriter, csvPreference);
		  List<String> columns;
		  StringBuilder sb = new StringBuilder();
		  while ((columns = reader.read()) != null) {
		   columns.add("");
//		   writer.write(columns);
		  }
		 } catch (IOException e) {
//		  throw new MetatronException("Fail to transform csv file :" + e.getMessage());
		 } finally {
		  try {
		   if (reader != null) reader.close();
//		   if (writer != null) writer.close();
		  } catch (IOException e) {}
		 }
//		 return outputFile.getAbsolutePath();s
		return jsonObjectAsArray;
		}
	
	
	public static String addTimestampColumnFromCsvFile(String timeStr, String inputPath, String outputPath) {
		 File inputFile = new File(inputPath);
		 File outputFile = new File(outputPath);
		 CsvListReader reader = null;
		 CsvListWriter writer = null;
		 try {
		  CsvPreference csvPreference = new CsvPreference.Builder('"', ',', "\r\n")
		      .ignoreEmptyLines(false)
		      .build();
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
		   if (reader != null) reader.close();
		   if (writer != null) writer.close();
		  } catch (IOException e) {}
		 }
		 return outputFile.getAbsolutePath();
		}
}
