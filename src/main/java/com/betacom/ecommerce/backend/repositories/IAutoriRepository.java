package com.betacom.ecommerce.backend.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Autori;

@Repository
public interface IAutoriRepository extends JpaRepository<Autori, Integer>{
	Boolean existsByNomeAndCognomeAndDataNascitaAndIdNot(String nome, String cognome, LocalDate dataNascita, Integer id);
}
