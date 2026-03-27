package com.betacom.ecommerce.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.betacom.ecommerce.backend.models.Anagrafica;

public interface IAnagraficaRepository extends JpaRepository<Anagrafica, Integer>{
	List<Anagrafica> findByAccountId(Integer id);
}
