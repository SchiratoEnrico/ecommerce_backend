package com.betacom.ecommerce.backend.services.implementations;

import java.time.LocalDate;
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
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaCarrello;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.IAnagraficaRepository;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
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
public class OrdineImplemetation implements IOrdineServices {
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
	private final ICarrelloRepository carrR;

	// ================================================================
	// STATO ORDINE — MAPPA TRANSIZIONI PERMESSE
	// NW QUI SOLO:
	// - CREATO → CONSEGNATO + CANCELLATO
	// meccanismo di reso (stati RIFIUTATO, RIMBORSATO, CONFERMATO) solo in fattura (mirroring qui)
	// ================================================================

	private static final java.util.Map<String, List<String>> ALLOWED_ORDINE = java.util.Map.ofEntries(
		java.util.Map.entry("CREATO",          List.of("PAGATO", "CANCELLATO")),
		java.util.Map.entry("PAGATO",          List.of("LAVORAZIONE", "CANCELLATO")),
		java.util.Map.entry("LAVORAZIONE",     List.of("SPEDITO")),
		java.util.Map.entry("SPEDITO",         List.of("CONSEGNATO")),
		java.util.Map.entry("CONSEGNATO",      List.of("RICHIESTA_RESO", "CONFERMATO")),
		java.util.Map.entry("RICHIESTA_RESO",  List.of("RIFIUTATO")),   // da fattura, non raggiungibile direttamente
		java.util.Map.entry("CANCELLATO",      List.of()),              // stato terminale
		java.util.Map.entry("CONFERMATO",      List.of()),              // stato terminale: finestra reso scaduta
		java.util.Map.entry("RIFIUTATO",       List.of()),              // terminale: reso respinto
		java.util.Map.entry("RIMBORSATO",      List.of())               // terminale: rimborsato
	);

	private void validateOrdineTransition(String from, String to) {
		if ("CANCELLATO".equals(from))
			throw new MangaException("ord_canc");
		List<String> allowed = ALLOWED_ORDINE.getOrDefault(from, List.of());
		if (!allowed.contains(to))
			throw new MangaException("ord_transition_invalid");
	}

	// ================================================================
	// advanceStatoOrdine — metodo x cambi stato
	// Solo tranzizioni in avanti, reso solo da fattura.
	// ================================================================

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void advanceStatoOrdine(Integer ordineId, Integer statoId) throws MangaException {
		log.debug("advanceStatoOrdine ordineId={}, statoId={}", ordineId, statoId);

		if (ordineId == null) throw new MangaException("null_ord");
		if (statoId == null)  throw new MangaException("null_sta");

		Ordine o = ordeR.findById(ordineId)
			.orElseThrow(() -> new MangaException("!exists_ord"));

		StatoOrdine nuovoStato = statR.findById(statoId)
			.orElseThrow(() -> new MangaException("!exists_sta"));

		String current = o.getStato().getStatoOrdine();
		String target = nuovoStato.getStatoOrdine();
		log.debug("transizione ordine stato richiesta: {} → {}", current, target);

		// Valida transizione
		validateOrdineTransition(current, target);

		// sincronizzazioe di fattura
		switch (target) {
			case "PAGATO" ->
				// Fattura creata
				fattS.updateStatoFromOrdine(o, "PAGATO", false);

			case "LAVORAZIONE", "SPEDITO", "CONSEGNATO" ->
				// solo copia stato a fattura
				fattS.updateStatoFromOrdine(o, target, false);

			case "CANCELLATO" -> {
				// Copie sempre ripristinate (raggiungibile solo se CREATO/PAGATO)
				mangaS.ripristinaNumeroCopie(o);
				if (!current.equals("CREATO")) {
					// Annulla fattura se esiste (creata a PAGATO)
					fattS.updateStatoFromOrdine(o, "CANCELLATO", false);
				}
			}	

			case "RICHIESTA_RESO" -> {
				// chiama fattura reso pipeline (con 30-day check)
				fattS.iniziaReso(
					fattR.findByOrdineId(o.getId())
						.orElseThrow(() -> new MangaException("!exists_fat"))
						.getId(),
					o.getAccount().getId()
				);
			}

			default -> throw new MangaException("ord_transition_invalid");
		}

		// Aggiorna stato ordine dopo fattura update
		o.setStato(nuovoStato);
		ordeR.save(o);
	}

	// ================================================================
	// HELPERS
	// ================================================================

	private Anagrafica checkAnagrafica(Integer anagId, Integer accId) {
		Anagrafica ana = anaR.findById(anagId)
			.orElseThrow(() -> new MangaException("!exists_ana"));
		if (!ana.getAccount().getId().equals(accId))
			throw new MangaException("wrong_acc_ana");
		return ana;
	}

