package com.betacom.ecommerce.backend.dto.inputs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TipoPagamentoRequest {
	private Integer id;
	private String tipoPagamento;
}
