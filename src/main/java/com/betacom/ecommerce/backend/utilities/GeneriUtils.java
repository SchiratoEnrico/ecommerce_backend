package com.betacom.ecommerce.backend.utilities;

import com.betacom.ecommerce.backend.dto.inputs.GenereRequest;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Genere;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneriUtils {

	public static void validateRequest(GenereRequest req, boolean create) {
		
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
	
	public static GenereDTO buildGenDTO(Genere g) {
		return GenereDTO.builder()
				.id(g.getId())
				.descrizione(g.getDescrizione())
//				.manga(g.getManga()
//						.stream()
//						.map(m -> MangaUtils.buildMangaDTO(m))
//						.toList())
				.build();
	}
}
