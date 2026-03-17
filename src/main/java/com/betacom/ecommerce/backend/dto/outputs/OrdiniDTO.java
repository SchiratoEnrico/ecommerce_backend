package com.betacom.ecommerce.backend.dto.outputs;

import java.time.LocalDate;
import java.util.List;

//import com.betacom.ecommerce.backend.models.AccountsDTO;
//import com.betacom.ecommerce.backend.models.PagamentiDTO;
//import com.betacom.ecommerce.backend.models.SpedizioniDTO;
//import com.betacom.ecommerce.backend.models.StatiOrdineDTO;

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
public class OrdiniDTO {
	private Integer id;
	private AccountsDTO account;
	private PagamentiDTO pagamento;
	private SpedizioniDTO spedizione;
	private LocalDate data;
	private StatiOrdineDTO stato;
	private List<RigheOrdineDTO> righeOrdine;
	
	public OrdiniDTO(Integer id) {
		this.id = id;
	}
}
