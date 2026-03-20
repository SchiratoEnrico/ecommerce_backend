package com.betacom.ecommerce.backend.dto.outputs;

import java.util.List;

import com.betacom.ecommerce.backend.models.Manga;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class CarrelloDTO {
	private Integer id;
	private AccountDTO account;
	private List<RigaCarrelloDTO> righeCarrello;
}
