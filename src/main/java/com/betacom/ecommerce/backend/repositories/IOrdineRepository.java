package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.models.Ordine;

public interface IOrdineRepository extends JpaRepository<Ordine, Integer>, JpaSpecificationExecutor<Ordine>{
}
