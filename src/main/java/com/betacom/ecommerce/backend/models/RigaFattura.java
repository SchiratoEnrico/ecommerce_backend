package com.betacom.ecommerce.backend.models;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table (name="righe_fattura")
public class RigaFattura {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fattura", 
    			nullable = false)
    private Fattura idFattura;

    // Snapshot Manga 
	@Column(name = "isbn")
    private String isbn;

    @Column(name = "titolo", 
    		nullable = false,
    		length = 255)
    private String titolo;

    @Column(name = "autore", 
    		length = 150)
    private String autore;

    @Column(name = "prezzo_unitario", 
    		nullable = false, 
    		precision = 10, 
    		scale = 2)
    private BigDecimal prezzoUnitario;

    @Column(name = "quantita", 
    		nullable = false)
    private Integer quantita;

    @Column(name = "totale_riga", 
    		nullable = false, 
    		precision = 10, 
    		scale = 2)
    private BigDecimal totaleRiga;

}
