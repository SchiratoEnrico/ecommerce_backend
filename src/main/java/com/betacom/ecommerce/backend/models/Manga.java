package com.betacom.ecommerce.backend.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "manga")
public class Manga {
	
	@Id
	private String isbn;
	
	@Column(nullable = false)
	private String titolo;
	
	@Column(name = "data_pubblicazione",
			nullable = false)
	private LocalDate dataPubblicazione;
	
	@Column(nullable = false)
	private Double prezzo;
	
	@Column(nullable = false)
	private String immagine;
	
	@Column(nullable = false,
			name = "numero_copie")
	private Integer numeroCopie;
	
	@Column(nullable = false,
			name = "case_editrici")
	@ManyToOne
    @JoinColumn(name = "id_casa_editrice", nullable = false)
	private CaseEditrici casaEditrice;
	

	@ManyToMany
	@JoinTable(
	        name = "manga_autori",
	        joinColumns = @JoinColumn(name = "isbn_manga"),
	        inverseJoinColumns = @JoinColumn(name = "id_autore")
	    )
	private List<Autori> autori;
	
	
	@ManyToMany
    @JoinTable(
        name = "manga_generi",
        joinColumns = @JoinColumn(name = "isbn_manga"),
        inverseJoinColumns = @JoinColumn(name = "id_genere")
    )
	private List<Generi> generi;
}