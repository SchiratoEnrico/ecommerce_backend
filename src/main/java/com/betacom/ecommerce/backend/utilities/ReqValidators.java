package com.betacom.ecommerce.backend.utilities;

import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.betacom.ecommerce.backend.dto.inputs.GenereRequest;
import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReqValidators {
	public static void validateAutoreRequest(AutoreRequest req, boolean create) throws MangaException {

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

	public static Autore buildAutore(Autore toReturn, AutoreRequest req, Boolean mode) {
		
		if(mode) {
			toReturn.setNome(req.getNome());
			toReturn.setCognome(req.getCognome());
			toReturn.setDescrizione(req.getDescrizione());
			toReturn.setDataNascita(Utils.stringToDate(req.getDataNascita()));
		} else {
			
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

	public static void validateGenereRequest(GenereRequest req, boolean create) {
		
		log.debug("checking genere parameters");
	    if (req == null) {
	        throw new MangaException("null_req");
	    }
	    
	    // update → id obbligatorio
	    if (!create && req.getId() == null) {
	        throw new MangaException("id_required");
	    }
	    
	    req.setDescrizione(Utils.normalize(req.getDescrizione()));
	
	    if(create && Utils.isBlank(req.getDescrizione())) {
	    	throw new MangaException("null_des");
	    }
	}
	
	public static Genere buildGenere(Genere toReturn, GenereRequest req, Boolean mode) {
		if(mode) {
			toReturn.setDescrizione(req.getDescrizione());
		}else {
			if(!Utils.isBlank(req.getDescrizione()))
				toReturn.setDescrizione(req.getDescrizione());
		}
		
		return toReturn;
	}
	
	public static Manga buildManga(Manga toReturn, MangaRequest req, Boolean mode) {
		
		if(mode) {
			toReturn.setIsbn(req.getIsbn());
			toReturn.setDataPubblicazione(Utils.stringToDate(req.getDataPubblicazione()));
			toReturn.setImmagine(req.getImmagine());
			toReturn.setNumeroCopie(req.getNumeroCopie());
			toReturn.setTitolo(req.getTitolo());
			toReturn.setPrezzo(req.getPrezzo());
		}else {
			
			if(!Utils.isBlank(req.getIsbn()))
				toReturn.setIsbn(req.getIsbn());
			
			if(!Utils.isBlank(req.getDataPubblicazione()))
				toReturn.setDataPubblicazione(Utils.stringToDate(req.getDataPubblicazione()));
			
			if(!Utils.isBlank(req.getImmagine()))
				toReturn.setIsbn(req.getImmagine());
			
			if(req.getNumeroCopie()!=null)
				toReturn.setNumeroCopie(req.getNumeroCopie());
			
			if(!Utils.isBlank(req.getTitolo()))
				toReturn.setTitolo(req.getTitolo());
			
			if(req.getPrezzo()!=null) 
				toReturn.setPrezzo(req.getPrezzo());
		}
		
		return toReturn;
	}

	public static void validateMangaRequest(MangaRequest req, boolean create) {
		log.debug("checking  manga parameters");
		if (req == null) {
	        throw new MangaException("null_req");
	    }
		
		// update -> id obbligatorio
	    if (!create && req.getIsbn() == null) {
	        throw new MangaException("id_required");
	    }
	    
	    //trim e uppercase
	    req.setDataPubblicazione(Utils.normalize(req.getDataPubblicazione()));
	    req.setImmagine(Utils.normalize(req.getImmagine()));
	    req.setIsbn(Utils.normalize(req.getIsbn()));
	    req.setTitolo(Utils.normalize(req.getTitolo()));
	    
	    if(create && Utils.isBlank(req.getDataPubblicazione()))
	    	throw new MangaException("null_dtp");
	    
	    //if(create && Utils.isBlank(req.getImmagine()))
	    //	throw new MangaException("null_img");
	    
	    if(create && Utils.isBlank(req.getIsbn()))
	    	throw new MangaException("null_isb");
	    
	    if(create && Utils.isBlank(req.getTitolo()))
	    	throw new MangaException("null_tit");
	    
	    if(create && req.getAutori()==null)
	    	throw new MangaException("null_aut");
	    
	    if(create && req.getGeneri()==null)
	    	throw new MangaException("null_gen");
	    
	    if(create && req.getCasaEditrice()==null)
	    	throw new MangaException("null_ced");
	    
	    if(create && req.getNumeroCopie()==null)
	    	throw new MangaException("null_ncp");
	    
	    if(create && req.getPrezzo()==null)
	    	throw new MangaException("null_pre");
	    // check se volSaga inserito ma non id saga
	    if (create && req.getSaga() == null && req.getSagaVol() != null && req.getSagaVol() > 0) {
	    	throw new MangaException("null_sag");
	    }

	    // validazione data (solo se presente)
	    if (!Utils.isBlank(req.getDataPubblicazione())) {
	    	Utils.stringToDate(req.getDataPubblicazione());
	    }
	}
	
	public static void validatePassword(String password) throws MangaException {
	    if (password == null || password.isBlank())
	        throw new MangaException("null_pwd");

	    if (password.trim().length() < 6)
	        throw new MangaException("pwd_short");

	    if (!password.chars().anyMatch(c -> Character.isUpperCase(c)))
	        throw new MangaException("pwd_upper");

	    if (!password.chars().anyMatch(c -> Character.isLowerCase(c)))
	        throw new MangaException("pwd_lower");

	    if (!password.chars().anyMatch(c -> Character.isDigit(c)))
	        throw new MangaException("pwd_digit");

	    if (!password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:',.<>?/`~".indexOf(c) >= 0))
	        throw new MangaException("pwd_special");
	}
}
