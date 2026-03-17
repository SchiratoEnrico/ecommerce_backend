package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.PagamentiRequest;
import com.betacom.ecommerce.backend.dto.outputs.PagamentiDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IPagamentiServices {

	public void create(PagamentiRequest req) throws MangaException;
	
	public void delete(Integer id) throws MangaException;
	    
	public void update(PagamentiRequest req) throws MangaException;
	    
	public List<PagamentiDTO> list();
	    
	PagamentiDTO findById(Integer id) throws MangaException;
	  
}
