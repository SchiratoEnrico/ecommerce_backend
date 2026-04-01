package com.betacom.ecommerce.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.ecommerce.backend.dto.inputs.RigaFatturaRequest;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaFatturaServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/riga_fattura")
public class RigaFatturaController {

	private final IRigaFatturaServices rowS;
	private final IMessagesServices msgS;

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/create")
    public ResponseEntity<Response> create(@RequestBody(required = true) RigaFatturaRequest req) {
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
    public ResponseEntity<Response> update(@RequestBody(required = true) RigaFatturaRequest req) {
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

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping ("/list")
	public ResponseEntity<Object> list(){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
            r = rowS.list();
        } catch (MangaException e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/findById")
    public ResponseEntity<Object> findById(@RequestParam(required = true) Integer id) {
		Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        try {
            r = rowS.findById(id);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
}
