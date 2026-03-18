package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.StatoOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IStatoOrdineServices {
	Integer create(StatoOrdineRequest req) throws MangaException; 
	void update(StatoOrdineRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<StatoOrdineDTO> list();
	StatoOrdineDTO findById(Integer id) throws MangaException;
}
