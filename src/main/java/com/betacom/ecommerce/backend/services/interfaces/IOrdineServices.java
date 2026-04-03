package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.OrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IOrdineServices {
	Integer create(OrdineRequest req) throws MangaException; 
	void update(OrdineRequest req) throws MangaException; 
	void delete(Integer id) throws MangaException; 
	
	List<OrdineDTO> list(
		    AccountDTO account,
		    TipoPagamentoDTO tipoPagamento,
		    TipoSpedizioneDTO tipoSpedizione,
		    Integer anno,
		    Integer mese,
		    Integer giorno,
		    StatoOrdineDTO stato,
		    List<String> isbns) throws MangaException;
	
	OrdineDTO findById(Integer id) throws MangaException;

	Boolean isOrdineOwnedByAccount(Integer ordineId, Integer accountId);

}
