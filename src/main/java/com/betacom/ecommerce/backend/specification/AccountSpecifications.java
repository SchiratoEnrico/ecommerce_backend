package com.betacom.ecommerce.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.enums.Ruoli;
import com.betacom.ecommerce.backend.models.Account;

public class AccountSpecifications {
	
	public static Specification<Account> usernameContains(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isBlank()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.get("username")),
                    "%" + username.toLowerCase() + "%"
            );
        };
    }
	
	public static Specification<Account> emailContains(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
            );
        };
    }
	
	public static Specification<Account> ruoloEquals(String ruolo) {
	    return (root, query, cb) -> {
	        if (ruolo == null || ruolo.isBlank()) {
	            return null;
	        }

	        try {
	            Ruoli ruoloEnum = Ruoli.valueOf(ruolo.toUpperCase());
	            return cb.equal(root.get("ruolo"), ruoloEnum);
	        } catch (IllegalArgumentException e) {
	            return null; 
	        }
	    };
	}
	
	public static Specification<Account> usernameAndEmailAndRuolo(String username, String email, String ruolo) {
        return Specification
                .where(usernameContains(username))
                .and(emailContains(email))
                .and(ruoloEquals(ruolo));
    }
}
