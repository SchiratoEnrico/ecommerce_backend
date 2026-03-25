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

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.inputs.TipoSpedizioneRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TipoSpedizioneControllerTest {
	
	@Autowired
	private TipoSpedizioneController speC;
	@Autowired 
	private IMessagesServices msgS;

	@Test
	public void testTipoSpedizioneController() {
		createSpedizione();
		findByIdSuccess();
		findByIdError();
		updteSpedizioneSuccess();
		updateSpedizioneError();
		updateTipoSpedizioneDuplicata();
		deleteSpedizioneSuccess();
		deleteSpedizioneError();
		listSpedizioni();
	}

	public void createSpedizione() {
		log.debug("*** Test creazione Tipo spedizione ***");
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setTipoSpedizione("Aereo");
		ResponseEntity<Response> resp = speC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_created"));
	}

	public void findByIdSuccess() {
		log.debug("*** Test ricerca per Tipo spedizione per id - successo ***");
		ResponseEntity<Object> resp = speC.findById(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Object b =  resp.getBody();
		Assertions.assertThat(b).isNotNull();
		Assertions.assertThat(b).isInstanceOf(TipoSpedizioneDTO.class);
		assertThat(((TipoSpedizioneDTO) b).getId()).isEqualTo(1);
	}
	
	public void findByIdError() {
		log.debug("*** Test ricerca per Tipo spedizione per id - errore ***");
		ResponseEntity<Object> resp = speC.findById(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Assertions.assertThat(resp.getBody().toString()).isNotEmpty();
	}

	public void updteSpedizioneSuccess() {
		log.debug("*** Test update Tipo spedizione - successo ***");
		
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setId(1);
		req.setTipoSpedizione("Treno");
		
		ResponseEntity<Response> resp = speC.update(req);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_updated"));
	}
	
	public void updateSpedizioneError() {
		log.debug("*** Test update Tipo spedizione - errore ***");
		
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setId(0);
		req.setTipoSpedizione("Aereo");
		
		ResponseEntity<Response> resp = speC.update(req);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	public void updateTipoSpedizioneDuplicata() { 
		log.debug("Begin update TipoSpedizione test");
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setId(1);
		req.setTipoSpedizione("Treno");
		
		ResponseEntity<Response> re = speC.update(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("exists_spe");
			
	}
	
	
	public void deleteSpedizioneSuccess() {
		log.debug("*** Test delete Spedizione - successo ***");
		
		ResponseEntity<Response> resp = speC.delete(2);
		Assertions.assertThat(resp.getBody().getMsg()).isEqualTo(msgS.get("rest_deleted"));

		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
	}
	
	public void deleteSpedizioneError() {
		log.debug("*** Test delete Spedizione - errore ***");
		ResponseEntity<Response> resp = speC.delete(0);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	public void listSpedizioni() {
		log.debug("*** Test list Spedizioni ***");
		
		log.debug("* list: no params *");
		ResponseEntity<?> resp = speC.list(null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Object body = resp.getBody();
		Assertions.assertThat(body).isInstanceOf(List.class);

		List<?> lS = (List<?>) body;
		Assertions.assertThat(lS.size()).isGreaterThan(0);
		Assertions.assertThat(lS.getFirst()).isInstanceOf(TipoSpedizioneDTO.class);
		lS.forEach(s -> log.debug(s.toString()));
		
		log.debug("* list: with params *");
		resp = speC.list("Aereo");
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		body = resp.getBody();
		Assertions.assertThat(body).isInstanceOf(List.class);
		lS = (List<?>) body;
		Assertions.assertThat(lS.size()).isGreaterThan(0);
		Assertions.assertThat(lS.getFirst()).isInstanceOf(TipoSpedizioneDTO.class);
		lS.forEach(s -> log.debug(s.toString()));
	}
	

}
