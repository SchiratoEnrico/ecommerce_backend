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

import com.betacom.ecommerce.backend.dto.inputs.CasaEditriceRequest;
import com.betacom.ecommerce.backend.response.Response;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CasaEditriceControllerTest {
	@Autowired 
	private CasaEditriceController casC;
	
	@Test
	@Order(1)
	public void createCasa() {
		log.debug("*** Test creazione Case Editrice ***");
		CasaEditriceRequest req = new CasaEditriceRequest();
		req.setDescrizione("String");
		req.setEmail("Email");
		req.setIndirizzo("Indiririzzo");
		req.setNome("Nome");
		ResponseEntity<Response> resp = casC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_created");
	}
	
	@Test
	@Order(2)
	public void findCasaByIdSuccesso() {
		log.debug("*** Test ricerca Casa Editrice per id - successo");
		ResponseEntity<Object> resp = casC.findById(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isNotNull();
	}
	
	@Test
	@Order(3)
	public void findCasaByIdErrore() {
		log.debug("*** Test ricerca Casa Editrice per id - errore ***");
		ResponseEntity<Object> resp = casC.findById(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Assertions.assertThat(resp.getBody().toString()).isNotEmpty();
	}
	
	@Test
	@Order(4)
	public void updateCasaSuccess() {
		log.debug("*** Test update Casa Editrice - successo ***");
		
		CasaEditriceRequest req = new CasaEditriceRequest();
		req.setId(1);
		req.setDescrizione("string");
		req.setEmail("string");
		req.setIndirizzo("string");
		req.setNome("string");
		
		ResponseEntity<Response> resp = casC.update(req);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_updated");
	}
	
	@Test
	@Order(5)
	public void updateCasaError() {
		log.debug("*** Test update Casa Editrice - errore ***");
		
		CasaEditriceRequest req = new CasaEditriceRequest();
		req.setId(0);
		req.setDescrizione("string");
		req.setEmail("string");
		req.setIndirizzo("string");
		req.setNome("string");
		
		ResponseEntity<Response> resp = casC.update(req);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	@Test
	@Order(6)
	public void deleteCasaSuccess() {
		log.debug("*** Test delete Casa Editrice - successo ***");
		
		ResponseEntity<Response> resp = casC.delete(1);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_deleted");
	}
	
	@Test
	@Order(7)
	public void deleteCasaError() {
		log.debug("*** Test delete Casa Editrice - errore ***");
		
		ResponseEntity<Response> resp = casC.delete(0);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	@Test
	@Order(8)
	public void listCase() {
		log.debug("*** Test list Case ***");
		
		log.debug("* list: no params *");
		ResponseEntity<?> resp = casC.list(null, null, null, null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Object body = resp.getBody();
		List<?> lC = (List<?>) body;
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		lC.forEach(c -> c.toString());
		
		log.debug("* list: with params *");
		resp = casC.list("Casa", null, null, null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		body = resp.getBody();
		lC = (List<?>) body;
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		lC.forEach(c -> c.toString());
		
	}
	
}
