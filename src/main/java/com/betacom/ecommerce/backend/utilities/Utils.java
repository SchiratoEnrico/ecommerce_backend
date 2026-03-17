package com.betacom.ecommerce.backend.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.betacom.ecommerce.backend.exceptions.MangaException;

public class Utils {

	public static String normalize(String value) {
	    if (value == null) return null;
	    return value.trim().toUpperCase();
	}

	public static Boolean isBlank(String value) {
	    return value == null || value.trim().isEmpty();
	}
	
	public static LocalDate stringToDate(String date) throws MangaException{
		
	 try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new MangaException("!valid_dtn");
        }
	}
}
