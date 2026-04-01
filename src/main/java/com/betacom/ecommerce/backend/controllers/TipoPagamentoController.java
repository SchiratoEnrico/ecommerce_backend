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

import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.ITipoPagamentoServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/tipo_pagamento")
public class TipoPagamentoController {

	private final ITipoPagamentoServices pagS;
	private final IMessagesServices msgS;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/create")
    public ResponseEntity<Response> create(@RequestBody(required = true) TipoPagamentoRequest req) {
        Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
           pagS.create(req);
            r.setMsg(msgS.get("rest_created"));
        } catch (MangaException e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/update")
    public ResponseEntity<Response> update(@RequestBody(required = true) TipoPagamentoRequest req) {
        Response r = new Response();
        HttpStatus status = HttpStatus.OK;

        try {
        	pagS.update(req);
            r.setMsg(msgS.get("rest_updated"));
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
            pagS.delete(id);
            r.setMsg(msgS.get("rest_deleted"));
        } catch (MangaException e) {
            r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
    
	@GetMapping("/list")
    public ResponseEntity<Object> list() {
    	Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        try {
            r = pagS.list();
        } catch (MangaException e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
    
	@GetMapping ("/findById")
	public ResponseEntity<Object> findById(@RequestParam(required=true) Integer id){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		
		try {
			r = pagS.findById(id);
		}catch(Exception e ) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r); 
	}
	
}
