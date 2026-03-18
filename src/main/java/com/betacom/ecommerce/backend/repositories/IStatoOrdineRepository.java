package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.StatoOrdine;

public interface IStatoOrdineRepository extends JpaRepository<StatoOrdine, Integer>{
	Optional<StatoOrdine> findByStatoOrdine(String stato) throws MangaException;
}
