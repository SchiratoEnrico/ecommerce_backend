package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.StatoOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IStatoOrdineServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatoOrdineImplemetation implements IStatoOrdineServices{

	private final IStatoOrdineRepository statR;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public Integer create(StatoOrdineRequest req) throws MangaException {
		log.debug("creating stato ordine {}", req);
		String myStato = null;
		
		StatoOrdine o = new StatoOrdine();
		if (req.getStatoOrdine() != null && !req.getStatoOrdine().isEmpty()) {
			myStato = req.getStatoOrdine().trim().toUpperCase();
		} else {
			throw new MangaException("null_sta");
		}
		Optional<StatoOrdine> dup = statR.findByStatoOrdine(myStato);
		if (dup.isEmpty()) {
			o.setStatoOrdine(myStato); 
		} else {
			throw new MangaException("exists_sta");
		}
		return statR.save(o).getId();
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("removing stato ordine with id {}", id);

		StatoOrdine stato = statR.findById(id).orElseThrow(() ->
				new MangaException("!exists_sta"));
		statR.delete(stato);
	}


	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(StatoOrdineRequest req) throws MangaException {
		log.debug("removing stato ordine with id {}", req.getId());

		StatoOrdine stato = statR.findById(req.getId()).orElseThrow(() ->
			new MangaException("!exists_sta"));
		String myStato = null;
		
		if (req.getStatoOrdine() != null && !req.getStatoOrdine().isEmpty()) {
			myStato = req.getStatoOrdine().trim().toUpperCase();
		} else {
			throw new MangaException("null_sta");
		}
		Optional<StatoOrdine> dup = statR.findByStatoOrdine(myStato);
		if (dup.isEmpty()) {
			stato.setStatoOrdine(myStato); 
		} else {
			throw new MangaException("exists_sta");
		}
		statR.save(stato);		
	}

	@Override
	public List<StatoOrdineDTO> list() {
		List<StatoOrdine> lS = statR.findAll();
		return lS.stream()
				.map(s -> DtoBuilders.buildStatoOrdineDTO(s))
				.collect(Collectors.toList());
	}

	@Override
	public StatoOrdineDTO findById(Integer id) throws MangaException {
		StatoOrdine o = statR.findById(id).orElseThrow(() ->
				new MangaException("!exists_sta"));
		return DtoBuilders.buildStatoOrdineDTO(o);
	}
}
