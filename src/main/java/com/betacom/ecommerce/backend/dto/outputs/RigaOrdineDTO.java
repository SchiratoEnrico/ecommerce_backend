package com.betacom.ecommerce.backend.dto.outputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RigaOrdineDTO {
	private Integer id;
	private Integer idOrdine;
	//private MangaDTO manga;
	private String manga;
	private Integer numeroCopie;
}
