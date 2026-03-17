package com.betacom.ecommerce.backend.utilities;

import java.util.List;
import java.util.stream.Collectors;

import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficheDTO;
import com.betacom.ecommerce.backend.dto.outputs.CarrelliDTO;
import com.betacom.ecommerce.backend.dto.outputs.CaseEditriciDTO;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.dto.outputs.SpedizioniDTO;
import com.betacom.ecommerce.backend.models.Accounts;
import com.betacom.ecommerce.backend.models.Anagrafiche;
import com.betacom.ecommerce.backend.models.Carrelli;
import com.betacom.ecommerce.backend.models.CaseEditrici;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Spedizioni;

public class Mapper {
	
	public static List<MangaDTO> buildMangaDTO(List<Manga> manga){
		return null;
	}
	
	public static AccountDTO buildAccountDTO(Accounts a) {
		return AccountDTO.builder()
				.id(a.getId())
				.username(a.getUsername())
				.email(a.getEmail())
				.ruolo(a.getRuolo())
				.anagrafiche(buildAnagraficheDTO(a.getAnagrafiche()))
				.carrello()
				.build();
	}
	
	public static CarrelliDTO buildCarrelliDTO(Carrelli c) {
		return CarrelliDTO.builder()
				.id(c.getId())
				.account(buildAccountDTO(c.getAccount()))
				.manga(buildMangaDTO(c.getManga()))
				.build();
	}
	
	public static List<CarrelliDTO> buildCarrelliDTO(List<Carrelli> lC){
		return lC.stream()
				.map(c -> CarrelliDTO.builder()
						.id(c.getId())
						.account(buildAccountDTO(c.getAccount()))
						.manga(buildMangaDTO(c.getManga()))
						.build()
						)
				.collect(Collectors.toList());
	}
	
	public static List<AnagraficheDTO> buildAnagraficheDTO(List<Anagrafiche> lA) {
		return lA.stream()
				.map(a -> AnagraficheDTO.builder()
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

	public static List<CaseEditriciDTO> buildCaseEditriciDTO(List<CaseEditrici> lC) {
		return lC.stream()
				.map(c -> CaseEditriciDTO.builder()
						.descrizione(c.getDescrizione())
						.email(c.getEmail())
						.id(c.getId())
						.indirizzo(c.getIndirizzo())
						.manga(buildMangaDTO(c.getManga()))
						.build()
						).collect(Collectors.toList());
	}
	
	public static CaseEditriciDTO buildCaseEditriciDTO(CaseEditrici c) {
		return CaseEditriciDTO.builder()
				.descrizione(c.getDescrizione())
				.email(c.getEmail())
				.id(c.getId())
				.indirizzo(c.getIndirizzo())
				.manga(buildMangaDTO(c.getManga()))
				.build();
	}
	
	public static List<SpedizioniDTO> buildSpedizioniDTO(List<Spedizioni> lS){
		return lS.stream()
				.map(s -> SpedizioniDTO.builder()
						.id(s.getId())
						.tipoSpedizione(s.getTipoSpedizione())
						.build()
						).collect(Collectors.toList());
	}
	
	public static SpedizioniDTO buildSpedizioniDTO(Spedizioni s) {
		return SpedizioniDTO.builder()
				.id(s.getId())
				.tipoSpedizione(s.getTipoSpedizione())
				.build();
	}
}
