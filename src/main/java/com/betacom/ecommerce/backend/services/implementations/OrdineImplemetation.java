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
	private final IFatturaRepository fattR;
	private final IRigaOrdineServices rowS;
	private final IFatturaServices fattS;
	private final IAnagraficaRepository anaR;
	private final IMangaServices mangaS;
	
	private Anagrafica checkAnagrafica(Integer anagId, Integer accId) {
		Anagrafica ana = anaR.findById(anagId)
			    .orElseThrow(() -> new MangaException("!exists_ana"));
		
		if (!ana.getAccount().getId().equals(accId)) {
		    throw new MangaException("wrong_acc_ana");
		}
		return ana;
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
			o.setData(req.getData());
		} else {
			throw new MangaException("null_dat");
		}
		
		// stato; NW FORSE DOVREMMO FORZARE CREATO IN CREAZIONE
		String stato = "CREATO"; //Utils.normalize(req.getStato());
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
		// block copies
		mangaS.decrementaNumeroCopie(savedOrdine);

		return savedOrdine.getId(); 
	}
    
		
    private void assertTransition(String current, String expected) {
        if (!expected.equals(current))
            throw new MangaException("ord_not_cancellable");
    }
    
    private void modificaStatoOrdine(Ordine o, String nuovoStato) {
        String current = o.getStato().getStatoOrdine();

        if ("CANCELLATO".equals(current))
            throw new MangaException("ord_canc");

        switch (nuovoStato) {
            case "PAGATO"      -> assertTransition(current, "CREATO");
            case "LAVORAZIONE" -> assertTransition(current, "PAGATO");
            case "SPEDITO"     -> assertTransition(current, "LAVORAZIONE");
            case "CONSEGNATO"  -> {
                assertTransition(current, "SPEDITO");
                fattS.updateFromOrdine(o, true);
            	}
            case "CANCELLATO"  -> {
                	fattS.rimborsaNonConsegnato(o, null);
            	}
            case "RICHIESTA_RESO" -> {
                assertTransition(current, "CONSEGNATO");
                fattS.iniziaReso(
                        fattR.findByOrdineId(o.getId())
                            .orElseThrow(() -> new MangaException("!exists_fat"))
                            .getId(),
                        o.getAccount().getId()
                    );
            }
            default -> throw new MangaException("ord_not_cancellable");
        }
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
			o.setData(req.getData());
		}
		
		// stato;
		String stato = Utils.normalize(req.getStato());
		if (stato != null) {
			StatoOrdine stat = statR.findByStatoOrdine(stato)
					.orElseThrow(() -> new MangaException("!exists_sta"));
			modificaStatoOrdine(o, stat.getStatoOrdine());
			o.setStato(stat);			
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
	public void delete(Integer id) throws MangaException {
		log.debug("removing ordine con id {}", id);
		if (id == null) {
			throw new MangaException("null_ord");
		}
		Ordine o = ordeR.findById(id).orElseThrow(() ->
					new MangaException("!exists_ord"));
		
		log.debug("will update corresponding fattura");

		fattS.updateFromOrdine(o, true);
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
