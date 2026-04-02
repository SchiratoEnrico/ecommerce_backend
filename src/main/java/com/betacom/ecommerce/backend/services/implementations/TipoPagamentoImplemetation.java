package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.ITipoPagamentoRepository;
import com.betacom.ecommerce.backend.services.interfaces.ITipoPagamentoServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipoPagamentoImplemetation implements ITipoPagamentoServices{

	private final ITipoPagamentoRepository repPag;
	private final IOrdineRepository ordeR;

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(TipoPagamentoRequest req) throws MangaException {
	
		log.debug("Create Pagamento", req);
		String myPag= Utils.normalize(req.getTipoPagamento());

		if (myPag == null || myPag.isEmpty()) {
			throw new MangaException("null_pag");
		}
		
		TipoPagamento pag = new TipoPagamento();
		pag.setTipoPagamento(myPag);
		
		repPag.save(pag);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
		
		TipoPagamento pag = repPag.findById(id)
				.orElseThrow(() -> new MangaException("null_pag"));
		if (ordeR.existsByTipoPagamentoId(pag.getId())) {
			throw new MangaException("order_pag");
		}
		repPag.delete(pag);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(TipoPagamentoRequest req) throws MangaException {
		TipoPagamento pag = repPag.findById(req.getId())
				.orElseThrow(() -> new MangaException("null_pag"));
		
		String myPag= Utils.normalize(req.getTipoPagamento());

		if (myPag == null || myPag.isEmpty()) {
			throw new MangaException("null_pag");
		}

		Optional<TipoPagamento> dup = repPag.findByTipoPagamento(myPag);
		
		if (dup.isEmpty()) {
			pag.setTipoPagamento(myPag); 
		} else {
			throw new MangaException("exists_pag");
		}
		repPag.save(pag);
	}

	@Override
	public List<TipoPagamentoDTO> list() {
	    log.debug("findAll() Pagamenti");

	    List<TipoPagamento> lP = repPag.findAll();

	    return lP.stream()
	            .map(p -> DtoBuilders.buildTipoPagamentoDTO(p))
	            .collect(Collectors.toList());
	}
	
	@Override
	public TipoPagamentoDTO findById(Integer id) throws MangaException {
		log.debug("findById() Pagamento {}", id);
		
		TipoPagamento pag = repPag.findById(id)
				.orElseThrow(() -> new MangaException("!exists_pag"));
		
		return DtoBuilders.buildTipoPagamentoDTO(pag);
	}

}
