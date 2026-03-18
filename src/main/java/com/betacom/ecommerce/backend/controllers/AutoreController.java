package com.betacom.ecommerce.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IAutoreServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/autore")
public class AutoreController {
	
	private final IAutoreServices autS;
	private final IMessagesServices msgS;
	
	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody (required = true) AutoreRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			autS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.CONFLICT;
		}
		
		return ResponseEntity.status(status).body(r);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Response> update(@RequestBody (required = true) AutoreRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			autS.update(req);
			r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.CONFLICT;
		}
		
		return ResponseEntity.status(status).body(r);
	}
	
	@GetMapping("/list")
	public ResponseEntity<Object> list(){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r =  autS.list();
		}catch (Exception e) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}	
		
		return ResponseEntity.status(status).body(r);
	}
	
	@GetMapping ("/findById")
	public ResponseEntity<Object> findById(@RequestParam (required = true) Integer id){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r =  autS.findById(id);
		}catch (Exception e) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}	
		
		return ResponseEntity.status(status).body(r);
	}
}
