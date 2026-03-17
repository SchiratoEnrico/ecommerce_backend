package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AnagraficheRequest;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficheDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Anagrafiche;
import com.betacom.ecommerce.backend.repositories.IAnagraficheRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAnagraficheServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnagraficheImplementation implements IAnagraficheServices{

	private final IAnagraficheRepository repAna;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(AnagraficheRequest req) throws MangaException {
		log.debug("Create Account", req);
		
		if(req.getNome()==null || req.getNome().isBlank())
			throw new MangaException("null_nom");
		
		if(req.getCognome()==null || req.getCognome().isBlank())
			throw new MangaException("null_cog");
			
		if(req.getStato()==null || req.getStato().isBlank())
			throw new MangaException("null_sta");
		
		if(req.getCitta()==null || req.getCitta().isBlank())
			throw new MangaException("null_cit");
		
		if(req.getProvincia()==null || req.getProvincia().isBlank())
			throw new MangaException("null_pro");

		if(req.getCap()==null || req.getCap().isBlank())
			throw new MangaException("null_cap");
		
		if(req.getVia()==null || req.getVia().isBlank())
			throw new MangaException("null_via");
		
		if(req.getPredefinito()==null)
			throw new MangaException("null_pre");
		
	
		Anagrafiche an = new Anagrafiche();
	    an.setNome(req.getNome().trim().toUpperCase());
	    an.setCognome(req.getCognome().trim().toUpperCase());
	    an.setStato(req.getStato().trim().toUpperCase());
	    an.setCitta(req.getCitta().trim().toUpperCase());
	    an.setProvincia(req.getProvincia().trim().toUpperCase());
	    an.setCap(req.getCap().trim());
	    an.setVia(req.getVia().trim().toUpperCase());
	    an.setPredefinito(false);

	    repAna.save(an);
	
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
        
		Anagrafiche ana = repAna.findById(id)
				.orElseThrow(() -> new MangaException("null_ana"));
		repAna.delete(ana);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(AnagraficheRequest req) throws MangaException {
		log.debug("Update Account: ", req);
        
		Anagrafiche ana = repAna.findById(req.getId())
				.orElseThrow(() -> new MangaException("null_ana"));
		
		if(req.getNome()!=null && !req.getNome().isBlank())
			ana.setNome(req.getNome().trim().toUpperCase());
		
		if (req.getCognome() != null && !req.getCognome().isBlank())
	        ana.setCognome(req.getCognome().trim().toUpperCase());

	    if (req.getStato() != null && !req.getStato().isBlank())
	        ana.setStato(req.getStato().trim().toUpperCase());

	    if (req.getCitta() != null && !req.getCitta().isBlank())
	        ana.setCitta(req.getCitta().trim().toUpperCase());

	    if (req.getProvincia() != null && !req.getProvincia().isBlank())
	        ana.setProvincia(req.getProvincia().trim().toUpperCase());

	    if (req.getCap() != null && !req.getCap().isBlank())
	        ana.setCap(req.getCap().trim().toUpperCase());

	    if (req.getVia() != null && !req.getVia().isBlank())
	        ana.setVia(req.getVia().trim().toUpperCase());

	    if (req.getPredefinito() != null ? req.getPredefinito() : false)
	        ana.setPredefinito(req.getPredefinito());
	    
	    repAna.save(ana);
		
	}

	@Override
	public List<AnagraficheDTO> list() {
		log.debug("findAll() Anagrafica");
		List<Anagrafiche> lA = repAna.findAll();
		
		return lA.stream()
				.map(a->AnagraficheDTO.builder()
						.id(a.getId())
						.nome(a.getNome())
						.cognome(a.getCognome())
						.stato(a.getStato())
						.citta(a.getCitta())
						.provincia(a.getProvincia())
						.cap(a.getCap())
						.via(a.getVia())
						.predefinito(a.getPredefinito())
						.build()
				).toList();
						
	}

	@Override
	public AnagraficheDTO findById(Integer id) throws MangaException {
		log.debug("findById() Anagrafica {}", id);
		Anagrafiche a = repAna.findById(id)
				.orElseThrow(() -> new MangaException("!exists_ana"));
		
		return AnagraficheDTO.builder()
				.id(a.getId())
				.nome(a.getNome())
				.cognome(a.getCognome())
				.stato(a.getStato())
				.citta(a.getCitta())
				.provincia(a.getProvincia())
				.cap(a.getCap())
				.via(a.getVia())
				.predefinito(a.getPredefinito())
				.build();
	}

}
