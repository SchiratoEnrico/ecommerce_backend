package com.betacom.ecommerce.backend.dto.outputs;

import java.util.List;

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
public class GeneriDTO {

	private Integer id;
	
	private String descrizione;
	
	private List<MangaDTO> manga;
}
