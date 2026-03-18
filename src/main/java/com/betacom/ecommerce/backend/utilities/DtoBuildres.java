 package com.betacom.ecommerce.backend.utilities;

import java.util.stream.Collectors;

import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.dto.outputs.CasaEditriceDTO;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.dto.outputs.OrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.RigaOrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;

public class DtoBuildres {

	private static AccountDTO idOnly(Account a) {
	    return a == null ? 
	    		null : 
	    		AccountDTO.builder()
	    			.id(a.getId())
	    			.build();
	}
	
	private static AutoreDTO idOnly(Autore a) {
	    return a == null ? 
	    		null : 
	    		AutoreDTO.builder()
	    			.id(a.getId())
	    			.build();
	}

	private static TipoSpedizioneDTO idOnly(TipoSpedizione s) {
	    return s == null ? 
	    		null : 
	    		TipoSpedizioneDTO.builder()
	    			.id(s.getId())
	    			.build();	
	    }

	private static TipoPagamentoDTO idOnly(TipoPagamento p) {
	    return p == null ? 
	    		null : 
	    		TipoPagamentoDTO.builder()
	    			.id(p.getId())
	    			.build();	
	    }

	private static MangaDTO idOnly(Manga m) {
	    return m == null ? 
	    		null : 
	    		MangaDTO.builder()
    			.isbn(m.getIsbn())
    			.build();
	}

	private static StatoOrdineDTO idOnly(StatoOrdine s) {
	    return s == null ? 
	    		null : 
	    		StatoOrdineDTO.builder()
    			.id(s.getId())
    			.build();
	}

	private static AnagraficaDTO idOnly(Anagrafica a) {
	    return a == null ? 
	    		null : 
	    		AnagraficaDTO.builder()
    			.id(a.getId())
    			.build();
	}

	private static RigaOrdineDTO idOnly(RigaOrdine r) {
	    return r == null ? 
	    		null : 
	    		RigaOrdineDTO.builder()
    			.id(r.getId())
    			.build();
	}

	private static CarrelloDTO idOnly(Carrello c) {
	    return c == null ? 
	    		null : 
	    		CarrelloDTO.builder()
    			.id(c.getId())
    			.build();
	}

	public static AccountDTO buildAccountDTO(Account a, Boolean expand) {
		return AccountDTO.builder()
				.id(a.getId())
				.anagrafiche(
						a.getAnagrafiche().stream()
							.map(anag -> 
								expand? 
									buildAnagraficaDTO(anag, false) :
									idOnly(anag)
							)
							.toList()
							)
				.carrello(expand?
						buildCarrelloDTO(a.getCarrello(), false) :
						idOnly(a.getCarrello()))
				.email(a.getEmail())
				.ruolo(a.getRuolo())
				.username(a.getUsername())
				.build();	
	}
	
	public static AnagraficaDTO buildAnagraficaDTO(Anagrafica a, Boolean expand) {
		return AnagraficaDTO.builder()
				.id(a.getId())
				.cap(a.getCap())
				.citta(a.getCitta())
				.cognome(a.getCognome())
				.nome(a.getNome())
				.provincia(a.getProvincia())
				.predefinito(a.getPredefinito())
				.stato(a.getStato())
				.via(a.getVia())
				.build();
	}
	
	public static AutoreDTO buildAutoreDTO(Autore a, Boolean expand) {
		return AutoreDTO.builder()
				.id(a.getId())
				.nome(a.getNome())
				.cognome(a.getCognome())
				.dataNascita(a.getDataNascita())
				.descrizione(a.getDescrizione())
				.manga(a.getManga().stream()
						.map(m -> expand?
								buildMangaDTO(m, false) :
								idOnly(m)
								)
						.toList())
				.build();
	}
	
	public static CarrelloDTO buildCarrelloDTO(Carrello c, Boolean expand) {
		return CarrelloDTO.builder()
				.id(c.getId())
				.account(expand? buildAccountDTO(c.getAccount(), false) : idOnly(c.getAccount()))
				.manga(c.getManga().stream()
						.map(m -> expand?
								buildMangaDTO(m, false) :
								idOnly(m)
								)
						.toList())
				.build();
	}

	
	public static CasaEditriceDTO buildCasaEditriceDTO(CasaEditrice c, Boolean expand) {
		return CasaEditriceDTO.builder()
				.nome(c.getNome())
				.descrizione(c.getDescrizione())
				.indirizzo(c.getIndirizzo())
				.email(c.getEmail())
				.manga(c.getManga().stream()
						.map(m -> expand?
								buildMangaDTO(m, false) :
								idOnly(m)
								)
						.toList())
				.build();
	}
	
	public static GenereDTO buildGenereDTO(Genere g, Boolean expand) {
		return GenereDTO.builder()
				.id(g.getId())
				.descrizione(g.getDescrizione())
				.manga(g.getManga().stream()
						.map(m -> expand?
								buildMangaDTO(m, false) :
								idOnly(m)
								)
						.toList())
				.build();
	}
	
	public static MangaDTO buildMangaDTO(Manga m, Boolean expand) {
		return MangaDTO.builder()
				.isbn(m.getIsbn())
				.titolo(m.getTitolo())
				.dataPubblicazione(m.getDataPubblicazione())
				.prezzo(m.getPrezzo())
				.immagine(m.getImmagine())
				.numeroCopie(m.getNumeroCopie())
				.autori(
						m.getAutori().stream()
							.map(a -> expand?
									buildAutoreDTO(a, false) :
									idOnly(a))
							.toList()
						)
				.generi(null)
				.build();
	}
	
	public static OrdineDTO buildOrdineDTO(Ordine o, Boolean expand) {
		return OrdineDTO.builder()
			.id(o.getId())
			.account(expand? buildAccountDTO(o.getAccount(), false) : idOnly(o.getAccount()))
			.spedizione(expand? buildTipoSpedizioneDTO(o.getSpedizione(), false) : idOnly(o.getSpedizione()))
			.pagamento(expand? buildTipoPagamentoDTO(o.getPagamento(), false) :  idOnly(o.getPagamento()))
			.data(o.getData())
			.stato(expand? buildStatoOrdineDTO(o.getStato(), false) : idOnly(o.getStato()))
			.righeOrdine(o.getRigheOrdine().stream()
							.map(r ->
								expand? 
								buildRigaOrdineDTO(r, false) : 
								idOnly(r)
							)
							.collect(Collectors.toList())
						)
			.build();
	}

	public static TipoPagamentoDTO buildTipoPagamentoDTO(TipoPagamento p, Boolean expand) {
		return TipoPagamentoDTO.builder()
				.id(p.getId())
				.tipoPagamento(p.getTipoPagamento())
				.build();
	}

	
	
	public static RigaOrdineDTO buildRigaOrdineDTO(RigaOrdine r, Boolean expand) {
		return RigaOrdineDTO.builder()
				.id(r.getId())
				.idOrdine(r.getIdOrdine())
				.manga(expand? buildMangaDTO(r.getManga(), false): idOnly(r.getManga()))
				.numeroCopie(r.getNumeroCopie())
				.build();
	}
	
	public static TipoSpedizioneDTO buildTipoSpedizioneDTO(TipoSpedizione s, Boolean expand) {
		return TipoSpedizioneDTO.builder()
				.id(s.getId())
				.tipoSpedizione(s.getTipoSpedizione())
				.build();
	}

	public static StatoOrdineDTO buildStatoOrdineDTO(StatoOrdine stato, Boolean expand) {
		return StatoOrdineDTO.builder()
				.id(stato.getId())
				.statoOrdine(stato.getStatoOrdine())
				.build();
	}
}

