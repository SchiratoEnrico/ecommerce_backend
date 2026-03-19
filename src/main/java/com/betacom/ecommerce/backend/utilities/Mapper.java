package com.betacom.ecommerce.backend.utilities;

import java.util.List;
import java.util.stream.Collectors;

import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.dto.outputs.CasaEditriceDTO;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.TipoSpedizione;

public class Mapper {
	
	public static List<MangaDTO> buildMangaDTO(List<Manga> manga){
		return null;
	}
	
	public static AccountDTO buildAccountDTO(Account a) {
		return a==null ? null : AccountDTO.builder()
				.id(a.getId())
				.username(a.getUsername())
				.email(a.getEmail())
				.ruolo(a.getRuolo())
				.anagrafiche(buildAnagraficaDTO(a.getAnagrafiche()))
				.carrelloId(a.getCarrello()==null ? null : a.getCarrello().getId())
				.build();
	}
	
	public static CarrelloDTO buildCarrelloDTO(Carrello c) {
		return CarrelloDTO.builder()
				.id(c.getId())
				.account(buildAccountDTO(c.getAccount()))
				.build();
	}
	
	public static List<CarrelloDTO> buildCarrelloDTO(List<Carrello> lC){
		return lC.stream()
				.map(c -> 
						CarrelloDTO.builder()
						.id(c.getId())
						.account(buildAccountDTO(c.getAccount()))
						.build()
						)
				.collect(Collectors.toList());
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

	public static List<CasaEditriceDTO> buildCaseEditriciDTO(List<CasaEditrice> lC) {
		return lC.stream()
				.map(c -> CasaEditriceDTO.builder()
						.nome(c.getNome())
						.descrizione(c.getDescrizione())
						.email(c.getEmail())
						.id(c.getId())
						.indirizzo(c.getIndirizzo())
						.manga(buildMangaDTO(c.getManga()))
						.build()
						).collect(Collectors.toList());
	}
	
	public static CasaEditriceDTO buildCaseEditriciDTO(CasaEditrice c) {
		return CasaEditriceDTO.builder()
				.descrizione(c.getDescrizione())
				.email(c.getEmail())
				.id(c.getId())
				.indirizzo(c.getIndirizzo())
				.manga(buildMangaDTO(c.getManga()))
				.build();
	}
	
	public static List<TipoSpedizioneDTO> buildSpedizioniDTO(List<TipoSpedizione> lS){
		return lS.stream()
				.map(s -> TipoSpedizioneDTO.builder()
						.id(s.getId())
						.tipoSpedizione(s.getTipoSpedizione())
						.build()
						).collect(Collectors.toList());
	}
	
	public static TipoSpedizioneDTO buildSpedizioniDTO(TipoSpedizione s) {
		return TipoSpedizioneDTO.builder()
				.id(s.getId())
				.tipoSpedizione(s.getTipoSpedizione())
				.build();
	}
}
