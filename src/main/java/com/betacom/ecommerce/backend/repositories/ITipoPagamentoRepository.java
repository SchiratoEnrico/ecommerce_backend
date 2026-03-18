package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.TipoPagamento;

public interface ITipoPagamentoRepository extends JpaRepository<TipoPagamento, Integer>{
	Optional<TipoPagamento> findByTipoPagamento(String pagamento) throws MangaException;
}
