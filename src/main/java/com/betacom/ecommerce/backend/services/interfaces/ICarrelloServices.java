package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface ICarrelloServices {
	Integer create(CarrelloRequest req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	void addRow(Integer chartId, String isbn, Integer nCopie) throws MangaException;
	void updateRow(Integer chartId, Integer rowId, String isbn, Integer nCopie) throws MangaException;
	void deleteRow(Integer chartId, Integer rowId) throws MangaException;
	void empty(Integer id) throws Exception;
	
	List<CarrelloDTO> list(List<String> isbns) throws Exception;
	CarrelloDTO findById(Integer id) throws Exception;
	CarrelloDTO findByAccountId(Integer id) throws Exception;
	
	
	boolean isCartOwnedByAccount(Integer chartId, Integer accountId);
}
