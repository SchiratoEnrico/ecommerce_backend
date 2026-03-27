package com.betacom.ecommerce.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.RigaCarrello;

public class RigaCarrelloSpecifications {
	public static Specification<RigaCarrello> chartIdLike(Integer id){
		return (root, _, cb) -> (id==null)
				? cb.conjunction()
				: cb.equal(root.get("carrello").get("id"), id);
				
	}
	
	public static Specification<RigaCarrello> mangaLike(String isbn){
		return (root, _, cb) -> (isbn==null || isbn.isBlank())
				? cb.conjunction()
				: cb.like(cb.lower(root.get("manga").get("isbn")), "%"+isbn.toLowerCase()+"%");
	}
	
	public static Specification<RigaCarrello> hasAtLeast(Integer nCopie){
		return (root, _, cb) -> (nCopie==null)
				? cb.conjunction()
				: cb.equal(root.get("numeroCopie"), nCopie);
	}
}
