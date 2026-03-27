package com.betacom.ecommerce.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.RigaOrdine;

public interface IRigaOrdineRepository  extends JpaRepository<RigaOrdine, Integer>, JpaSpecificationExecutor<RigaOrdine>{
	  boolean existsByMangaIsbn(String isbn);
	  List<RigaOrdine> findAllByOrdineId(Integer id) throws MangaException;
}
