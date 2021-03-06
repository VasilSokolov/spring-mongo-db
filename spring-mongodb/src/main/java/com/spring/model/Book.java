package com.spring.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@Getter
//@Setter
//@ToString
@Document(collection = "Books")
public class Book {

	@Id
	@Field("_id")
	private String id;

	// @Field("bookId")
	private Long bookId;
	private String bookName;
	private String authorName;

	private List<String> categories = new ArrayList<String>();
	private List<Address> addressList = new ArrayList<Address>();

	public Book() {
		super();
	}

	public Book(Long bookId, String bookName, String authorName, List<String> categories) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
		this.authorName = authorName;
		this.categories = categories;
	}

	public Book(Long bookId, String bookName, String authorName, List<String> categories, List<Address> addressList) {
		this.bookId = bookId;
		this.bookName = bookName;
		this.authorName = authorName;
		this.categories = categories;
		this.addressList = addressList;
	}

	public List<Address> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<Address> addressList) {
		this.addressList = addressList;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", bookId=" + bookId + ", bookName=" + bookName + ", authorName=" + authorName
				+ ", categories=" + categories + "]";
	}

}
