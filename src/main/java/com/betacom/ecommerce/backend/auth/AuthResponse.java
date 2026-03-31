package com.betacom.ecommerce.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuthResponse {
	
	private String token;
	private String ruolo;
}
