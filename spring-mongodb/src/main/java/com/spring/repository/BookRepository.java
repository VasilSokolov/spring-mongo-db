package com.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.spring.model.Book;

public interface BookRepository extends MongoRepository<Book, Long> {

	@Query(value = "{\"categories\": {$elemMatch:{$in:?0}}}", fields = "{'bookId':1}")
	public List<Book> findByCategories(String[] categories);
	
	public List<Book> findByBookId(Long id);
	public List<Book> findAllByBookIdIsNotNull(Long id);
	public List<Book> findAllByBookIdIsNotNullAndAuthorNameIsNull();
	public List<Book> findBooksDistinctByAuthorNameNotIn(String authorName);
	public List<Book> findAllByOrderByBookIdAsc();
	public Long deleteByBookId(Long id);

}
