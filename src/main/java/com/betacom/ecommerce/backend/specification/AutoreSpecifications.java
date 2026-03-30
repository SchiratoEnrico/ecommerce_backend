package com.betacom.ecommerce.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Autore;

public class AutoreSpecifications {
	
	public static Specification<Autore> nomeContains(String nome) {
        return (root, query, cb) -> {
            if (nome == null || nome.isBlank()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.get("nome")),
                    "%" + nome.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Autore> cognomeContains(String cognome) {
        return (root, query, cb) -> {
            if (cognome == null || cognome.isBlank()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.get("cognome")),
                    "%" + cognome.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Autore> nomeAndCognome(String nome, String cognome) {
        return Specification
                .where(nomeContains(nome))
                .and(cognomeContains(cognome));
    }
}
