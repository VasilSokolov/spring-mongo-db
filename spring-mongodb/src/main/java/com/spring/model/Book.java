package com.spring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "Books")
public class Book {

	@Id
	@Field("_id")
	private String id;
	
//	@Field("bookId")
	private Long bookId;
	private String bookName;
	private String authorName;

	
}
