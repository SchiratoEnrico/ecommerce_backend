package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface ITipoPagamentoServices {

	public void create(TipoPagamentoRequest req) throws MangaException;
	
	public void delete(Integer id) throws MangaException;
	    
	public void update(TipoPagamentoRequest req) throws MangaException;
	    
	public List<TipoPagamentoDTO> list();
	    
	TipoPagamentoDTO findById(Integer id) throws MangaException;
	  
}
