package com.betacom.ecommerce.backend.dto.inputs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdineRequest {
	private Integer id;
	private Integer account;
	private String pagamento;
	private String spedizione;
	private String data;
	private String stato;
	private List<String> righeOrdineRequest;
}
