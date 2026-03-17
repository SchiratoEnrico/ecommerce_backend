package com.betacom.ecommerce.backend.dto.outputs;

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
public class RigheOrdineDTO {
	private Integer id;
	private Integer idOrdine;
	private MangaDTO manga;
	private Integer numeroCopie;
	
	public RigheOrdineDTO(Integer id) {
		this.id = id;
	}
}
