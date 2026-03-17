package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.models.Carrelli;

public interface ICarrelliRepository extends 
JpaRepository<Carrelli, Integer>,
JpaSpecificationExecutor<Carrelli>{

}
