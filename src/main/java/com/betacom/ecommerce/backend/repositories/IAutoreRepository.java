package com.betacom.ecommerce.backend.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Autore;

@Repository
public interface IAutoreRepository extends JpaRepository<Autore, Integer>, JpaSpecificationExecutor<Autore>{
	Boolean existsByNomeAndCognomeAndDataNascitaAndIdNot(String nome, String cognome, LocalDate dataNascita, Integer id);
	List<Autore> findAllByMangaIsbn(String isbn);
}
