package com.betacom.ecommerce.backend.controllers;

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

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IAccountServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/account")
public class AccountController {

	private final IAccountServices accS;
	private final IMessagesServices msgS;
	private final IAccountRepository accountRepository; // Aggiunto per i controlli di sicurezza

	// alcuni metodi utili alla sicurezza

	//capisco se l'utente che fa richiesta è un admin oppure il proprietario dell'account
	private boolean isAdminOrOwnerById(Authentication auth, Integer targetAccountId) {
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		if (isAdmin) return true;

		Account loggedAccount = accountRepository.findByUsername(auth.getName()).orElse(null);
		return loggedAccount != null && loggedAccount.getId().equals(targetAccountId);
	}

	//come il metodo sopra ma utilizza l'username piuttosto che l'id
	private boolean isAdminOrOwnerByUsername(Authentication auth, String targetUsername) {
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		if (isAdmin) return true;

		// Visto che lo username è nel token, il controllo è istantaneo e non richiede query al DB
		return auth.getName().equals(targetUsername);
	}

	
	//ENDPOINT PUBBLICI

	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody(required = true) AccountRequest req) {
        // Nessun blocco qui. Ci pensa SecurityConfig a lasciarlo passare.
		// tutti possono creare account
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			accS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) { 
			r.setMsg(msgS.get(e.getMessage()));
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
			r = accS.list();
		} catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/findByFilters")
	public ResponseEntity<Object> findByFilters(@RequestParam(required = false) String username,
			@RequestParam(required = false) String email, @RequestParam(required = false) String ruolo) {
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r = accS.findByFilters(AccountRequest.builder().username(username).email(email).ruolo(ruolo).build());
		} catch (Exception e) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	
	// ENDPOINT CONDIVISI (ADMIN + PROPRIETARIO)

	@PutMapping("/update")
	public ResponseEntity<Response> update(@RequestBody(required = true) AccountRequest req, Authentication auth) {
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// Controlliamo se è Admin 
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

		// Controllo proprietario
		if (req.getUsername() != null && !isAdminOrOwnerByUsername(auth, req.getUsername())) {
			r.setMsg("Accesso negato: puoi modificare solo il tuo profilo.");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
		}

		try {
			accS.update(req, isAdmin); 
			r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Response> delete(@PathVariable(required = true) Integer id, Authentication auth) {
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// Blocco Sicurezza
		if (!isAdminOrOwnerById(auth, id)) {
			r.setMsg("Accesso negato: puoi eliminare solo il tuo account.");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
		}

		try {
			accS.delete(id);
			r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	@GetMapping("/findById")
	public ResponseEntity<Object> findById(@RequestParam(required = true) Integer id, Authentication auth) {
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		// Blocco Sicurezza
		if (!isAdminOrOwnerById(auth, id)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: non puoi vedere i dati di altri utenti.");
		}

		try {
			r = accS.findById(id);
		} catch (Exception e) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	@GetMapping("/findByUsername")
	public ResponseEntity<Object> findByUsername(@RequestParam String username, Authentication auth) {
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		// Blocco Sicurezza
		if (!isAdminOrOwnerByUsername(auth, username)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: non puoi vedere i dati di altri utenti.");
		}

		try {
			r = accS.findByUsername(username);
		} catch (Exception e) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
}