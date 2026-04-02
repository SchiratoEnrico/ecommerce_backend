package com.betacom.ecommerce.backend.controllers;

import java.security.Principal;
import java.util.List;

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

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.models.Account; // Assicurati di importare la tua entità Account
import com.betacom.ecommerce.backend.repositories.IAccountRepository; // Importa la tua repository
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.ICarrelloServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/carrello")
public class CarrelloController {

	private final ICarrelloServices carS;
	private final IMessagesServices msgS;
	// Aggiungiamo il repository per recuperare l'utente loggato dal database
	private final IAccountRepository accountRepository;


	/**
	 * Controlla se l'utente che fa la richiesta è un ADMIN, 
	 * oppure se è un utente normale e sta chiedendo di operare sul SUO account.
	 */
	private boolean isAdminOrOwner(Authentication auth, Principal principal, Integer targetAccountId) {
		// Se ha il ruolo ADMIN, passa sempre
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		if (isAdmin) return true;

		// Se non è admin, cerchiamo il suo account nel DB usando l'username del token
		Account loggedAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
		
		// Ritorna true solo se l'ID richiesto è uguale al suo ID reale
		return loggedAccount != null && loggedAccount.getId().equals(targetAccountId);
	}


	// ENDPOINT SOLO ADMIN 
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/list")
	public ResponseEntity<Object> list(@RequestParam(required=false) List<String> isbns){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r= carS.list(isbns);
		} catch (Exception e) {
			r=e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Response> delete(@PathVariable(required = true)  Integer id){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			carS.delete(id);
			r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);		
	}


	//ENDPOINT CONDIVISI (ADMIN + PROPRIETARIO)

	@GetMapping("/findByAccountId")
	public ResponseEntity<Object> findByAccountId (@RequestParam (required = true) Integer id, Authentication auth, Principal principal){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		
		// BLOCCO DI SICUREZZA
		//qui l'id che arriva dal frontend è l'id dell'account
		//bisogna controllare che quindi questo id corrisponda all'id dell'utente loggato che sta facendo la richiesta
		if (!isAdminOrOwner(auth, principal, id)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: puoi visualizzare solo il tuo carrello.");
		}

		try {
			r= carS.findByAccountId(id);
		} catch (Exception e) {
			r=e.getMessage();
			status = HttpStatus.BAD_REQUEST; 
		}
		return ResponseEntity.status(status).body(r);
	}


	
	@PutMapping("/addRow")
	public ResponseEntity<Response> addRow(
			@RequestParam(required=true) Integer chartId,
			@RequestParam(required=true) String isbn,
			@RequestParam(required=true) Integer nCopie,
			Authentication auth, Principal principal 
			){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		//INIZIO BLOCCO DI SICUREZZA
		//Controlliamo se è ADMIN
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		
		if (!isAdmin) {
			// Se non è ADMIN, troviamo l'ID del suo account dal DB usando lo username del token
			Account loggedAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
			
			// Usiamo il  metodo del service per verificare la proprietà
			if (loggedAccount == null || !carS.isCartOwnedByAccount(chartId, loggedAccount.getId())) {
				r.setMsg("Accesso negato: non puoi modificare il carrello di un altro utente.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}
		//FINE BLOCCO DI SICUREZZA

		try {
			carS.addRow(chartId, isbn, nCopie);
			r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	
	@PutMapping("/updateRow")
	public ResponseEntity<Response> updateRow(
			@RequestParam(required=true) Integer chartId,
			@RequestParam(required=true) Integer rowId,
			@RequestParam(required=true) String isbn,
			@RequestParam(required=true) Integer nCopie,
			Authentication auth, Principal principal
			){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// --- BLOCCO DI SICUREZZA ---
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		
		if (!isAdmin) {
			Account loggedAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
			if (loggedAccount == null || !carS.isCartOwnedByAccount(chartId, loggedAccount.getId())) {
				r.setMsg("Accesso negato: non puoi modificare questo carrello.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}
		// --- FINE SICUREZZA ---

		try {
			carS.updateRow(chartId, rowId, isbn, nCopie);
			if(nCopie > 0)
				r.setMsg(msgS.get("rest_updated"));
			else
				r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	
	@DeleteMapping("/deleteRow")
	public ResponseEntity<Response> deleteRow(
			@RequestParam(required=true) Integer chartId,
			@RequestParam(required=true) Integer rowId,
			Authentication auth, Principal principal
			){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;

		// --- BLOCCO DI SICUREZZA ---
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		
		if (!isAdmin) {
			Account loggedAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
			if (loggedAccount == null || !carS.isCartOwnedByAccount(chartId, loggedAccount.getId())) {
				r.setMsg("Accesso negato: non puoi eliminare righe da questo carrello.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}
		// --- FINE SICUREZZA ---

		try {
			carS.deleteRow(chartId, rowId);
			r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody(required=true) CarrelloRequest req,
			Authentication auth, Principal principal){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		
		if (!isAdmin) {
			Account loggedAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
			if (loggedAccount == null || !carS.isCartOwnedByAccount(req.getId_account(), loggedAccount.getId())) {
				r.setMsg("Accesso negato: non puoi creare carrello per altri");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
			}
		}
		
		try {
			carS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
}