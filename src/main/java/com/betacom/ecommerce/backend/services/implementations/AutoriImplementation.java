package com.betacom.ecommerce.backend.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AutoriRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoriDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autori;
import com.betacom.ecommerce.backend.repositories.IAutoriRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAutoriServices;
import com.betacom.ecommerce.backend.utilities.AutoriUtils;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoriImplementation implements IAutoriServices{

	private final IAutoriRepository autRepo;
	
	@Override
	@Transactional
	public void create(AutoriRequest req) throws MangaException {
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

		autRepo.save(AutoriUtils.buildAutore(new Autori(), req, true));
		log.debug("autore saved in db succesfully");
	}


	@Transactional
	@Override
	public void update(AutoriRequest req) throws MangaException {
		log.debug("begin update autore id {}", req.getId());
		log.debug("new autore parameters {}", req);
		
		//false flag, update
		AutoriUtils.validateRequest(req, false);
		
		//check se autore che voglio modificare esiste
		Autori aut = autRepo.findById(req.getId())
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
	public List<AutoriDTO> list() throws Exception {
		log.debug("Begin find all autori");
		
		List<Autori> lA = autRepo.findAll();
		
		return lA.stream().map(a -> AutoriUtils.buildAutDTO(a)).toList();
	}

	@Override
	public AutoriDTO findById(Integer id) throws Exception {
		log.debug("Begin find autore by id: {}", id);
		
		Autori aut = autRepo.findById(id)
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
