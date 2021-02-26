package com.spring.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.dto.BookDto;
import com.spring.model.Book;
import com.spring.repository.BookRepository;
import com.spring.utils.DataMap;

@RestController()
@RequestMapping("book")
public class BookController {

	@Autowired
	private BookRepository bookRepository;

	@PostMapping("/add")
	public String save(@RequestBody Book book) {
		Book bookSaved = null;
		BookDto bb = transform(book);
		if (book.getBookId() != null) {
			Book b = bookRepository.findByBookId(book.getBookId()).orElse(null);
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
		
		return bookSaved.toString();
	}

	@GetMapping("/all")
	public List<Book> getBooks() {
		return bookRepository.findAllByOrderByBookIdAsc();
	}

	@GetMapping("/get/by-id")
	public Optional<Book> get(@RequestParam(value = "id", required = true) Long id) {
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
}
