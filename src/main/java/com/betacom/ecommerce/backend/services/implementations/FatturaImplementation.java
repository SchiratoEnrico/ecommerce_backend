package com.betacom.ecommerce.backend.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.inputs.RigaFatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IRigaFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.repositories.ITipoPagamentoRepository;
import com.betacom.ecommerce.backend.repositories.ITipoSpedizioneRepository;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMangaServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaFatturaServices;
import com.betacom.ecommerce.backend.specification.FatturaSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FatturaImplementation implements IFatturaServices{
	private final IOrdineRepository ordeR;
	private final IRigaOrdineRepository rigoR;
	private final IFatturaRepository fattR;
	private final ITipoPagamentoRepository pagR;
	private final ITipoSpedizioneRepository spedR;
	
	private final IRigaFatturaRepository rigfR;
	private final IRigaFatturaServices rigfS;
	private final IMangaServices mangS;

	/// operazioni CRUD e helpers
	
    private String generateNumeroFattura(Integer idOrdine){
		// devo assegnare automaticamente numero fattura
		Boolean exists = true;
		String numFattura = "FAT-" + idOrdine;
		while (exists) {
			String trial = numFattura + "-" + UUID.randomUUID()
				.toString()
				.substring(0, 8);
			if (fattR.findByNumeroFattura(trial).isEmpty()) {
				numFattura = trial;
				exists = false;
			}
		}
		return numFattura;
    }
    
    
    @Override
    @Transactional(rollbackFor = MangaException.class)
	public void create(FatturaRequest req) throws MangaException {
		log.debug("Create Fattura: {}", req);
		
		//log.debug("Is blank: {}", Utils.isBlank(req.getNumeroFattura()));
		
        //validazione cliente
		if (Utils.isBlank(req.getClienteNome()))
            throw new MangaException("null_nom");

        if (Utils.isBlank(req.getClienteCognome()))
            throw new MangaException("null_cog");

        if (Utils.isBlank(req.getClienteEmail()))
            throw new MangaException("null_ema");

        if (Utils.isBlank(req.getClienteIndirizzo()))
            throw new MangaException("null_ind");

        if (Utils.isBlank(req.getClienteCitta()))
            throw new MangaException("null_cit");

        if (Utils.isBlank(req.getClienteCap()))
            throw new MangaException("null_cap");

        if (Utils.isBlank(req.getClienteProvincia()))
            throw new MangaException("null_pro");

        if (Utils.isBlank(req.getClienteStato()))
            throw new MangaException("null_sta");

        //validazione pag e sped
        if (req.getTipoPagamentoId() == null)
            throw new MangaException("null_pag");

        if (req.getTipoSpedizioneId() == null)
            throw new MangaException("null_spe");

        if (req.getOrdineId() == null) {
        	throw new MangaException("null_ord");
        }

        Fattura fat = new Fattura();
        
        // Snapshot cliente
        fat.setClienteNome(Utils.normalize(req.getClienteNome()));
        fat.setClienteCognome(Utils.normalize(req.getClienteCognome()));
        fat.setClienteEmail(Utils.normalize(req.getClienteEmail()));
        fat.setClienteIndirizzo(Utils.normalize(req.getClienteIndirizzo()));
        fat.setClienteCitta(Utils.normalize(req.getClienteCitta()));
        fat.setClienteCap(Utils.normalize(req.getClienteCap()));
        fat.setClienteProvincia(Utils.normalize(req.getClienteProvincia()));
        fat.setClienteStato(Utils.normalize(req.getClienteStato()));

        // Pagamento e spedizione
        TipoPagamento pag = pagR.findById(req.getTipoPagamentoId()).orElseThrow(()->
        	new MangaException("!exists_pag"));
        fat.setTipoPagamento(pag.getTipoPagamento());
        
        TipoSpedizione spe = spedR.findById(req.getTipoSpedizioneId()).orElseThrow(()->
    		new MangaException("!exists_spe"));

        fat.setTipoSpedizione(spe.getTipoSpedizione());
        fat.setCostoSpedizione(spe.getCostoSpedizione());
        fat.setTotale(fat.getCostoSpedizione()); // totale iniziale, le righe lo aggiorneranno

        Ordine ord = ordeR.findById(req.getOrdineId()).orElseThrow(()
        		-> new MangaException("!exists_ord"));
        fat.setOrdine(ord);
        fat.setStatoFattura(ord.getStato().getStatoOrdine());

		String numFattura = generateNumeroFattura(ord.getId());
		log.debug("N fattura: {}", numFattura);
        fat.setNumeroFattura(numFattura);
        fat.setNote(req.getNote());
        
		log.debug("fattura: {}", fat);

        Fattura f = fattR.save(fat);
		Integer myId = f.getId();

		if (req.getRigheFatturaRequest() != null && !req.getRigheFatturaRequest().isEmpty()) {
			 for (RigaFatturaRequest r : req.getRigheFatturaRequest()) {
			        r.setIdFattura(myId);  // collega la riga alla fattura
			        r.setNumeroCopie(
			            r.getNumeroCopie() != null ? r.getNumeroCopie() : 1
			        );
			        saveOrUpdateRigaFattura(fat, r);
			    }
			 
		f.setRighe(rigfR.findAllByFatturaId(myId));
		Utils.ricalcolaTotale(f);
		fattR.save(f);
		}
     }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(FatturaRequest req) throws MangaException {
		 log.debug("updating Fattura {}", req);
		 
	     Fattura fat = fattR.findById(req.getId()).orElseThrow(() ->
	                new MangaException("!exists_fat"));
	     // Dati fattura

	     // Snapshot cliente
	     if (!Utils.isBlank(req.getClienteNome()))
	         fat.setClienteNome(Utils.normalize(req.getClienteNome()));

	     if (!Utils.isBlank(req.getClienteCognome()))
	         fat.setClienteCognome(Utils.normalize(req.getClienteCognome()));

	     
	     if (!Utils.isBlank(req.getClienteEmail()))
		      fat.setClienteEmail(Utils.normalize(req.getClienteEmail()));

	     if (!Utils.isBlank(req.getClienteIndirizzo()))
	         fat.setClienteIndirizzo(Utils.normalize(req.getClienteIndirizzo()));

	     if (!Utils.isBlank(req.getClienteCitta()))
	         fat.setClienteCitta(Utils.normalize(req.getClienteCitta()));

	     if (!Utils.isBlank(req.getClienteCap()))
	         fat.setClienteCap(Utils.normalize(req.getClienteCap()));

	     if (!Utils.isBlank(req.getClienteProvincia()))
	         fat.setClienteProvincia(Utils.normalize(req.getClienteProvincia()));

	     if (!Utils.isBlank(req.getClienteStato()))
	         fat.setClienteStato(Utils.normalize(req.getClienteStato()));

	     // Pagamento e spedizione
	     
	     if (req.getTipoPagamentoId() != null) {
	    	Optional<TipoPagamento> pag = pagR.findById(req.getTipoPagamentoId());
	        if (pag.isEmpty()) {
	        	throw new MangaException("!exists_pag");
	        }
	        fat.setTipoPagamento(pag.get().getTipoPagamento());
	     }
	     
	     if (req.getTipoSpedizioneId() != null) {
		    Optional<TipoSpedizione> spe = spedR.findById(req.getTipoSpedizioneId());
		    if (spe.isEmpty()) {
		        	throw new MangaException("!exists_spe");
		     } 
		    fat.setTipoSpedizione(spe.get().getTipoSpedizione());
		    fat.setCostoSpedizione(spe.get().getCostoSpedizione());
		    Utils.ricalcolaTotale(fat);
	     }

	     if (req.getOrdineId() != null) {
	    	 Ordine ord = ordeR.findById(req.getOrdineId()).orElseThrow(() ->
	    			 new MangaException("!exists_ord"));
	     	 fat.setStatoFattura(ord.getStato().getStatoOrdine());
	 		List<RigaOrdine> lR = rigoR.findAllByOrdineId(ord.getId());
			rigfS.righeFatturaFromRigheOrdine(lR, fat);
	     }
	     	     
	     if (!Utils.isBlank(req.getNote())) {
	      	fat.setOrdine(null);
	     }
			
	     Integer myId = fat.getId();
	     if (req.getRigheFatturaRequest() != null && !req.getRigheFatturaRequest().isEmpty()) {
			for (RigaFatturaRequest r : req.getRigheFatturaRequest()) {
				r.setIdFattura(myId);  // collega la riga alla fattura
				r.setNumeroCopie(
				   r.getNumeroCopie() != null ? r.getNumeroCopie() : 1
				);
				saveOrUpdateRigaFattura(fat, r);
			}
				 
			fat.setRighe(rigfR.findAllByFatturaId(myId));
			Utils.ricalcolaTotale(fat);
	     }
		fattR.save(fat);
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdateRigaFattura(Fattura f, RigaFatturaRequest r) {
		List<String> lR = f.getRighe().stream()
				.map(rf -> rf.getIsbn())
				.toList();
		if (lR.contains(r.getIsbn())) {
			try {
				rigfS.update(r);
			} catch (MangaException e) {
			    throw new MangaException(e.getMessage());
			}
			return;
		}
		try {
			rigfS.create(r);
		} catch (MangaException e) {
		    throw new MangaException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		 log.debug("removing Fattura with id {}", id);
	        if (id == null)
	            throw new MangaException("null_fat");

	        Fattura fat = fattR.findById(id).orElseThrow(() ->
	                new MangaException("!exists_fat"));
	        fattR.delete(fat);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FatturaDTO> list(
			String numeroFattura,
			LocalDate from,
			LocalDate to,
			String clienteNome,
			String clienteCognome,
			String clienteEmail,
			String tipoPagamento,
			String tipoSpedizione,
			String statoFattura,
			Integer idOrdine,
			List<String> isbns
			) {
		log.debug("Fattura list()");
		Specification<Fattura> spec = Specification
				.where(FatturaSpecifications.dataEmissioneBetween(from, to))
				.or(FatturaSpecifications.numeroFatturaLike(numeroFattura))
				.or(FatturaSpecifications.clienteNomeLike(clienteNome))
				.or(FatturaSpecifications.clienteEmailLike(clienteEmail))
				.or(FatturaSpecifications.tipoPagamentoEquals(tipoPagamento))
				.or(FatturaSpecifications.tipoSpedizioneEquals(tipoSpedizione))
				.or(FatturaSpecifications.statoFatturaEquals(statoFattura))
				.or(FatturaSpecifications.idOrdineEquals(idOrdine))
				.or(FatturaSpecifications.idOrdineEquals(idOrdine))
				.or(FatturaSpecifications.anyMangaIsbns(isbns));
        List<Fattura> lF = fattR.findAll(spec);
        return lF.stream()
                .map(f -> DtoBuilders.buildFatturaDTO(f, Optional.empty(), Optional.empty()))
                .collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public FatturaDTO findById(Integer id) throws MangaException {
		log.debug("Fattura findById({})", id);

        if (id == null)
            throw new MangaException("null_fat");

        Fattura fat = fattR.findById(id).orElseThrow(() ->
                new MangaException("!exists_fat"));
        return DtoBuilders.buildFatturaDTO(fat, Optional.ofNullable(fat.getRighe()), Optional.ofNullable(fat.getOrdine()));
    }
	
	
	// FATTURE DA ORDINE
	
	private Fattura mirrorAnagrafica(Anagrafica ana, Fattura f) {
		f.setClienteNome(ana.getNome());
		f.setClienteCognome(ana.getCognome());
		f.setClienteIndirizzo(ana.getVia());
		f.setClienteCap(ana.getCap());
		f.setClienteStato(ana.getStato());
		f.setClienteCitta(ana.getCitta());
		f.setClienteProvincia(ana.getProvincia());
		return f;
	}


	// si assicura che ci sia sempre una fattura collegata ad un ordine
	@Transactional(rollbackFor = Exception.class)
	public Fattura getOrCreateFromOrdine(Ordine o) {
		log.debug("Checking if fattura corresponding to ordine id: {} exists.", o.getId());

		return fattR.findByOrdineId(o.getId())
			.orElseGet(() -> createFromOrdine(o));
	}
 
	@Transactional(rollbackFor = Exception.class)
	public Fattura createFromOrdine(Ordine o) throws MangaException {
		log.debug("Creating fattura from ordine");
		Fattura f = new Fattura();
		f.setOrdine(o);
		f.setDataEmissione(LocalDate.now());
		f.setNumeroFattura(generateNumeroFattura(o.getId()));
 
		
		// mirroring dati account e anagrafica
		Account acc = o.getAccount();
		f.setClienteEmail(acc.getEmail());
		
		Anagrafica ana = o.getAnagrafica();
		f = mirrorAnagrafica(ana, f);
		
		// mirroring dati pagamento e spedizione
		f.setTipoPagamento(o.getTipoPagamento().getTipoPagamento());
		f.setTipoSpedizione(o.getTipoSpedizione().getTipoSpedizione());
		f.setCostoSpedizione(o.getTipoSpedizione().getCostoSpedizione());
		
		// fattura eredita stato da ordine
		f.setStatoFattura(o.getStato().getStatoOrdine());
		fattR.save(f);

		List<RigaOrdine> lR = rigoR.findAllByOrdineId(o.getId());
		rigfS.righeFatturaFromRigheOrdine(lR, f);
		return f;
	}

	
	// chiamato per tenere traccia cambiamenti stato ordine
	// sincronizza fattura a ordine
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFromOrdine(Ordine o, String nuovoStatoOrdine, Boolean ripristinaCopie) throws MangaException {
		Fattura fat = getOrCreateFromOrdine(o);
		fat.setDataEmissione(LocalDate.now());
	 
		// Map ordine state to fattura state
		String nuovoStatoFattura = switch (nuovoStatoOrdine) {
			case "CANCELLATO" -> "ANNULLATA";
			default -> nuovoStatoOrdine;
		};
	 
		advanceStatoFattura(fat.getId(), nuovoStatoFattura, ripristinaCopie);
	}
	

	// quando ordine cancellato da DB
	@Transactional(rollbackFor = Exception.class)
	public void detachFromOrdine(Ordine o, String note) {
		Optional<Fattura> opt = fattR.findByOrdineId(o.getId());
		if (opt.isPresent()) {
			Fattura fat = opt.get();
			fat.setOrdine(null);
			fat.setNote(note);
			fattR.save(fat);
		} else {
			Fattura fat = getOrCreateFromOrdine(o);
			fat.setDataEmissione(LocalDate.now());
			fat.setOrdine(null);
			fat.setNote(note);
			fattR.save(fat);
		}
	}	
	//PIPELINE STATI FATTURA

	// mappa di transizion possibili
	private static final Map<String, List<String>> ALLOWED_TRANSITIONS = Map.of(
			"CREATO",          List.of("PAGATO", "CANCELLATO"),
			"PAGATO",          List.of("LAVORAZIONE", "CANCELLATO"),
			"LAVORAZIONE",     List.of("SPEDITO"),
			"SPEDITO",         List.of("CONSEGNATO"),
			"CONSEGNATO",      List.of("RICHIESTA_RESO"),
			"RICHIESTA_RESO",  List.of("RESTITUITO", "RIFIUTATO"),
			"RESTITUITO",      List.of("RIMBORSATO"),
			"ANNULLATA",       List.of()
		);
	 
		private void validateTransition(String from, String to) {
			List<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, List.of());
			if (!allowed.contains(to))
				throw new MangaException("stato_fat_invalid");
		}
	 

	// unico metodo che agisce su stato fattura e fa check per ripristino copie
	@Transactional(rollbackFor = Exception.class)
	public void advanceStatoFattura(Integer fatturaId, String nuovoStato, Boolean ripristinaCopie) {
		Fattura fat = load(fatturaId);
		String current = fat.getStatoFattura();
		log.debug("advanceStatoFattura: {} → {}, ripristinaCopie={}", current, nuovoStato, ripristinaCopie);
 
		// 1. Validazione
		validateTransition(current, nuovoStato);
 
		// 2. ripristino copie
		if (Boolean.TRUE.equals(ripristinaCopie)) {
			mangS.ripristinaNumeroCopie(fat);
		}
 
		// 3. ricalcolo totale
		switch (nuovoStato) {
			case "RIMBORSATO" -> {
				if (Boolean.TRUE.equals(ripristinaCopie)) {
					fat.setTotale(BigDecimal.ZERO);
				} else {
					// se copie non ripristinate, fattura = - prezzo
					fat.setTotale(fat.getTotale().negate());
				}
			}
			case "ANNULLATA" -> fat.setTotale(BigDecimal.ZERO);
			default -> {} // no cambiamenti
		}
 
		// 4. imposto nuovo stato
		fat.setStatoFattura(nuovoStato);
		fattR.save(fat);
	}
 
	// Overload: ripristinaCopie = false
	@Transactional(rollbackFor = Exception.class)
	public void advanceStatoFattura(Integer fatturaId, String nuovoStato) {
		advanceStatoFattura(fatturaId, nuovoStato, false);
	}

	// PIPELINE PER RESO:
	// RICHIESTA RESO
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void iniziaReso(Integer fatturaId, Integer accountId) throws MangaException {
        Fattura fat = load(fatturaId);
        if (!fat.getOrdine().getAccount().getId().equals(accountId))
            throw new MangaException("wrong_acc_ana");
        
        long days = ChronoUnit.DAYS.between(
        		fat.getDataEmissione(), LocalDate.now());
        
        if (days > 30) {
        	throw new MangaException("reso_scad");
        }
		advanceStatoFattura(fatturaId, "RICHIESTA_RESO");
    }
	
    // Admin: RESO RESPINTO
    public void rifiutaReso(Integer fatturaId) {
		advanceStatoFattura(fatturaId, "RIFIUTATO");
    }
    
    // Admin: 
    public void confermaReso(Integer fatturaId) {
		advanceStatoFattura(fatturaId, "RESTITUITO");
    }

    // Admin: REINTEGRO OGGETTO RESO, RIMBORSO, REINTEGRO COPIE SE ripristina
    public void rimborsa(Integer fatturaId, Boolean ripristina) {
		advanceStatoFattura(fatturaId, "RIMBORSATO", ripristina);
    }


    private Fattura load(Integer id) {
        return fattR.findById(id)
            .orElseThrow(() -> new MangaException("!exists_fat"));
    }

}
