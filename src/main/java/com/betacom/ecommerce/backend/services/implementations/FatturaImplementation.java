package com.betacom.ecommerce.backend.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
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
	private final IRigaFatturaServices rigfS;
	private final IMangaServices mangS;
	 
//    if (ChronoUnit.DAYS.between(fat.getDataEmissione(), LocalDate.now()) > 10)
//        throw new MangaException("reso_scaduto");
    
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
        if (Utils.isBlank(req.getTipoPagamento()))
            throw new MangaException("null_pag");

        if (Utils.isBlank(req.getTipoSpedizione()))
            throw new MangaException("null_spe");

        if (req.getRigheFatturaRequest() == null || req.getRigheFatturaRequest().isEmpty())
            throw new MangaException("null_rig_fat");

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
        fat.setTipoPagamento(Utils.normalize(req.getTipoPagamento()));
        fat.setTipoSpedizione(Utils.normalize(req.getTipoSpedizione()));
        Ordine ord = ordeR.findById(req.getOrdineId()).orElseThrow(()
        		-> new MangaException("!exists_ord"));
        
		String numFattura = generateNumeroFattura(ord.getId());
		log.debug("N fattura: {}", numFattura);
        fat.setNumeroFattura(numFattura);

        fat.setOrdine(ord);
        
        
        fat.setStatoFattura(ord.getStato().getStatoOrdine());
        
        // Costi spedizione prob dovremmo prenderceli da classe tiposped per automatizzare
        fat.setCostoSpedizione(req.getCostoSpedizione() != null 
        		? req.getCostoSpedizione()
                : BigDecimal.ZERO
        );
        fat.setTotale(fat.getCostoSpedizione()); // totale iniziale, le righe lo aggiorneranno
        fat.setNote(req.getNote());
        fattR.save(fat);
        
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
	        if (!Utils.isBlank(req.getTipoPagamento()))
	            fat.setTipoPagamento(Utils.normalize(req.getTipoPagamento()));

	        if (!Utils.isBlank(req.getTipoSpedizione()))
	            fat.setTipoSpedizione(Utils.normalize(req.getTipoSpedizione()));

	        // Costi
	        if (req.getCostoSpedizione() != null) {
	            fat.setCostoSpedizione(req.getCostoSpedizione());
	            Utils.ricalcolaTotale(fat); // aggiorna totale se cambia spedizione
	        }

	        if (!Utils.isBlank(req.getNote()))
	            fat.setNote(req.getNote());

	        fat.setStatoFattura("CREATO");
	        fat.setOrdine(null);
	        fattR.save(fat);
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

	// Trigger automatici quando statoOrdine SPEDITO -> CONSEGATO
	// 					e quando ordine viene rimosso
	@Transactional(rollbackFor = Exception.class)
	public void createFromOrdine(Ordine o, Boolean toDel) throws MangaException {
		Fattura f = new Fattura();
		if (toDel) {
			f.setOrdine(null);
			f.setNote("Order deleted");
		} else {
			f.setOrdine(o);
			f.setNote("Created from ordine");
		}
		f.setDataEmissione(LocalDate.now());

		String numFattura = generateNumeroFattura(o.getId());
		f.setNumeroFattura(numFattura);
		
		// mirroring dati account
		Account acc = o.getAccount();
		f.setClienteEmail(acc.getEmail());
		
		Anagrafica ana = o.getAnagrafica();
		f = mirrorAnagrafica(ana, f);
		
		f.setTipoPagamento(o.getTipoPagamento().getTipoPagamento());
		f.setTipoSpedizione(o.getTipoSpedizione().getTipoSpedizione());
		f.setCostoSpedizione(o.getTipoSpedizione().getCostoSpedizione());
		
		// fattura eredita stato da ordine
		f.setStatoFattura(o.getStato().getStatoOrdine());
		
		List<RigaOrdine> lR = rigoR.findAllByOrdineId(o.getId());
		fattR.save(f);
		rigfS.righeFatturaFromRigheOrdine(lR, f);
	}

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFromOrdine(Ordine o, Boolean toDel) throws MangaException {
		Integer id = o.getId();
		if (id == null) {
			throw new MangaException("null_ord");
		}
		
		if (fattR.existsByOrdineId(id)) {
            // fattura pre-created (account deleted before delivery)
            // update the emission date
			// check get new eventual account id
			
            Fattura f = fattR.findByOrdineId(o.getId())
                .orElseThrow(() -> new MangaException("!exists_fat"));
            f.setDataEmissione(LocalDate.now());
            if (toDel) {
            	f.setOrdine(null);
    			f.setNote("Order deleted");
    		}
            fattR.save(f);
        } else {
        	createFromOrdine(o, false);
        }		
	}

	// PIPELINE PER RESO:
	// RICHIESTA RESO
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void iniziaReso(Integer fatturaId, Integer accountId) throws MangaException {
        Fattura fat = load(fatturaId);
        if (!fat.getOrdine().getAccount().getId().equals(accountId))
            throw new MangaException("wrong_acc_ana");
        
        validateTransition(fat, "CONSEGNATO");
        long days = ChronoUnit.DAYS.between(
        		fat.getDataEmissione(), LocalDate.now());
        
        if (days > 30) {
        	throw new MangaException("reso_scad");
        }
        fat.setStatoFattura("RICHIESTA_RESO");
        fattR.save(fat);
    }
	
    // Admin: RESO RESPINTO
    public void rifiutaReso(Integer fatturaId) {
        Fattura fat = load(fatturaId);
        validateTransition(fat, "RICHIESTA_RESO");
        fat.setStatoFattura("RIFIUTATO");
        fattR.save(fat);
    }
    
    // Admin: 
    public void confermaReso(Integer fatturaId) {
        Fattura fat = load(fatturaId);
        validateTransition(fat, "RICHIESTA_RESO");
        fat.setStatoFattura("RESTITUITO");
        fattR.save(fat);
    }

    // Admin: REINTEGRO OGGETTO RESO, RIMBORSO, REINTEGRO COPIE SE ripristina
    public void rimborsa(Integer fatturaId, Boolean ripristina) {
        Fattura fat = load(fatturaId);
        validateTransition(fat, "RESTITUITO");
        if (Boolean.TRUE.equals(ripristina)) {
        	mangS.ripristinaNumeroCopie(fat);
        	fat.setTotale(BigDecimal.valueOf(0.0));
        } else {
        	// - totale siccome non ci sono tornati i manga
        	BigDecimal tot = fat.getTotale().multiply(BigDecimal.valueOf(-1));
        	fat.setTotale(tot);
        }
        //
        fat.setStatoFattura("RIMBORSATO");
        fattR.save(fat);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rimborsaNonConsegnato(Ordine o, Boolean toDel) throws MangaException {
    	String current = o.getStato().getStatoOrdine();
    	// arrivo con update stato ordine-> cancellato
        // QUI ripristino copie solo se non spedito/consegnato
        if (!List.of("SPEDITO", "CONSEGNATO").contains(current)) {
            mangS.ripristinaNumeroCopie(o);
        }
        
        // se fattura già presente, setto come annullata
        if (fattR.existsByOrdineId(o.getId())) {
            Fattura f = fattR.findByOrdineId(o.getId())
                .orElseThrow(() -> new MangaException("!exists_fat"));
            f.setStatoFattura("ANNULLATA");
            if (Boolean.TRUE.equals(toDel)) {
                f.setOrdine(null);
                f.setNote("Ordine cancellato");
            }
            fattR.save(f);
        }
        // rimborso
    }

    private Fattura load(Integer id) {
        return fattR.findById(id)
            .orElseThrow(() -> new MangaException("!exists_fat"));
    }

    private void validateTransition(Fattura fat, String allowedFrom) {
        String current = fat.getStatoFattura();
        if (!Objects.equals(current, allowedFrom))
            throw new MangaException("stato_fat_invalid");
    }

}
