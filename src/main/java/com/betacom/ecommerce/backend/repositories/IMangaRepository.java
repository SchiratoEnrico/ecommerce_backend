package com.betacom.ecommerce.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Manga;

@Repository
public interface IMangaRepository extends JpaRepository<Manga, String>{

	boolean existsByAutoriId(Integer idAutore);
	
	boolean existsByGeneriId(Integer idGenere);
	
	Optional<Manga> findByIsbn(String isbn);
	
	List<Manga> findAllByAutoriId(Integer id);
	
	List<Manga> findAllByGeneriId(Integer id);
}
