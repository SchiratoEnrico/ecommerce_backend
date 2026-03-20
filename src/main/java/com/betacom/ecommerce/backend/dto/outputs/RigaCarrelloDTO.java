package com.betacom.ecommerce.backend.dto.outputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RigaCarrelloDTO {
	private Integer id;
	private Integer carrelloId;
	private MangaDTO manga;
	private Integer numeroCopie;
}
