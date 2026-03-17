package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.models.Ordini;

public interface IOrdiniRepository extends JpaRepository<Ordini, Integer>{
}
