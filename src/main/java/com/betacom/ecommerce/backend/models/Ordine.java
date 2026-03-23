package com.betacom.ecommerce.backend.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Setter
@Getter
@Entity
@Table (name="ordini")
public class Ordine {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_account", nullable = false)
	private Account account;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_pagamento", nullable = false)
	private TipoPagamento tipoPagamento;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_spedizione", nullable = false)
	private TipoSpedizione tipoSpedizione;
	
	@Column(
			name = "data",
			nullable = false
			)
	private LocalDate data;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_stato", nullable = false)
	private StatoOrdine stato;
	
	
//	cascade = CascadeType.REMOVE,
//    orphanRemoval = true

	@OneToMany(
		    mappedBy = "ordine",
		    orphanRemoval = true
		)
	private List<RigaOrdine> righeOrdine;
}
