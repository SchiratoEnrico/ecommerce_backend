package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.OrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
import com.betacom.ecommerce.backend.repositories.ITipoPagamentoRepository;
import com.betacom.ecommerce.backend.repositories.ITipoSpedizioneRepository;
import com.betacom.ecommerce.backend.services.interfaces.IOrdineServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdineImplemetation implements IOrdineServices{
	private final IOrdineRepository ordeR;
	private final IAccountRepository accR;
	private final ITipoPagamentoRepository pagR;
	private final ITipoSpedizioneRepository speR;
	private final IStatoOrdineRepository statR;
	private final IRigaOrdineRepository rowR;

	@Override
	@Transactional (rollbackFor = Exception.class)
	public Integer create(OrdineRequest req) throws MangaException {
		log.debug("creating ordine {}", req);

		Ordine o = new Ordine();
		
		//Account
		if (req.getAccount() == null) {
			throw new MangaException("null_acc");
		}
		Account acc = accR.findById(req.getAccount()).orElseThrow(() ->
				new MangaException("!exists_acc"));
		o.setAccount(acc);
			
		// Pagamento (da fare come id?)
		String tipoPag = Utils.normalize(req.getPagamento());
		if (tipoPag != null) {
			TipoPagamento pag = pagR.findByTipoPagamento(tipoPag).orElseThrow(() ->
					new MangaException("!exists_pag"));
				o.setTipoPagamento(pag);			
		} else {
			throw new MangaException("null_pag");
		}
		
		// Spedizioni
		String tipoSpe = Utils.normalize(req.getSpedizione());
		if (tipoSpe != null) {
			TipoSpedizione spe = speR.findByTipoSpedizione(tipoSpe).orElseThrow(() ->
					new MangaException("!exists_spe"));
				o.setTipoSpedizione(spe);
		} else {
			throw new MangaException("null_spe");
		}

		// data;
		if (req.getData() != null) {
			o.setData(Utils.stringToDate(req.getData()));
		} else {
			throw new MangaException("null_dat");
		}
		
		// stato;
		String stato = Utils.normalize(req.getStato());
		if (stato != null) {
			StatoOrdine stat = statR.findByStatoOrdine(stato).orElseThrow(() ->
					new MangaException("!exists_sta"));
				o.setStato(stat);			
		} else {
			throw new MangaException("null_sta");
		}
		
		//righe ordine: gestione da implementazione di righe ordine 
		
		return ordeR.save(o).getId();
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void update(OrdineRequest req) throws MangaException {
		log.debug("updating ordine {}", req);
		
		if (req.getId() == null) {
			throw new MangaException("null_ord");
		}
		
		Ordine o = ordeR.findById(req.getId()).orElseThrow(() ->
					new MangaException("!exists_ord"));
		//Account
		if (req.getAccount() != null) {
			Account acc = accR.findById(req.getAccount())
					.orElseThrow(() -> new MangaException("!exists_acc"));
			o.setAccount(acc);
		}
			
		// Pagamento
		String tipoPag = Utils.normalize(req.getPagamento());
		if (tipoPag != null) {
			TipoPagamento pag = pagR.findByTipoPagamento(tipoPag)
					.orElseThrow(() -> new MangaException("!exists_pag"));
			o.setTipoPagamento(pag);
		}		 
		
		// Spedizioni
		String tipoSpe = Utils.normalize(req.getSpedizione());
		if (tipoSpe != null) {
			TipoSpedizione spe = speR.findByTipoSpedizione(tipoSpe)
					.orElseThrow(() -> new MangaException("!exists_spe"));
				o.setTipoSpedizione(spe);
			
		}

		// data;
		if (req.getData() != null) {
			try {
				o.setData(Utils.stringToDate(req.getData()));
			} catch (MangaException e) {
				throw new MangaException("null_dat");
			}
		}
		
		// stato;
		String stato = Utils.normalize(req.getStato());
		if (stato != null) {
			StatoOrdine stat = statR.findByStatoOrdine(stato)
					.orElseThrow(() -> new MangaException("!exists_sta"));
			o.setStato(stat);			
		}
		ordeR.save(o);
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void delete(Integer id) throws MangaException {
		log.debug("removing ordine con id {}", id);
		if (id == null) {
			throw new MangaException("null_ord");
		}
		Ordine o = ordeR.findById(id).orElseThrow(() ->
					new MangaException("!exists_ord"));
		List<RigaOrdine> lR = o.getRigheOrdine();
		
		// rimozione automatica righe quando ordine viene eliminato
		lR.stream()
		  .forEach(r -> rowR.delete(r));
		ordeR.delete(o);
	}

	@Override
	public List<OrdineDTO> list() {
		log.debug("ordine list()");

		List<Ordine> lO = ordeR.findAll();
		return lO.stream()
			.map(o -> DtoBuildres.buildOrdineDTO(o, true))
			.collect(Collectors.toList());
		
	}

	@Override
	public OrdineDTO findById(Integer id) throws MangaException {
		log.debug("ordine findById({})", id);
		
		if (id == null) {
			throw new MangaException("null_ord");
		}

		Ordine o = ordeR.findById(id).orElseThrow(() ->
						new MangaException("!exists_ord"));
		return DtoBuildres.buildOrdineDTO(o, true);
	}
}
