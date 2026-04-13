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
import org.springframework.security.core.Authentication;
import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.inputs.RigaFatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IRigaFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
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
public class FatturaImplementation implements IFatturaServices {
	private final IOrdineRepository ordeR;
	private final IRigaOrdineRepository rigoR;
	private final IFatturaRepository fattR;
	private final ITipoPagamentoRepository pagR;
	private final IStatoOrdineRepository staR;
	private final ITipoSpedizioneRepository spedR;
	private final IAccountRepository accountRepository;
	private final IRigaFatturaRepository rigfR;
	private final IRigaFatturaServices rigfS;
	private final IMangaServices mangS;
 
	// ================================================================
	// HELPERS
	// ================================================================
 
	public Boolean isAdminOrOwner(Authentication auth, Integer targetFatturaId) {
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		if (isAdmin) return true;
 
		Fattura targetFattura = fattR.findById(targetFatturaId).orElse(null);
		if (targetFattura == null) return false;
 
		Ordine targetOrd = targetFattura.getOrdine();
		if (targetOrd == null) return false;
 
		Integer targetAccountId = targetOrd.getAccount().getId();
		Account loggedAccount = accountRepository.findByUsername(auth.getName()).orElse(null);
		return loggedAccount != null && loggedAccount.getId().equals(targetAccountId);
	}
 
	private String generateNumeroFattura(Integer idOrdine) {
		Boolean exists = true;
		String numFattura = "FAT-" + idOrdine;
		while (exists) {
			String trial = numFattura + "-" + UUID.randomUUID()
				.toString().substring(0, 8);
			if (fattR.findByNumeroFattura(trial).isEmpty()) {
				numFattura = trial;
				exists = false;
			}
		}
		return numFattura;
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
 
	private Fattura load(Integer id) {
		return fattR.findById(id)
			.orElseThrow(() -> new MangaException("!exists_fat"));
	}
 
	// ================================================================
	// STATO FATTURA — MAPPA TRANSIZIONI PERMESSE
	// ================================================================
 
	private static final Map<String, List<String>> ALLOWED_TRANSITIONS = Map.ofEntries(
		    Map.entry("CREATO",          List.of("PAGATO")),
		    Map.entry("PAGATO",          List.of("LAVORAZIONE", "ANNULLATA")),
		    Map.entry("LAVORAZIONE",     List.of("SPEDITO")),
		    Map.entry("SPEDITO",         List.of("CONSEGNATO")),
		    Map.entry("CONSEGNATO",      List.of("CONFERMATO", "RICHIESTA_RESO")),
		    Map.entry("CONFERMATO",      List.of()),
		    Map.entry("RICHIESTA_RESO",  List.of("RICONSEGNATO", "RIFIUTATO")),
		    Map.entry("RICONSEGNATO",    List.of("RIMBORSATO")),
		    Map.entry("RIMBORSATO",      List.of()),
		    Map.entry("RIFIUTATO",       List.of()),
		    Map.entry("ANNULLATA",       List.of())
		);
	
	// metodo per vedere se nuovo stato è permesso
	private void validateTransition(String from, String to) {
		List<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, List.of());
		if (!allowed.contains(to))
			throw new MangaException("stato_fat_invalid");
	}
 
	// ================================================================
	// advanceStatoFattura — UNICO metodo che fa avanzare statoFattura
	// ================================================================
 
