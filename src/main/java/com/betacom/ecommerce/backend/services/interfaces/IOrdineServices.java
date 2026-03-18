package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.OrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IOrdineServices {
	Integer create(OrdineRequest req) throws MangaException; 
	void update(OrdineRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<OrdineDTO> list();
	OrdineDTO findById(Integer id) throws MangaException;

}
