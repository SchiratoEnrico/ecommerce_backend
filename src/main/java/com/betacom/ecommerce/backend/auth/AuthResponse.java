package com.betacom.ecommerce.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class AuthResponse {
	
	private String token;
}
