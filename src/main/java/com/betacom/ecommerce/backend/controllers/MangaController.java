package com.betacom.ecommerce.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMangaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/manga")
@RequiredArgsConstructor
@Slf4j
public class MangaController {
	
	private final IMangaServices mangaS;
	private final IMessagesServices msgS;
	
	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody (required = true) MangaRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			mangaS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) { 
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		
		return ResponseEntity.status(status).body(r);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Response> update(@RequestBody (required = true) MangaRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			mangaS.update(req);
			r.setMsg(msgS.get("rest_updated"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		
		return ResponseEntity.status(status).body(r);
	}
	
	
	@GetMapping("/list")
	public ResponseEntity<Object> list(
			@RequestParam (required = false)  String titolo,
			@RequestParam (required = false)  String casaEditriceNome,
			@RequestParam (required = false)  String autoreNome,
			@RequestParam (required = false)  String sagaNome,
			@RequestParam (required = false)  Integer sagaId,
			@RequestParam (required = false)  Integer casaEditriceId,
			@RequestParam (required = false)  Integer autoreId,
			@RequestParam (required = false)  List<Integer> generiId
			){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r =  mangaS.list(
					titolo,
					casaEditriceNome,
					autoreNome,
					sagaNome,
					sagaId,
					casaEditriceId,
					autoreId,
					generiId
					);
		}catch (Exception e) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}	
		return ResponseEntity.status(status).body(r);
	}

	@GetMapping ("/find_by_isbn")
	public ResponseEntity<Object> findById(@RequestParam (required = true) String id){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r =  mangaS.findByIsbn(id);
		}catch (Exception e) {
			r = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		}	
		
		return ResponseEntity.status(status).body(r);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Response> delete(@RequestParam(required = true) String id){
	    Response r = new Response();
	    HttpStatus status = HttpStatus.OK;

	    try {
	        mangaS.delete(id);
	        r.setMsg(msgS.get("rest_deleted"));
	    } catch (Exception e) {
	        r.setMsg(msgS.get(e.getMessage()));
	        status = HttpStatus.BAD_REQUEST;
	    }

	    return ResponseEntity.status(status).body(r);
	}
}
