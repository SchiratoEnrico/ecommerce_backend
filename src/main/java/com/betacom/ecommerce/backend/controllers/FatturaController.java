package com.betacom.ecommerce.backend.controllers;

import java.security.Principal;
import java.time.LocalDate;
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

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;

// Qui tutti endpoint admin-only tranne findById

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/fattura")
public class FatturaController {
	
	private final IFatturaServices fattS;
	private final IMessagesServices msgS;
	private final IAccountRepository accountRepository;

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/create")
    public ResponseEntity<Response> create(@RequestBody(required = true) FatturaRequest req) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
        	fattS.create(req);
            r.setMsg(msgS.get("rest_created"));
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/update")
    public ResponseEntity<Response> update(@RequestBody(required = true) FatturaRequest req) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            fattS.update(req);
            r.setMsg(msgS.get("rest_updated"));
        } catch (Exception e) {
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
            fattS.delete(id);
            r.setMsg(msgS.get("rest_deleted"));
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping ("/list")
	public ResponseEntity<Object> list(
			String numeroFattura,
			LocalDate from,
			LocalDate to,
			String clienteNome,
			String clienteCognome,
			String clienteEmail,
			String tipoPagamento,
			String tipoSpedizione,
			String statoFattura,
			Integer idOrdine,
			List<String> isbns
			){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
            r = fattS.list(
        			numeroFattura,
        			from,
        			to,
        			clienteNome,
        			clienteCognome,
        			clienteEmail,
        			tipoPagamento,
        			tipoSpedizione,
        			statoFattura,
        			idOrdine,
        			isbns
            		);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
	}
		
	//ENDPOINT SOLO ADMIN

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/reso/rifiuta")
	public ResponseEntity<Response> rifiutaReso(@RequestParam(required = true) Integer fatturaId) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            fattS.rifiutaReso(fatturaId);
            r.setMsg("reso_ref");
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(r);

	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/reso/conferma")
	public ResponseEntity<Response> confermaReso(@RequestParam(required = true) Integer fatturaId) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            fattS.confermaReso(fatturaId);
            r.setMsg("reso_conf");
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(r);

	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/reso/rimborso")
	public ResponseEntity<Response> rimborso(@RequestParam(required = true) Integer fatturaId, @RequestParam(required = true) Boolean ripristina) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            fattS.confermaReso(fatturaId);
            r.setMsg("refunded");
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(r);

	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/reso/annulla")
	public ResponseEntity<Response> annulla(@RequestParam(required = true) Integer fatturaId) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            fattS.confermaReso(fatturaId);
            r.setMsg("reso_annulla");
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(r);
	}

	//ENDPOINT CONDIVISI (ADMIN + Account corrispondente)
	@PostMapping("/reso/inizia")
    public ResponseEntity<Response> iniziaReso(@RequestParam(required = true) Integer fatturaId, @RequestParam(required = true) Integer accountId, Authentication auth, Principal principal) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;

       // BLOCCO DI SICUREZZA
       //qui l'id che arriva dal frontend è l'id dell'account
       //bisogna controllare che quindi questo id corrisponda all'id dell'utente loggato che sta facendo la richiesta
       if (!isAdminOrOwner(auth, principal, accountId)) {
    	    r.setMsg("Accesso negato: puoi chiedere il reso di ordini solo del tuo account.");
       		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
       }
       try {
            fattS.iniziaReso(fatturaId, accountId);
            r.setMsg("reso_start");
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
	
	@GetMapping("/findById")
    public ResponseEntity<Object> findById(@RequestParam(required = true) Integer idFattura, @RequestParam(required = true) Integer accountId, Authentication auth, Principal principal) {
		Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        // BLOCCO DI SICUREZZA
        //qui l'id che arriva dal frontend è l'id dell'account
        //bisogna controllare che quindi questo id corrisponda all'id dell'utente loggato che sta facendo la richiesta
        // msg: !auth_fat: Accesso negato: puoi visualizzare solo le fatture del tuo account.
        if (!isAdminOrOwner(auth, principal, accountId)) {
        	((Response) r).setMsg(msgS.get("!auth_fat"));
        		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
        }

        try {
            r = fattS.findById(idFattura);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }


	private boolean isAdminOrOwner(Authentication auth, Principal principal, Integer targetAccountId) {
		// Se ha il ruolo ADMIN, passa sempre
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		if (isAdmin) return true;

		// Se non è admin, cerchiamo il suo account nel DB usando l'username del token
		Account loggedAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
		
		// Ritorna true solo se l'ID richiesto è uguale al suo ID reale
		return loggedAccount != null && loggedAccount.getId().equals(targetAccountId);
	}

}
