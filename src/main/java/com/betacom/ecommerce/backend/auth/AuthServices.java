package com.betacom.ecommerce.backend.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServices {
	
	private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IAccountRepository accountRepository;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        String token = jwtService.generateToken(request.getUsername());
        
        // Recupera l'utente per sapere il suo ruolo
        Account acc = accountRepository.findByUsername(request.getUsername()).orElseThrow();
        log.debug(acc.getRuolo().name());
        // Restituisci sia token che ruolo
        return new AuthResponse(token, acc.getRuolo().name()); 
    }
}