	public Ordine getUltimoPendente(Integer accountId) throws MangaException {
		return ordeR.findFirstByAccount_IdAndStato_StatoOrdineOrderByIdDesc(accountId, "CREATO")
			.orElseThrow(() -> new MangaException("pending_order_ntfnd"));
	}

	private RigaOrdineRequest rigaOrdineFromRigaCarrello(RigaCarrello rc) {
		return RigaOrdineRequest.builder()
			.manga(rc.getManga().getIsbn())
			.numeroCopie(rc.getNumeroCopie())
			.build();
	}

	// ================================================================
	// CREATE — stato CREATO, copie bloccate
	// ================================================================

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer create(OrdineRequest req) throws MangaException {
		log.debug("creating ordine {}", req);

		Ordine o = new Ordine();

		// Account
		if (req.getAccount() == null) throw new MangaException("null_acc");
		Account acc = accR.findById(req.getAccount())
			.orElseThrow(() -> new MangaException("!exists_acc"));
		o.setAccount(acc);

		// pagamento (ID)
		if (req.getPagamentoId() == null) throw new MangaException("null_pag");
		TipoPagamento pag = pagR.findById(req.getPagamentoId())
			.orElseThrow(() -> new MangaException("!exists_pag"));
		o.setTipoPagamento(pag);

		// spedizione (ID)
		if (req.getSpedizioneId() == null) throw new MangaException("null_spe");
		TipoSpedizione spe = speR.findById(req.getSpedizioneId())
			.orElseThrow(() -> new MangaException("!exists_spe"));
		o.setTipoSpedizione(spe);

		// Data
		if (req.getData() == null) throw new MangaException("null_dat");
		o.setData(req.getData());

		// Stato CREATO sempre
		StatoOrdine stat = statR.findByStatoOrdine("CREATO")
			.orElseThrow(() -> new MangaException("!exists_sta"));
		o.setStato(stat);

		// Anagrafica
		if (req.getAnagrafica() == null) throw new MangaException("null_ana");
		Anagrafica ana = checkAnagrafica(req.getAnagrafica(), req.getAccount());
		o.setAnagrafica(ana);

		Ordine savedOrdine = ordeR.save(o);

		// Crea righe ordine
		if (req.getRigheOrdineRequest() != null && !req.getRigheOrdineRequest().isEmpty()) {
			for (RigaOrdineRequest r : req.getRigheOrdineRequest()) {
				r.setIdOrdine(savedOrdine.getId());
				r.setNumeroCopie(r.getNumeroCopie() != null ? r.getNumeroCopie() : 1);
				rowS.create(r);
			}
		}

		Integer myId = savedOrdine.getId();
		savedOrdine.setRigheOrdine(rowR.findAllByOrdineId(myId));

		return myId;
	}

	// ================================================================
	// CREA DA CARRELLO — carrello -> ordine
	// ================================================================

	@Transactional(rollbackFor = Exception.class)
	public void createOrdineFromCarrello(Integer carrelloId, Integer anagraficaId, Integer tipoPagamentoId, Integer tipoSpedizioneId) throws MangaException {
		log.debug("createOrdineFromCarrello: carrelloId={}, anagraficaId={}, tipoPagamentoId={}, tipoSpedizioneId={}",
			carrelloId, anagraficaId, tipoPagamentoId, tipoSpedizioneId);

		Carrello carr = carrR.findById(carrelloId)
			.orElseThrow(() -> new MangaException("!exists_carr"));

		// se carrello vuoto => errore
		if (carr.getRigheCarrello().isEmpty())
			throw new MangaException("no_items");

		OrdineRequest ord = new OrdineRequest();
		ord.setAnagrafica(anagraficaId);
		ord.setSpedizioneId(tipoSpedizioneId);
		ord.setPagamentoId(tipoPagamentoId);
		ord.setAccount(carr.getAccount().getId());
		ord.setData(LocalDate.now());

		// Conversione righe carrello -> righe ordine
		List<RigaOrdineRequest> lR = carr.getRigheCarrello().stream()
			.map(rc -> rigaOrdineFromRigaCarrello(rc))
			.collect(Collectors.toList());
		ord.setRigheOrdineRequest(lR);

		create(ord);

		// rimuovi oggetti da carrello dopo creazione oridne
		carr.getRigheCarrello().clear();
		carrR.save(carr);
	}

