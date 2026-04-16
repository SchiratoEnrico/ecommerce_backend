package com.betacom.ecommerce.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.PasswordResetToken;

public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    // Utile per eliminare un vecchio token se l'utente richiede due volte il reset
    Optional<PasswordResetToken> findByAccount(Account account); 
}