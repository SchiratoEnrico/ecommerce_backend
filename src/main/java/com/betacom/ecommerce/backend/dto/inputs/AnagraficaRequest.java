package com.betacom.ecommerce.backend.dto.inputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnagraficaRequest {

	private Integer id;
	private String nome;
	private String cognome;
	private String stato;
	private String citta;
	private String provincia;
	private String cap;
	private String via;
	private Boolean predefinito;
}