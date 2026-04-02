package com.betacom.ecommerce.backend.services.implementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.enums.Ruoli;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAccountServices;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.specification.AccountSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.ReqValidators;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountImplementation implements IAccountServices{
	private final CarrelloImplementation carI;
	private final IAccountRepository repAcc;
	private final ICarrelloRepository carR;
	private final IFatturaRepository fattR;
	private final IFatturaServices fattS;
	private final IMessagesServices msgS;
	private final PasswordEncoder passwordEncoder;
	private final IOrdineRepository ordeR;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void create(AccountRequest req) throws MangaException {
	    log.debug("create account {}", req);

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
	    
	    String encodedPassword = passwordEncoder.encode(req.getPassword().trim());
	    
	    acc.setUsername(req.getUsername().trim());
	    acc.setPassword(encodedPassword);
	    acc.setEmail(req.getEmail().trim());
	    acc.setDataCreazione(LocalDateTime.now());
	    
	    try {
	    	acc.setRuolo(Ruoli.valueOf(Utils.normalize(req.getRuolo())));
	    }catch(IllegalArgumentException e) {
	    	throw new MangaException("!valid_rol");
	    }

	    repAcc.save(acc);
	    //Integer id = repAcc.save(acc).getId();
	    
//	    CarrelloRequest carReq = new CarrelloRequest();
//	    carReq.setId_account(id);
//	    Integer chartId = carI.create(carReq);
//	    Carrello car = carR.findById(chartId)
//	    		.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
//	    acc.setCarrello(car);
//	    repAcc.save(acc);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
        Account acc = repAcc.findById(id)
                .orElseThrow(() -> new MangaException("null_acc"));
        
        if(acc.getRuolo().equals(Ruoli.ADMIN)) {
			List<Account> lU = repAcc.findByRuolo(Ruoli.ADMIN);
			
			if(lU.size()==1)
				throw new MangaException("last_adm");
		}
        
        // controllo ordini
        ordeR.findAllByAccountId(id).stream()
        	.forEach(o -> fattS.updateFromOrdine(o, true));
        
        repAcc.delete(acc);	
        // cascad su carrello, righecarrello, anagrafiche
	}
	


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(AccountRequest req, boolean isAdmin) throws MangaException { 
	    log.debug("Update Account, id: {}", req.getId());

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

	    
	    if (req.getPassword() != null && !req.getPassword().isBlank()) { 
	    	ReqValidators.validatePassword(req.getPassword());
            // Criptiamo la password prima di salvarla nel DB
	    	acc.setPassword(passwordEncoder.encode(req.getPassword().trim()));
        } 

	    // SICUREZZA: Aggiornamento ruolo solo se chi chiama è admin
	    if (!Utils.isBlank(req.getRuolo()) && isAdmin) {
	    	acc.setRuolo(Ruoli.valueOf(Utils.normalize(req.getRuolo())));
        }

	    repAcc.save(acc);
	}
	
	@Override
	public List<AccountDTO> list() {
	    log.debug("findAll() Account");

	    return repAcc.findAll()
	            .stream()
	            .map(a -> DtoBuilders.buildAccountDTO(a, Optional.ofNullable(a.getCarrello()), Optional.empty()))//ofNullable(a.getAnagrafiche())))
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
	public AccountDTO findByUsername(String username) throws MangaException {
		Account acc = repAcc.findByUsername(username)
	            .orElseThrow(() -> new MangaException("!exists_acc"));

	    return DtoBuilders.buildAccountDTO(acc, Optional.ofNullable(acc.getCarrello()), Optional.ofNullable(acc.getAnagrafiche()));
	}

	@Override
	public List<AccountDTO> findByFilters(AccountRequest req) throws MangaException {
		
		Specification<Account> spec = AccountSpecifications.usernameAndEmailAndRuolo(req.getUsername(), req.getEmail(), req.getRuolo());
		List<Account> lA = repAcc.findAll(spec);
		return lA.stream()
	            .map(a -> DtoBuilders.buildAccountDTO(a, Optional.ofNullable(a.getCarrello()), Optional.empty()))//ofNullable(a.getAnagrafiche())))
	            .collect(Collectors.toList());
	}

	
}
