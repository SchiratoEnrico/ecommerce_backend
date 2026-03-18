package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AnagraficaRequest;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.repositories.IAnagraficaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAnagraficaServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnagraficaImplementation implements IAnagraficaServices{

	private final IAnagraficaRepository repAna;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(AnagraficaRequest req) throws MangaException {
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

		if(req.getCap()==null)
			throw new MangaException("null_cap");
		
		if(req.getVia()==null || req.getVia().isBlank())
			throw new MangaException("null_via");
		
		if(req.getPredefinito()==null)
			throw new MangaException("null_pre");
		
	
		Anagrafica an = new Anagrafica();
	    an.setNome(req.getNome().trim().toUpperCase());
	    an.setCognome(req.getCognome().trim().toUpperCase());
	    an.setStato(req.getStato().trim().toUpperCase());
	    an.setCitta(req.getCitta().trim().toUpperCase());
	    an.setProvincia(req.getProvincia().trim().toUpperCase());
	    an.setCap(req.getCap());
	    an.setVia(req.getVia().trim().toUpperCase());
	    an.setPredefinito(false);

	    repAna.save(an);
	
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
        
		Anagrafica ana = repAna.findById(id)
				.orElseThrow(() -> new MangaException("null_ana"));
		repAna.delete(ana);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(AnagraficaRequest req) throws MangaException {
		log.debug("Update Account: ", req);
        
		Anagrafica ana = repAna.findById(req.getId())
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

	    if (req.getCap() != null)
	        ana.setCap(req.getCap());

	    if (req.getVia() != null && !req.getVia().isBlank())
	        ana.setVia(req.getVia().trim().toUpperCase());

	    if (req.getPredefinito() != null ? req.getPredefinito() : false)
	        ana.setPredefinito(req.getPredefinito());
	    
	    repAna.save(ana);
		
	}

	@Override
	public List<AnagraficaDTO> list() {
		log.debug("findAll() Anagrafica");
		List<Anagrafica> lA = repAna.findAll();
		
		return lA.stream()
				.map(a->AnagraficaDTO.builder()
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
	public AnagraficaDTO findById(Integer id) throws MangaException {
		log.debug("findById() Anagrafica {}", id);
		Anagrafica a = repAna.findById(id)
				.orElseThrow(() -> new MangaException("!exists_ana"));
		
		return AnagraficaDTO.builder()
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
