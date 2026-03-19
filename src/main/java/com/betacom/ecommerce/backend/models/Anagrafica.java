package com.betacom.ecommerce.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "anagrafiche")
public class Anagrafica {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column (length = 100,
			nullable = false)
	private String nome;
	
	@Column (length = 100,
			nullable = false)
	private String cognome;
	
	@Column (length = 100,
			nullable = false)
	private String stato;
	
	@Column (length = 100,
			nullable = false)
	private String citta;
	
	@Column (length = 100,
			nullable = false)
	private String provincia;
	
	@Column (length = 100,
			nullable = false)
	private String cap;
	
	@Column (length = 100,
			nullable = false)
	private String via;
	
	@Column (nullable = false)
	private Boolean predefinito;
	
}
