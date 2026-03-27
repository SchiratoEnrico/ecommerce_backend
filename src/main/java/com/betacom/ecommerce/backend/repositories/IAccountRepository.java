package com.betacom.ecommerce.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.enums.Ruoli;
import com.betacom.ecommerce.backend.models.Account;


public interface IAccountRepository extends JpaRepository<Account, Integer>{

	public Optional<Account> findByUsername(String username);
	public Optional<Account> findByEmail(String email);
	
	List<Account> findByRuolo(Ruoli role);
}
