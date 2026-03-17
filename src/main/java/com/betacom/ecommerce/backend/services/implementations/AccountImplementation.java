package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficheDTO;
import com.betacom.ecommerce.backend.dto.outputs.CarrelliDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Accounts;
import com.betacom.ecommerce.backend.models.Anagrafiche;
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
		log.debug("create macchina {}", req);
		
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
		
	    Accounts acc = new Accounts();
		acc.setUsername(req.getUsername().trim().toUpperCase());
		acc.setEmail(req.getEmail().trim().toLowerCase());
		acc.setRuolo(req.getRuolo().trim().toUpperCase());	
		
		repAcc.save(acc).getId();
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
        Accounts acc = repAcc.findById(id)
                .orElseThrow(() -> new MangaException("null_acc"));
        repAcc.delete(acc);	
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(AccountRequest req) throws MangaException {
		log.debug("Update Account, id: {}", req);
		
		Accounts acc = repAcc.findById(req.getId())
				.orElseThrow(() -> new MangaException("null_acc"));
		
	    if (req.getUsername() != null && !req.getUsername().isBlank()) {
	        Optional<Accounts> byUsername = repAcc.findByUsername(req.getUsername().trim());	        
	        
	        if (byUsername.isPresent() && !byUsername.get().getId().equals(req.getId()))
	            throw new MangaException("exists_usr");
	        
	        acc.setUsername(req.getUsername().trim().toUpperCase());
	    }
		
	    if (req.getEmail() != null && !req.getEmail().isBlank()) {
	        Optional<Accounts> byEmail = repAcc.findByEmail(req.getEmail().trim());
	        
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
		
		 List<Accounts> lA = repAcc.findAll();

		    return lA.stream()
		            .map(a -> AccountDTO.builder()
		                    .id(a.getId())
		                    .username(a.getUsername())
		                    .email(a.getEmail())
		                    .ruolo(a.getRuolo())
		               	 .carrello((a.getCarrello() == null) ? null : CarrelliDTO.builder()
		                         .id(a.getCarrello().getId())
		                         //da aggiungere i campi di carrello
		                         .build())
		                 .anagrafiche(buildAnagraficheDTO(a.getAnagrafiche()))
		                 .build()
		            ).toList();
		       }
	
	@Override
	public AccountDTO findById(Integer id) throws MangaException {
		log.debug("findById Account");
		
		Accounts acc = repAcc.findById(id)
				.orElseThrow(() -> new MangaException("!exists_acc"));
		
		
		return AccountDTO.builder()
				.id(acc.getId())
                .username(acc.getUsername())
                .email(acc.getEmail())
                .ruolo(acc.getRuolo())
                .carrello((acc.getCarrello() == null) ? null : CarrelliDTO.builder()
                		.id(acc.getCarrello().getId())
                		//da aggiungere i campi di carrello
                		.build())
                .anagrafiche(buildAnagraficheDTO(acc.getAnagrafiche()))
                .build();
	
	}
	
	private List<AnagraficheDTO> buildAnagraficheDTO(List<Anagrafiche> anagrafiche) {
		
		if (anagrafiche == null || anagrafiche.isEmpty())
			return null;
		return anagrafiche.stream()
					.map(an -> AnagraficheDTO.builder()
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
