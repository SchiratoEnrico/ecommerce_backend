package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.RigaFatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaFatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaOrdine;

public interface IRigaFatturaServices {
	
	void create(RigaFatturaRequest req) throws MangaException; 
	void update(RigaFatturaRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<RigaFatturaDTO> list();
	RigaFatturaDTO findById(Integer id) throws MangaException;
	void righeFatturaFromRigheOrdine(List<RigaOrdine> lO, Fattura f);
}
