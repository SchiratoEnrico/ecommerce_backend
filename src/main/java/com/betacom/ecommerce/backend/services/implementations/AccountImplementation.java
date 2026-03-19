package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAccountServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountImplementation implements IAccountServices{

	private final IAccountRepository repAcc;
	
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(AccountRequest req) throws MangaException {		
		if(req.getUsername()==null || req.getUsername().isBlank()) 
			throw new MangaException("null_usr");
		
		if(req.getEmail()==null || req.getEmail().isBlank()) 
			throw new MangaException("null_ema");
		
		if(req.getRuolo()==null || req.getRuolo().isBlank()) 
			throw new MangaException("null_ruo");
		
		if (repAcc.findByUsername(req.getUsername().trim()).isPresent())
	        throw new MangaException("exists_usr");

	    if (repAcc.findByEmail(req.getEmail().trim()).isPresent())
	        throw new MangaException("exists_ema");
		
	    Account acc = new Account();
		acc.setUsername(req.getUsername().trim().toUpperCase());
		acc.setEmail(req.getEmail().trim().toLowerCase());
		acc.setRuolo(req.getRuolo().trim().toUpperCase());	
		
		repAcc.save(acc).getId();
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
        Account acc = repAcc.findById(id)
                .orElseThrow(() -> new MangaException("null_acc"));
        repAcc.delete(acc);	
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(AccountRequest req) throws MangaException {
		log.debug("Update Account, id: {}", req);
		
		Account acc = repAcc.findById(req.getId())
				.orElseThrow(() -> new MangaException("null_acc"));
		
	    if (req.getUsername() != null && !req.getUsername().isBlank()) {
	        Optional<Account> byUsername = repAcc.findByUsername(req.getUsername().trim());	        
	        
	        if (byUsername.isPresent() && !byUsername.get().getId().equals(req.getId()))
	            throw new MangaException("exists_usr");
	        
	        acc.setUsername(req.getUsername().trim().toUpperCase());
	    }
		
	    if (req.getEmail() != null && !req.getEmail().isBlank()) {
	        Optional<Account> byEmail = repAcc.findByEmail(req.getEmail().trim());
	        
	        if (byEmail.isPresent() && !byEmail.get().getId().equals(req.getId()))
	            throw new MangaException("exists_ema");
	        
	        acc.setEmail(req.getEmail().trim().toLowerCase());
	    }
		
		if(req.getRuolo()!=null && !req.getRuolo().isBlank())
			acc.setRuolo(req.getRuolo().toUpperCase());
		
		repAcc.save(acc);
	}

	@Override
	public List<AccountDTO> list() {
		log.debug("findAll() Account");
		
		 List<Account> lA = repAcc.findAll();
		 
		 return lA.stream()
				 .map(a -> AccountDTO.builder()
						 .id(a.getId())
						 .anagrafiche(buildAnagraficheDTO(a.getAnagrafiche()))
						 .carrelloId(a.getCarrello()==null ? null : a.getCarrello().getId())
						 .email(a.getEmail())
						 .ruolo(a.getRuolo())
						 .build()
						 )
				 .collect(Collectors.toList());
	}
	
	@Override
	public AccountDTO findById(Integer id) throws MangaException {
		log.debug("findById Account");
		
		Account acc = repAcc.findById(id)
				.orElseThrow(() -> new MangaException("!exists_acc"));
		
		
		return AccountDTO.builder()
				.id(acc.getId())
                .username(acc.getUsername())
                .email(acc.getEmail())
                .ruolo(acc.getRuolo())
                .carrelloId(acc.getCarrello()==null ? null : acc.getCarrello().getId())
                .anagrafiche(buildAnagraficheDTO(acc.getAnagrafiche()))
                .build();
	
	}
	
	private List<AnagraficaDTO> buildAnagraficheDTO(List<Anagrafica> anagrafiche) {
		
		if (anagrafiche == null || anagrafiche.isEmpty())
			return null;
		return anagrafiche.stream()
					.map(an -> AnagraficaDTO.builder()
					     .id(an.getId())
					     .nome(an.getNome())
					     .cognome(an.getCognome())
					     .stato(an.getStato())
					     .citta(an.getCitta())
					     .provincia(an.getProvincia())
					     .cap(an.getCap())
					     .via(an.getVia())
					     .predefinito(an.getPredefinito())
					     .build()
					).toList();
		}



}
