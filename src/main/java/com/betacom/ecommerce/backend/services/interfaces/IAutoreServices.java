package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IAutoreServices {

	void create(AutoreRequest req) throws MangaException;
	
	void update(AutoreRequest req) throws MangaException;
	
	void delete(Integer id) throws MangaException;
	
	List<AutoreDTO> list() throws Exception;
	
	AutoreDTO findById(Integer id) throws Exception;
}
