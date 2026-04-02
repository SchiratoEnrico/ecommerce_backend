package com.betacom.ecommerce.backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "fatture")
@EntityListeners(AuditingEntityListener.class)
public class Fattura {
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Integer id;
	 
	 @Column(name = "numero_fattura", 
			 unique = true, 
			 nullable = false, 
			 length = 100)
	 private String numeroFattura;
	 
	 @CreatedDate
	 @Column(name = "data_emissione", 
			 nullable = false)
	 private LocalDate dataEmissione;
	 
	 //Snapshot Cliente
	 @Column(name = "cliente_nome", 
			 nullable = false, 
			 length = 100)
	 private String clienteNome;

    @Column(name = "cliente_cognome", 
    		nullable = false, 
    		length = 100)
    private String clienteCognome;

    @Column(name = "cliente_email", 
    		nullable = false, 
    		length = 100)
    private String clienteEmail;

    @Column(name = "cliente_indirizzo", 
    		length = 255)
    private String clienteIndirizzo;

    @Column(name = "cliente_citta", 
    		length = 100)
    private String clienteCitta;

    @Column(name = "cliente_cap", 
    		length = 10)
    private String clienteCap;

    @Column(name = "cliente_provincia",
    		length = 100)
    private String clienteProvincia;

    @Column(name = "cliente_stato", 
    		length = 100)
    private String clienteStato;
    
    //Dati pagamento e spedizione
    @Column(name = "tipo_pagamento", 
    		nullable = false, 
    		length = 100)
    private String tipoPagamento;

    @Column(name = "tipo_spedizione", 
    		length = 100)
    private String tipoSpedizione;	
    
    //Costi
    @Column(name = "costo_spedizione", 
    		precision = 10, 
    		scale = 2)
    private BigDecimal costoSpedizione = BigDecimal.ZERO;

    @Column(name = "totale", 
    		nullable = false, 
    		precision = 10, 
    		scale = 2)
    private BigDecimal totale;
    
    // Metadata
    @Column(name = "note", 
    		columnDefinition = "TEXT")
    private String note;
    
       
    @OneToMany(mappedBy = "fattura", 
    		cascade = CascadeType.ALL,
           orphanRemoval = true, 
           fetch = FetchType.LAZY)
    private List<RigaFattura> righe = new ArrayList<>();
        
    @Column(name = "stato_fattura", length = 20, nullable = false)
    private String statoFattura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordine", nullable = true)
    private Ordine ordine;//probabilmente meglio storage come integer


}
