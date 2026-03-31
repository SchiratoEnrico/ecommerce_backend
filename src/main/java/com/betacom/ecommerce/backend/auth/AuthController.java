package com.betacom.ecommerce.backend.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	
	private final AuthServices authService;

	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            // Se va tutto bene, restituiamo il token e il ruolo con stato 200 OK
            AuthResponse response = authService.login(req);
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            // Se le credenziali sono sbagliate
            // restituiamo 401 Unauthorized con un JSON: {"msg": "Username o password errati."}
            log.warn("Tentativo di login fallito per l'utente: {}", req.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Username o password errati. Riprova."));
            
        } catch (Exception e) {
            // Per qualsiasi altro errore imprevisto
            log.error("Errore durante il login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("msg", "Errore del server. Riprova più tardi."));
        }
    }
}
