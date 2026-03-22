package com.betacom.ecommerce.backend.dto.outputs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class RigaCarrelloDTO {
	private Integer id;
	private Integer carrelloId;
	private MangaDTO manga;
	private Integer numeroCopie;
}
