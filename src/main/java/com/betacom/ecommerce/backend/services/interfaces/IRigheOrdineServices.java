package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.RigheOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigheOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IRigheOrdineServices {
	Integer create(RigheOrdineRequest req) throws MangaException; 
	void update(RigheOrdineRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<RigheOrdineDTO> list();
	RigheOrdineDTO findById(Integer id) throws MangaException;

}
