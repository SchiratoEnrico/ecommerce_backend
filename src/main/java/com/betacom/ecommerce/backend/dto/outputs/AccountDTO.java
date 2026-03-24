package com.betacom.ecommerce.backend.dto.outputs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class AccountDTO {

	private Integer id;
	private String username;
	private String email;
	private String ruolo;
	private List<AnagraficaDTO> anagrafiche;
    private Integer carrelloId;
    
}
