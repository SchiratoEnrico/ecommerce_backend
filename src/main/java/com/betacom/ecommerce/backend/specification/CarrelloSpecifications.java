package com.betacom.ecommerce.backend.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Carrello;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class CarrelloSpecifications {
	public static Specification<Carrello> hasAnyMangaIds(List<String> isbns){
		return (root, query, cb) -> {
			if(isbns == null || isbns.isEmpty())
				return cb.conjunction();
			Join<Object, Object> joinRighe = root.join("righeCarrello", JoinType.INNER);
			Join<Object, Object> joinManga = joinRighe.join("manga", JoinType.INNER);
			query.distinct(true);
			return joinManga.get("isbn").in(isbns);
		};
	}
}
