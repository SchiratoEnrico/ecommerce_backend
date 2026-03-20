package com.betacom.ecommerce.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import com.betacom.ecommerce.backend.dto.inputs.GenereRequest;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class GenereControllerTest {
	
	@Autowired
	private GenereController genC;
	
	@Test
	@Order(1)
	public void list() {
		log.debug("start list generi test");
		
		ResponseEntity<?> resp = genC.list();
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
	}
	
	@Test
	@Order(2)
	public void listById() {
		log.debug("start list generi by id test");
		
		ResponseEntity<?> resp = genC.findById(1);
		GenereDTO g = (GenereDTO)resp.getBody();
		assertEquals(g.getDescrizione(), "AZIONE");
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		
		//error
		resp = genC.findById(99);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		
	}
	
	@Test
	@Order(3)
	public void create() {
		GenereRequest req = new GenereRequest();
		
		req.setDescrizione(" comico");
		
		ResponseEntity<?> resp = genC.create(req);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		

		resp = genC.findById(3);
		GenereDTO g = (GenereDTO)resp.getBody();
		assertEquals(g.getDescrizione(), "COMICO");
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		
		//errore duplicazione
		ResponseEntity<?> resp1 = genC.create(req);
		
		assertEquals(HttpStatus.CONFLICT, resp1.getStatusCode());
	}
	
	@Test
	@Order(4)
	public void update() {
	    GenereRequest req = new GenereRequest();

	    // creo prima un altro genere che servirà per il test duplicato
	    GenereRequest createReq = new GenereRequest();
	    createReq.setDescrizione("comico");
	    genC.create(createReq);

	    req.setDescrizione(" romantico");
	    req.setId(1);

	    ResponseEntity<?> resp = genC.update(req);
	    assertEquals(HttpStatus.OK, resp.getStatusCode());

	    // update error id inesistente
	    req.setId(99);
	    resp = genC.update(req);
	    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

	    // update error duplicato
	    req.setDescrizione(" comico");
	    req.setId(1);
	    resp = genC.update(req);

	    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	
	    //update error id
	    req.setDescrizione(" comico");
	    req.setId(99);
	    resp = genC.update(req);

	    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	
	 // update error request null
	    resp = genC.update(null);

	    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	@Test
	@Order(4)
	public void delete() {
		
		//delete a buon fine
		ResponseEntity<?> resp = genC.delete(3);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		
		//delete andato male id sbagliato
		resp = genC.delete(99);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		
		//delete andato male ci sono manga collegati
		resp = genC.delete(1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
}
