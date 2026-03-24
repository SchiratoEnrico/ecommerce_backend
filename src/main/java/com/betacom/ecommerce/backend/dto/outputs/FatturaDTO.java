package com.betacom.ecommerce.backend.dto.outputs;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class FatturaDTO {
	
	private Integer id;
    private String numeroFattura;
    private LocalDate dataEmissione;

    //Snapshot Cliente 
    private String clienteNome;
    private String clienteCognome;
    private String clienteEmail;
    private String clienteIndirizzo;
    private String clienteCitta;
    private String clienteCap;
    private String clienteProvincia;
    private String clienteStato;

    //Pagamento e Spedizione 
    private String tipoPagamento;
    private String tipoSpedizione;

    //Costi 
    private BigDecimal costoSpedizione;
    private BigDecimal totale;

    // Riferimenti 
//    private String riferimentoOrdine;
//    private String riferimentoAccount;

    private String note;

    private List<RigaFatturaDTO> righeFattura;

}
