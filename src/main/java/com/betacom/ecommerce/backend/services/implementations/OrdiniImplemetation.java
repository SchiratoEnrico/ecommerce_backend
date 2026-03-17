package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.OrdiniRequest;
import com.betacom.ecommerce.backend.dto.outputs.OrdiniDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Accounts;
import com.betacom.ecommerce.backend.models.Ordini;
import com.betacom.ecommerce.backend.models.Pagamenti;
import com.betacom.ecommerce.backend.models.RigheOrdine;
import com.betacom.ecommerce.backend.models.Spedizioni;
import com.betacom.ecommerce.backend.models.StatiOrdine;
import com.betacom.ecommerce.backend.repositories.IOrdiniRepository;
import com.betacom.ecommerce.backend.repositories.IRigheOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IStatiOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IOrdiniServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;
import com.betacom.ecommerce.backend.utilities.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdiniImplemetation implements IOrdiniServices{
	private final IOrdiniRepository ordeR;
	private final IAccountRepository accR;
	private final IPagamentiRepository pagR;
	private final ISpedizioniRepository speR;
	private final IStatiOrdineRepository statR;
	private final IRigheOrdineRepository rowR;

	@Override
	@Transactional (rollbackFor = Exception.class)
	public Integer create(OrdiniRequest req) throws MangaException {
		log.debug("creating ordine {}", req);

		Ordini o = new Ordini();
		
		//Account
		if (req.getAccount() == null) {
			throw new MangaException("null_acc");
		} 
		if (!req.getAccount().isEmpty())  {
			throw new MangaException("null_acc");
		}
		
		try {
			Integer accId = Integer.parseInt(req.getAccount());
			Accounts acc = accR.findById(accId).orElseThrow(() ->
				new MangaException("null_acc"));
			o.setAccount(acc);
		} catch (NumberFormatException e) {
			throw new MangaException("null_acc");
		}
			
		// Pagamento
		String tipoPag = Utils.formatStringParam(req.getPagamento());
		if (tipoPag != null) {
			Pagamenti pag = pagR.findByTipoPagamento(tipoPag).orElseThrow(() ->
					new MangaException("null_pag"));
				o.setPagamento(pag);			
		} else {
			throw new MangaException("null_pag");
		}
		
		// Spedizioni
		String tipoSpe = Utils.formatStringParam(req.getSpedizione());
		if (tipoSpe != null) {
			Spedizioni spe = speR.findByTipoSpedizione(tipoSpe).orElseThrow(() ->
					new MangaException("null_spe"));
				o.setSpedizione(spe);
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
		String stato = Utils.formatStringParam(req.getStato());
		if (stato != null) {
			StatiOrdine stat = statR.findByStato(stato).orElseThrow(() ->
					new MangaException("null_spe"));
				o.setStato(stat);			
		} else {
			throw new MangaException("null_sta");
		}
		
		//righe ordine: gestione da implementazione di righe ordine 
		
		return ordeR.save(o).getId();
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void update(OrdiniRequest req) throws MangaException {
		log.debug("updating ordine {}", req);
		Ordini o = ordeR.findById(req.getId()).orElseThrow(() ->
					new MangaException("!exists_ord"));
		//Account
		if (req.getAccount() != null && !req.getAccount().isEmpty()) {
			try {
				Integer accId = Integer.parseInt(req.getAccount());
				Optional<Accounts> acc = accR.findById(accId);
				if (!acc.isEmpty()) {
					o.setAccount(acc.get());
				}
			} catch (NumberFormatException e) {
				continue;
			}
		}
			
		// Pagamento
		String tipoPag = Utils.formatStringParam(req.getPagamento());
		if (tipoPag != null) {
			Optional<Pagamenti> pag = pagR.findByTipoPagamento(tipoPag);
			if (!pag.isEmpty()) {
				o.setPagamento(pag.get());
			}		
		} 
		
		// Spedizioni
		String tipoSpe = Utils.formatStringParam(req.getSpedizione());
		if (tipoSpe != null) {
			Optional<Spedizioni> spe = speR.findBySpedizione(tipoSpe);
			if (!tipoSpe.isEmpty()) {
				o.setSpedizione(spe.get());
			}
		}

		// data;
		if (req.getData() != null) {
			try {
				o.setData(Utils.stringToDate(req.getData()));
			} catch (MangaException e) {
				continue;
			}
		}
		
		// stato;
		String stato = Utils.formatStringParam(req.getStato());
		if (stato != null) {
			Optional<StatiOrdine> stat = statR.findByStato(stato);
			if (!stat.isEmpty()){ 
				o.setStato(stat.get());			
			} 
		}
		ordeR.save(o);
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void delete(Integer id) throws MangaException {
		log.debug("removing ordine con id {}", id);
		Ordini o = ordeR.findById(id).orElseThrow(() ->
					new MangaException("!exists_ord"));
		List<RigheOrdine> lR = o.getRigheOrdine();
		
		// rimozione automatica righe quando ordine viene eliminato
		lR.stream()
		  .forEach(r -> rowR.delete(r));

		ordeR.delete(o);
	}

	@Override
	public List<OrdiniDTO> list() {
		log.debug("ordine list()");

		List<Ordini> lO = ordeR.findAll();
		return lO.stream()
			.map(o -> DtoBuildres.buildOrdiniDTO(o, true))
			.collect(Collectors.toList());
		
	}

	@Override
	public OrdiniDTO findById(Integer id) throws MangaException {
		log.debug("ordine findById({})", id);
		Ordini o = ordeR.findById(id).orElseThrow(() ->
						new MangaException("!exists_ord"));
		return DtoBuildres.buildOrdiniDTO(o, true);
	}
}
