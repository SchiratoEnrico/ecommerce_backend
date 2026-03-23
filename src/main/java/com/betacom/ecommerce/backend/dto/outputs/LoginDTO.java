package com.betacom.ecommerce.backend.dto.outputs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@Builder
public class LoginDTO {

	private String username;
	
	private String ruolo;
	
	private Integer id;
}
