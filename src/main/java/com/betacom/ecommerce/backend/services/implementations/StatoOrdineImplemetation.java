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
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IStatoOrdineServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatoOrdineImplemetation implements IStatoOrdineServices{

	private final IStatoOrdineRepository statR;
	private final IOrdineRepository ordeR;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public Integer create(StatoOrdineRequest req) throws MangaException {
		log.debug("creating stato ordine {}", req);
		
		StatoOrdine o = new StatoOrdine();
		String myStato = Utils.normalize(req.getStatoOrdine());
		if (myStato == null || myStato.isEmpty()) {
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
		
		if (ordeR.existsByStatoId(stato.getId())) {
			throw new MangaException("order_sta");
		}
		statR.delete(stato);
	}


	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(StatoOrdineRequest req) throws MangaException {
		log.debug("removing stato ordine with id {}", req.getId());

		StatoOrdine stato = statR.findById(req.getId()).orElseThrow(() ->
			new MangaException("!exists_sta"));
		String myStato = Utils.normalize(req.getStatoOrdine());
		
		if (myStato == null || myStato.isEmpty()) {
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
