package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.inputs.LoginRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.LoginDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAccountServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.ReqValidators;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountImplementation implements IAccountServices{

	private final IAccountRepository repAcc;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void create(AccountRequest req) throws MangaException {
	    log.debug("create macchina {}", req);

	    if (req.getUsername()==null || req.getUsername().isBlank())
	        throw new MangaException("null_usr");
	    
	    if(req.getPassword()==null || req.getPassword().isBlank())
	    	throw new MangaException("null_pwd");
	    
	    ReqValidators.validatePassword(req.getPassword());

	    if (req.getEmail() == null || req.getEmail().isBlank())
	        throw new MangaException("null_ema");

	    if (Utils.isBlank(req.getRuolo()))
	        throw new MangaException("null_ruo");

	    if (repAcc.findByUsername(req.getUsername().trim()).isPresent() )
	        throw new MangaException("exists_usr");

	    if (repAcc.findByEmail(req.getEmail().trim()).isPresent())
	        throw new MangaException("exists_ema");

	    Account acc = new Account();
	    acc.setUsername(req.getUsername().trim());
	    acc.setPassword(req.getPassword().trim());
	    acc.setEmail(req.getEmail().trim());
	    acc.setRuolo(Utils.normalize(req.getRuolo()));

	    repAcc.save(acc);
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
	@Transactional(rollbackFor = Exception.class)
	public void update(AccountRequest req) throws MangaException {
	    log.debug("Update Account, id: {}", req);

	    Account acc = repAcc.findById(req.getId())
	            .orElseThrow(() -> new MangaException("null_acc"));

	    if (!Utils.isBlank(req.getUsername())) {
	        Optional<Account> byUsername = repAcc.findByUsername(req.getUsername().trim());

	        if (byUsername.isPresent() && !byUsername.get().getId().equals(req.getId()))
	            throw new MangaException("exists_usr");
	        	
	        acc.setUsername(req.getUsername().trim());
	    }

	    if (!Utils.isBlank(req.getEmail())) {
	        Optional<Account> byEmail = repAcc.findByEmail(req.getEmail().trim().toLowerCase());

	        if (byEmail.isPresent() && !byEmail.get().getId().equals(req.getId()))
	            throw new MangaException("exists_ema");

	        acc.setEmail(req.getEmail().trim().toLowerCase());
	    }

	    if (req.getPassword() != null && !req.getPassword().isBlank())
	    	ReqValidators.validatePassword(req.getPassword());
	    	acc.setPassword(req.getPassword().trim());

	    if (!Utils.isBlank(req.getRuolo()))
	        acc.setRuolo(Utils.normalize(req.getRuolo()));

	    repAcc.save(acc);
	}
	
	@Override
	public List<AccountDTO> list() {
	    log.debug("findAll() Account");

	    return repAcc.findAll()
	            .stream()
	            .map(a -> DtoBuilders.buildAccountDTO(a, Optional.ofNullable(a.getCarrello()), Optional.ofNullable(a.getAnagrafiche())))
	            .collect(Collectors.toList());
	}

	@Override
	public AccountDTO findById(Integer id) throws MangaException {
	    log.debug("findById Account");

	    Account acc = repAcc.findById(id)
	            .orElseThrow(() -> new MangaException("!exists_acc"));

	    return DtoBuilders.buildAccountDTO(acc, Optional.ofNullable(acc.getCarrello()), Optional.ofNullable(acc.getAnagrafiche()));
	}
	
	@Override
	public LoginDTO login(LoginRequest req) throws MangaException {
		log.debug("login {}", req);
		
		Account usr = repAcc.findByUsername(req.getUsername())
				.orElseThrow(()-> new MangaException("!valid_log"));
		
		if(!usr.getPassword().equals(req.getPassword())) 
			throw new MangaException("!valid_log");
		
		return LoginDTO.builder()
				.id(usr.getId())
				.username(usr.getUsername())
				.ruolo(usr.getRuolo().toString())
				.build();
		
	}

	
}
