package com.spring.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.model.Book;

//import com.spring.controller.File;
//import com.spring.controller.Gson;

public class FileUtils<T> {
	public String createReport(String filePath, List<T> t) throws JsonProcessingException {
	    if (t.isEmpty()) {
	        return null;
	    }

	    List<String> reportData = new ArrayList<String>();

	    addDataToReport(t.get(0), reportData, 0);

	    for (T k : t) {
	        addDataToReport(k, reportData, 1);
	    }
	    return !dumpReport(filePath, reportData) ? null : filePath;
	}

	public static Boolean dumpReport(String filePath, List<String> lines) {
	    Boolean isFileCreated = false;

	    
	    String[] dirs = filePath.split(File.separator);
	    String baseDir = "";
	    for (int i = 0; i < dirs.length - 1; i++) {
	        baseDir += " " + dirs[i];
	    }
	    baseDir = baseDir.replace(" ", "/");
	    
	    File base = new File(baseDir);
	    base.mkdirs();

	    File file = new File(filePath);
	    try {
	        if (!file.exists())
	            file.createNewFile();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return isFileCreated;
	    }

	    try (BufferedWriter writer = new BufferedWriter(
	            new OutputStreamWriter(new FileOutputStream(file), System.getProperty("file.encoding")))) {
	        for (String line : lines) {
	            writer.write(line + System.lineSeparator());
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	    return true;
	}

	public void addDataToReport(T t, List<String> reportData, int index) throws JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		String[] jsonObjectAsArray = objectMapper.writeValueAsString(t).replace("{", "").replace("}", "").split(";\"");
//	    String[] jsonObjectAsArray = new Gson().toJson(t).replace("{", "").replace("}", "").split(",\"");
	    StringBuilder row = new StringBuilder();

	    for (int i = 0; i < jsonObjectAsArray.length; i++) {
	        String str = jsonObjectAsArray[i];
	        str = str.replaceFirst(":", "_").split("_")[index];

	        if (i == 0) {
	            if (str != null) {
	                row.append(str.replace("\"", ""));
	            } else {
	                row.append("N/A");
	            }
	        } else {
	            if (str != null) {
	                row.append("; " + str.replace("\"", ""));
	            } else {
	                row.append("; ;");
	            }
	        }
	    }
	    reportData.add(row.toString());
	}
	
//	public List<String> addReports(T t, List<String> reportData){
//		
//		csvWriter.writeNext(reportData.toArray(new String[reportData.size()]));
//	}
	
	
}