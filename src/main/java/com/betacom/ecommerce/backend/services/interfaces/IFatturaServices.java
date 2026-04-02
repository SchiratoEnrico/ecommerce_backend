package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Ordine;

public interface IFatturaServices {
	
	void create(FatturaRequest req) throws MangaException; 
	void update(FatturaRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<FatturaDTO> list();
	FatturaDTO findById(Integer id) throws MangaException;
	void updateFromOrdine(Ordine o, Boolean toDel) throws MangaException;
	void checkRefund(Ordine o, Boolean delete) throws MangaException;
	public void iniziaReso(Integer fatturaId, Integer accountId) throws MangaException;
}
