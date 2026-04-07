package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.models.Fattura;

public interface IFatturaRepository extends 
JpaRepository<Fattura, Integer>, JpaSpecificationExecutor<Fattura>{
	Boolean existsByOrdineId(Integer idOrdine);
	Optional<Fattura> findByNumeroFattura(String numeroFattura);
	Optional<Fattura> findByOrdineId(Integer idOrdine);
}
