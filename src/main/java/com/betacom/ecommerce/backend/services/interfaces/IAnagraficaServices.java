package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.AnagraficaRequest;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IAnagraficaServices {

	  public void create(AnagraficaRequest req) throws MangaException;
		
	  public void delete(Integer id) throws MangaException;
	    
	  public void update(AnagraficaRequest req) throws MangaException;
	    
	  public List<AnagraficaDTO> list();
	    
	  AnagraficaDTO findById(Integer id) throws MangaException;

}
