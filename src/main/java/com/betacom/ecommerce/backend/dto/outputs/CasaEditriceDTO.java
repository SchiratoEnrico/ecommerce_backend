package com.betacom.ecommerce.backend.dto.outputs;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class CasaEditriceDTO {
	private Integer id;
	private String nome;
	private String descrizione;
	private String indirizzo;
	private String email;
	private List<String> isbns;
}
