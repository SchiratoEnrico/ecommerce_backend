package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.TipoSpedizioneRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface ITipoSpedizioneServices {
	Integer create(TipoSpedizioneRequest req) throws MangaException;
	void update(TipoSpedizioneRequest req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	List<TipoSpedizioneDTO> list(String tipoSpedizione) throws Exception;
	TipoSpedizioneDTO findById(Integer id) throws Exception;
}
