package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.RigaOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IRigaOrdineServices {
	Integer create(RigaOrdineRequest req) throws MangaException; 
	void update(RigaOrdineRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<RigaOrdineDTO> list();
	RigaOrdineDTO findById(Integer id) throws MangaException;

}
