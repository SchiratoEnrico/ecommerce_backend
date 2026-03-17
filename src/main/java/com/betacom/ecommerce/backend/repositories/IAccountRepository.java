package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.models.Accounts;


public interface IAccountRepository extends JpaRepository<Accounts, Integer>{

	public Optional<Accounts> findByUsername(String username);
	public Optional<Accounts> findByEmail(String email);
	

}
