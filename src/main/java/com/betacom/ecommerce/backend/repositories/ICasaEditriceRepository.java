package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.CasaEditrice;

@Repository
public interface ICasaEditriceRepository extends 
JpaRepository<CasaEditrice, Integer>, 
JpaSpecificationExecutor<CasaEditrice> {
	boolean existsByIdAndMangaIsNotEmpty(Integer id);
	Optional<CasaEditrice> findByNomeIgnoreCase(String nome);
	Optional<CasaEditrice> findByEmail(String email);
}
