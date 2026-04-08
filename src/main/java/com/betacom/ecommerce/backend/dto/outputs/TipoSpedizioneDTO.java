package com.betacom.ecommerce.backend.dto.outputs;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class TipoSpedizioneDTO {
	private Integer id;
	private String tipoSpedizione;
	private BigDecimal costoSpedizione;
}
