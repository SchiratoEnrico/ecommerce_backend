package com.betacom.ecommerce.backend.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.ecommerce.backend.dto.inputs.RigaCarrelloRequest;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.ICarrelloServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaCarrelloServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/riga_carrello")
public class RigaCarrelloController {
	private final IRigaCarrelloServices rcS;
	private final ICarrelloServices carS; // Aggiunto per i controlli di sicurezza
	private final IMessagesServices msgS;
	private final IAccountRepository accountRepository; 

	private boolean isAdmin(Authentication auth) {
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")); 
	}

	private Account getLoggedAccount(Principal principal) {
		return accountRepository.findByUsername(principal.getName()).orElse(null);
	}

	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody(required=true) RigaCarrelloRequest req, Authentication auth, Principal principal){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		//  Controlla se il carrello in cui sta inserendo la riga è il suo
		if (!isAdmin(auth)) {
			Account loggedAcc = getLoggedAccount(principal);
			if (loggedAcc == null || !carS.isCartOwnedByAccount(req.getCarrelloId(), loggedAcc.getId())) {
				r.setMsg("Accesso negato.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}

		try {
			rcS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Response> update(@RequestBody(required=true) RigaCarrelloRequest req, Authentication auth, Principal principal){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// SICUREZZA
		if (!isAdmin(auth)) {
			Account loggedAcc = getLoggedAccount(principal);
			if (loggedAcc == null || !rcS.isRigaCarrelloOwnedByAccount(req.getId(), loggedAcc.getId())) {
				r.setMsg("Accesso negato.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}

		try {
			rcS.update(req);
			if(req.getNumeroCopie()==null || req.getNumeroCopie()<=0)
				r.setMsg(msgS.get("rest_deleted"));
			else
				r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Response> delete(@PathVariable(required = true) Integer id, Authentication auth, Principal principal){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// SICUREZZA
		if (!isAdmin(auth)) {
			Account loggedAcc = getLoggedAccount(principal);
			if (loggedAcc == null || !rcS.isRigaCarrelloOwnedByAccount(id, loggedAcc.getId())) {
				r.setMsg("Accesso negato.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}

		try {
			rcS.delete(id);
			r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);		
	}
	
	@GetMapping("/list")
	public ResponseEntity<Object> list(
			@RequestParam(required=false) Integer chartId,
			@RequestParam(required=false) String isbn,
			@RequestParam(required=false) Integer nCopie,
			Authentication auth, Principal principal
			){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		// L'utente normale può listare solo le righe passando il suo chartId.
		if (!isAdmin(auth)) {
			Account loggedAcc = getLoggedAccount(principal);
			if (chartId == null || loggedAcc == null || !carS.isCartOwnedByAccount(chartId, loggedAcc.getId())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato.");
			}
		}

		try {
			r= rcS.list(chartId, isbn, nCopie);
		} catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@GetMapping("/findById")
	public ResponseEntity<Object> findById (@RequestParam (required = true) Integer id, Authentication auth, Principal principal){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		// SICUREZZA
		if (!isAdmin(auth)) {
			Account loggedAcc = getLoggedAccount(principal);
			if (loggedAcc == null || !rcS.isRigaCarrelloOwnedByAccount(id, loggedAcc.getId())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato.");
			}
		}

		try {
			r= rcS.findById(id);
		} catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST; 
		}
		return ResponseEntity.status(status).body(r);
	}
}