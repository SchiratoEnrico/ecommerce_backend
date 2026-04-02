package com.betacom.ecommerce.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.ecommerce.backend.dto.inputs.SagaRequest;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.ISagaServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/saga")
@RequiredArgsConstructor
@Slf4j
public class SagaController {
	private final ISagaServices sagaS;
	private final IMessagesServices msgS;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody (required = true) SagaRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			sagaS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) { 
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		
		return ResponseEntity.status(status).body(r);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/update")
	public ResponseEntity<Response> update(@RequestBody (required = true) SagaRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			sagaS.update(req);
			r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			status = HttpStatus.BAD_REQUEST;
		}
		
		return ResponseEntity.status(status).body(r);
	}
	
	
	@GetMapping("/list")
	public ResponseEntity<Object> list(
			@RequestParam (required = false)  String casaEditriceNome,
			@RequestParam (required = false)  String autoreNome,
			@RequestParam (required = false)  String autoreCognome,
			@RequestParam (required = false)  String sagaNome,
			@RequestParam (required = false)  Integer sagaId,
			@RequestParam (required = false)  Integer casaEditriceId,
			@RequestParam (required = false)  Integer autoreId,
			@RequestParam (required = false)  List<Integer> generiId
			){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r =  sagaS.list(
					sagaNome,
					casaEditriceNome,
					autoreNome,
					autoreCognome,
					sagaId,
					casaEditriceId,
					autoreId,
					generiId
					);
		}catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}	
		return ResponseEntity.status(status).body(r);
	}

	@GetMapping ("/find_by_isbn")
	public ResponseEntity<Object> findById(@RequestParam (required = true) Integer id){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r =  sagaS.findById(id);
		}catch (Exception e) {
			r = msgS.get(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}	
		
		return ResponseEntity.status(status).body(r);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/delete")
	public ResponseEntity<Response> delete(@RequestParam(required = true) Integer id){
	    Response r = new Response();
	    HttpStatus status = HttpStatus.OK;

	    try {
	        sagaS.delete(id);
	        r.setMsg(msgS.get("rest_deleted"));
	    } catch (Exception e) {
	        r.setMsg(msgS.get(e.getMessage()));
	        status = HttpStatus.BAD_REQUEST;
	    }

	    return ResponseEntity.status(status).body(r);
	}

}
