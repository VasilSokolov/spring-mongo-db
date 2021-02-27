package com.spring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.spring.model.Book;
import com.spring.repository.BookRepository;

class BookControllerTest {
	BookController bookController = null;
	List<Book> books = new ArrayList<>();
	
//	@InjectMocks
//	BookRepository bookRepository;
//
//  @Rule
//	public MockitoRule rule = MockitoJUnit.rule();
	
	BookRepository bookRepository = mock(BookRepository.class);
	
	@BeforeEach
	public void setUp(){
		bookController = new BookController(bookRepository);
		books.addAll(Arrays.asList(
						new Book(15l, "Java 1", "Oracle 1", Arrays.asList("One", "Two")),
						new Book(15l, "Java 2", "Oracle 2", Arrays.asList("One 1", "Two 1"))));
	}
	
	@Test
	void getBooksTest() {
		
		when(bookRepository.findAllByOrderByBookIdAsc()).thenReturn(books);
		List<Book> books2 = bookController.getBooks();
		assertEquals(books, books2);
		verify(bookRepository).findAllByOrderByBookIdAsc();
		
	}

}
