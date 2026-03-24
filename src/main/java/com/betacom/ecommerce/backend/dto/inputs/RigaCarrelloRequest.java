package com.betacom.ecommerce.backend.dto.inputs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RigaCarrelloRequest {
	private Integer id;
	private Integer carrelloId;
	private String manga;
	private Integer numeroCopie;
}
