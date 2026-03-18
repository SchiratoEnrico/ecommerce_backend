package com.betacom.ecommerce.backend.dto.outputs;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MangaDTO {
	
	private String isbn;
	
	private String titolo;
	
	private LocalDate dataPubblicazione;
	
	private Double prezzo;
	
	private String immagine;
	
	private Integer numeroCopie;
	
	//questi sono id
	//private CaseEditrici casaEditrice;
	private List<GenereDTO> generi;
	private List<AutoreDTO> autori;
}
