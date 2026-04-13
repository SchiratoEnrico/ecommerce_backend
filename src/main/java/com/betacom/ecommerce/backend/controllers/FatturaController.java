package com.betacom.ecommerce.backend.controllers;

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
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;

// Qui tutti endpoint admin-only tranne findById
// iniziaReso e getNextAllowedStates

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/fattura")
public class FatturaController {
	
	private final IFatturaServices fattS;
	private final IMessagesServices msgS;

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
			@RequestParam(required=false) String numeroFattura,
			@RequestParam(required=false) LocalDate from,
			@RequestParam(required=false) LocalDate to,
			@RequestParam(required=false) String clienteNome,
			@RequestParam(required=false) String clienteCognome,
			@RequestParam(required=false) String clienteEmail,
			@RequestParam(required=false) String tipoPagamento,
			@RequestParam(required=false) String tipoSpedizione,
			@RequestParam(required=false) String statoFattura,
			@RequestParam(required=false) Integer idOrdine,
			@RequestParam(required=false) List<String> isbns
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
		
	//RESO: ENDPOINT SOLO ADMIN

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/reso/rifiuta")
	public ResponseEntity<Response> rifiutaReso(@RequestParam(required = true) Integer fatturaId) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            fattS.rifiutaReso(fatturaId);
            r.setMsg(msgS.get("reso_ref"));
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
            fattS.confermaRiconsegna(fatturaId);
            r.setMsg(msgS.get("reso_conf"));
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(r);

	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/reso/rimborso")
	public ResponseEntity<Response> rimborso(@RequestParam(required = true) Integer fatturaId, @RequestParam(required = true) Boolean ripristinaCopie) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            fattS.rimborsa(fatturaId, Boolean.TRUE.equals(ripristinaCopie));
            r.setMsg(msgS.get("reso_rimb"));
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(r);
	}

	//ENDPOINT CONDIVISI (ADMIN + Account corrispondente)
	
	// NW CONTROLLO CHE ID ACCOUNT LEGATO A FATTURA 
	// E ID ACCOUNT RICHIESTA COINCIDANO
	@PostMapping("/reso/inizia")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
    public ResponseEntity<Response> iniziaReso(
    		@RequestParam(required = true) Integer fatturaId,
    		@RequestParam(required = true) Integer accountId, 
    		Authentication auth) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;

       // BLOCCO DI SICUREZZA
       //qui l'id che arriva dal frontend è l'id dell'account
       //bisogna controllare che quindi questo id corrisponda all'id dell'utente loggato che sta facendo la richiesta
       if (!fattS.isAdminOrOwner(auth, fatturaId)) {
    	    r.setMsg(msgS.get("!owner"));
       		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
       }
       try {
            fattS.iniziaReso(fatturaId, accountId);
            r.setMsg(msgS.get("reso_start"));
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
	
	@GetMapping("/findById")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
    public ResponseEntity<Object> findById(
    		@RequestParam(required = true) Integer idFattura,
    		Authentication auth) {
		Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        // BLOCCO DI SICUREZZA
        //qui l'id che arriva dal frontend è l'id dell'account
        //bisogna controllare che quindi questo id corrisponda all'id dell'utente loggato che sta facendo la richiesta
        // msg: !auth_fat: Accesso negato: puoi visualizzare solo le fatture del tuo account.
        if (!fattS.isAdminOrOwner(auth, idFattura)) {
        		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msgS.get("!auth_fat"));
        }

        try {
            r = fattS.findById(idFattura);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

    @GetMapping("/get_next_allowed_states")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
    public ResponseEntity<Object> getNextAllowedStates(
    		@RequestParam(required = true) Integer idFattura, 
    		Authentication auth) {
        Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        if (!fattS.isAdminOrOwner(auth, idFattura)) {
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msgS.get("!owned_fat"));
        }

        try {
            r = fattS.getNextAllowedStates(idFattura);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
}
