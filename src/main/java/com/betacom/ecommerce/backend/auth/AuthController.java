package com.betacom.ecommerce.backend.auth;

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
    public AuthResponse login(@RequestBody LoginRequest req) {
        
    	return authService.login(req);
    }
}
