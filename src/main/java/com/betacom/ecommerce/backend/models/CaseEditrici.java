package com.betacom.ecommerce.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="case_editrici")
public class CaseEditrici {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (length=100,
			nullable=false)
	private String nome;
	
	@Column (nullable=false)
	private String descrizione;
	
	@Column (length=100, nullable=false)
	private String indirizzo;
	
	@Column (nullable=false)
	private String email;
}