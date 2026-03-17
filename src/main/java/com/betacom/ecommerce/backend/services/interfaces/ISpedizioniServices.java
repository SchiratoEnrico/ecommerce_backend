package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.SpedizioniReq;
import com.betacom.ecommerce.backend.dto.outputs.SpedizioniDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface ISpedizioniServices {
	Integer create(SpedizioniReq req) throws MangaException;
	void update(SpedizioniReq req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	List<SpedizioniDTO> list(String tipoSpedizione) throws Exception;
	SpedizioniDTO findById(Integer id) throws Exception;
}
