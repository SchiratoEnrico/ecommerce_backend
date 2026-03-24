package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.CasaEditriceRequest;
import com.betacom.ecommerce.backend.dto.outputs.CasaEditriceDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface ICasaEditriceServices {
	Integer create(CasaEditriceRequest req) throws MangaException;
	void update(CasaEditriceRequest req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	List<CasaEditriceDTO> list(String nome, String descrizione, String indirizzo, String email) throws Exception;
	CasaEditriceDTO findById(Integer id) throws Exception;
}
