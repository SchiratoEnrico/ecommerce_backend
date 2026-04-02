package com.betacom.ecommerce.backend.dto.inputs;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class AutoreRequest {

	private Integer id;
	
	private String nome;
	
	private String cognome;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataNascita;
	
	private String descrizione;
}
