package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.StatiOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatiOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.StatiOrdine;
import com.betacom.ecommerce.backend.repositories.IStatiOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IStatiOrdineServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatiOrdineImplemetation implements IStatiOrdineServices{

	private final IStatiOrdineRepository statR;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public Integer create(StatiOrdineRequest req) throws MangaException {
		log.debug("creating stato ordine {}", req);
		String myStato = null;
		
		StatiOrdine o = new StatiOrdine();
		if (req.getStato() != null && !req.getStato().isEmpty()) {
			myStato = req.getStato().trim().toUpperCase();
		} else {
			throw new MangaException("null_sta");
		}
		Optional<StatiOrdine> dup = statR.findByStato(myStato);
		if (dup.isEmpty()) {
			o.setStato(myStato); 
		} else {
			throw new MangaException("exists_sta");
		}
		return statR.save(o).getId();
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("removing stato ordine with id {}", id);

		StatiOrdine stato = statR.findById(id).orElseThrow(() ->
				new MangaException("!exists_sta"));
		statR.delete(stato);
	}


	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(StatiOrdineRequest req) throws MangaException {
		log.debug("removing stato ordine with id {}", req.getId());

		StatiOrdine stato = statR.findById(req.getId()).orElseThrow(() ->
			new MangaException("!exists_sta"));
		String myStato = null;
		
		if (req.getStato() != null && !req.getStato().isEmpty()) {
			myStato = req.getStato().trim().toUpperCase();
		} else {
			throw new MangaException("null_sta");
		}
		Optional<StatiOrdine> dup = statR.findByStato(myStato);
		if (dup.isEmpty()) {
			stato.setStato(myStato); 
		} else {
			throw new MangaException("exists_sta");
		}
		statR.save(stato);		
	}

	@Override
	public List<StatiOrdineDTO> list() {
		List<StatiOrdine> lS = statR.findAll();
		return lS.stream()
				.map(s -> DtoBuildres.buildStatiOrdineDTO(s, true))
				.collect(Collectors.toList());
	}

	@Override
	public StatiOrdineDTO findById(Integer id) throws MangaException {
		StatiOrdine o = statR.findById(id).orElseThrow(() ->
				new MangaException("!exists_sta"));
		return DtoBuildres.buildStatiOrdineDTO(o, true);
	}
}
