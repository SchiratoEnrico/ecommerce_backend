package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.CasaEditrice;

@Repository
public interface ICasaEditriceRepository extends 
JpaRepository<CasaEditrice, Integer>, 
JpaSpecificationExecutor<CasaEditrice> {
	
}
