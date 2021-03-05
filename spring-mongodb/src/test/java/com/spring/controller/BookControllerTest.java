package com.spring.controller;

import com.spring.dto.BookDto;
import com.spring.model.Book;
import com.spring.repository.BookRepository;
import com.spring.utils.DataMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest
class BookControllerTest {
	BookController bookController = null;
	List<Book> books = new ArrayList<>();
	Book book = null;
	BookDto bookDto = null;
	DataMap dataMap = new DataMap();
//	MockedStatic<DataMap> mockedStatic = null;
//	@InjectMocks
//	BookRepository bookRepository;
//
//  @Rule
//	public MockitoRule rule = MockitoJUnit.rule();
	
	BookRepository bookRepository = mock(BookRepository.class, withSettings().verboseLogging());

//	@Spy
//	DataMap dataMap = new DataMap();
	
	@BeforeEach
	public void setUp(){
//		mockedStatic = mockStatic(DataMap.class);
//		dataMap = mock(DataMap.class);
		bookController = new BookController(bookRepository);
		book = new Book(3l, "Java 3", "Oracle 3", Arrays.asList("One 3", "Two 3"));
		bookDto = new BookDto(null, "Oracle 3");
		books.addAll(Arrays.asList(
						new Book(1l, "Java 1", "Oracle 1", Arrays.asList("One 1", "Two 1")),
						new Book(2l, "Java 2", "Oracle 2", Arrays.asList("One 2", "Two 2"))));
	}
	
	@Test
	void getBooksTest() {
		
		doReturn(books).when(bookRepository).findAllByOrderByBookIdAsc();
//		when(bookRepository.findAllByOrderByBookIdAsc()).thenReturn(books);
		List<Book> books2 = bookController.getBooks();
		verify(bookRepository).findAllByOrderByBookIdAsc();
		assertEquals(books, books2);
		
	}

	@Test
	void save() {
		//Arrange
		doReturn(books).when(bookRepository).findByBookId(anyLong());
		Book updatedBook = books.get(0);
		updatedBook.setAuthorName(book.getAuthorName());
		updatedBook.setBookName(book.getBookName());
		when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
		//Act
		Book response = bookController.save(book);
		Mockito.verify(bookRepository).save(response);
		assertEquals(response, updatedBook);
		
		//Act
		updatedBook.setBookId(null);
//		Book book2 = updatedBook;
		when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
		Book response2 = bookController.save(updatedBook);
//		//Assert
		System.out.println("------------------------------------------------------");
		Mockito.verify(bookRepository, times(2)).save(response2);
		assertEquals(response2, updatedBook);
		
		when(bookRepository.save(updatedBook)).thenReturn(nullable(Book.class));
		Book response3 = bookController.save(updatedBook);
//		//Assert
		System.out.println("------------------------------------------------------");
		Mockito.verify(bookRepository, times(3)).save(updatedBook);
		assertNull(response3);
		
		
	}

	@Test
	void transform() {
		try(MockedStatic<DataMap> mockedStatic = mockStatic(DataMap.class)){
		mockedStatic.when(()-> DataMap.dataToEntityOrDto(book, BookDto.class)).thenReturn(bookDto);
		BookDto bDto = bookController.transform(book);
		String authorName = bDto.getAuthorName();
		String authorName1 = this.bookDto.getAuthorName();
		mockedStatic.verify(()-> DataMap.dataToEntityOrDto(book, BookDto.class));
		assertEquals(authorName1, authorName);
		}
	}

	@Test
	void getAllBooksInPage() {
	}

	@Test
	void getAllByExample() {
	}


}
