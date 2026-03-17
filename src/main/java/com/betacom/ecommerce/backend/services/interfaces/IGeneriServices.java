package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.GeneriRequest;
import com.betacom.ecommerce.backend.dto.outputs.GeneriDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IGeneriServices {
	
	void create(GeneriRequest req) throws MangaException;
	
	void update(GeneriRequest req) throws MangaException;
	
	void delete(Integer id) throws MangaException;
	
	List<GeneriDTO> list() throws MangaException;
	
	GeneriDTO findById(Integer id) throws MangaException;
}
