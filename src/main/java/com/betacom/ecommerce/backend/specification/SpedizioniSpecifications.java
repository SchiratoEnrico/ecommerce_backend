package com.betacom.ecommerce.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Spedizioni;

public class SpedizioniSpecifications {
	public static Specification<Spedizioni> tipoSpedizioneLike(String tipoSpedizione){
		return (root, query, cb) -> (tipoSpedizione==null || tipoSpedizione.isBlank())
				? cb.conjunction()
				: cb.like(cb.lower(root.get("tipoSpedizione")), "%" + tipoSpedizione.toLowerCase() +"%");
	}
}
