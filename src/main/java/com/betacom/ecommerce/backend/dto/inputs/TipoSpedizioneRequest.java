package com.betacom.ecommerce.backend.dto.inputs;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TipoSpedizioneRequest {
	private Integer id;
	private String tipoSpedizione;
	private BigDecimal costoSpedizione;
}
