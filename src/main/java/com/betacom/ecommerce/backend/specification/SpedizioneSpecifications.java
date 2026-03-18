package com.betacom.ecommerce.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.TipoSpedizione;

public class SpedizioneSpecifications {
	public static Specification<TipoSpedizione> tipoSpedizioneLike(String tipoSpedizione){
		return (root, query, cb) -> (tipoSpedizione==null || tipoSpedizione.isBlank())
				? cb.conjunction()
				: cb.like(cb.lower(root.get("tipoSpedizione")), "%" + tipoSpedizione.toLowerCase() +"%");
	}
}
