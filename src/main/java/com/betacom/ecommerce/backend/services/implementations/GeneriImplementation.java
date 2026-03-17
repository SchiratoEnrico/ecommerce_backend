package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.GeneriRequest;
import com.betacom.ecommerce.backend.dto.outputs.GeneriDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Generi;
import com.betacom.ecommerce.backend.repositories.IGeneriRepository;
import com.betacom.ecommerce.backend.services.interfaces.IGeneriServices;
import com.betacom.ecommerce.backend.utilities.GeneriUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeneriImplementation implements IGeneriServices{

	private final IGeneriRepository genRepo;
	
	@Override
	@Transactional
	public void create(GeneriRequest req) throws MangaException {
		log.debug("Begin creating genere {}", req);
		
		GeneriUtils.validateRequest(req, true);
		log.debug("Genere validated...");
		
		//check unicità autore
	    if (checkDuplicateGenere(req.getDescrizione(), null)) {
	    	log.debug("genere already present");
	        throw new MangaException("exists_gen");   
	    }

		genRepo.save(GeneriUtils.buildGenere(new Generi(), req, true));
		log.debug("genere saved in db succesfully");		
	}

	@Override
	@Transactional
	public void update(GeneriRequest req) throws MangaException {
		log.debug("Begin update genere id {}", req.getId());
		log.debug(" new genere parameters {}", req);
		
		//update mode
		GeneriUtils.validateRequest(req, false);
		
		//check se genere che voglio modificare esiste
		Generi gen = genRepo.findById(req.getId())
				.orElseThrow(()-> new MangaException("!exists_gen"));
		
		GeneriUtils.buildGenere(gen, req, false);
		
		log.debug("builded genere");
		
		//check per vedere che il genere aggiornato non sia duplicato
		if(checkDuplicateGenere(gen.getDescrizione(), gen.getId()))
			throw new MangaException("exists_gen");
		
		genRepo.save(gen);
		
		log.debug("genere saved in db succesfully");
	}

	@Override
	public void delete(Integer id) throws MangaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<GeneriDTO> list() throws MangaException {
		log.debug("Begin find all generi");
		
		List<Generi> lG = genRepo.findAll();
		
		return lG.stream().map(g -> GeneriUtils.buildGenDTO(g)).toList();
	}

	@Override
	public GeneriDTO findById(Integer id) throws MangaException {
		log.debug("Begin find genere by id: {}", id);
		
		Generi gen = genRepo.findById(id)
				.orElseThrow(()-> new MangaException("!exists_gen"));
		
		return GeneriUtils.buildGenDTO(gen);
	}

	private Boolean checkDuplicateGenere(String descrizione, Integer id) throws MangaException{
		log.debug("checking duplicate genere");
		
		return genRepo.existsByDescrizioneAndIdNot(descrizione, id);
	}
}
