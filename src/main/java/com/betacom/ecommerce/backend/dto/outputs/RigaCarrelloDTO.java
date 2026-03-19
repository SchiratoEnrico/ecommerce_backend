package com.betacom.ecommerce.backend.dto.outputs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RigaCarrelloDTO {
	private Integer id;
	private CarrelloDTO carrello;
	private MangaDTO manga;
	private Integer numeroCopie;
}
