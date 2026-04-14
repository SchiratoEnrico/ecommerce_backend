package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.RigaOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IRigaOrdineServices;
import com.betacom.ecommerce.backend.specification.RigaOrdineSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RigaOrdineImplemetation implements IRigaOrdineServices{
	private final IRigaOrdineRepository righR;
	private final IOrdineRepository ordeR;
	private final IMangaRepository mangR;

	// helper per check e update copie rimanenti
	@Transactional (rollbackFor = Exception.class)
	private void removeCopies(Manga m, Integer request) {
        Integer left = m.getNumeroCopie() - request;
        if (left < 0) {
        	throw new MangaException("insufficiente_copie");
        } else {
	        m.setNumeroCopie(left);
	        mangR.save(m);
        }
	}
	
	// devo aggiungere check a stato ordine e agire conseguentemente
	@Transactional (rollbackFor = Exception.class)
	@Override
	public Integer create(RigaOrdineRequest req) throws MangaException {
		log.debug("creating RigheOrdine {}", req);

		Integer myId = req.getIdOrdine();
		if (myId == null) {
			throw new MangaException("null_ord");
		}
		Ordine o = ordeR.findById(myId).orElseThrow(()->
						new MangaException("!exists_ord"));
		
		String myISBN = Utils.normalize(req.getManga());
		if (myISBN == null || myISBN.isBlank()) {
			throw new MangaException("null_man");
		}
		
		Optional<Manga> man = mangR.findById(myISBN);
		if (man.isEmpty()) {
			throw new MangaException("!exists_man");
		} 
		
		Manga m = man.get();
		
		Integer n = req.getNumeroCopie();
		if (n == null || n <= 0) {
			throw new MangaException("!exists_qua");
		}
		
		// controllo e rimuovo copie
		removeCopies(m, n);

		// controllo per vedere se manga già presente nello stesso ordine		
		if (o.getRigheOrdine() != null &&
			o.getRigheOrdine().stream()
		        .filter(rc -> rc.getManga().getIsbn().equals(m.getIsbn()))
		        .findFirst().isPresent()) {
			throw new MangaException("exists_ro");
		}

		RigaOrdine r = new RigaOrdine();
		r.setOrdine(o);
		r.setNumeroCopie(n);
	    r.setManga(m);
		r.setPrezzo(m.getPrezzo());
		
		Integer rid = righR.save(r).getId();
		r.setId(rid);
		List<RigaOrdine> lR = o.getRigheOrdine();

		lR.add(r);
		ordeR.save(o);
		return rid;
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void update(RigaOrdineRequest req) throws MangaException {
		log.debug("updating RigheOrdine {}", req);
		RigaOrdine r = righR.findById(req.getId()).orElseThrow(() ->
						new MangaException("!exists_ro"));
		
		Integer myId = req.getIdOrdine();
		if (myId != null) {
			Ordine o = ordeR.findById(myId).orElseThrow(()->
							new MangaException("!exists_ord"));
			r.setOrdine(o);
		}
		
		Integer n = req.getNumeroCopie();
		if (n <= 0) {
			throw new MangaException("!exists_qua");
		}
		
		String myISBN = Utils.normalize(req.getManga());
		if (myISBN != null && !myISBN.isBlank()) {
			Manga m = mangR.findById(myISBN).orElseThrow(()->
				new MangaException("!exists_man"));
			
			// 1 - check duplicati
            boolean duplicate = r.getOrdine().getRigheOrdine().stream()
                    	.filter(ro -> ro.getId() != r.getId())
                    	.anyMatch(ro -> 
                    	ro.getManga().getIsbn().equals(m.getIsbn())
                        ); // ritorna true se almeno 1
                
            if (duplicate) {
            	throw new MangaException("exists_ro");
            }
            
            // 2 - resetto copie vecchio manga
            Manga oldManga = r.getManga();
            removeCopies(oldManga, - r.getNumeroCopie());

            // 3 - check se n copie richiesto 
            Integer copies = n == null? r.getNumeroCopie() : n;
            removeCopies(m, copies);
            // setto manga
			r.setManga(m);		
            
		}
		
		if ((n != null) && (myISBN == null || myISBN.isBlank())) {			
			// aggiorno con delta inventario
		    Manga m = r.getManga();
		    Integer delta = n - r.getNumeroCopie();
		    
		    removeCopies(m, delta);
		    r.setNumeroCopie(n);
		}
		
		righR.save(r);
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void delete(Integer id) throws MangaException {
		log.debug("removing RigheOrdine with id {}", id);
		RigaOrdine r = righR.findById(id).orElseThrow(() ->
						new MangaException("!exists_row"));
		
		Ordine o = r.getOrdine();
		o.getRigheOrdine().remove(r);
		
		Manga m = r.getManga();
		Integer copie = r.getNumeroCopie();
		removeCopies(m, -copie);

		righR.delete(r);
		ordeR.save(o);
	}

	@Override
	public List<RigaOrdineDTO> list(Integer idOrdine) throws MangaException {
	    Specification<RigaOrdine> spec = Specification
	            .where(RigaOrdineSpecifications.idOrdineEquals(idOrdine));
	    List<RigaOrdine> lR = righR.findAll(spec);
	    return lR.stream()
	            .map(r -> DtoBuilders.buildRigaOrdineDTO(r, Optional.empty()))
	            .toList();
	}

	@Override
	public RigaOrdineDTO findById(Integer id) throws MangaException {
		RigaOrdine r = righR.findById(id).orElseThrow(()->
							new MangaException("!exists_row"));
		return DtoBuilders.buildRigaOrdineDTO(r, Optional.ofNullable(r.getManga()));
	}
	
	public Boolean isRigaOrdineOwnedByAccount(Integer rigaId, Integer accountId) {
	    var rigaOpt = righR.findById(rigaId);
	    if (rigaOpt.isEmpty() || rigaOpt.get().getOrdine() == null || rigaOpt.get().getOrdine().getAccount() == null) {
	        return false;
	    }
	    return rigaOpt.get().getOrdine().getAccount().getId().equals(accountId);
	}
}