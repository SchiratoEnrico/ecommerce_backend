package com.betacom.ecommerce.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "righe_carrello")
public class RigaCarrello {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn (name="id_carrello", nullable = false)
	private Carrello carrello;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name="isbn_manga",
			referencedColumnName = "isbn",
			nullable = false
			)
	private Manga manga;
	
	@Column(name = "numero_copie")
	private Integer numeroCopie;
}
