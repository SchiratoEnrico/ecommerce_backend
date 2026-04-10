package com.betacom.ecommerce.backend.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	 private final IAccountRepository accountRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Account acc = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

		return new org.springframework.security.core.userdetails.User(
                acc.getUsername(),
                acc.getPassword(),
                List.of(new SimpleGrantedAuthority(acc.getRuolo().name()))
        );
	}
}
