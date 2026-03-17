package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.AnagraficheRequest;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficheDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IAnagraficheServices {

	  public void create(AnagraficheRequest req) throws MangaException;
		
	  public void delete(Integer id) throws MangaException;
	    
	  public void update(AnagraficheRequest req) throws MangaException;
	    
	  public List<AnagraficheDTO> list();
	    
	  AnagraficheDTO findById(Integer id) throws MangaException;

}
