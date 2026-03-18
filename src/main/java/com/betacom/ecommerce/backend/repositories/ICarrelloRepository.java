package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.models.Carrello;

public interface ICarrelloRepository extends 
JpaRepository<Carrello, Integer>,
JpaSpecificationExecutor<Carrello>{

}
