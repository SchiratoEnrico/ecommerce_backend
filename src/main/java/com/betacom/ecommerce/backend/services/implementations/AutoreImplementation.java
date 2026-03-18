package com.betacom.ecommerce.backend.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.repositories.IAutoreRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAutoreServices;
import com.betacom.ecommerce.backend.utilities.AutoriUtils;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoreImplementation implements IAutoreServices{

	private final IAutoreRepository autRepo;
	
	@Override
	@Transactional
	public void create(AutoreRequest req) throws MangaException {
		log.debug("Begin creating autore -> {}", req);
		
		AutoriUtils.validateRequest(req, true);
		log.debug("Autore validated...");
		
		//check unicità autore
	    if (checkDuplicateAutore(req.getNome(),
	    		req.getCognome(),
	    		Utils.stringToDate(req.getDataNascita()),
	    		null)) {
	    	log.debug("autore already present");
	        throw new MangaException("exists_aut");   
	    }

		autRepo.save(AutoriUtils.buildAutore(new Autore(), req, true));
		log.debug("autore saved in db succesfully");
	}


	@Transactional
	@Override
	public void update(AutoreRequest req) throws MangaException {
		log.debug("begin update autore id {}", req.getId());
		log.debug("new autore parameters {}", req);
		
		//false flag, update
		AutoriUtils.validateRequest(req, false);
		
		//check se autore che voglio modificare esiste
		Autore aut = autRepo.findById(req.getId())
				.orElseThrow(()-> new MangaException("!exists_aut"));
		
		AutoriUtils.buildAutore(aut, req, false);
		
		log.debug("builded autore");
		
		//check se l'autore con i campi aggioranti non sia duplicato
		if(checkDuplicateAutore(aut.getNome(),
				aut.getCognome(),
				aut.getDataNascita(),
				aut.getId()))
			throw new MangaException("exists_aut");
		
		autRepo.save(aut);
		
		log.debug("autore saved in db succesfully");
	}

	@Override
	public void delete(Integer id) throws MangaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AutoreDTO> list() throws Exception {
		log.debug("Begin find all autori");
		
		List<Autore> lA = autRepo.findAll();
		
		return lA.stream().map(a -> AutoriUtils.buildAutDTO(a)).toList();
	}

	@Override
	public AutoreDTO findById(Integer id) throws Exception {
		log.debug("Begin find autore by id: {}", id);
		
		Autore aut = autRepo.findById(id)
				.orElseThrow(()-> new MangaException("!exists_aut"));
		
		return AutoriUtils.buildAutDTO(aut);
	}

	
	private Boolean checkDuplicateAutore(String nome, String cognome, LocalDate dataNascita, Integer id) throws MangaException{
		log.debug("checking duplicate autore");
		return autRepo.existsByNomeAndCognomeAndDataNascitaAndIdNot(
	            nome,
	            cognome,
	            dataNascita,
	            id
	    );
	}
}
