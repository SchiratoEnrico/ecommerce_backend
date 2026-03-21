package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.RigaCarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaCarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IRigaCarrelloServices {
	Integer create(RigaCarrelloRequest req) throws MangaException;
	void update(RigaCarrelloRequest req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	List<RigaCarrelloDTO> list() throws Exception;
	RigaCarrelloDTO findById(Integer id) throws Exception;
}
