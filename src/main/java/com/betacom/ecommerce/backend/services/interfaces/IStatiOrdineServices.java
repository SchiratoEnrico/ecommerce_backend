package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.StatiOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatiOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IStatiOrdineServices {
	Integer create(StatiOrdineRequest req) throws MangaException; 
	void update(StatiOrdineRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<StatiOrdineDTO> list();
	StatiOrdineDTO findById(Integer id) throws MangaException;
}
