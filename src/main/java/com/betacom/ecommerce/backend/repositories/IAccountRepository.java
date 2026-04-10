package com.betacom.ecommerce.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.betacom.ecommerce.backend.enums.Ruoli;
import com.betacom.ecommerce.backend.models.Account;


public interface IAccountRepository extends JpaRepository<Account, Integer>, JpaSpecificationExecutor<Account>{

	public Optional<Account> findByUsername(String username);
	public Optional<Account> findByEmail(String email);
	
	List<Account> findByRuolo(Ruoli role);
	
	@Query("SELECT a.validated FROM Account a WHERE a.id = :id")
	Optional<Boolean> checkValidationStatus(@Param("id") Integer id);
}
