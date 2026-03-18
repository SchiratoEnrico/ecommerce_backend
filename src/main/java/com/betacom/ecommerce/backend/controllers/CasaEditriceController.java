package com.betacom.ecommerce.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.ecommerce.backend.dto.inputs.CasaEditriceRequest;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.ICasaEditriceServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/casa_editrice")
public class CasaEditriceController {

	private final ICasaEditriceServices casS;
	private final IMessagesServices msgS;
	
	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody(required=true) CasaEditriceRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			casS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@PostMapping("/update")
	public ResponseEntity<Response> update(@RequestBody(required=true) CasaEditriceRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			casS.update(req);
			r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Response> delete(@PathVariable(required = true)  Integer id){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			casS.delete(id);
			r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);		
	}
	
	/**
	 * TODO: Da testare
	 * @param nome
	 * @param descrizione
	 * @param indirizzo
	 * @param email
	 * @return
	 */
	@GetMapping("/list")
	public ResponseEntity<Object> list(
			@RequestParam (required = false)  String nome,
			@RequestParam (required = false)  String descrizione,
			@RequestParam (required = false)  String indirizzo,
			@RequestParam (required = false)  String email
			){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r= casS.list(nome, descrizione, indirizzo, email);
		} catch (Exception e) {
			r=e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@GetMapping("/findById")
	public ResponseEntity<Object> findById (@RequestParam (required = true)  Integer id){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r= casS.findById(id);
		} catch (Exception e) {
			r=e.getMessage();
			status = HttpStatus.BAD_REQUEST; 
		}
		return ResponseEntity.status(status).body(r);
	}
	
}
