package com.betacom.ecommerce.backend.dto.outputs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class SpedizioniDTO {
	private Integer id;
	private String tipoSpedizione;
}
