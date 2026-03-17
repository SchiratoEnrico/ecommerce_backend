package com.betacom.ecommerce.backend.utilities;

import com.betacom.ecommerce.backend.dto.inputs.GeneriRequest;
import com.betacom.ecommerce.backend.dto.outputs.GeneriDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Generi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneriUtils {

	public static void validateRequest(GeneriRequest req, boolean create) {
		
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
	
	public static Generi buildGenere(Generi toReturn, GeneriRequest req, Boolean mode) {
		if(mode) {
			toReturn.setDescrizione(req.getDescrizione());
		}else {
			if(!Utils.isBlank(req.getDescrizione()))
				toReturn.setDescrizione(req.getDescrizione());
		}
		
		return toReturn;
	}
	
	public static GeneriDTO buildGenDTO(Generi g) {
		return GeneriDTO.builder()
				.id(g.getId())
				.descrizione(g.getDescrizione())
//				.manga(g.getManga()
//						.stream()
//						.map(m -> MangaUtils.buildMangaDTO(m))
//						.toList())
				.build();
	}
}
