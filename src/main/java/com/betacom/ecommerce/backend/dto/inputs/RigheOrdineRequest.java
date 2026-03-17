package com.betacom.ecommerce.backend.dto.inputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RigheOrdineRequest {
	private Integer id;
	private Integer idOrdine;
	private String manga;
	private Integer numeroCopie;
}
