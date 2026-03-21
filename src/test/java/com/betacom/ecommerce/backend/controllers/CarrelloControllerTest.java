package com.betacom.ecommerce.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class CarrelloControllerTest {
	@Autowired
	private CarrelloController carC;

	@Test
	@Order(1)
	public void createCarrello() {
		log.debug("*** Test creazione Carrello ***");
		
		log.debug("* Expected: success *");
		CarrelloRequest req = new CarrelloRequest();
		req.setId_account(1);
		ResponseEntity<Response> resp = carC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_created");
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad account id *");
		req = new CarrelloRequest();
		req.setId_account(0);
		resp = carC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing chart creation successfully ***");
	}
	
	@Test
	@Order(2)
	public void addRow() {
		log.debug("*** Test aggiunta RigaCarrello a Carrello ***");
		
		log.debug("* Expected: success *");
		ResponseEntity<Response> resp = carC.addRow(1, "DragonBall1", 1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_updated");
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id*");
		resp = carC.addRow(0, "DragonBall1", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad isbn *");
		resp = carC.addRow(1, "Kawabanga", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad copies number *");
		resp = carC.addRow(1, "DragonBall1", 0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing row adding to the chart ***");
	}
	
	@Test
	@Order(3)
	public void updateRow() {
		log.debug("*** Test aggiornamento riga carrello del Carrello ***");
		
		log.debug("* Expected: success - modifying the row *");
		ResponseEntity<Response> resp = carC.updateRow(1, 1, "DragonBall1", 2);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_updated");
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id *");
		resp = carC.updateRow(0, 1, "DragonBall1", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad isbn *");
		resp = carC.updateRow(1, 1, "Kawabanga", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: success - removing the row due to insufficient copies number *");
		resp = carC.updateRow(1, 1, "DragonBall1", 0);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_deleted");
		log.debug("* Done *");
		
		log.debug("*** Finished testing row updating of the chart ***");
	}
	
	@Test
	@Order(4)
	public void deleteRow() {
		log.debug("*** Test rimozione riga carrello dal Carrello ***");
		
		log.debug("* Expected - success *");
		ResponseEntity<Response> resp = carC.deleteRow(1, 1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_deleted");
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to chart id *");
		resp = carC.deleteRow(0, 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing row deletion from the chart ***");
	}
	
	@Test
	@Order(5)
	public void deleteCarrelloSuccess() {
		log.debug("*** Test delete Carrello - successo ***");
		
		log.debug("* Expected: success *");
		ResponseEntity<Response> resp = carC.delete(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_deleted");
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id *");
		resp = carC.delete(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing chart deletion ***");
	}
	
	@Test
	@Order(6)
	public void listCarrelli() {
		log.debug("*** Test lista Carrelli ***");
		
		log.debug("* Expected: success - listAll *");
		ResponseEntity<Object> resp = carC.list(null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		List<?> lC = (List<?>) resp.getBody();
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		lC.forEach(c -> log.debug(c.toString()+'\n'));
		log.debug("* Done *");
		
		log.debug("* Expected: success - listAll (filteredList with empty list) *");
		List<String> isbn = new ArrayList<>();
		resp = carC.list(isbn);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		lC = (List<?>) resp.getBody();
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		lC.forEach(c -> log.debug(c.toString()+'\n'));
		log.debug("* Done *");
		
		log.debug("* Expected: success - filteredList *");
		isbn.add("DragonBall1");
		resp = carC.list(isbn);			
		log.debug("Resp body: {}", resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		lC = (List<?>) resp.getBody();
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		lC.forEach(c -> log.debug(c.toString()+'\n'));
		log.debug("* Done *");
		
		log.debug("*** Finished testing list chart ***");
	}
	
	@Test
	@Order(7)
	public void findByIdSuccess() {
		log.debug("*** Test ricerca per id Carrello - successo ***");
		
		log.debug("* Expected: success *");
		ResponseEntity<Object> resp = carC.findById(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isNotNull();
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id *");
		resp = carC.findById(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Assertions.assertThat(resp.getBody().toString()).isNotEmpty();
	}
}
