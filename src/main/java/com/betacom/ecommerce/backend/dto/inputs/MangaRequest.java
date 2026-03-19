package com.betacom.ecommerce.backend.dto.inputs;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MangaRequest {
	
	private String isbn;
	
	private String titolo;
	
	private String dataPubblicazione;
	
	private BigDecimal prezzo;
	
	private String immagine;
	
	private Integer numeroCopie;
	
	//questi sono id
	private Integer casaEditrice;
	private List<Integer> generi;
	private List<Integer> autori;
	
	

}
