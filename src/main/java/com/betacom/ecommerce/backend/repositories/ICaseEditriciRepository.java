package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.CaseEditrici;

@Repository
public interface ICaseEditriciRepository extends 
JpaRepository<CaseEditrici, Integer>, 
JpaSpecificationExecutor<CaseEditrici> {
	
}
