package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.CaseEditriciReq;
import com.betacom.ecommerce.backend.dto.outputs.CaseEditriciDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Manga;

public interface ICaseEditriciServices {
	Integer create(CaseEditriciReq req) throws MangaException;
	void update(CaseEditriciReq req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	List<CaseEditriciDTO> list(String nome, String descrizione, String indirizzo, String email) throws Exception;
	CaseEditriciDTO findById(Integer id) throws Exception;
}
