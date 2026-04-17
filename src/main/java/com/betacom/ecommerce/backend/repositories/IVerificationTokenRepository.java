package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.VerificationToken;

@Repository
public interface IVerificationTokenRepository extends JpaRepository<VerificationToken, Integer>{
	Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByAccount(Account account);
}
