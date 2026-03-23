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
		 	// sarà yyyy-MM-dd
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new MangaException("!valid_dtn");
        }
	}
	
	public static void validatePassword(String password) throws MangaException {
	    if (password == null || password.isBlank())
	        throw new MangaException("null_pwd");

	    if (password.trim().length() < 6)
	        throw new MangaException("pwd_short");

	    if (!password.chars().anyMatch(c -> Character.isUpperCase(c)))
	        throw new MangaException("pwd_upper");

	    if (!password.chars().anyMatch(c -> Character.isLowerCase(c)))
	        throw new MangaException("pwd_lower");

	    if (!password.chars().anyMatch(c -> Character.isDigit(c)))
	        throw new MangaException("pwd_digit");

	    if (!password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:',.<>?/`~".indexOf(c) >= 0))
	        throw new MangaException("pwd_special");
	}
	
}
