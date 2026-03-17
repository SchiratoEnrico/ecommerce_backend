package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Generi;

@Repository
public interface IGeneriRepository extends JpaRepository<Generi, Integer>{
	Boolean existsByDescrizioneAndIdNot(String genere, Integer id);
}
