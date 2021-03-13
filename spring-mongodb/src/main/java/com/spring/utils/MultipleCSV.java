package com.spring.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.spring.model.Book;

public class MultipleCSV {
	public static String directory = "C:\\Users\\Administrator\\eclipse-workspace\\spring-mongo-db\\spring-mongodb\\src\\main\\resources\\csv_files\\";
	public static String inputFile = directory + "inventory.xls";
	private static final String[] CSV_HEADER = new String[] { "Book Id", "Book Name", "Author Name" };

	
	public void multipleCSV(List<Book> list, int maxRecords) {
		int countRows = list.size(); // counts the number of rows in the sheet.
		int numberOfFiles = (countRows / maxRecords) + 1;

		for (int file = 0; file < numberOfFiles; file++) {
			System.out.println("Create file number " + (file + 1));
			int fileNumber = file + 1;
			System.out.println("Start number: " + ((file * maxRecords) + 1));
			int startNumber = (file * maxRecords);
			try {
				List<Book> subList = list.subList(startNumber, Math.min(startNumber+maxRecords, list.size()));

				ArrayList<Book> listBooks = new ArrayList<Book>(subList);
				populateFile(fileNumber, startNumber, listBooks);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("");
		}
	}

	public static void populateFile(int fileNumber, int startNumber, List<Book> books) throws IOException {
		BufferedWriter bw = setFile(fileNumber);
		Book book;
		writeRow(bw, CSV_HEADER);
		bw.newLine();
		System.out.println(books);
		int limit = books.size();
		System.out.println(String.format("End Number: %d", startNumber + limit));
		System.out.println();
		for (int i = 0; i < limit; i++) {
			book = books.get(i);
			String[] row = new String[] { book.getBookId() + "", book.getBookName()+"", book.getAuthorName()+"" };
			// System.out.println(i);
			writeRow(bw, row);
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	
	public static void writeRow(BufferedWriter bw, String[] row) throws IOException { 
	    if (row.length > 0) {
	        bw.write(row[0]);
	        for (int j = 1; j < row.length; j++) {
	            bw.write(',');
	            bw.write(row[j]);
	        }
	    }
	}
	public static BufferedWriter setFile(int fileNumber) throws IOException {
		String csvFilename = directory + "file-" + fileNumber + ".csv";
		FileWriter csvFile = new FileWriter(csvFilename);
		// the Unicode value for UTF-8 BOM
		csvFile.write("\ufeff");
		BufferedWriter bw = new BufferedWriter(csvFile);
		return bw;
	}

	public static int getLimit(List<Book> books, int startNumber, int maxRecords) {
		int limit;
		int countRows = books.size();
		if (maxRecords <= countRows) {
			limit = maxRecords;
		} else {
			limit = (countRows - startNumber);
		}
		return limit;
	}
}
