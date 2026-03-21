package com.betacom.ecommerce.backend.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table (name="righe_ordine")
public class RigaOrdine {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;

	
    @ManyToOne
    @JoinColumn(name = "id_ordine", nullable = false)
    private Ordine ordine;


	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "isbn_manga", nullable = false, unique = false)
	private Manga manga;
	
	@Column(
			name = "numero_copie",
			nullable = false)
	private Integer numeroCopie;
	
	
	private BigDecimal prezzo;
}
