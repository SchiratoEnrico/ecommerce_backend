package com.betacom.ecommerce.backend.dto.inputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountRequest {
	
	private Integer id;
	private String username;
	private String password;
	private String email;
	private String ruolo;
	

}
