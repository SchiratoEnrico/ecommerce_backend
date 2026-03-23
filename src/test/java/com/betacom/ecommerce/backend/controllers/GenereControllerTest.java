package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.GenereRequest;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public class GenereControllerTest {
	
	@Autowired
	private GenereController genC;
	
	@Test
	public void testGenereController() {
		list();
		listById();
		create();
		update();
		delete();
	}

	public void list() {
		log.debug("start list generi test");
		ResponseEntity<?> resp = genC.list();
		assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		assertThat(((List<?>) b).size()).isGreaterThan(0);
		Assertions.assertThat(((List<?>) b).getFirst()).isInstanceOf(GenereDTO.class);

	}
	
	public void listById() {
		log.debug("start list generi by id test");
		
		ResponseEntity<?> resp = genC.findById(1);
		Object b =  resp.getBody();
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(b).isInstanceOf(GenereDTO.class);
		GenereDTO g = (GenereDTO) b;
		assertEquals(g.getDescrizione(), "AZIONE");
		
		//error
		resp = genC.findById(99);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
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
		resp = genC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
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
