package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;

public interface ITipoSpedizioneRepository extends 
JpaRepository<TipoSpedizione, Integer>,
JpaSpecificationExecutor<TipoSpedizione>
{
	Optional<TipoSpedizione> findByTipoSpedizione(String spedizione) throws MangaException;

}
