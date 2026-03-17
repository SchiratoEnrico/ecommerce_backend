package com.betacom.ecommerce.backend.dto.inputs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class SpedizioniReq {
	private Integer id;
	private String tipoSpedizione;
}
