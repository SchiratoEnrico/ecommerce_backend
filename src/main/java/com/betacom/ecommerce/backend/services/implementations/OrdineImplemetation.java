package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
import com.betacom.ecommerce.backend.dto.inputs.RigaOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.OrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.IAnagraficaRepository;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
import com.betacom.ecommerce.backend.repositories.ITipoPagamentoRepository;
import com.betacom.ecommerce.backend.repositories.ITipoSpedizioneRepository;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMangaServices;
import com.betacom.ecommerce.backend.services.interfaces.IOrdineServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaOrdineServices;
import com.betacom.ecommerce.backend.specification.OrdineSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;

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
	private final IFatturaRepository fattR;
	private final IRigaOrdineServices rowS;
	private final IFatturaServices fattS;
	private final IAnagraficaRepository anaR;
	private final IMangaServices mangaS;
	

	/// operazioni CRUD e helpers
	
	private Anagrafica checkAnagrafica(Integer anagId, Integer accId) {
		Anagrafica ana = anaR.findById(anagId)
			    .orElseThrow(() -> new MangaException("!exists_ana"));
		
		if (!ana.getAccount().getId().equals(accId)) {
		    throw new MangaException("wrong_acc_ana");
		}
		return ana;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void advanceStatoOrdine(Integer ordineId, Integer statoId) throws MangaException {
		log.debug("advanceStatoOrdine ordineId={}, statoId={}", ordineId, statoId);
 
		if (ordineId == null)
			throw new MangaException("null_ord");
		if (statoId == null)
			throw new MangaException("null_sta");
 
		Ordine o = ordeR.findById(ordineId)
			.orElseThrow(() -> new MangaException("!exists_ord"));
 
		StatoOrdine nuovoStato = statR.findById(statoId)
			.orElseThrow(() -> new MangaException("!exists_sta"));
 
		String current = o.getStato().getStatoOrdine();
		String target = nuovoStato.getStatoOrdine();
 
		// Controllo target stato permesso
		validateOrdineTransition(current, target);
 
		// aggiorno fattura in base a nuovo stato
		switch (target) {
			case "PAGATO", "LAVORAZIONE", "SPEDITO", "CONSEGNATO" ->
				// copia statoordine su fattura, no cambio copie
				fattS.updateFromOrdine(o, target, false);
 
			case "CANCELLATO" ->
				// ANNULLATA su fattura + ripristino copie 
				// settabile solo se stato CREATO/PAGATO
				fattS.updateFromOrdine(o, "CANCELLATO", true);
 
			case "RICHIESTA_RESO" -> {
				// Passa a fattura reso
				fattS.iniziaReso(
					fattR.findByOrdineId(o.getId())
						.orElseThrow(() -> new MangaException("!exists_fat"))
						.getId(),
					o.getAccount().getId()
				);
			}
 
			default -> throw new MangaException("ord_transition_invalid");
		}
 
		// Aggiorno stato ordine dopo sincronizzazione con fattura
		o.setStato(nuovoStato);
		ordeR.save(o);
	}

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
		
		if (req.getPagamentoId() != null) {
			TipoPagamento pag = pagR.findById(req.getPagamentoId()).orElseThrow(() ->
					new MangaException("!exists_pag"));
				o.setTipoPagamento(pag);			
		} else {
			throw new MangaException("null_pag");
		}
		
		// Spedizioni
		if (req.getSpedizioneId() != null) {
			TipoSpedizione spe = speR.findById(req.getSpedizioneId()).orElseThrow(() ->
					new MangaException("!exists_spe"));
				o.setTipoSpedizione(spe);
		} else {
			throw new MangaException("null_spe");
		}

		// data;
		if (req.getData() != null) {
			o.setData(req.getData());
		} else {
			throw new MangaException("null_dat");
		}
		
		// NW: stato FORZATO IN CREAZIONE
		String stato = "CREATO";
		StatoOrdine stat = statR.findByStatoOrdine(stato).orElseThrow(() ->
					new MangaException("!exists_sta"));
				o.setStato(stat);
		
				// anagrafica:
		if (req.getAnagrafica() == null)
		    throw new MangaException("null_ana");
		
		Anagrafica ana = checkAnagrafica(req.getAnagrafica(), req.getAccount());
		o.setAnagrafica(ana);	
		
		Ordine savedOrdine = ordeR.save(o);
		
		if (req.getRigheOrdineRequest() != null && !req.getRigheOrdineRequest().isEmpty()) {
		
			 for (RigaOrdineRequest r : req.getRigheOrdineRequest()) {
			        r.setIdOrdine(savedOrdine.getId());  // collega la riga all'ordine
			        r.setNumeroCopie(
			            r.getNumeroCopie() != null ? r.getNumeroCopie() : 1
			        );
			        try {
			            rowS.create(r);    
			        } catch (MangaException e) {
			            throw new MangaException(e.getMessage());
			        }
			    }
		
		}
		
		Integer myId = savedOrdine.getId();
		savedOrdine.setRigheOrdine(rowR.findAllByOrdineId(myId));
		// blocca copie richieste da ordine
		mangaS.decrementaNumeroCopie(savedOrdine);

		return savedOrdine.getId(); 
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
		if (req.getPagamentoId() != null) {
			TipoPagamento pag = pagR.findById(req.getPagamentoId())
					.orElseThrow(() -> new MangaException("!exists_pag"));
			o.setTipoPagamento(pag);
		}		 
				
		// Spedizioni
		if (req.getSpedizioneId() != null) {
			TipoSpedizione spe = speR.findById(req.getSpedizioneId())
					.orElseThrow(() -> new MangaException("!exists_spe"));
				o.setTipoSpedizione(spe);
		}

		// data;
		if (req.getData() != null) {
			o.setData(req.getData());
		}
		
		// anagrafica
		Integer anagId = req.getAnagrafica();
		if (anagId != null) {
			Anagrafica ana = checkAnagrafica(req.getAnagrafica(), req.getAccount());
			o.setAnagrafica(ana);	
		}

		ordeR.save(o);
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void delete(Integer id, Boolean ripristinaCopie) throws MangaException {
		log.debug("removing ordine con id {}", id);
		if (id == null) {
			throw new MangaException("null_ord");
		}
		Ordine o = ordeR.findById(id).orElseThrow(() ->
					new MangaException("!exists_ord"));
		
		log.debug("will update corresponding fattura");
		fattS.detachFromOrdine(o, "Ordine eliminato");
		if (Boolean.TRUE.equals(ripristinaCopie)) {
			mangaS.ripristinaNumeroCopie(o);
		}

		ordeR.delete(o);
	}

	
	List<OrdineDTO> getDTOs(List<Ordine> lO) {
		return lO.stream()
		.map(o -> 
			DtoBuilders.buildOrdineDTO(
				o, 
				Optional.ofNullable(o.getAccount()), 
				Optional.ofNullable(o.getTipoPagamento()), 
				Optional.ofNullable(o.getStato()), 
				Optional.ofNullable(o.getTipoSpedizione()), 
				Optional.empty(),
				Optional.ofNullable(o.getAnagrafica())))
			.collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrdineDTO> list(
			AccountDTO account,
	        TipoPagamentoDTO tipoPagamento,
	        TipoSpedizioneDTO tipoSpedizione,
	        Integer anno,
	        Integer mese,
	        Integer giorno,
	        StatoOrdineDTO stato,
	        List<String> isbns)  {

		 Specification<Ordine> spec = Specification
		            .where(OrdineSpecifications.accountUsernameLike(account != null ? account.getUsername() : null))
		            .and(OrdineSpecifications.tipoPagamentoLike(tipoPagamento != null ? tipoPagamento.getTipoPagamento() : null))
		            .and(OrdineSpecifications.tipoSpedizioneLike(tipoSpedizione != null ? tipoSpedizione.getTipoSpedizione() : null))
		            .and(OrdineSpecifications.statoOrdineLike(stato != null ? stato.getStatoOrdine() : null))
		            .and(OrdineSpecifications.hasAnyMangaIds(isbns))
		            .and(OrdineSpecifications.meseAnnoEquals(giorno, mese, anno));

		    List<Ordine> lO = ordeR.findAll(spec);
		    return getDTOs(lO);
		}

	@Override
	public OrdineDTO findById(Integer id) throws MangaException {
		log.debug("ordine findById({})", id);
		
		if (id == null) {
			throw new MangaException("null_ord");
		}

		Ordine o = ordeR.findById(id).orElseThrow(() ->
						new MangaException("!exists_ord"));
		return DtoBuilders.buildOrdineDTO(
				o, 
				Optional.ofNullable(o.getAccount()), 
				Optional.ofNullable(o.getTipoPagamento()), 
				Optional.ofNullable(o.getStato()), 
				Optional.ofNullable(o.getTipoSpedizione()), 
				Optional.ofNullable(rowR.findAllByOrdineId(o.getId())),
				Optional.ofNullable(o.getAnagrafica()));
	}
	
	//PIPELINE STATI ORDINE

	// mappa di transizion possibili

	private static final java.util.Map<String, List<String>> ALLOWED_ORDINE = java.util.Map.of(
			"CREATO",      List.of("PAGATO", "CANCELLATO"),
			"PAGATO",      List.of("LAVORAZIONE", "CANCELLATO"),
			"LAVORAZIONE", List.of("SPEDITO"),
			"SPEDITO",     List.of("CONSEGNATO"),
			"CONSEGNATO",  List.of("RICHIESTA_RESO")
		);
	 
	private void validateOrdineTransition(String from, String to) {
			if ("CANCELLATO".equals(from))
				throw new MangaException("ord_canc");
			List<String> allowed = ALLOWED_ORDINE.getOrDefault(from, List.of());
			if (!allowed.contains(to))
				throw new MangaException("ord_transition_invalid");
		}
	 
	
	@Override
	public Boolean isOrdineOwnedByAccount(Integer ordineId, Integer accountId) {
	    var ordineOpt = ordeR.findById(ordineId);
	    
	    if (ordineOpt.isEmpty() || ordineOpt.get().getAccount() == null) {
	        return false;
	    }
	    
	    // Confronto tra l'ID dell'account dell'ordine e l'ID fornito
	    return ordineOpt.get().getAccount().getId().equals(accountId);
	}
}
