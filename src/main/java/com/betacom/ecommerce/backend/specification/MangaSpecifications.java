package com.betacom.ecommerce.backend.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Manga;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class MangaSpecifications {
//	public static Specification<Manga> isbnLike(String isbn) {
//	    return (root, query, cb) -> (isbn == null || isbn.isBlank())
//	            ? cb.conjunction()
//	            : cb.like(cb.lower(root.get("isbn")), "%" + isbn.toLowerCase() + "%");
//	}
	
	public static Specification<Manga> consigliatiSpec(List<Integer> generiIds, List<String> isbnComprati) {
	    return (root, query, cb) -> {
	        var generiJoin = root.join("generi");
	        var inGeneri = generiJoin.get("id").in(generiIds);
	        
	        if (isbnComprati != null && !isbnComprati.isEmpty()) {
	            var notInIsbn = cb.not(root.get("isbn").in(isbnComprati));
	            return cb.and(inGeneri, notInIsbn);
	        }
	        return inGeneri;
	    };
	}

	public static Specification<Manga> distinct() {
	    return (root, query, cb) -> {
	        query.distinct(true);
	        return cb.conjunction();
	    }; 
	}
	
	public static Specification<Manga> titoloLike(String titolo) {
	    return (root, query, cb) -> (titolo == null || titolo.isBlank())
	            ? cb.conjunction()
	            : cb.like(cb.lower(root.get("titolo")), "%" + titolo.toLowerCase() + "%");
	}
	
	// casaEditrice nome
	public static Specification<Manga> casaEditriceNomeLike(String casaEditriceNome) {
		//      tabella, query, CriteriaBuilder
		return (root, query, cb) -> {
			if (casaEditriceNome == null || casaEditriceNome.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> caseJoin= root.join("casaEditrice", JoinType.INNER);
			return cb.like(
					cb.lower(caseJoin.get("nome")),
					"%" + casaEditriceNome.toLowerCase() + "%"
					);
			};		
	}
	
	// casaEditrice id
	public static Specification<Manga> casaEditriceIdEquals(Integer casaEditriceId) {
	    return (root, query, cb) -> {
	        if (casaEditriceId == null) {
	            return cb.conjunction();
	        }

			Join<Object, Object> caseJoin= root.join("casaEditrice", JoinType.INNER);

	        return cb.equal(caseJoin.get("id"), casaEditriceId);
	    };
	}

	// autore
	// autore id (es many to many)
	public static Specification<Manga> autoreIdEquals(Integer autoreId) {
	    return (root, query, cb) -> {
	        if (autoreId == null) {
	            return cb.conjunction();
	        }

	        Join<Object, Object> autoriJoin = root.join("autori", JoinType.LEFT);

	        return cb.equal(autoriJoin.get("id"), autoreId);
	    };
	}
	
	// autore nome
	public static Specification<Manga> autoreNomeLike(String autoreNome) {
		//      tabella, query, CriteriaBuilder
		return (root, query, cb) -> {
			if (autoreNome == null || autoreNome.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> autoriJoin= root.join("autori", JoinType.LEFT);
			return cb.like(
					cb.lower(autoriJoin.get("nome")),
					"%" + autoreNome.toLowerCase() + "%"
					);
			};		
	}

	// genere
	// genere id (es many to many)
	public static Specification<Manga> generiIdEqual(List<Integer> generiId) {
	    return (root, query, cb) -> {
	        if (generiId == null || generiId.isEmpty()) {
	            return cb.conjunction();
	        }

	        Join<Object, Object> generiJoin = root.join("generi", JoinType.LEFT);
	        query.distinct(true);
			return generiJoin.get("id").in(generiId);
	    };
	}

	// saga
	// saga id
	public static Specification<Manga> sagaIdEquals(Integer sagaId) {
	    return (root, query, cb) -> {
	        if (sagaId == null) {
	            return cb.conjunction();
	        }

	        Join<Object, Object> sagheJoin = root.join("saga", JoinType.LEFT);

	        return cb.equal(sagheJoin.get("id"), sagaId);
	    };
	}
	
	// saga nome
	public static Specification<Manga> sagaNomeLike(String sagaNome) {
		//      tabella, query, CriteriaBuilder
		return (root, query, cb) -> {
			if (sagaNome == null || sagaNome.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> sagheJoin= root.join("saga", JoinType.LEFT);
			return cb.like(
					cb.lower(sagheJoin.get("nome")),
					"%" + sagaNome.toLowerCase() + "%"
					);
			};		
	}
}
