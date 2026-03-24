package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.models.Fattura;

public interface IFatturaRepository extends JpaRepository<Fattura, Integer>{

}