	// ================================================================
	// UPDATE — SOLO DATI, NO STATO ORDINE (usa advanceStatoOrdine())
	// Ovvero: Account, pagamento, spedizione, data e righe ordine
	//
	// ================================================================

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void update(OrdineRequest req) throws MangaException {
		log.debug("updating ordine {}", req);

		if (req.getId() == null) throw new MangaException("null_ord");

		Ordine o = ordeR.findById(req.getId())
			.orElseThrow(() -> new MangaException("!exists_ord"));

		// Account
		if (req.getAccount() != null) {
			Account acc = accR.findById(req.getAccount())
				.orElseThrow(() -> new MangaException("!exists_acc"));
			o.setAccount(acc);
		}

		// pagamento (da ID)
		if (req.getPagamentoId() != null) {
			TipoPagamento pag = pagR.findById(req.getPagamentoId())
				.orElseThrow(() -> new MangaException("!exists_pag"));
			o.setTipoPagamento(pag);
		}

		// spedizione (da ID)
		if (req.getSpedizioneId() != null) {
			TipoSpedizione spe = speR.findById(req.getSpedizioneId())
				.orElseThrow(() -> new MangaException("!exists_spe"));
			o.setTipoSpedizione(spe);
		}

		// Data
		if (req.getData() != null)
			o.setData(req.getData());

		// Anagrafica (da ID) — usa sempre o.getId() (se req.getAccount() != null viene settato prima) 
		if (req.getAnagrafica() != null) {
			Integer accId = o.getAccount().getId();
			Anagrafica ana = checkAnagrafica(req.getAnagrafica(), accId);
			o.setAnagrafica(ana);
		}
		ordeR.save(o);
	}

	// ================================================================
	// DELETE — admin
	// ================================================================

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(Integer id, Boolean ripristinaCopie) throws MangaException {
		log.debug("Rimozione ordine con id {}", id);
		if (id == null) throw new MangaException("null_ord");

		Ordine o = ordeR.findById(id)
			.orElseThrow(() -> new MangaException("!exists_ord"));

		// Scollega fattura da ordine — (ordineId = null)
		fattS.detachFromOrdine(o, "Ordine eliminato");

		// se richiesto ripristina copie
		if (Boolean.TRUE.equals(ripristinaCopie)) {
			mangaS.ripristinaNumeroCopie(o);
		}
		ordeR.delete(o);
	}

	// ================================================================
	// metodi Get
	// ================================================================

	List<OrdineDTO> getDTOs(List<Ordine> lO) {
		return lO.stream()
			.map(o -> DtoBuilders.buildOrdineDTO(
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
			Integer anno, Integer mese, Integer giorno,
			StatoOrdineDTO stato,
			List<String> isbns) {

		log.debug("Ordine: list");

		Specification<Ordine> spec = Specification
			.where(OrdineSpecifications.accountUsernameLike(account != null ? account.getUsername() : null))
			.and(OrdineSpecifications.tipoPagamentoLike(tipoPagamento != null ? tipoPagamento.getTipoPagamento() : null))
			.and(OrdineSpecifications.tipoSpedizioneLike(tipoSpedizione != null ? tipoSpedizione.getTipoSpedizione() : null))
			.and(OrdineSpecifications.statoOrdineLike(stato != null ? stato.getStatoOrdine() : null))
			.and(OrdineSpecifications.hasAnyMangaIds(isbns))
			.and(OrdineSpecifications.meseAnnoEquals(giorno, mese, anno));

		return getDTOs(ordeR.findAll(spec));
	}

	@Override
	public OrdineDTO findById(Integer id) throws MangaException {
		log.debug("ordine findById({})", id);
		if (id == null) throw new MangaException("null_ord");

		Ordine o = ordeR.findById(id)
			.orElseThrow(() -> new MangaException("!exists_ord"));

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
		if (ordineOpt.isEmpty() || ordineOpt.get().getAccount() == null) return false;
		return ordineOpt.get().getAccount().getId().equals(accountId);
	}

	// ================================================================
	// FRONTEND HELPERS
	// ================================================================

	// Prossimi stati  permessi per ordine (bottoni frontend)
	// RIFIUTATO/RIMBORSATO/CONFERMATO -> da sincronizzazione con fattura, non qui
	public List<StatoOrdineDTO> getNextAllowedStates(Integer ordineId) throws MangaException {
		log.debug("getNextAllowedStates, ordineId: {}", ordineId);

		Ordine o = ordeR.findById(ordineId)
			.orElseThrow(() -> new MangaException("!exists_ord"));

		String sta = o.getStato().getStatoOrdine();
		List<String> allowed = ALLOWED_ORDINE.getOrDefault(sta, List.of());

		// NW: Non mostro stati di cui si occupa la fattura
		// NO STATI DI RESO (RIFIUTATO, RIMBORSATO, CONFERMATO)
		List<String> actionable = allowed.stream()
			.filter(s -> !List.of("RIFIUTATO", "RIMBORSATO", "CONFERMATO").contains(s))
			.toList();

		List<StatoOrdineDTO> lS = actionable.stream()
			.map(s -> statR.findByStatoOrdine(s))
			.filter(s -> s.isPresent())
			.map(opt -> DtoBuilders.buildStatoOrdineDTO(opt.get()))
			.toList();

		log.debug("Stato ordine: {}", sta);
		lS.forEach(s -> log.debug("\tpermesso: {}", s));
		return lS;
	}
}