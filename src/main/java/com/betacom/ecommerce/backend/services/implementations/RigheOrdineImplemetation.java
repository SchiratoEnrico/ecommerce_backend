package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.RigheOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigheOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Ordini;
import com.betacom.ecommerce.backend.models.RigheOrdine;
import com.betacom.ecommerce.backend.repositories.IOrdiniRepository;
import com.betacom.ecommerce.backend.repositories.IRigheOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IStatiOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IRigheOrdineServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RigheOrdineImplemetation implements IRigheOrdineServices{
	private final IRigheOrdineRepository righR;
	private final IOrdiniRepository ordeR;
	private final IMangaRepository mangR;

	@Transactional (rollbackFor = Exception.class)
	@Override
	public Integer create(RigheOrdineRequest req) throws MangaException {
		log.debug("creating RigheOrdine {}", req);

		Integer myId = req.getIdOrdine();
		if (myId == null) {
			throw new MangaException("null_ord");
		}
		Ordini o = ordeR.findById(myId).orElseThrow(()->
						new MangaException("!exists_ord"));
		
		String myISBN = Utils.formatStringParam(req.getManga());
		Manga m = null;
		if (myISBN != null && !myISBN.isBlank()) {
			m = mangR.findById(myISBN).orElseThrow(()->
				new MangaException("!exists_man"));
		} else {
			throw new MangaException("null_man");
		}
		
		RigheOrdine r = new RigheOrdine();
		r.setIdOrdine(o.getId());
		r.setManga(m);
		r.setNumeroCopie(req.getNumeroCopie());
		
		return righR.save(r).getId();
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void update(RigheOrdineRequest req) throws MangaException {
		log.debug("updating RigheOrdine {}", req);
		RigheOrdine r = righR.findById(req.getId()).orElseThrow(() ->
						new MangaException("!exists_row"));
		
		Integer myId = req.getIdOrdine();
		if (myId != null) {
			Ordini o = ordeR.findById(myId).orElseThrow(()->
							new MangaException("!exists_ord"));
			r.setIdOrdine(myId);
		}
		
		String myISBN = Utils.formatStringParam(req.getManga());
		Manga m = null;
		if (myISBN != null && !myISBN.isBlank()) {
			m = mangR.findById(myISBN).orElseThrow(()->
				new MangaException("!exists_man"));
			r.setManga(m);
		}
		righR.save(r);
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void delete(Integer id) throws MangaException {
		log.debug("removing RigheOrdine with id {}", id);
		RigheOrdine r = righR.findById(id).orElseThrow(() ->
						new MangaException("!exists_row"));
		righR.delete(r);
	}

	@Override
	public List<RigheOrdineDTO> list() {
		List<RigheOrdine> lR = righR.findAll();
		return lR.stream()
				.map(r -> DtoBuildres.buildRigheOrdineDTO(r, true))
				.collect(Collectors.toList());
	}

	@Override
	public RigheOrdineDTO findById(Integer id) throws MangaException {
		RigheOrdine r = righR.findById(id).orElseThrow(()->
							new MangaException("!exists_man"));
		return DtoBuildres.buildRigheOrdineDTO(r, true);
	}
}