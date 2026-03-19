package com.betacom.ecommerce.backend.models;

import java.math.BigDecimal;
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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "manga")
@Getter
@Setter
@ToString(exclude = {"casaEditrice", "autori", "generi"})
public class Manga {
	
	@Id
	private String isbn;
	
	@Column(nullable = false)
	private String titolo;
	
	@Column(name = "data_pubblicazione",
			nullable = false)
	private LocalDate dataPubblicazione;
	
	@Column(nullable = false)
	private BigDecimal prezzo;
	
	@Column(nullable = false)
	private String immagine;
	
	@Column(nullable = false,
			name = "numero_copie")
	private Integer numeroCopie;
	
	@ManyToOne
    @JoinColumn(name = "id_casa_editrice", nullable = false)
	private CasaEditrice casaEditrice;
	

	@ManyToMany
	@JoinTable(
	        name = "manga_autori",
	        joinColumns = @JoinColumn(name = "isbn_manga"),
	        inverseJoinColumns = @JoinColumn(name = "id_autore")
	    )
	private List<Autore> autori;
	
	
	@ManyToMany
    @JoinTable(
        name = "manga_generi",
        joinColumns = @JoinColumn(name = "isbn_manga"),
        inverseJoinColumns = @JoinColumn(name = "id_genere")
    )
	private List<Genere> generi;
}