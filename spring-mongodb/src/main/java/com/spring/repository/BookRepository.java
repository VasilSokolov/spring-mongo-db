package com.spring.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.spring.model.Book;

public interface BookRepository extends MongoRepository<Book, Long> {

	Optional<Book> findByBookId(Long id);

	Long deleteByBookId(Long id);

}
