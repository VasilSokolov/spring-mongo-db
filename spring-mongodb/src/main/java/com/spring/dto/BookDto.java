package com.spring.dto;

import lombok.Data;

//@Data
public class BookDto {
	
	private Long id;
	private String authorName;

	public BookDto() {
	}

	public BookDto(Long id, String authorName) {
		this.id = id;
		this.authorName = authorName;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	
}
