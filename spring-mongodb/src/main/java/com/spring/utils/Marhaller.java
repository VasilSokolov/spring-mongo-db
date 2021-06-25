package com.spring.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.spring.model.Address;
import com.spring.model.Book;

public class Marhaller {


	int rowCount;
	int fileCount = 1;
	PrintWriter writer;
	int lineCount;
	int index;
	String line;
	ArrayList<Book> listBooks;
	
	 public void readCSV(){
	        try {
	            BufferedReader buffeReader = new BufferedReader(new FileReader("inputCSV.csv"));
	            while ((line = buffeReader.readLine()) != null) {   //get every single line individually in csv file

	                rowCount++;
	                if(rowCount == 1){
	                    openNewFile();
	                }

	                String[] value = line.split(",");   //collect the comma separated values into array
	                StringBuilder stringBuilder = new StringBuilder();
	                stringBuilder.append(value[0]+','+value[1]+','+value[2]+'\n');  //append the array values to stringBuilder with comma separation
	                writer.write(stringBuilder.toString()); //write individual data line into csv file

	                if(rowCount == 100){
	                    fileCount++;
	                    closeWriter();
	                }
	            }
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 public void readMultipleCSV(){
	        ArrayList<File> xmlFileList = new ArrayList();
	        System.out.println(xmlFileList.size());
	        xmlFileList.addAll(Arrays.asList(new File("your_multiple_csv_file_output_path").listFiles()));  //add all generated csv files into array list

	        for (int i = 0; i < xmlFileList.size(); i++) {
	            Book book = new Book();
	            ArrayList<Address> addressList = new ArrayList();
	            try {
	                BufferedReader br = new BufferedReader(new FileReader(xmlFileList.get(i).toString()));  //get csv file separately
	                while ((line = br.readLine()) != null) {    //get every single line individually in csv file
	                    String[] value = line.split(",");   //collect the comma separated values into array
	                    Address address = new Address();
	                    address.setId(Long.parseLong(value[0])); //first element of an array is id
	                    address.setfName(value[1]);  //second element of an array is firstName
	                    address.setCity(value[2]);   //third element of an array is lastName
	                    addressList.add(address);   //add person object into the list
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            book.setAddressList(addressList);
	            prepareXML(book, i);
	        }
	    }

	    public void prepareXML(Book book, int csvFileNo){
	        //marshaling with java
//	        try {
//
//	            JAXBContext jaxbContext = JAXBContext.newInstance(Book.class);
//	            javax.xml.bind.Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//	            jaxbMarshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
//	            jaxbMarshaller.marshal(people, new File("your_xml_file_output_path/output "+(csvFileNo+1)+".xml"));
//	            jaxbMarshaller.marshal(people, System.out);
//
//	        } catch (JAXBException e) {
//	            e.printStackTrace();
//	        }
	    }
	 
	public void openNewFile() {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)); // create a

		// the Unicode value for UTF-8 BOM
		writer.write("\ufeff");																						
																										
	}

	public void closeWriter() {
		writer.close(); // close a csv file
		rowCount = 0;
	}
}
