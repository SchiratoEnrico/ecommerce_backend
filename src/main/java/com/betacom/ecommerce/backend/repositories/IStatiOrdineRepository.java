package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.StatiOrdine;

public interface IStatiOrdineRepository extends JpaRepository<StatiOrdine, Integer>{
	Optional<StatiOrdine> findByStato(String stato) throws MangaException;
}
