package com.spring.dto;

import lombok.Data;

@Data
public class BookDto {
	
	private Long id;
	private String authorName;
	
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
	@Override
	public String toString() {
		return "BookDto [id=" + id + ", authorName=" + authorName + "]";
	}
	
	
}
