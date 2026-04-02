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
		Manga m = null;
		if (myISBN != null && !myISBN.isBlank()) {
			m = mangR.findById(myISBN).orElseThrow(()->
				new MangaException("!exists_man"));
		} else {
			throw new MangaException("null_man");
		}
		
		RigaOrdine r = new RigaOrdine();
		r.setOrdine(o);
		r.setManga(m);
		r.setNumeroCopie(req.getNumeroCopie());
		
		return righR.save(r).getId();
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void update(RigaOrdineRequest req) throws MangaException {
		log.debug("updating RigheOrdine {}", req);
		RigaOrdine r = righR.findById(req.getId()).orElseThrow(() ->
						new MangaException("!exists_row"));
		
		Integer myId = req.getIdOrdine();
		if (myId != null) {
			Ordine o = ordeR.findById(myId).orElseThrow(()->
							new MangaException("!exists_ord"));
			r.setOrdine(o);
		}
		
		String myISBN = Utils.normalize(req.getManga());
		Manga m = null;
		if (myISBN != null && !myISBN.isBlank()) {
			m = mangR.findById(myISBN).orElseThrow(()->
				new MangaException("!exists_man"));
			r.setManga(m);
		}
		
		if (req.getNumeroCopie() != null) {
		    // update inventory delta: difference between old and new
		    Integer toAdd = r.getNumeroCopie() - req.getNumeroCopie();
		    m = r.getManga();
		    Integer left = m.getNumeroCopie() - toAdd; // negative delta = more copies locked
		    if (left < 0) throw new MangaException("insufficiente_copie");
		    m.setNumeroCopie(left);
		    mangR.save(m);
		    r.setNumeroCopie(req.getNumeroCopie());
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