package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.GenereRequest;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IGenereServices {
	
	void create(GenereRequest req) throws MangaException;
	
	void update(GenereRequest req) throws MangaException;
	
	void delete(Integer id) throws MangaException;
	
	List<GenereDTO> list() throws MangaException;
	
	GenereDTO findById(Integer id) throws MangaException;
}
