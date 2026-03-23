package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.repositories.ITipoPagamentoRepository;
import com.betacom.ecommerce.backend.services.interfaces.ITipoPagamentoServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipoPagamentoImplemetation implements ITipoPagamentoServices{

	private final ITipoPagamentoRepository repPag;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(TipoPagamentoRequest req) throws MangaException {
	
		log.debug("Create Pagamento", req);
		
		if(Utils.isBlank(req.getTipoPagamento()))
			throw new MangaException("null_pag");
		
		TipoPagamento pag = new TipoPagamento();
		pag.setTipoPagamento(Utils.normalize(req.getTipoPagamento()));
		
		repPag.save(pag);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
		
		TipoPagamento pag = repPag.findById(id)
				.orElseThrow(() -> new MangaException("null_pag"));
		repPag.delete(pag);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(TipoPagamentoRequest req) throws MangaException {
		
		TipoPagamento pag = repPag.findById(req.getId())
				.orElseThrow(() -> new MangaException("null_pag"));
		
		if(!Utils.isBlank(req.getTipoPagamento()))
			pag.setTipoPagamento(Utils.normalize(req.getTipoPagamento()));
		
		repPag.save(pag);
	}

	@Override
	public List<TipoPagamentoDTO> list() {
	    log.debug("findAll() Pagamenti");

	    List<TipoPagamento> lP = repPag.findAll();

	    return lP.stream()
	            .map(p -> DtoBuildres.buildTipoPagamentoDTO(p, true))
	            .collect(Collectors.toList());
	}
	
	@Override
	public TipoPagamentoDTO findById(Integer id) throws MangaException {
		log.debug("findById() Pagamento {}", id);
		
		TipoPagamento pag = repPag.findById(id)
				.orElseThrow(() -> new MangaException("!exists_pag"));
		
		return DtoBuildres.buildTipoPagamentoDTO(pag, true);
	}

}
