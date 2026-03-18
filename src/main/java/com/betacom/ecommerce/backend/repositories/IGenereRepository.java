package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Genere;

@Repository
public interface IGenereRepository extends JpaRepository<Genere, Integer>{
	Boolean existsByDescrizioneAndIdNot(String genere, Integer id);
}
