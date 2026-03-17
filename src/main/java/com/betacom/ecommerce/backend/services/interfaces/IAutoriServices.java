package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.AutoriRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoriDTO;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IAutoriServices {

	void create(AutoriRequest req) throws MangaException;
	
	void update(AutoriRequest req) throws MangaException;
	
	void delete(Integer id) throws MangaException;
	
	List<AutoriDTO> list() throws Exception;
	
	AutoriDTO findById(Integer id) throws Exception;
}
