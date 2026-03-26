package com.betacom.ecommerce.backend.models;
import java.util.List;

import jakarta.persistence.Entity;
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

    private String nome;        // "One Piece"
    private String immagine;    // saga cover
    private String descrizione;

    @OneToMany(mappedBy = "saga")
    private List<Manga> manga;
}