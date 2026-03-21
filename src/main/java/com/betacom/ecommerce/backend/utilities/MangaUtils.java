package com.betacom.ecommerce.backend.utilities;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Manga;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MangaUtils {
	
	public static MangaDTO buildMangaDTO(Manga a) {
		return MangaDTO.builder()
				.isbn(a.getIsbn())
				.titolo(a.getTitolo())
				.dataPubblicazione(a.getDataPubblicazione())
				.prezzo(a.getPrezzo())
				.immagine(a.getImmagine()) 
				.numeroCopie(a.getNumeroCopie())
				.casaEditrice(Mapper.buildCaseEditriciDTO(a.getCasaEditrice()))
				.generi(a.getGeneri()==null ? null: a.getGeneri().stream()
						.map(g -> DtoBuildres.buildGenereDTO(g, false)).toList())
				.autori(a.getAutori()==null ? null: a.getAutori().stream() 
						.map(aut -> DtoBuildres.buildAutoreDTO(aut, false)).toList())
				.build();
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
			
			//da capire generi, casa editrice e autori cosa fare
		}
		
		return toReturn;
	}

	public static void validateRequest(MangaRequest req, boolean create) {
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
	    
	    if(create && Utils.isBlank(req.getImmagine()))
	    	throw new MangaException("null_img");
	    
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
	    
	    // validazione data (solo se presente)
	    if (!Utils.isBlank(req.getDataPubblicazione())) {
	    	
	    	Utils.stringToDate(req.getDataPubblicazione());
	    }
	}
}
