package com.betacom.ecommerce.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
	@PostMapping("/create")
	public ResponseEntity<Response> create(@RequestBody(required=true) CarrelloRequest req){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			carS.create(req);
			r.setMsg(msgS.get("rest_created"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);
	}
	
	@PutMapping("/addRow")
	public ResponseEntity<Response> addRow(
			@RequestParam(required=true) Integer chartId,
			@RequestParam(required=true) String isbn,
			@RequestParam(required=true) Integer nCopie
			){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
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
			@RequestParam(required=true) Integer nCopie
			){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			carS.updateRow(chartId, rowId, isbn, nCopie);
			if(nCopie>0)
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
			@RequestParam(required=true) Integer rowId
			){
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			carS.deleteRow(chartId, rowId);
			r.setMsg(msgS.get("rest_deleted"));
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
			carS.delete(id);
			r.setMsg(msgS.get("rest_deleted"));
		} catch (Exception e) {
			r.setMsg(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(r);		
	}
	
	/**
	 * TODO: Da implementare
	 * filtro sui manga contenuti?
	 */
	@GetMapping("/list")
	public ResponseEntity<Object> list(
			@RequestParam(required=false) List<String> isbns){
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
	
	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/findById")
	public ResponseEntity<Object> findById (@RequestParam (required = true)  Integer id){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
			r= carS.findById(id);
		} catch (Exception e) {
			r=e.getMessage();
			status = HttpStatus.BAD_REQUEST; 
		}
		return ResponseEntity.status(status).body(r);
	}
}
