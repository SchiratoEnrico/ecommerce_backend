package com.betacom.ecommerce.backend.models;

import java.time.LocalDate;
import java.util.List;

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
public class Ordini {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_account")
	@Column(
			name = "account",
			nullable = false
			)
	private Accounts account;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pagamento")
	@Column(
			name = "pagamento",
			nullable = false
			)
	private Pagamenti pagamento;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_spedizione")
	@Column(
			name = "spedizione",
			nullable = false
			)
	private Spedizioni spedizione;
	
	@Column(
			name = "data",
			nullable = false
			)
	private LocalDate data;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_stato")
	@Column(
			name = "stato_oridine",
			nullable = false
			)
	private StatiOrdine stato;
	
	@OneToMany
	@JoinColumn(name = "id_ordine")
	@Column(
			name = "oggetti",
			nullable = false
			)
	private List<RigheOrdine> oggetti;
}