	@Transactional(rollbackFor = Exception.class)
	public void advanceStatoFattura(Integer fatturaId, String nuovoStato, Boolean ripristinaCopie) {
		Fattura fat = load(fatturaId);
		String current = fat.getStatoFattura();
		log.debug("advanceStatoFattura: {} → {}, ripristinaCopie={}", current, nuovoStato, ripristinaCopie);
 
		// 1. Valida transizione
		validateTransition(current, nuovoStato);
 
		// 2. ripristino copie (unico punto, verifica se ordine cancellato prima di pagamento ripristina)
		if (Boolean.TRUE.equals(ripristinaCopie)) {
			mangS.ripristinaNumeroCopie(fat);
		}
 
		// 3. Totale + data per stato
		switch (nuovoStato) {
			case "RIMBORSATO" -> {
				if (Boolean.TRUE.equals(ripristinaCopie)) {
					fat.setTotale(BigDecimal.ZERO);
				} else {
					// rimborso senza manga resi → totale negativo 
					fat.setTotale(fat.getTotale().negate());
				}
				// setto data a data rimborso
				fat.setDataEmissione(LocalDate.now()); 
			}
			case "ANNULLATA" -> {
				fat.setTotale(BigDecimal.ZERO);
				 // setto data a data annullamento
				fat.setDataEmissione(LocalDate.now());
			}
			case "RICONSEGNATO" -> {
				// data riconsegna
				fat.setDataEmissione(LocalDate.now()); 
			}
			default -> {}
		}
 
		// 4. aggiorno stato
		fat.setStatoFattura(nuovoStato);
		fattR.save(fat);
 
		// 5. aggiorno stato ordine (per avere stato ordine corrispondente, tutte funzioni reso vengono fatte esclusivamente su fattura)
		updateOrdineFromFattura(fat, nuovoStato);
	}
 
	@Transactional(rollbackFor = Exception.class)
	public void advanceStatoFattura(Integer fatturaId, String nuovoStato) {
		advanceStatoFattura(fatturaId, nuovoStato, false);
	}
 
	// ================================================================
	// updateOrdineFromFattura — per mantenere stato ordine aggiornato anche a livello di reso
	// Fattura adibita a meccanismo reso, per ordine solo mirroriing dati
	// ================================================================
 
	private void updateOrdineFromFattura(Fattura fat, String nuovoStatoFattura) {
		 // fattura scollegata, no ordine (account eliminato/cancellato manualmente)
		if (fat.getOrdine() == null) return;
 
		Ordine o = fat.getOrdine();
		if (List.of("RIFIUTATO", "RIMBORSATO", "CONFERMATO").contains(nuovoStatoFattura)) {
			StatoOrdine stat = staR.findByStatoOrdine(nuovoStatoFattura)
				.orElseThrow(() -> new MangaException("!exists_sta"));
			o.setStato(stat);
			ordeR.save(o);
			log.debug("Stato ordine con id {} aggiornato a {} mediante fattura reso ", o.getId(), nuovoStatoFattura);
		}
	}
 
	// ================================================================
	// autoConfirmExpired: controllo se tempo reso scaduto da fattura CONSEGNATO
	// controlla se son passti più di 30 gg da data consegna
	// ================================================================
 
	@Transactional(rollbackFor = Exception.class)
	public void autoConfirmExpired() {
		// Find all fatture in CONSEGNATO where 30+ days have passed
		List<Fattura> expired = fattR.findAllByStatoFattura("CONSEGNATO").stream()
			.filter(f -> {
				if (f.getDataEmissione() == null) return false;
				// calcolo giorni da consegna
				Long days = ChronoUnit.DAYS.between(f.getDataEmissione(), LocalDate.now());
				return days > 30;
			})
			.toList();
 
		for (Fattura f : expired) {
			log.debug("Fattura confermata autenticamente {} (finestra di reso scaduta)", f.getId());
			advanceStatoFattura(f.getId(), "CONFERMATO");
		}
	}
  
	// ================================================================
	// RESO PIPELINE
	// ================================================================
 
	// Unico metodo User: inizia reso (entro 30 gg da CONSEGNATO)
	// NW advanceStatoFattura chiama updateOrdineFromFattura
	//    che fa mirroring stato su ordine quando necessrio
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void iniziaReso(Integer fatturaId, Integer accountId) throws MangaException {
		Fattura fat = load(fatturaId);
 
		// controllo fattura corrisponde ad account 
		if (fat.getOrdine() == null || !fat.getOrdine().getAccount().getId().equals(accountId))
			throw new MangaException("wrong_acc_ana");
 
		// controllo se reso ancora possibile
		long days = ChronoUnit.DAYS.between(fat.getDataEmissione(), LocalDate.now());
		if (days > 30) {
			// reso scaduto, setto fattura a confermata
			advanceStatoFattura(fatturaId, "CONFERMATO");
			throw new MangaException("reso_scad");
		}
 
		advanceStatoFattura(fatturaId, "RICHIESTA_RESO");
	}
 
