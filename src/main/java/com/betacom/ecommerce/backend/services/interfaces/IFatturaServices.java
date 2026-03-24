package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IFatturaServices {
	
	void create(FatturaRequest req) throws MangaException; 
	void update(FatturaRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<FatturaDTO> list();
	FatturaDTO findById(Integer id) throws MangaException;

}
