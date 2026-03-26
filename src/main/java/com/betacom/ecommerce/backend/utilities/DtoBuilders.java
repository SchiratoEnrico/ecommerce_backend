package com.betacom.ecommerce.backend.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.dto.outputs.CasaEditriceDTO;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.dto.outputs.OrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.RigaCarrelloDTO;
import com.betacom.ecommerce.backend.dto.outputs.RigaFatturaDTO;
import com.betacom.ecommerce.backend.dto.outputs.RigaOrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.SagaDTO;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.RigaCarrello;
import com.betacom.ecommerce.backend.models.RigaFattura;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.models.Saga;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.TipoSpedizione;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DtoBuilders {
	
	public static List<TipoSpedizioneDTO> buildSpedizioniDTO(List<TipoSpedizione> lS){
		return lS.stream()
				.map(s -> TipoSpedizioneDTO.builder()
						.id(s.getId())
						.tipoSpedizione(s.getTipoSpedizione())
						.build()
						).collect(Collectors.toList());
	}
	

	public static List<AnagraficaDTO> buildAnagraficaDTO(List<Anagrafica> lA) {
		return lA.stream()
				.map(a -> AnagraficaDTO.builder()
						.id(a.getId())
						.nome(a.getNome())
						.cognome(a.getCognome())
						.stato(a.getStato())
						.citta(a.getCitta())
						.provincia(a.getProvincia())
						.cap(a.getCap())
						.via(a.getVia())
						.predefinito(a.getPredefinito())
						.build()
						)
				.collect(Collectors.toList());
		
	}
	
	private static List<String> IsbnsSupport(List<Manga> manga){
		try {
			log.debug("MangaSize: {}}", manga.size());
			List<String> isbns = new ArrayList<>();
			for(Manga m : manga) {
				isbns.add(m.getIsbn());
			}
			return isbns;
		}
		catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static List<CasaEditriceDTO> buildCaseEditriciDTO(List<CasaEditrice> lC) {
		return lC.stream()
				.map(c -> CasaEditriceDTO.builder()
						.nome(c.getNome())
						.descrizione(c.getDescrizione())
						.email(c.getEmail())
						.id(c.getId())
						.indirizzo(c.getIndirizzo())
						.isbns(IsbnsSupport(c.getManga()))
						.build()
						).collect(Collectors.toList());
	}

	public static CasaEditriceDTO buildCasaEditriceDTO(CasaEditrice c) {
		return CasaEditriceDTO.builder()
						.nome(c.getNome())
						.descrizione(c.getDescrizione())
						.email(c.getEmail())
						.id(c.getId())
						.indirizzo(c.getIndirizzo())
						.isbns(IsbnsSupport(c.getManga()))
						.build();
	}

	public static CarrelloDTO buildCarrelloDTO(Carrello c, Optional<Account> acc, Optional<List<RigaCarrello>> lR) {
	return CarrelloDTO.builder()
			.id(c.getId())
			.account(
					acc.isPresent()?
					buildAccountDTO(acc.get(), Optional.empty(), Optional.empty()):
						null
					)
			.righe(lR.isPresent()?
					lR.get().stream()
					.map(r -> buildRigaCarrelloDTO(r, Optional.empty())).toList():
						null)
			.build();
	}
	
	public static RigaCarrelloDTO buildRigaCarrelloDTO(RigaCarrello c, Optional<Manga> man) {
		return RigaCarrelloDTO.builder()
				.carrelloId(c.getCarrello().getId())
				.id(c.getId())
				.manga(man.isPresent()?
						buildMangaDTO(man.get(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()):
							null
							)
				.numeroCopie(c.getNumeroCopie())
				.build();
	}
	
	/*
	 * 
	 */
	public static AccountDTO buildAccountDTO(Account a, Optional<Carrello> c, Optional<List<Anagrafica>> lA) {
		return AccountDTO.builder()
				.id(a.getId())
				.anagrafiche(lA.isPresent()?
						lA.get().stream()
							.map(anag -> buildAnagraficaDTO(anag)).toList() :
								null						
							)
				.carrelloId(c.isPresent()?
						 c.get().getId() : null
						 )
				.email(a.getEmail())
				.ruolo(a.getRuolo())
				.username(a.getUsername())
				.build();	
	}
	
	public static AnagraficaDTO buildAnagraficaDTO(Anagrafica a) {
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
	
	
	public static AutoreDTO buildAutoreDTO(Autore a, Optional<List<Manga>> lM) {
		return AutoreDTO.builder()
				.id(a.getId())
				.nome(a.getNome())
				.cognome(a.getCognome())
				.dataNascita(a.getDataNascita())
				.descrizione(a.getDescrizione())
				.manga(
					lM.isPresent() ?
							lM.get().stream()
									.map(g -> buildMangaDTO(g, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())).toList() :
										null
						)
				.build();
	}

	
	
	public static GenereDTO buildGenereDTO(Genere g, Optional<List<Manga>> lM) {
		return GenereDTO.builder()
				.id(g.getId())
				.descrizione(g.getDescrizione())
				.manga(lM.isPresent() ?
						lM.get().stream()
						.map(m -> buildMangaDTO(m, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())).toList() :
							null
					)
				.build();
	}
	
	public static MangaDTO buildMangaDTO(Manga m, Optional<CasaEditrice> c, Optional<List<Autore>> lA, Optional<List<Genere>> lG, Optional<Saga> s) {
		return MangaDTO.builder()
				.isbn(m.getIsbn())
				.titolo(m.getTitolo())
				.dataPubblicazione(m.getDataPubblicazione())
				.prezzo(m.getPrezzo())
				.immagine(m.getImmagine())
				.numeroCopie(m.getNumeroCopie())
				.casaEditrice(c.isPresent()?
						buildCasaEditriceDTO(c.get()) : null)
				.autori(
						lA.isPresent() ?
						lA.get().stream()
							.map(a -> buildAutoreDTO(a, Optional.empty())).toList() :
								null
						)
				.generi(
						lG.isPresent() ?
								lG.get().stream()
									.map(g -> buildGenereDTO(g, Optional.empty())).toList() :
										null
						)
				.saga(s.isPresent()?
						buildSagaDTO(s.get(), Optional.empty()):null )
				.sagaVol(m.getSagaVol())
				.build();
	}
	
	@Transactional(readOnly = true)
	public static OrdineDTO buildOrdineDTO(Ordine o,
			Optional<Account> acc, 
			Optional<TipoPagamento> pag, 
			Optional<StatoOrdine> sta, 
			Optional<TipoSpedizione> spe,
			Optional<List<RigaOrdine>> lR
			) {
		return OrdineDTO.builder()
			.id(o.getId())
			.account(acc.isPresent()? 
					buildAccountDTO(acc.get(), Optional.empty(), Optional.empty()) 
					: null
					)
			.spedizione(spe.isPresent()? 
					buildTipoSpedizioneDTO(spe.get()) 
					: null
					)
			.pagamento(pag.isPresent()? 
						buildTipoPagamentoDTO(pag.get()) 
						: null
					)
			.data(o.getData())
			.stato(sta.isPresent()? 
					buildStatoOrdineDTO(sta.get()) 
					: null
				)
			.righeOrdine(lR.isPresent()?
					lR.get().stream().map(r -> buildRigaOrdineDTO(r, Optional.empty())).toList() : 
						null
				)
			.build();
	}
	
	public static TipoPagamentoDTO buildTipoPagamentoDTO(TipoPagamento p) {
		return TipoPagamentoDTO.builder()
				.id(p.getId())
				.tipoPagamento(p.getTipoPagamento())
				.build();
	}

	
	@Transactional(readOnly = true)
	public static RigaOrdineDTO buildRigaOrdineDTO(RigaOrdine r, Optional<Manga> man) {
		return RigaOrdineDTO.builder()
				.id(r.getId())
				.idOrdine(r.getOrdine().getId())
				.manga(r.getManga().getIsbn())
				.numeroCopie(r.getNumeroCopie())
				.build();
	}
	
	public static TipoSpedizioneDTO buildTipoSpedizioneDTO(TipoSpedizione s) {
		return TipoSpedizioneDTO.builder()
				.id(s.getId())
				.tipoSpedizione(s.getTipoSpedizione())
				.build();
	}

	public static StatoOrdineDTO buildStatoOrdineDTO(StatoOrdine stato) {
		return StatoOrdineDTO.builder()
				.id(stato.getId())
				.statoOrdine(stato.getStatoOrdine())
				.build();
	}
	
	// build RigaFattura
    public static RigaFatturaDTO buildRigaFatturaDTO(RigaFattura r, Optional<Fattura> f) {
        return RigaFatturaDTO.builder()
                .id(r.getId())
                .idFattura(f.isPresent()?
                		f.get().getId()
                		:null
                		)
                .isbn(r.getIsbn())
                .titolo(r.getTitolo())
                .autore(r.getAutore())
                .prezzoUnitario(r.getPrezzoUnitario())
                .quantita(r.getQuantita())
                .totaleRiga(r.getTotaleRiga())
                .build();
    }

    // build Fattura
    public static FatturaDTO buildFatturaDTO(Fattura f, Optional<List<RigaFattura>> lR) {
        return FatturaDTO.builder()
                .id(f.getId())
                .numeroFattura(f.getNumeroFattura())
                // snapshot cliente
                .clienteNome(f.getClienteNome())
                .clienteCognome(f.getClienteCognome())
                .clienteEmail(f.getClienteEmail())
                .clienteIndirizzo(f.getClienteIndirizzo())
                .clienteCitta(f.getClienteCitta())
                .clienteCap(f.getClienteCap())
                .clienteProvincia(f.getClienteProvincia())
                .clienteStato(f.getClienteStato())
                // pagamento e spedizione
                .tipoPagamento(f.getTipoPagamento())
                .tipoSpedizione(f.getTipoSpedizione())
                // costi
                .costoSpedizione(f.getCostoSpedizione())
                .totale(f.getTotale())
                // metadata
                .note(f.getNote())
                // righe
                .righeFattura(lR.isPresent()?
                		lR.get().stream().map(
                				r -> buildRigaFatturaDTO(r, Optional.empty())
                		).collect(Collectors.toList())
                        : null)
                .build();
    }

    public static SagaDTO buildSagaDTO(Saga s, Optional<List<Manga>> lM) {
    	return SagaDTO.builder()
    			.id(s.getId())
    			.nome(s.getNome())
    			.descrizione(s.getDescrizione())
    			.immagine(s.getImmagine())
    			.manga(lM.isPresent()?
                		lM.get().stream().map(
                				r -> buildMangaDTO(r, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())
                		).collect(Collectors.toList())
                        : null)
    			.build();
    }
}

