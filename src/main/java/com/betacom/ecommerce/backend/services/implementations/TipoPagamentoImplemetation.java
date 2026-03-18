package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.repositories.ITipoPagamentoRepository;
import com.betacom.ecommerce.backend.services.interfaces.ITipoPagamentoServices;

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
		
		if(req.getTipoPagamento()==null || req.getTipoPagamento().isBlank())
			throw new MangaException("null_pag");
		
		TipoPagamento pag = new TipoPagamento();
		pag.setTipoPagamento(req.getTipoPagamento().trim().toUpperCase());
		
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
		if(req.getTipoPagamento()!=null && !req.getTipoPagamento().isBlank())
			pag.setTipoPagamento(req.getTipoPagamento().trim());
		
		repPag.save(pag);
	}

	@Override
	public List<TipoPagamentoDTO> list() {
		log.debug("findAll() Pagamenti");
		List<TipoPagamento> lP = repPag.findAll();
		return lP.stream()
				.map(p->TipoPagamentoDTO.builder()
						.id(p.getId())
						.tipoPagamento(p.getTipoPagamento())
						.build()
				).toList();
				
	}

	@Override
	public TipoPagamentoDTO findById(Integer id) throws MangaException {
		log.debug("findById() Pagamento {}", id);
		
		TipoPagamento pag = repPag.findById(id)
				.orElseThrow(() -> new MangaException("!exists_pag"));
		
		return TipoPagamentoDTO.builder()
				.id(pag.getId())
				.tipoPagamento(pag.getTipoPagamento())
				.build();
				
	}

}
