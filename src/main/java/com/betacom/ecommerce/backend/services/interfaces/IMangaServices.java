package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IMangaServices {
	
	void create(MangaRequest req) throws MangaException;
	
	void update(MangaRequest req) throws MangaException;
	
	MangaDTO findByIsbn(String isbn) throws MangaException;
	
	List<MangaDTO> list() throws MangaException;
	
	void delete(String isbn) throws MangaException;
	
}
