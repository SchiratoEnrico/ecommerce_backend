package com.betacom.ecommerce.backend.utilities;

import com.betacom.ecommerce.backend.dto.inputs.AutoriRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoriDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autori;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public  class AutoriUtils {
	
	public static AutoriDTO buildAutDTO(Autori a) {
		return AutoriDTO.builder()
				.nome(a.getNome())
				.cognome(a.getCognome())
				.dataNascita(a.getDataNascita())
				.descrizione(a.getDescrizione())
//				.manga(a.getManga()
//						.stream()
//						.map(m ->MangaUtil.buildMangaDTO(m))
//						.toList())
				.id(a.getId())
				.build();
	}
	
	public static void validateRequest(AutoriRequest req, boolean create) throws MangaException {

		log.debug("checking autore parameters");
	    if (req == null) {
	        throw new MangaException("null_req");
	    }
	    
	    // update -> id obbligatorio
	    if (!create && req.getId() == null) {
	        throw new MangaException("id_required");
	    }

	    // normalizzazione
	    req.setNome(Utils.normalize(req.getNome()));
	    req.setCognome(Utils.normalize(req.getCognome()));
	    req.setDescrizione(Utils.normalize(req.getDescrizione()));
	    req.setDataNascita(Utils.normalize(req.getDataNascita()));

	    // controlli obbligatorietà
	    //in create tutto obbligatorio
	    if (create && Utils.isBlank(req.getNome())) {
	        throw new MangaException("null_nom");
	    }

	    if (create && Utils.isBlank(req.getCognome())) {
	        throw new MangaException("null_cgn");
	    }

	    if (create && Utils.isBlank(req.getDescrizione())) {
	        throw new MangaException("null_dsc");
	    }

	    if (create && Utils.isBlank(req.getDataNascita())) {
	        throw new MangaException("null_dtn");
	    }

	    // validazione data (solo se presente)
	    if (!Utils.isBlank(req.getDataNascita())) {
	    	
	    	Utils.stringToDate(req.getDataNascita());
	    }
	}	

	public static Autori buildAutore(Autori toReturn, AutoriRequest req, Boolean mode) {
		
		if(mode) {
			toReturn.setNome(req.getNome());
			toReturn.setCognome(req.getCognome());
			toReturn.setDescrizione(req.getDescrizione());
			toReturn.setDataNascita(Utils.stringToDate(req.getDataNascita()));
		}else {
			
			if(!Utils.isBlank(req.getNome()))
				toReturn.setNome(req.getNome());
			
			if(!Utils.isBlank(req.getCognome()))
				toReturn.setCognome(req.getCognome());
			
			if(!Utils.isBlank(req.getDescrizione()))
				toReturn.setDescrizione(req.getDescrizione());
			
			if(!Utils.isBlank(req.getDataNascita()))
				toReturn.setDataNascita(Utils.stringToDate(req.getDataNascita()));
		}
		
		return toReturn;
	}
}
