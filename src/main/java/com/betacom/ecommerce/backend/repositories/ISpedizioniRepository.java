package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.models.Spedizioni;

public interface ISpedizioniRepository extends 
JpaRepository<Spedizioni, Integer>,
JpaSpecificationExecutor<Spedizioni>
{

}
