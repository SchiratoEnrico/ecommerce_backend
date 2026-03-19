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

import com.betacom.ecommerce.backend.dto.inputs.TipoSpedizioneRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TipoSpedizioneControllerTest {
	
	@Autowired
	private TipoSpedizioneController speC;
	
	@Test
	@Order(1)
	public void createSpedizione() {
		log.debug("*** Test creazione Tipo spedizione ***");
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setTipoSpedizione("Aereo");
		ResponseEntity<Response> resp = speC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_created");
	}

	@Test
	@Order(2)
	public void findByIdSuccess() {
		log.debug("*** Test ricerca per Tipo spedizione per id - successo ***");
		ResponseEntity<Object> resp = speC.findById(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isNotNull();
	}
	
	@Test
	@Order(3)
	public void findByIdError() {
		log.debug("*** Test ricerca per Tipo spedizione per id - errore ***");
		ResponseEntity<Object> resp = speC.findById(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Assertions.assertThat(resp.getBody().toString()).isNotEmpty();
	}
	
	@Test
	@Order(4)
	public void updteSpedizioneSuccess() {
		log.debug("*** Test update Tipo spedizione - successo ***");
		
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setId(1);
		req.setTipoSpedizione("Aereo");
		
		ResponseEntity<Response> resp = speC.update(req);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_updated");
	}
	
	@Test
	@Order(5)
	public void updateSpedizioneError() {
		log.debug("*** Test update Tipo spedizione - errore ***");
		
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setId(0);
		req.setTipoSpedizione("Aereo");
		
		ResponseEntity<Response> resp = speC.update(req);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	@Test
	@Order(6)
	public void deleteSpedizioneSuccess() {
		log.debug("*** Test delete Spedizione - successo ***");
		
		ResponseEntity<Response> resp = speC.delete(1);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo("rest_deleted");
	}
	
	@Test
	@Order(7)
	public void deleteSpedizioneError() {
		log.debug("*** Test delete Spedizione - errore ***");
		ResponseEntity<Response> resp = speC.delete(0);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	@Test
	@Order(8)
	public void listSpedizioni() {
		log.debug("*** Test list Spedizioni ***");
		
		log.debug("* list: no params *");
		ResponseEntity<?> resp = speC.list(null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Object body = resp.getBody();
		List<?> lS = (List<?>) body;
		Assertions.assertThat(lS.size()).isGreaterThan(0);
		lS.forEach(s -> log.debug(s.toString()));
		
		log.debug("* list: with params *");
		resp = speC.list("Aereo");
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		body = resp.getBody();
		lS = (List<?>) body;
		Assertions.assertThat(lS.size()).isGreaterThan(0);
		lS.forEach(s -> log.debug(s.toString()));
	}
	

}
