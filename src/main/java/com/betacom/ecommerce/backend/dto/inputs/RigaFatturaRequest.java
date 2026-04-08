package com.betacom.ecommerce.backend.dto.inputs;

import java.math.BigDecimal;
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
public class RigaFatturaRequest {
	
	private Integer id;
	private Integer idFattura;
    private String isbn;
    private BigDecimal prezzoUnitario;
    private Integer numeroCopie;
}