	// Admin: reso respinto — manga a cliente, totale e n copie invariato
	public void rifiutaReso(Integer fatturaId) {
		advanceStatoFattura(fatturaId, "RIFIUTATO");
	}
 
	// Admin: manga restituito (ancora da valutare condizioni ripristino copia/rimborso)
	public void confermaRiconsegna(Integer fatturaId) {
		advanceStatoFattura(fatturaId, "RICONSEGNATO");
	}
 
	// Admin: Rimborso (Boolean ripristina: manga rivendibile, copie ripristinate)
	public void rimborsa(Integer fatturaId, Boolean ripristina) {
		advanceStatoFattura(fatturaId, "RIMBORSATO", ripristina);
	}
 
	// ================================================================
	// SINCRONIZZAZIONE ORDINE <-> FATTURA 
	// ================================================================
 
	// Si assicura che ad ogni ordine corrisponda una fattura (crea una se assente)
	@Transactional(rollbackFor = Exception.class)
	public Fattura getOrCreateFromOrdine(Ordine o) {
		log.debug("Controllo se fattura per ordine id: {} esiste", o.getId());
		return fattR.findByOrdineId(o.getId())
			.orElseGet(() -> createFromOrdine(o));
	}
 
	// crea fattura da ordine
	@Transactional(rollbackFor = Exception.class)
	public Fattura createFromOrdine(Ordine o) throws MangaException {
		log.debug("CreO fattura DA ordine con id {}", o.getId());
		Fattura f = new Fattura();
		f.setOrdine(o);
		f.setDataEmissione(LocalDate.now());
		f.setNumeroFattura(generateNumeroFattura(o.getId()));
 
		// Mirror account + anagrafica
		Account acc = o.getAccount();
		f.setClienteEmail(acc.getEmail());
		f = mirrorAnagrafica(o.getAnagrafica(), f);
 
		// Mirror pagamento e spedizione
		f.setTipoPagamento(o.getTipoPagamento().getTipoPagamento());
		f.setTipoSpedizione(o.getTipoSpedizione().getTipoSpedizione());
		f.setCostoSpedizione(o.getTipoSpedizione().getCostoSpedizione());
 
		// Fattura eredita statoordine
		f.setStatoFattura(o.getStato().getStatoOrdine());
		fattR.save(f);
 
		// Copia righe ordine -> righe fattura
		List<RigaOrdine> lR = rigoR.findAllByOrdineId(o.getId());
		rigfS.righeFatturaFromRigheOrdine(lR, f);
		return f;
	}
 
	// da OrdineImpl qundo stato ordine avanza
	// Crea fattura a PAGATO, altrimenti mirror stato
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatoFromOrdine(Ordine o, String nuovoStatoOrdine, Boolean ripristinaCopie) throws MangaException {
		String nuovoStatoFattura = switch (nuovoStatoOrdine) {
			case "CANCELLATO" -> "ANNULLATA";
			default -> nuovoStatoOrdine;
		};
 
		if ("ANNULLATA".equals(nuovoStatoFattura)) {
			// controllo se fattura esiste prima
			// (altrimenti transizione CREATO (no fattura)-> crea fattura e annulla)
			Optional<Fattura> opt = fattR.findByOrdineId(o.getId());
			if (opt.isPresent()) {
				opt.get().setDataEmissione(LocalDate.now());
				advanceStatoFattura(opt.get().getId(), "ANNULLATA", ripristinaCopie);
			}
			return;
		}
 
		// per tutti altri stati 
		// 1-trova o crea fattura (fattura creata a PAGATO)
		Fattura fat = getOrCreateFromOrdine(o);
		// 2-aggiorna data
		fat.setDataEmissione(LocalDate.now());
		Integer myId = fattR.save(fat).getId();
		// 3 - salvo (fattura creata in pagato) poi avanzo stato
		advanceStatoFattura(myId, nuovoStatoFattura, ripristinaCopie);
	}
 
