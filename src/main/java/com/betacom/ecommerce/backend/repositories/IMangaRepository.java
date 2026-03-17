package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Manga;

@Repository
public interface IMangaRepository extends JpaRepository<Manga, String>{

}
