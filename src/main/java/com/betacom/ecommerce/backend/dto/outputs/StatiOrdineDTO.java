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
public class StatiOrdineDTO {
	private Integer id;
	private String stato;
	
	public StatiOrdineDTO(Integer id) {
		this.id = id;
	}
}
