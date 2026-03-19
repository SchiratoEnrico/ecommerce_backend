package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Manga;

public interface ICarrelloServices {
	Integer create(CarrelloRequest req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	List<CarrelloDTO> list() throws Exception;
	CarrelloDTO findById(Integer id) throws Exception;
}
