package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.OrdiniRequest;
import com.betacom.ecommerce.backend.dto.outputs.OrdiniDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IOrdiniServices {
	Integer create(OrdiniRequest req) throws MangaException; 
	void update(OrdiniRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<OrdiniDTO> list();
	OrdiniDTO findById(Integer id) throws MangaException;

}
