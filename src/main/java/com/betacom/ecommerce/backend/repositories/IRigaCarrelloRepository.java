package com.betacom.ecommerce.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.RigaCarrello;

@Repository
public interface IRigaCarrelloRepository extends
JpaRepository<RigaCarrello, Integer>,
JpaSpecificationExecutor<RigaCarrello>{
	boolean existsByMangaIsbn(String isbn);
}
