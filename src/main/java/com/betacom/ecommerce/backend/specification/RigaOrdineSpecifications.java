package com.betacom.ecommerce.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.RigaOrdine;

public class RigaOrdineSpecifications {

	public static Specification<RigaOrdine> idOrdineEquals(Integer idOrdine) {
	    return (root, query, cb) -> {
	        if (idOrdine == null) return cb.conjunction();
	        return cb.equal(root.get("ordine").get("id"), idOrdine);
	    };
	}
}
