package com.betacom.ecommerce.backend.dto.outputs;

import java.time.LocalDate;
import java.util.List;

import com.betacom.ecommerce.backend.models.Manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoreDTO {

	private Integer id;
	
	private String nome;
	
	private String cognome;
	
	private LocalDate dataNascita;
	
	private String descrizione;
	
	private List<MangaDTO> manga;

}
