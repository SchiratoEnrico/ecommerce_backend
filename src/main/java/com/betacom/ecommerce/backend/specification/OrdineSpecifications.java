package com.betacom.ecommerce.backend.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Ordine;

@Slf4j
public class OrdineSpecifications {
	
	//Account Username
	public static Specification<Ordine> accountUsernameLike(String username) {
		return (root, query, cb) -> {
			if (username == null || username.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> caseJoin= root.join("account", JoinType.INNER);
			return cb.like(
					cb.lower(caseJoin.get("username")),
					"%" + username.toLowerCase() + "%"
					);
			};		
	}

	public static Specification<Ordine> tipoPagamentoLike(String tipoPagamento) {
		return (root, query, cb) -> {
			if (tipoPagamento == null || tipoPagamento.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> caseJoin= root.join("tipoPagamento", JoinType.INNER);
			return cb.like(
					cb.lower(caseJoin.get("tipoPagamento")),
					"%" + tipoPagamento.toLowerCase() + "%"
					);
			};		
	}
	public static Specification<Ordine> tipoSpedizioneLike(String tipoSpedizione) {
		return (root, query, cb) -> {
			if (tipoSpedizione == null || tipoSpedizione.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> caseJoin= root.join("tipoSpedizione", JoinType.INNER);
			return cb.like(
					cb.lower(caseJoin.get("tipoSpedizione")),
					"%" + tipoSpedizione.toLowerCase() + "%"
					);
			};		
	}
	
	public static Specification<Ordine> statoOrdineLike(String statoOrdine) {
		return (root, query, cb) -> {
			if (statoOrdine == null || statoOrdine.isBlank()) {
				return cb.conjunction();
			}
			Join<Object, Object> caseJoin= root.join("stato", JoinType.INNER);
			return cb.like(
					cb.lower(caseJoin.get("statoOrdine")),
					"%" + statoOrdine.toLowerCase() + "%"
					);
			};		
	}
	
	public static Specification<Ordine> hasAnyMangaIds(List<String> isbns){
		return (root, query, cb) -> {
			if(isbns == null || isbns.isEmpty())
				return cb.conjunction();
			Join<Object, Object> joinRighe = root.join("righeOrdine", JoinType.INNER);
			Join<Object, Object> joinManga = joinRighe.join("manga", JoinType.INNER);
			query.distinct(true);
			return joinManga.get("isbn").in(isbns);
		};
	}
	public static Specification<Ordine> meseAnnoEquals(Integer giorno, Integer mese, Integer anno) {
	    return (root, query, cb) -> {
	        if (anno == null) return cb.conjunction();

	        if (mese == null) {
	            // Solo anno
	            LocalDate inizio = LocalDate.of(anno, 1, 1);
	            LocalDate fine = LocalDate.of(anno, 12, 31);
	            return cb.between(root.get("data"), inizio, fine);
	        } else if (giorno == null) {
	            // Anno + mese
	            LocalDate inizio = LocalDate.of(anno, mese, 1);
	            LocalDate fine = inizio.withDayOfMonth(inizio.lengthOfMonth());
	            return cb.between(root.get("data"), inizio, fine);
	        } else {
	            // Anno + mese + giorno 
	            LocalDate data = LocalDate.of(anno, mese, giorno);
	            return cb.equal(root.get("data"), data);
	        }
	    };
	}
	
		

}
