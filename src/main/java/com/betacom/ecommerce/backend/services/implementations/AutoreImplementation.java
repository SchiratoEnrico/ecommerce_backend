package com.betacom.ecommerce.backend.services.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.repositories.IAutoreRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAutoreServices;
import com.betacom.ecommerce.backend.specification.AutoreSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.ReqValidators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoreImplementation implements IAutoreServices{

	private final IAutoreRepository autRepo;
	private final IMangaRepository mangaRepo;
	
	@Override
	@Transactional
	public void create(AutoreRequest req) throws MangaException {
		log.debug("Begin creating autore -> {}", req);
		
		ReqValidators.validateAutoreRequest(req, true);
		log.debug("Autore validated...");
		
		//check unicità autore
	    if (checkDuplicateAutore(req.getNome(),
	    		req.getCognome(),
	    		req.getDataNascita(), 
	    		null)) {
	    	log.debug("autore already present");
	        throw new MangaException("exists_aut");   
	    }

		autRepo.save(ReqValidators.buildAutore(new Autore(), req, true));
		log.debug("autore saved in db succesfully");
	}

	@Transactional
	@Override
	public void update(AutoreRequest req) throws MangaException {
		log.debug("begin update autore id {}", req.getId());
		log.debug("new autore parameters {}", req);
		
		//false flag, update
		ReqValidators.validateAutoreRequest(req, false);
		
		//check se autore che voglio modificare esiste
		Autore aut = autRepo.findById(req.getId())
				.orElseThrow(()-> new MangaException("!exists_aut"));
		
		ReqValidators.buildAutore(aut, req, false);
		
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
	@Transactional(rollbackFor = MangaException.class)
	public void delete(Integer id) throws MangaException {

	    log.debug("begin delete autore id {}", id);

	    // controllo esistenza autore
	    Autore aut = autRepo.findById(id)
	            .orElseThrow(() -> new MangaException("!exists_aut"));

	    // controllo se è collegato a manga
	    if (mangaRepo.existsByAutoriId(id)) {
	        log.debug("autore {} is linked to manga", id);
	        throw new MangaException("linked_man");
	    }

	    autRepo.delete(aut);

	    log.debug("autore deleted successfully");
	}

	@Override
	public List<AutoreDTO> list() throws Exception {
		log.debug("Begin find all autori");
		
		List<Autore> lA = autRepo.findAll();
//		for (Autore a : lA) {
//			log.debug(a.toString());
//		}
		return lA.stream().map(a -> DtoBuilders.buildAutoreDTO(a, Optional.empty())).toList();
	}

	@Override
	public AutoreDTO findById(Integer id) throws MangaException {
		log.debug("Begin find autore by id: {}", id);
		
		Autore aut = autRepo.findById(id)
				.orElseThrow(()-> new MangaException("!exists_aut"));
		
		return DtoBuilders.buildAutoreDTO(aut, Optional.ofNullable(mangaRepo.findAllByAutoriId(aut.getId())));
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


	@Override
	public List<AutoreDTO> findByFilters(AutoreRequest req) throws MangaException {
		
		Specification<Autore> spec = AutoreSpecifications.nomeAndCognome(req.getNome(), req.getCognome());
		
		List<Autore> lA = autRepo.findAll(spec);
		
		return lA.stream().map(a -> DtoBuilders.buildAutoreDTO(a, Optional.ofNullable(mangaRepo.findAllByAutoriId(a.getId())))).toList();
	}
}
