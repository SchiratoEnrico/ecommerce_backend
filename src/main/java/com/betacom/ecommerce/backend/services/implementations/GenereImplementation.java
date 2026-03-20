package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.GenereRequest;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.repositories.IGenereRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IGenereServices;
import com.betacom.ecommerce.backend.utilities.GeneriUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenereImplementation implements IGenereServices{

	private final IGenereRepository genRepo;
	private final IMangaRepository mangaRepo;
	
	@Override
	@Transactional
	public void create(GenereRequest req) throws MangaException {
		log.debug("Begin creating genere {}", req);
		
		GeneriUtils.validateRequest(req, true);
		log.debug("Genere validated...");
		log.debug(req.getDescrizione());
		//check unicità genere
	    if (checkDuplicateGenere(req.getDescrizione(), null)) {
	    	log.debug("genere already present");
	        throw new MangaException("exists_gen");   
	    }

		genRepo.save(GeneriUtils.buildGenere(new Genere(), req, true));
		log.debug("genere saved in db succesfully");		
	}

	@Override
	@Transactional
	public void update(GenereRequest req) throws MangaException {
		log.debug("Begin update genere id {}", req.getId());
		log.debug(" new genere parameters {}", req);
		
		//update mode
		GeneriUtils.validateRequest(req, false);
		
		//check se genere che voglio modificare esiste
		Genere gen = genRepo.findById(req.getId())
				.orElseThrow(()-> new MangaException("!exists_gen"));
		
		//check per vedere che il genere aggiornato non sia duplicato
		if(checkDuplicateGenere(req.getDescrizione(), req.getId()))
			throw new MangaException("exists_gen");
		
		GeneriUtils.buildGenere(gen, req, false);
		
		log.debug("builded genere");
		
		
		
		genRepo.save(gen);
		
		log.debug("genere saved in db succesfully");
	}

	@Override
	@Transactional(rollbackFor = MangaException.class)
	public void delete(Integer id) throws MangaException {

	    log.debug("begin delete genere id {}", id);

	    // controllo esistenza autore
	    Genere aut = genRepo.findById(id)
	            .orElseThrow(() -> new MangaException("!exists_gen"));

	    // controllo se è collegato a manga
	    if (mangaRepo.existsByAutoriId(id)) {
	        log.debug("genere {} is linked to manga", id);
	        throw new MangaException("linked_man");
	    }

	    genRepo.delete(aut);

	    log.debug("genere deleted successfully");
	}

	@Override
	public List<GenereDTO> list() throws MangaException {
		log.debug("Begin find all generi");
		
		List<Genere> lG = genRepo.findAll();
		
		return lG.stream().map(g -> GeneriUtils.buildGenDTO(g)).toList();
	}

	@Override
	public GenereDTO findById(Integer id) throws MangaException {
		log.debug("Begin find genere by id: {}", id);
		
		Genere gen = genRepo.findById(id)
				.orElseThrow(()-> new MangaException("!exists_gen"));
		
		return GeneriUtils.buildGenDTO(gen);
	}

	private Boolean checkDuplicateGenere(String descrizione, Integer id) throws MangaException{
		log.debug("checking duplicate genere");
		
		return genRepo.existsByDescrizioneAndIdNot(descrizione, id);
	}
}
