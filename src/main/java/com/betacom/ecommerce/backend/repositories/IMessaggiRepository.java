package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.models.Messages;



public interface IMessaggiRepository extends JpaRepository<Messages, String>{
	Optional<Messages> findByCode(String code) throws Exception;
}
