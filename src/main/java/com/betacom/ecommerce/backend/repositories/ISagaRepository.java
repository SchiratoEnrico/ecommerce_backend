package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Saga;

public interface ISagaRepository extends 
JpaRepository<Saga, Integer>,
JpaSpecificationExecutor<Saga>
{
	Optional<Saga> findByNome(String nome) throws MangaException;
}
