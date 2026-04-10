package com.betacom.ecommerce.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.betacom.ecommerce.backend.models.Fattura;

public interface IFatturaRepository extends 
JpaRepository<Fattura, Integer>, JpaSpecificationExecutor<Fattura>{
	Boolean existsByOrdineId(Integer idOrdine);
	Optional<Fattura> findByNumeroFattura(String numeroFattura);
	Optional<Fattura> findByOrdineId(Integer idOrdine);
	
	@Query("SELECT f FROM Fattura f WHERE f.ordine.account.id = :accountId")
	List<Fattura> findAllByAccountId(@Param("accountId") Integer accountId);
	 
	@Query("SELECT g.id FROM Fattura f " +
		       "JOIN f.righe r, Manga m JOIN m.generi g " +
		       "WHERE r.isbn = m.isbn AND f.ordine.account.id = :accountId " +
		       "GROUP BY g.id " +
		       "ORDER BY COUNT(g.id) DESC")
		List<Integer> findTopGeneriByAccount(@Param("accountId") Integer accountId, Pageable pageable);
		 
		@Query("SELECT DISTINCT r.isbn FROM Fattura f " +
		       "JOIN f.righe r " +
		       "WHERE f.ordine.account.id = :accountId")
		List<String> findIsbnCompratiByAccount(@Param("accountId") Integer accountId);
}
