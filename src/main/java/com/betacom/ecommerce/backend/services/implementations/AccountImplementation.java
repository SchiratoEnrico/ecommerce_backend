package com.betacom.ecommerce.backend.services.implementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.inputs.MailRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.enums.Ruoli;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.IOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAccountServices;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMailServices;
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
	private final IAccountRepository repAcc;
	private final PasswordEncoder passwordEncoder;
	private final IFatturaServices fattS;
	private final IOrdineRepository ordeR;
	private final IMailServices  mailS;
	
	@Value("${mail.validation}")
	private String validationURL;
	
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
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
	    log.debug("Delete Account, id: {}", id);
	    Account acc = repAcc.findById(id)
	        .orElseThrow(() -> new MangaException("null_acc"));

	    // check se ultimo admin
	    if (acc.getRuolo().equals(Ruoli.ADMIN)) {
	        List<Account> lU = repAcc.findByRuolo(Ruoli.ADMIN);
	        if (lU.size() == 1)
	            throw new MangaException("last_adm");
	    }

	    // rimuovo ordini aggiungendoli a fatture.
	    // 
	    List<Ordine> ordini = ordeR.findAllByAccountId(id);
	    for (Ordine o : ordini) {
	        fattS.detachFromOrdine(o, "Account eliminato");
	    }

	    repAcc.delete(acc);
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

	 // SICUREZZA: Aggiornamento ruolo
	    if (!Utils.isBlank(req.getRuolo())) {
	    	
	    	Ruoli nuovoRuolo = Ruoli.valueOf(Utils.normalize(req.getRuolo()));
	    	
	    	// Se sta effettivamente cercando di CAMBIARE il ruolo rispetto a quello che ha già...
	    	if (!acc.getRuolo().equals(nuovoRuolo)) {
	    		
	    		// deve essere per forza un Admin
	    		if (!isAdmin) {
	    			// Se non è Admin, blocchiamo l'intera operazione con un errore esplicito
	    			throw new MangaException("privilege_denied"); 
	    		}	    		
	    		// Se è Admin, procediamo con la modifica
	    		acc.setRuolo(nuovoRuolo);
	    	}
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

	
	@Override
	public void sendValidation(String username) throws MangaException {
		log.debug("sendValidation {}", username);

		Account ut = repAcc.findByUsername(username)
				.orElseThrow(() -> new MangaException("!exists_acc"));
		sendMailValidation(ut);
	}

	@Transactional (rollbackFor = Exception.class)
	@Override
	public void emailValidate(String username) throws MangaException {
		log.debug("emailValidate {}", username);
		
		Account ut = repAcc.findByUsername(username)
				.orElseThrow(() -> new MangaException("!exists_acc"));	
		ut.setValidated(true);
		repAcc.save(ut);
		
	}
	
	
	private void sendMailValidation(Account acc) throws MangaException {
	    String validationLink = validationURL + acc.getUsername();
	    
	    String body = """
	        <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>
	            <h2 style='color: #2c3e50; text-align: center;'>Benvenuto su Mangas Store! 📚</h2>
	            <p style='font-size: 16px; color: #555;'>Ciao <b>%s</b>,</p>
	            <p style='font-size: 16px; color: #555;'>Grazie per esserti registrato! Conferma la tua email cliccando sul pulsante qui sotto:</p>
	            
	            <div style='text-align: center; margin: 30px 0;'>
	                <a href='%s' style='background-color: #e74c3c; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>Conferma la tua Email</a>
	            </div>
	            
	            <p style='font-size: 14px; color: #777;'>Oppure copia e incolla questo link:</p>
	            <p style='font-size: 14px; color: #3498db; word-break: break-all;'>%s</p>
	            
	            <hr style='border: none; border-top: 1px solid #eee; margin-top: 30px;' />
	            <p style='font-size: 12px; color: #999; text-align: center;'>Il team di Mangas Store</p>
	        </div>
	        """.formatted(acc.getUsername(), validationLink, validationLink);

	    sendMail(acc, "Benvenuto! Conferma la tua email per Mangas Store", body);
	}

	private void sendMail(Account account, String oggetto, String body) throws MangaException{
		
		mailS.sendMail(MailRequest.builder()
				.to(account.getEmail())
				.oggetto(oggetto)
				.body(body)
				.build()
				);
	}
}
