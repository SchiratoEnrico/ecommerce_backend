package com.betacom.ecommerce.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.RigaFattura;

public interface IRigaFatturaRepository extends JpaRepository<RigaFattura, Integer>{
	  List<RigaFattura> findAllByFatturaId(Integer id) throws MangaException;

}
