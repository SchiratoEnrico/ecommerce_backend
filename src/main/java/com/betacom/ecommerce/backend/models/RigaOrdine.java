package com.betacom.ecommerce.backend.models;

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
@Table (name="righeordine")
public class RigaOrdine {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(
			name = "id_ordine",
			nullable = false
			)
	private Integer idOrdine;


	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "id_manga", nullable = false, unique = false)
	private Manga manga;
	
	@Column(
			name = "numero_copie",
			nullable = false)
	private Integer numeroCopie;
}
