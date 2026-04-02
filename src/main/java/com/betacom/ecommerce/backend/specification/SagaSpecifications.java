package com.betacom.ecommerce.backend.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Saga;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class SagaSpecifications {

	public static Specification<Saga> distinct() {
	    return (root, query, cb) -> {
	        query.distinct(true);
	        return cb.conjunction();
	    };
	}
		
	// casaEditrice nome
	public static Specification<Saga> casaEditriceNomeLike(String casaEditriceNome) {
		//      tabella, query, CriteriaBuilder
		return (root, query, cb) -> {
			if (casaEditriceNome == null || casaEditriceNome.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> mangaJoin= root.join("manga", JoinType.LEFT);
			Join<Object, Object> caseJoin= mangaJoin.join("casaEditrice", JoinType.INNER);
			return cb.like(
					cb.lower(caseJoin.get("nome")),
					"%" + casaEditriceNome.toLowerCase() + "%"
					);
			};		
	}
	
	// casaEditrice id
	public static Specification<Saga> casaEditriceIdEquals(Integer casaEditriceId) {
	    return (root, query, cb) -> {
	        if (casaEditriceId == null) {
	            return cb.conjunction();
	        }
			Join<Object, Object> mangaJoin= root.join("manga", JoinType.LEFT);
			Join<Object, Object> caseJoin= mangaJoin.join("casaEditrice", JoinType.INNER);

	        return cb.equal(caseJoin.get("id"), casaEditriceId);
	    };
	}

	// autore
	// autore id (es many to many)
	public static Specification<Saga> autoreIdEquals(Integer autoreId) {
	    return (root, query, cb) -> {
	        if (autoreId == null) {
	            return cb.conjunction();
	        }
			Join<Object, Object> mangaJoin= root.join("manga", JoinType.LEFT);
	        Join<Object, Object> autoriJoin = mangaJoin.join("autori", JoinType.LEFT);

	        return cb.equal(autoriJoin.get("id"), autoreId);
	    };
	}
	
	// autore nome
	public static Specification<Saga> autoreNomeLike(String autoreNome) {
		//      tabella, query, CriteriaBuilder
		return (root, query, cb) -> {
			if (autoreNome == null || autoreNome.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> mangaJoin= root.join("manga", JoinType.LEFT);
			Join<Object, Object> autoriJoin= mangaJoin.join("autori", JoinType.LEFT);
			return cb.like(
					cb.lower(autoriJoin.get("nome")),
					"%" + autoreNome.toLowerCase() + "%"
					);
			};		
	}

	// autore cognome
	public static Specification<Saga> autoreCognomeLike(String autoreCognome) {
		//      tabella, query, CriteriaBuilder
		return (root, query, cb) -> {
			if (autoreCognome == null || autoreCognome.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> mangaJoin= root.join("manga", JoinType.LEFT);
			Join<Object, Object> autoriJoin= mangaJoin.join("autori", JoinType.LEFT);
			return cb.like(
					cb.lower(autoriJoin.get("nome")),
					"%" + autoreCognome.toLowerCase() + "%"
					);
			};		
	}

	// genere
	// genere id (es many to many)
	public static Specification<Saga> generiIdEqual(List<Integer> generiId) {
	    return (root, query, cb) -> {
	        if (generiId == null || generiId.isEmpty()) {
	            return cb.conjunction();
	        }

			Join<Object, Object> mangaJoin= root.join("manga", JoinType.LEFT);
	        Join<Object, Object> generiJoin = mangaJoin.join("generi", JoinType.LEFT);
	        
	        query.distinct(true);
			return generiJoin.get("id").in(generiId);
	    };
	}

	// saga
	// saga id
	public static Specification<Saga> sagaIdEquals(Integer sagaId) {
	    return (root, query, cb) -> {
	        if (sagaId == null) {
	            return cb.conjunction();
	        }

	        return cb.equal(root.get("id"), sagaId);
	    };
	}
	
	// saga nome
	public static Specification<Saga> sagaNomeLike(String sagaNome) {
		//      tabella, query, CriteriaBuilder
		return (root, query, cb) -> {
			if (sagaNome == null || sagaNome.isBlank()) {
				return cb.conjunction();
			}
			return cb.like(
					cb.lower(root.get("nome")),
					"%" + sagaNome.toLowerCase() + "%"
					);
			};		
	}
}
