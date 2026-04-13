package com.betacom.ecommerce.backend.dto.outputs;

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
public class RigaFatturaDTO {

	private Integer id;
	private Integer idFattura;
    private String isbn;
    private String titolo;
    private String autore;
    private BigDecimal prezzoUnitario;
    private Integer numeroCopie;
    private BigDecimal totaleRiga;
	
}
