package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.stream.Collectors;

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
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IRigaOrdineServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;
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
		r.setIdOrdine(o.getId());
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
			r.setIdOrdine(myId);
		}
		
		String myISBN = Utils.normalize(req.getManga());
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
		RigaOrdine r = righR.findById(id).orElseThrow(() ->
						new MangaException("!exists_row"));
		righR.delete(r);
	}

	@Override
	public List<RigaOrdineDTO> list() {
		List<RigaOrdine> lR = righR.findAll();
		return lR.stream()
				.map(r -> DtoBuildres.buildRigaOrdineDTO(r, true))
				.collect(Collectors.toList());
	}

	@Override
	public RigaOrdineDTO findById(Integer id) throws MangaException {
		RigaOrdine r = righR.findById(id).orElseThrow(()->
							new MangaException("!exists_man"));
		return DtoBuildres.buildRigaOrdineDTO(r, true);
	}
}