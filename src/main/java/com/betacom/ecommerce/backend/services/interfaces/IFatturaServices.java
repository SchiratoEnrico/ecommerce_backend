package com.betacom.ecommerce.backend.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Ordine;

public interface IFatturaServices {
	
	void create(FatturaRequest req) throws MangaException; 
	void update(FatturaRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	
	List<FatturaDTO> list(
			String numeroFattura,
			LocalDate from,
			LocalDate to,
			String clienteNome,
			String clienteCognome,
			String clienteEmail,
			String tipoPagamento,
			String tipoSpedizione,
			String statoFattura,
			Integer idOrdine,
			List<String> isbns
			);
	
	FatturaDTO findById(Integer id) throws MangaException;
	List<FatturaDTO> listByAccountId(Integer accountId) throws Exception;
	
	// trigger automatico da cancellazione ordine
	public void updateFromOrdine(Ordine o, String nuovoStatoOrdine, Boolean ripristinaCopie) throws MangaException;
	
	// reso
	public void iniziaReso(Integer fatturaId, Integer accountId) throws MangaException;
	public void rifiutaReso(Integer fatturaId) throws MangaException;
	public void confermaReso(Integer fatturaId);
	public void rimborsa(Integer fatturaId, Boolean ripristinaCopie);
	public void detachFromOrdine(Ordine o, String note);
}
