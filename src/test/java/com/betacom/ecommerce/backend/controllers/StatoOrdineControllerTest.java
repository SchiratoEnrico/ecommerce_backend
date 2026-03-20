package com.betacom.ecommerce.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.StatoOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StatoOrdineControllerTest {

	@Autowired
	private StatoOrdineController statC;
	
	@Autowired
	private IMessagesServices msgS;

	@Test
	//@Order(1)
	public void testCertificatoController() {
		createTest();
		updateTest();
		listTest();
		findByIdTest();
		deleteTest();
	}
	
	public void listTest() {
		log.debug("Start StatoOrdineControllerTest.listTest()");
		
		ResponseEntity<?> resp = statC.list();
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(List.class);
		List<?> body = (List<?>) resp.getBody();
		if (body.size() > 0) {
			Assertions.assertThat(body.getFirst()).isInstanceOf(StatoOrdineDTO.class);
		}
	}
	
	public void findByIdTest() {
		// Id error
		Integer id = 99;
		log.debug("Start StatoOrdineControllerTest.findByIdTest(), error expected");
		ResponseEntity<?> resp = statC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get("!exists_sta"), resp.getBody());

		// Normal workflow
		id = 1;
		log.debug("Start StatoOrdineControllerTest.findByIdTest()");
		resp = statC.findById(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(StatoOrdineDTO.class);
		}

	public void createTest() {
		// Normal workflow
		log.debug("Start StatoOrdineControllerTest.createTest()");
		StatoOrdineRequest req = new StatoOrdineRequest();
		req.setStatoOrdine("Pagato");
		ResponseEntity<Response> resp = statC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_created"));

		// errore: null statoOrdine
		req = new StatoOrdineRequest();
		resp = statC.create(req);
		log.debug("Start StatoOrdineControllerTest.createTest(): error expected, statoOrdineReq {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_sta"));
		
		// errore: blank statoOrdine
		req = new StatoOrdineRequest();
		req.setStatoOrdine("");
		resp = statC.create(req);
		log.debug("Start StatoOrdineControllerTest.createTest(): error expected, statoOrdineReq {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_sta"));

		// errore: duplicate statoOrdine
		req = new StatoOrdineRequest();
		req.setStatoOrdine("CREATED");
		resp = statC.create(req);
		log.debug("Start StatoOrdineControllerTest.createTest(): error expected, statoOrdineReq duplicato");
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("exists_sta"));
	}

	public void updateTest() {
		// Normal workflow
		log.debug("Start StatoOrdineControllerTest.createTest()");
		StatoOrdineRequest req = new StatoOrdineRequest();
		req.setId(1);
		req.setStatoOrdine("Da pagare");
		ResponseEntity<Response> resp = statC.update(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_updated"));

		// errore: duplicate statoOrdine
		req = new StatoOrdineRequest();
		req.setId(1);
		req.setStatoOrdine("Da pagare");
		resp = statC.update(req);
		log.debug("Start StatoOrdineControllerTest.createTest(): error expected, statoOrdineReq duplicato");
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("exists_sta"));

		// errore: id sbagliato
		req = new StatoOrdineRequest();
		req.setId(100);
		req.setStatoOrdine("spedito");
		resp = statC.update(req);
		log.debug("Start StatoOrdineControllerTest.createTest(): error expected, statoOrdineReq {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_sta"));

		// errore: stato null
		req = new StatoOrdineRequest();
		req.setId(1);
		resp = statC.update(req);
		log.debug("Start StatoOrdineControllerTest.createTest(): error expected, statoOrdineReq {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_sta"));
	}

	public void deleteTest() {
		// errore: id non trovato in db/non valido
		Integer id = 99;
		log.debug("Start StatoOrdineControllerTest.deleteTest(): error expected, invalid id: {}", id);
		ResponseEntity<Response> resp = statC.delete(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_sta"));
		
		// Normal workflow
		id = 2;
		log.debug("Start StatoOrdineControllerTest.deleteTest(), id: {}", id);
		resp = statC.delete(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
	}
}
