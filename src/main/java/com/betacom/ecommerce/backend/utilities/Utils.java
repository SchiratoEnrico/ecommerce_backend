package com.betacom.ecommerce.backend.utilities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Fattura;

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new MangaException("!valid_dtn");
        }
	}	
	
	public static void ricalcolaTotale(Fattura fat) {
        BigDecimal costoSpedizione = fat.getCostoSpedizione() != null ? fat.getCostoSpedizione() : BigDecimal.ZERO;

        BigDecimal totale = fat.getRighe().stream()
                .map(r -> r.getTotaleRiga())
                .reduce(BigDecimal.ZERO, (acc, riga) -> acc.add(riga));

        fat.setTotale(totale.add(costoSpedizione));
    }

}
