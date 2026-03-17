package com.betacom.ecommerce.backend.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Carrelli;

import jakarta.persistence.criteria.JoinType;

public class CarrelliSpecifications {
	public static Specification<Carrelli> hasAnyMangaIds(List<String> mangaIds){
		return (root, query, cb) -> {
			if(mangaIds==null || mangaIds.isEmpty())
				return cb.conjunction();
			var joinManga = root.join("manga", JoinType.INNER);
			
			query.distinct(true);
			
			return joinManga.get("isbn").in(mangaIds);
		};
	}
}
