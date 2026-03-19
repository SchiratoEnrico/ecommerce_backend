package com.betacom.ecommerce.backend.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"manga"})
@Entity
@Table(name = "autori")
public class Autore {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nome;
	
	@Column(nullable = false)
	private String cognome;
	
	@Column(nullable = false,
			name = "data_nascita")
	private LocalDate dataNascita;
	
	@Column(nullable = false)
	private String descrizione;
	
	@ManyToMany(mappedBy = "autori")
	private List<Manga> manga;
}