package com.betacom.ecommerce.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Manga;

@Repository
public interface IMangaRepository extends 
	JpaRepository<Manga, String>,
	JpaSpecificationExecutor<Manga>
{

	boolean existsByAutoriId(Integer idAutore);
	
	boolean existsByGeneriId(Integer idGenere);
	
	Optional<Manga> findByIsbn(String isbn);
	
	List<Manga> findAllByAutoriId(Integer id);
	
	List<Manga> findAllByGeneriId(Integer id);

	List<Manga> findAllBySagaId(Integer id);

	boolean existsBySagaIdAndSagaVol(Integer sagaId, Integer sagaVol);
	
	@Query("SELECT m FROM Fattura f " +
		       "JOIN f.righe r, Manga m " +
		       "WHERE r.isbn = m.isbn " +
		       "GROUP BY m " +
		       "ORDER BY SUM(r.numeroCopie) DESC")
		List<Manga> findTopBestSellers(Pageable pageable);
	
	List<Manga> findTop10ByOrderByIsbnDesc();
}
