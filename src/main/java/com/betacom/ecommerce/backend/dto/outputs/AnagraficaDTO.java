package com.betacom.ecommerce.backend.dto.outputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnagraficaDTO {

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
