package com.betacom.ecommerce.backend.dto.inputs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TipoSpedizioneRequest {
	private Integer id;
	private String tipoSpedizione;
}
