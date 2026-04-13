package com.betacom.ecommerce.backend.specification;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.betacom.ecommerce.backend.models.Fattura;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;


public class FatturaSpecifications {
	public static Specification<Fattura> numeroFatturaLike(String numeroFattura) {
		return (root, query, cb) -> (numeroFattura == null || numeroFattura.isBlank())
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("numeroFattura")), "%" + 
            			numeroFattura.toLowerCase() + "%");
	}

	public static Specification<Fattura> clienteNomeLike(String clienteNome) {
		return (root, query, cb) -> (clienteNome == null || clienteNome.isBlank())
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("clienteNome")), 
            			"%" + clienteNome.toLowerCase() + "%");
	}

	public static Specification<Fattura> clienteCognomeLike(String clienteCognome) {
		return (root, query, cb) -> (clienteCognome == null || clienteCognome.isBlank())
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("clienteCognome")), 
            		"%" + clienteCognome.toLowerCase() + "%");
	}
	
	public static Specification<Fattura> clienteEmailLike(String clienteEmail) {
		return (root, query, cb) -> (clienteEmail == null || clienteEmail.isBlank())
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("clienteEmail")), 
            		"%" + clienteEmail.toLowerCase() + "%");
	}
	
	public static Specification<Fattura> dataEmissioneBetween(
								LocalDate from, 
								LocalDate to) 
	{
	    return (root, query, cb) -> {
	        if (from == null && to == null) {
	            return cb.conjunction();
	        } else if (from != null && to != null) {
	            return cb.between(root.get("dataEmissione"), from, to);
	        } else if (from != null) {
	            return cb.greaterThanOrEqualTo(root.get("dataEmissione"), from);
	        } else {
	            return cb.lessThanOrEqualTo(root.get("dataEmissione"), to);
	        }
	    };
	}
	
	public static Specification<Fattura> tipoPagamentoEquals(String tipoPagamento) {
		return (root, query, cb) -> {
			if (tipoPagamento == null || tipoPagamento.isBlank()) {
				return cb.conjunction();
			}
            return cb.equal(cb.lower(root.get("tipoPagamento")), tipoPagamento.toLowerCase());
		};
	}

	public static Specification<Fattura> tipoSpedizioneEquals(String tipoSpedizione) {
		return (root, query, cb) -> {
			if (tipoSpedizione == null || tipoSpedizione.isBlank()) {
				return cb.conjunction();
			}

            return cb.equal(cb.lower(root.get("tipoSpedizione")), tipoSpedizione.toLowerCase());
		};
	}

	public static Specification<Fattura> statoFatturaEquals(String statoFattura) {
		return (root, query, cb) -> (statoFattura == null || statoFattura.isBlank())
	            ? cb.conjunction()
	            : cb.equal(cb.lower(root.get("statoFattura")), statoFattura.toLowerCase());
	}
	
	public static Specification<Fattura> idOrdineEquals(Integer idOrdine) {
		return (root, query, cb) -> idOrdine == null
	            ? cb.conjunction()
	            : cb.equal(root.get("ordine").get("id"), idOrdine);
	}
	
	public static Specification<Fattura> anyMangaIsbns(List<String> isbns){
		return (root, query, cb) -> {
			if(isbns == null || isbns.isEmpty())
				return cb.conjunction();
			
			Join<Object, Object> joinRighe = root.join("righe", JoinType.INNER);
			query.distinct(true);
			return joinRighe.get("isbn").in(isbns);
		};
	}
}