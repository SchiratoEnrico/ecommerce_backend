package com.betacom.ecommerce.backend.dto.inputs;

import java.math.BigDecimal;
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
public class FatturaRequest {
	
	private Integer id;
	private String numeroFattura;
	
	//Snapshot Cliente
    private String clienteNome;
    private String clienteCognome;
    private String clienteEmail;
    private String clienteIndirizzo;
    private String clienteCitta;
    private String clienteCap;
    private String clienteProvincia;
    private String clienteStato;
    
    //Dati pagamento e spedizione
    private String tipoPagamento;
    private String tipoSpedizione;
    
    //Costi
    private BigDecimal costoSpedizione;
    private BigDecimal totale;
    
    // Metadata
    private String note;
    
    private List<String> righeFatturaRequest;
 
	
	

}
