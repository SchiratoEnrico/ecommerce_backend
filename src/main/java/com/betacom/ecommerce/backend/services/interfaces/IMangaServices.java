package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.Ordine;

public interface IMangaServices {
	
	void create(MangaRequest req) throws MangaException;
	
	void update(MangaRequest req) throws MangaException;
	
	MangaDTO findByIsbn(String isbn) throws MangaException;
	
	List<MangaDTO> list(
			String titolo,
			String casaEditriceNome,
			String autoreNome,
			String sagaNome,
			Integer sagaId,
			Integer casaEditriceId,
			Integer autoreId,
			List<Integer> generiId
			) throws MangaException;

	void delete(String isbn) throws MangaException;
	
	void ripristinaNumeroCopie(Ordine o);
	void ripristinaNumeroCopie(Fattura f);
	void decrementaNumeroCopie(Ordine o) throws MangaException;
	void decrementaNumeroCopie(Fattura f) throws MangaException;
	
}
