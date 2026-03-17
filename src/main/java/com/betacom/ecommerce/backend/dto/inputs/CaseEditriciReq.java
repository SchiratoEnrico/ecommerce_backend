package com.betacom.ecommerce.backend.dto.inputs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CaseEditriciReq {
	private Integer id;
	private String nome;
	private String descrizione;
	private String indirizzo;
	private String email;
}
