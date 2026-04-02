package com.betacom.ecommerce.backend.models;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "saghe")
@Getter 
@Setter
public class Saga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;        // "One Piece"
	@Column(nullable = true)
    private String immagine;    // saga cover
    @Column(nullable = false)
    private String descrizione;
    @Column(nullable = false)
    private Boolean proxy = false; // x capire se è stata autogenerata da manga senza saga

    @OneToMany(
    		mappedBy = "saga",
    		fetch = FetchType.LAZY
    		)
    private List<Manga> manga;
}