	// se ordine cancellato da DB devo staccare fattura (id ordine = null)
	@Transactional(rollbackFor = Exception.class)
	public void detachFromOrdine(Ordine o, String note) {
		Optional<Fattura> opt = fattR.findByOrdineId(o.getId());
		if (opt.isPresent()) {
			Fattura fat = opt.get();
			fat.setOrdine(null);
			fat.setNote(note);
			fattR.save(fat);
		} else {
			// Crea fattura come record di ordine prima della sua rimozione
			Fattura fat = createFromOrdine(o);
			fat.setOrdine(null);
			fat.setNote(note);
			fattR.save(fat);
		}
	}
 
	// ================================================================
	// CRUD — admin endpoints
	// ================================================================
 
	@Override
	@Transactional(rollbackFor = MangaException.class)
	public void create(FatturaRequest req) throws MangaException {
		log.debug("Create Fattura: {}", req);
 
		// Client snapshot validation
		if (Utils.isBlank(req.getClienteNome()))      throw new MangaException("null_nom");
		if (Utils.isBlank(req.getClienteCognome()))    throw new MangaException("null_cog");
		if (Utils.isBlank(req.getClienteEmail()))      throw new MangaException("null_ema");
		if (Utils.isBlank(req.getClienteIndirizzo()))  throw new MangaException("null_ind");
		if (Utils.isBlank(req.getClienteCitta()))      throw new MangaException("null_cit");
		if (Utils.isBlank(req.getClienteCap()))        throw new MangaException("null_cap");
		if (Utils.isBlank(req.getClienteProvincia()))  throw new MangaException("null_pro");
		if (Utils.isBlank(req.getClienteStato()))      throw new MangaException("null_sta");
		if (req.getTipoPagamentoId() == null)           throw new MangaException("null_pag");
		if (req.getTipoSpedizioneId() == null)          throw new MangaException("null_spe");
 
		Fattura fat = new Fattura();
 
		// snapshot Cliente
		fat.setClienteNome(Utils.normalize(req.getClienteNome()));
		fat.setClienteCognome(Utils.normalize(req.getClienteCognome()));
		fat.setClienteEmail(Utils.normalize(req.getClienteEmail()));
		fat.setClienteIndirizzo(Utils.normalize(req.getClienteIndirizzo()));
		fat.setClienteCitta(Utils.normalize(req.getClienteCitta()));
		fat.setClienteCap(Utils.normalize(req.getClienteCap()));
		fat.setClienteProvincia(Utils.normalize(req.getClienteProvincia()));
		fat.setClienteStato(Utils.normalize(req.getClienteStato()));
 
		// pagamento + spedizione
		TipoPagamento pag = pagR.findById(req.getTipoPagamentoId())
			.orElseThrow(() -> new MangaException("!exists_pag"));
		fat.setTipoPagamento(pag.getTipoPagamento());
 
		TipoSpedizione spe = spedR.findById(req.getTipoSpedizioneId())
			.orElseThrow(() -> new MangaException("!exists_spe"));
		fat.setTipoSpedizione(spe.getTipoSpedizione());
		fat.setCostoSpedizione(spe.getCostoSpedizione());
		fat.setTotale(fat.getCostoSpedizione());
 
		// Collegamento a ordine se id != null
		// copie già bloccate da creazione ordine
		// altrimenti verranno rimosse al momento della creazione 
		// di righefattura
		if (req.getOrdineId() != null) {
			Ordine ord = ordeR.findById(req.getOrdineId())
				.orElseThrow(() -> new MangaException("!exists_ord"));
			fat.setOrdine(ord);
			fat.setStatoFattura(ord.getStato().getStatoOrdine());
		}
 
		fat.setDataEmissione(LocalDate.now());
		String numFattura = generateNumeroFattura(
			req.getOrdineId() != null ? req.getOrdineId() : 0);
		fat.setNumeroFattura(numFattura);
		fat.setNote(req.getNote());
 
		Fattura f = fattR.save(fat);
		Integer myId = f.getId();
 
		// righe fattura
		if (req.getRigheFatturaRequest() != null && !req.getRigheFatturaRequest().isEmpty()) {
			for (RigaFatturaRequest r : req.getRigheFatturaRequest()) {
				r.setIdFattura(myId);
				r.setNumeroCopie(r.getNumeroCopie() != null ? r.getNumeroCopie() : 1);
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
		log.debug("update Fattura {}", req);
 
		Fattura fat = fattR.findById(req.getId())
			.orElseThrow(() -> new MangaException("!exists_fat"));
 
		// Update di snapshot cliente
		if (!Utils.isBlank(req.getClienteNome())) {
			fat.setClienteNome(Utils.normalize(req.getClienteNome()));
		}
		if (!Utils.isBlank(req.getClienteCognome())) {
			fat.setClienteCognome(Utils.normalize(req.getClienteCognome()));
		}
		if (!Utils.isBlank(req.getClienteEmail())) {
			fat.setClienteEmail(Utils.normalize(req.getClienteEmail()));
		}
		if (!Utils.isBlank(req.getClienteIndirizzo())) {
			fat.setClienteIndirizzo(Utils.normalize(req.getClienteIndirizzo()));
		}
		if (!Utils.isBlank(req.getClienteCitta())) {
			fat.setClienteCitta(Utils.normalize(req.getClienteCitta()));
		}
		if (!Utils.isBlank(req.getClienteCap())) {
			fat.setClienteCap(Utils.normalize(req.getClienteCap()));
		}
		if (!Utils.isBlank(req.getClienteProvincia())) {
			fat.setClienteProvincia(Utils.normalize(req.getClienteProvincia()));
		}
		if (!Utils.isBlank(req.getClienteStato())) {
			fat.setClienteStato(Utils.normalize(req.getClienteStato()));
		}
 
		// pagamento
		if (req.getTipoPagamentoId() != null) {
			TipoPagamento pag = pagR.findById(req.getTipoPagamentoId())
				.orElseThrow(() -> new MangaException("!exists_pag"));
			fat.setTipoPagamento(pag.getTipoPagamento());
		}
 
		// spedizione
		if (req.getTipoSpedizioneId() != null) {
			TipoSpedizione spe = spedR.findById(req.getTipoSpedizioneId())
				.orElseThrow(() -> new MangaException("!exists_spe"));
			fat.setTipoSpedizione(spe.getTipoSpedizione());
			fat.setCostoSpedizione(spe.getCostoSpedizione());
			Utils.ricalcolaTotale(fat);
		}
 
		// Conntrolla associazione ad ordine e in caso ricalcola righe
		if (req.getOrdineId() != null) {
			Ordine ord = ordeR.findById(req.getOrdineId())
				.orElseThrow(() -> new MangaException("!exists_ord"));
			fat.setStatoFattura(ord.getStato().getStatoOrdine());
			List<RigaOrdine> lR = rigoR.findAllByOrdineId(ord.getId());
			rigfS.righeFatturaFromRigheOrdine(lR, fat);
		}
 
		if (!Utils.isBlank(req.getNote()))
			fat.setNote(req.getNote());
 
		// righe fattura
		Integer myId = fat.getId();
		if (req.getRigheFatturaRequest() != null && !req.getRigheFatturaRequest().isEmpty()) {
			for (RigaFatturaRequest r : req.getRigheFatturaRequest()) {
				r.setIdFattura(myId);
				r.setNumeroCopie(r.getNumeroCopie() != null ? r.getNumeroCopie() : 1);
				saveOrUpdateRigaFattura(fat, r);
			}
			fat.setRighe(rigfR.findAllByFatturaId(myId));
			Utils.ricalcolaTotale(fat);
		}
		fattR.save(fat);
	}
 
	// metodo per aggiungere righe fattura a fattura durante 
	// create/update delle fatture
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdateRigaFattura(Fattura f, RigaFatturaRequest r) {
		List<String> isbns = f.getRighe().stream()
			.map(rf -> rf.getIsbn()).toList();
		
		if (isbns.contains(r.getIsbn())) {
			rigfS.update(r);
			return;
		}
		rigfS.create(r);
	}
 
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("removing Fattura with id {}", id);
		if (id == null) throw new MangaException("null_fat");
		Fattura fat = fattR.findById(id)
			.orElseThrow(() -> new MangaException("!exists_fat"));
		fattR.delete(fat);
	}
 
	@Override
	@Transactional(readOnly = true)
	public List<FatturaDTO> list(
			String numeroFattura, LocalDate from, LocalDate to,
			String clienteNome, String clienteCognome, String clienteEmail,
			String tipoPagamento, String tipoSpedizione, String statoFattura,
			Integer idOrdine, List<String> isbns) {
		log.debug("Fattura list()");
 
		// ricontrollo presenza fatture stato CONSEGNATO 
		// con finestra reso scaduta
		autoConfirmExpired();
 
		Specification<Fattura> spec = Specification
			.where(FatturaSpecifications.dataEmissioneBetween(from, to))
			.and(FatturaSpecifications.numeroFatturaLike(numeroFattura))
			.and(FatturaSpecifications.clienteNomeLike(clienteNome))
			.and(FatturaSpecifications.clienteCognomeLike(clienteCognome))
			.and(FatturaSpecifications.clienteEmailLike(clienteEmail))
			.and(FatturaSpecifications.tipoPagamentoEquals(tipoPagamento))
			.and(FatturaSpecifications.tipoSpedizioneEquals(tipoSpedizione))
			.and(FatturaSpecifications.statoFatturaEquals(statoFattura))
			.and(FatturaSpecifications.idOrdineEquals(idOrdine))
			.and(FatturaSpecifications.anyMangaIsbns(isbns));
		List<Fattura> lF = fattR.findAll(spec);
		return lF.stream()
			.map(f -> DtoBuilders.buildFatturaDTO(f, Optional.empty(), Optional.empty()))
			.collect(Collectors.toList());
	}
 
	@Override
	public FatturaDTO findById(Integer id) throws MangaException {
		log.debug("Fattura findById({})", id);
		if (id == null) throw new MangaException("null_fat");
 
		Fattura fat = load(id);
		return DtoBuilders.buildFatturaDTO(fat,
			Optional.ofNullable(fat.getRighe()),
			Optional.ofNullable(fat.getOrdine()));
	}
 
	@Override
	public List<FatturaDTO> listByAccountId(Integer accountId) throws Exception {
		// ricontrollo presenza fatture stato CONSEGNATO 
		// con finestra reso scaduta
		autoConfirmExpired();
 
		List<Fattura> lista = fattR.findAllByAccountId(accountId);
		return lista.stream()
			.map(f -> DtoBuilders.buildFatturaDTO(
				f,
				Optional.ofNullable(f.getRighe()),
				Optional.ofNullable(f.getOrdine())
			))
			.collect(Collectors.toList());
	}
 
	// prossimi stati permessi a fattura (frontend)
	public List<StatoOrdineDTO> getNextAllowedStates(Integer fatturaId) throws MangaException {
		Fattura f = load(fatturaId);
  
		String sta = f.getStatoFattura();
		List<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(sta, List.of());
 
		List<StatoOrdineDTO> lS = allowed.stream()
			.map(s -> {
				// Mappo unico stato solo di fattura a stato in DB
				// ANNULLATA -> CANCELLATO
				String dbStateName = "ANNULLATA".equals(s) ? "CANCELLATO" : s;
				return staR.findByStatoOrdine(dbStateName);
			})
			.filter(s -> s.isPresent())
			.map(opt -> DtoBuilders.buildStatoOrdineDTO(opt.get()))
			.toList();
 
		log.debug("Stato Fattura input: {}", sta);
		lS.forEach(s -> log.debug("\tpermessi: {}", s));
		return lS;
	}
}
 