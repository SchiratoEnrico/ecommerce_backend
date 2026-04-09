package com.betacom.ecommerce.backend.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.betacom.ecommerce.backend.dto.inputs.AnagraficaRequest;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IAnagraficaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/anagrafica")
public class AnagraficaController {

	private final IAnagraficaServices anaS;
	private final IMessagesServices msgS;
	private final IAccountRepository accountRepository; // Per trovare l'utente loggato

	//METODI DI SUPPORTO PER LA SICUREZZA 

	private boolean isAdmin(Authentication auth) {
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
	}

	private Account getLoggedAccount(Principal principal) {
		return accountRepository.findByUsername(principal.getName()).orElse(null);
	}

	// ENDPOINT PER TUTTI GLI UTENTI 

	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody(required = true) AnagraficaRequest req, Authentication auth, Principal principal) {
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// SICUREZZA: Evitiamo che un utente crei un indirizzo per l'Account di un altro
		if (!isAdmin(auth)) {
			Account loggedAccount = getLoggedAccount(principal);
			if (loggedAccount == null || !loggedAccount.getId().equals(req.getIdAccount())) {
				r.setMsg("Accesso negato: puoi creare indirizzi solo per il tuo account.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}

		try {
			anaS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	//ENDPOINT CONDIVISI (ADMIN + PROPRIETARIO)

	@PutMapping("/update")
	public ResponseEntity<Response> update(@RequestBody(required = true) AnagraficaRequest req, Authentication auth, Principal principal) {
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		//  Controllo tramite ID Anagrafica
		if (!isAdmin(auth)) {
			Account loggedAccount = getLoggedAccount(principal);
			if (loggedAccount == null || !anaS.isAnagraficaOwnedByAccount(req.getId(), loggedAccount.getId())) {
				r.setMsg("Accesso negato: non puoi modificare questo indirizzo.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}

		try {
			anaS.update(req);
			r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		} 
		return ResponseEntity.status(status).body(r);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Response> delete(@PathVariable(required = true) Integer id, Authentication auth, Principal principal) {
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// Controllo tramite ID Anagrafica
		if (!isAdmin(auth)) {
			Account loggedAccount = getLoggedAccount(principal);
			if (loggedAccount == null || !anaS.isAnagraficaOwnedByAccount(id, loggedAccount.getId())) {
				r.setMsg("Accesso negato: non puoi eliminare questo indirizzo.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}

		try {
			anaS.delete(id);
			r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	@GetMapping("/findById")
	public ResponseEntity<Object> findById(@RequestParam(required = true) Integer id, Authentication auth, Principal principal) {
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		//  Controllo tramite ID Anagrafica
		if (!isAdmin(auth)) {
			Account loggedAccount = getLoggedAccount(principal);
			if (loggedAccount == null || !anaS.isAnagraficaOwnedByAccount(id, loggedAccount.getId())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: indirizzo non tuo.");
			}
		}

		try {
			r = anaS.findById(id);
		} catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	@GetMapping("/findByAccountId")
	public ResponseEntity<Object> findByAccountId(@RequestParam(required = true) Integer id, Authentication auth, Principal principal) {
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		// Controllo diretto sull'ID dell'Account passato come parametro
		if (!isAdmin(auth)) {
			Account loggedAccount = getLoggedAccount(principal);
			if (loggedAccount == null || !loggedAccount.getId().equals(id)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: puoi vedere solo i tuoi indirizzi.");
			}
		}

		try {
			r = anaS.findByAccountId(id);
		} catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	// ENDPOINT SOLO ADMIN 

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/list")
	public ResponseEntity<Object> list() {
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		try {
			r = anaS.list();
		} catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
}