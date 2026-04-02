package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.SagaRequest;
import com.betacom.ecommerce.backend.dto.outputs.SagaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface ISagaServices {
	Integer create(SagaRequest req) throws MangaException; 
	void update(SagaRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	List<SagaDTO> list(			
			String sagaNome,
			String casaEditriceNome,
			String autoreNome,
			String autoreCognome,
			Integer sagaId,
			Integer casaEditriceId,
			Integer autoreId,
			List<Integer> generiId
			);
	SagaDTO findById(Integer id) throws MangaException;
}
