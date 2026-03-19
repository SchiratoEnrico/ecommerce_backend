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

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarrelloControllerTest {
	@Autowired
	private CarrelloController carC;

	@Test
	@Order(1)
	public void createCarrello() {
		log.debug("*** Test creazione Carrello ***");
		CarrelloRequest req = new CarrelloRequest();
		req.setId_account(1);
		ResponseEntity<Response> resp = carC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_created");
	}
	
	@Test
	@Order(2)
	public void findByIdSuccess() {
		log.debug("*** Test ricerca per id Carrello - successo ***");
		ResponseEntity<Object> resp = carC.findById(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isNotNull();
	}
	
	@Test
	@Order(3)
	public void findByIdError() {
		log.debug("*** Test ricerca per id Carrello - errore ***");
		ResponseEntity<Object> resp = carC.findById(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Assertions.assertThat(resp.getBody().toString()).isNotEmpty();
	}
	
	@Test
	@Order(4)
	public void deleteCarrelloSuccess() {
		log.debug("*** Test delete Carrello - successo ***");
		ResponseEntity<Response> resp = carC.delete(1);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_deleted");
	}
	
	@Test
	@Order(5)
	public void deleteCarrelloError() {
		log.debug("*** Test delete Carrello - errore ***");
		ResponseEntity<Response> resp = carC.delete(0);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	@Test
	@Order(6)
	public void listCarrelli() {
		log.debug("*** Test lista Carrelli - listAll ***");
		ResponseEntity<Object> resp = carC.list();
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		List<?> lC = (List<?>) resp.getBody();
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		lC.forEach(c -> log.debug(c.toString()));
	}
}
