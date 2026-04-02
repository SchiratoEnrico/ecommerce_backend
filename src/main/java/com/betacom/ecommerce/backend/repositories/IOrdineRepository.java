package com.betacom.ecommerce.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.betacom.ecommerce.backend.models.Ordine;

public interface IOrdineRepository extends JpaRepository<Ordine, Integer>, JpaSpecificationExecutor<Ordine>{
	 boolean existsByTipoPagamentoId(Integer id);
	 boolean existsByTipoSpedizioneId(Integer id);
	 boolean existsByStatoId(Integer id);
	 List<Ordine> findAllByAccountId(Integer id);
}
