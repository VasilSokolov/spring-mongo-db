package com.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.spring.model.Book;

public interface BookRepository extends MongoRepository<Book, Long> {

	Optional<Book> findByBookId(Long id);
	List<Book> findAllByBookIdIsNotNull(Long id);
	List<Book> findAllByBookIdIsNotNullAndAuthorNameIsNull();
	List<Book> findBooksDistinctByAuthorNameNotIn(String authorName);
	List<Book> findAllByOrderByBookIdAsc();
	Long deleteByBookId(Long id);

}
