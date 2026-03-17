package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.PagamentiRequest;
import com.betacom.ecommerce.backend.dto.outputs.PagamentiDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Pagamenti;
import com.betacom.ecommerce.backend.repositories.IPagamentiRepository;
import com.betacom.ecommerce.backend.services.interfaces.IPagamentiServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentiImplemetation implements IPagamentiServices{

	private final IPagamentiRepository repPag;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(PagamentiRequest req) throws MangaException {
	
		log.debug("Create Pagamento", req);
		
		if(req.getTipoPagamento()==null || req.getTipoPagamento().isBlank())
			throw new MangaException("null_pag");
		
		Pagamenti pag = new Pagamenti();
		pag.setTipoPagamento(req.getTipoPagamento().trim().toUpperCase());
		
		 repPag.save(pag);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
		
		Pagamenti pag = repPag.findById(id)
				.orElseThrow(() -> new MangaException("null_pag"));
		repPag.delete(pag);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(PagamentiRequest req) throws MangaException {
		Pagamenti pag = repPag.findById(req.getId())
				.orElseThrow(() -> new MangaException("null_pag"));
		if(req.getTipoPagamento()!=null && !req.getTipoPagamento().isBlank())
			pag.setTipoPagamento(req.getTipoPagamento().trim());
		
		repPag.save(pag);
	}

	@Override
	public List<PagamentiDTO> list() {
		log.debug("findAll() Pagamenti");
		List<Pagamenti> lP = repPag.findAll();
		return lP.stream()
				.map(p->PagamentiDTO.builder()
						.id(p.getId())
						.tipoPagamento(p.getTipoPagamento())
						.build()
				).toList();
				
	}

	@Override
	public PagamentiDTO findById(Integer id) throws MangaException {
		log.debug("findById() Pagamento {}", id);
		
		Pagamenti pag = repPag.findById(id)
				.orElseThrow(() -> new MangaException("!exists_pag"));
		
		return PagamentiDTO.builder()
				.id(pag.getId())
				.tipoPagamento(pag.getTipoPagamento())
				.build();
				
	}

}
