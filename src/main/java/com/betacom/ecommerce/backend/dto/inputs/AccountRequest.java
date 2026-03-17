package com.betacom.ecommerce.backend.dto.inputs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountRequest {
	
	private Integer id;
	private String username;
	private String email;
	private String ruolo;
	

}
