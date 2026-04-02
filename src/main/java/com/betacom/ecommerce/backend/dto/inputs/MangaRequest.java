package com.betacom.ecommerce.backend.dto.inputs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataPubblicazione;
	
	private BigDecimal prezzo;
	
	private String immagine;
	
	private Integer numeroCopie;
	
	private Integer saga;
	
	private Integer sagaVol;
	
	//questi sono id
	private Integer casaEditrice;
	private List<Integer> generi;
	private List<Integer> autori;
}
