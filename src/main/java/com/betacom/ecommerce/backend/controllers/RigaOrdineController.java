package com.betacom.ecommerce.backend.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.betacom.ecommerce.backend.dto.inputs.RigaOrdineRequest;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IOrdineServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaOrdineServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/riga_ordine")
public class RigaOrdineController {
	private final IRigaOrdineServices rowS;
	private final IOrdineServices ordS; // Aggiunto per controlli sicurezza
	private final IMessagesServices msgS;
	private final IAccountRepository accountRepository; 

	private boolean isAdmin(Authentication auth) {
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
	}

	private Account getLoggedAccount(Principal principal) {
		return accountRepository.findByUsername(principal.getName()).orElse(null);
	}

	//SOLO ADMIN POSSONO TOCCARE LE RIGHE DI UN ORDINE GIÀ PIAZZATO 

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/create")
    public ResponseEntity<Response> create(@RequestBody(required = true) RigaOrdineRequest req) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            rowS.create(req);
            r.setMsg(msgS.get("rest_created"));
        } catch (MangaException e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable(required = true) Integer id) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            rowS.delete(id);
            r.setMsg(msgS.get("rest_deleted"));
        } catch (MangaException e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/update")
    public ResponseEntity<Response> update(@RequestBody(required = true) RigaOrdineRequest req) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            rowS.update(req);
            r.setMsg(msgS.get("rest_updated"));
        } catch (MangaException e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

	//  LETTURE CONDIVISE (Proprietario o Admin) 

	@GetMapping ("/list")
	public ResponseEntity<Object> list(
			@RequestParam(required=false) Integer idOrdine, Authentication auth, Principal principal){
		
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;

		// SICUREZZA: Utente deve per forza filtrare per idOrdine, e deve essere il suo.
		if (!isAdmin(auth)) {
			Account loggedAcc = getLoggedAccount(principal);
			if (idOrdine == null || loggedAcc == null || !ordS.isOrdineOwnedByAccount(idOrdine, loggedAcc.getId())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato.");
			}
		}

		try {
            r = rowS.list(idOrdine);
        } catch (MangaException e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
	}
	
	@GetMapping("/findById")
    public ResponseEntity<Object> findById(@RequestParam(required = true) Integer id, Authentication auth, Principal principal) {
		Object r = new Object();
        HttpStatus status = HttpStatus.OK;

		// SICUREZZA
		if (!isAdmin(auth)) {
			Account loggedAcc = getLoggedAccount(principal);
			if (loggedAcc == null || !rowS.isRigaOrdineOwnedByAccount(id, loggedAcc.getId())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato.");
			}
		}

        try {
            r = rowS.findById(id);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
}