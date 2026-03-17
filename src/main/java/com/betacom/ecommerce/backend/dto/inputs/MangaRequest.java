package com.betacom.ecommerce.backend.dto.inputs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MangaRequest {
	
	private String isbn;
	
	private String titolo;
	
	private String dataPubblicazione;
	
	private Double prezzo;
	
	private String immagine;
	
	private Integer numeroCopie;
	
	//questi sono id
	private Integer casaEditrice;
	private List<Integer> generi;
	private List<Integer> autori;
	
	

}
