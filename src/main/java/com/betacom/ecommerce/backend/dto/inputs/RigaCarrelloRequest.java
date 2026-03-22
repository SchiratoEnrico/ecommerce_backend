package com.betacom.ecommerce.backend.dto.inputs;

import com.betacom.ecommerce.backend.models.Manga;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RigaCarrelloRequest {
	private Integer id;
	private Integer carrelloId;
	private Manga manga;
	private Integer numeroCopie;
}
