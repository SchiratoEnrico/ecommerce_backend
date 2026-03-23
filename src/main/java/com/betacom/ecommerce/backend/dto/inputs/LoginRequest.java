package com.betacom.ecommerce.backend.dto.inputs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@Builder
public class LoginRequest {
	
	private String password;
	
	private String username;
}
