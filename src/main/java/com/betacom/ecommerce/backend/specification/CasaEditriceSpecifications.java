package com.betacom.ecommerce.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.CasaEditrice;

public class CasaEditriceSpecifications {	
	public static Specification<CasaEditrice> nomeLike(String nome) {
        return (root, query, cb) -> (nome == null || nome.isBlank())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<CasaEditrice> descrizioneLike(String descrizione) {
        return (root, query, cb) -> (descrizione == null || descrizione.isBlank())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("descrizione")), "%" + descrizione.toLowerCase() + "%");
    }

    public static Specification<CasaEditrice> indirizzoLike(String indirizzo) {
        return (root, query, cb) -> (indirizzo == null || indirizzo.isBlank())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("indirizzo")), "%" + indirizzo.toLowerCase() + "%");
    }

    public static Specification<CasaEditrice> emailLike(String email) {
        return (root, query, cb) -> (email == null || email.isBlank())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
}
