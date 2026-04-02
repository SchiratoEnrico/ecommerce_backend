package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.SagaRequest;
import com.betacom.ecommerce.backend.dto.outputs.SagaDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public class SagaControllerTest {
    @Autowired
    private SagaController sagaC;
    
    @Autowired
	private IMessagesServices msgS;

	@Test
	public void testSagaController() {
		listTest();
		findByIdTest();
		createTest();
		updateTest();
		deleteTest();
	}
	
	private List<SagaDTO> getLoadedList(
			String casaEditriceNome,
			String autoreNome,
			String autoreCognome,
			String sagaNome,
			Integer sagaId,
			Integer casaEditriceId,
			Integer autoreId,
			List<Integer> generiId
			) {

    	ResponseEntity<?> resp = sagaC.list(
				casaEditriceNome,
				autoreNome,
				autoreCognome,
				sagaNome,
				sagaId,
				casaEditriceId,
				autoreId,
				generiId
        		);
    	// test per:
    	// linked_ord 
    	// linked_car
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);

    	return ((List<SagaDTO>) resp.getBody());
	}
	
	public void listTest() {
        log.debug("start list saga test");
		String casaEditriceNome = "";
		String autoreNome = "";
		String autoreCognome = "";
		String sagaNome = "";
		Integer sagaId = null;
		Integer casaEditriceId = null;
		Integer autoreId = null;
		List<Integer> generiId = new ArrayList<Integer>();

		// test vuoto
		List<SagaDTO> lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
		
    	// sagaNome
    	sagaNome = "piec";
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	sagaNome = "";
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// sagaNome
    	casaEditriceNome = "Shueis";
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceNome = "";
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// autoreNome
    	autoreNome = "iich";
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	autoreNome = "";
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// sagaId
    	sagaId= 1;
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	sagaId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// autoreId
    	autoreId= 1;
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	autoreId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// casaEditriceId
    	casaEditriceId= 1;
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// generiId
    	generiId.add(2);
    	lS = getLoadedList(casaEditriceNome, autoreNome, sagaNome, autoreCognome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));
	}

	public void findByIdTest() {
		// Id error
		Integer id = 99;
		String msg = "!exists_sag";
		log.debug("Start testSagaController.findByIdTest(), error expected");
		ResponseEntity<?> resp = sagaC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody());

		// Normal workflow
		id = 1;
		log.debug("Start testSagaController.findByIdTest()");
		resp = sagaC.findById(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(SagaDTO.class);
	}
	
	private SagaRequest getReq() {
		SagaRequest r = new SagaRequest();
		r.setNome("Ken Shiro");
		r.setDescrizione("Saga su formidabli guerrieri");
		return r;
	}
	
	public void createTest() {
		String msg = "null_req";
		log.debug("Start testSagaController.createTest(), error expected: {}", msg);
		ResponseEntity<Response> resp = sagaC.create(null);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());
		
		SagaRequest r = getReq();
		r.setNome(null);
		msg = "null_snom";
		log.debug("Start testSagaController.create(), error expected: {}", msg);
		resp = sagaC.create(r);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());

		r = getReq();
		r.setNome("One Piece");
		msg = "exists_sag";
		log.debug("Start testSagaController.create(), error expected: {}", msg);
		resp = sagaC.create(r);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());
		
		r = getReq();
		r.setDescrizione(null);
		msg = "null_desc";
		log.debug("Start testSagaController.create(), error expected: {}", msg);
		resp = sagaC.create(r);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());
		
		r = getReq();
		List<String> manga = new ArrayList<String>();
		manga.add("ABC");
		r.setManga(manga);
		msg = "!exists_man";
		log.debug("Start testSagaController.create(), error expected: {}", msg);
		resp = sagaC.create(r);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());
		
		r = getReq();
		msg = "rest_created";
		manga = new ArrayList<String>();
		manga.add("ISBN001");
		r.setManga(manga);

		log.debug("Start testSagaController.create(): {}", msg);
		resp = sagaC.create(r);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());
	}

	public void updateTest() {
		SagaRequest r = getReq();
		String msg = "!exists_sag";
		r.setId(99);

		log.debug("Start testSagaController.update(), error expected: {}", msg);
		ResponseEntity<Response> resp = sagaC.update(r);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());
		
		msg = "!exists_man";
		r = getReq();
		r.setId(1);
		List<String> manga = new ArrayList<String>();
		manga.add("kutvg");
		r.setManga(manga);
		log.debug("Start testSagaController.update(), error expected: {}", msg);
		resp = sagaC.update(r);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());

		msg = "rest_updated";
		r = getReq();
		r.setId(1);
		r.setDescrizione("descrizione aggriornata");
		log.debug("Start testSagaController.update(), error expected: {}", msg);
		resp = sagaC.update(r);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody().getMsg());
	}

	public void deleteTest() {
		// errore: id non trovato in db/non valido
		Integer id = 99;
		log.debug("Start testSagaController.deleteTest(): error expected, invalid id: {}", id);
		ResponseEntity<Response> resp = sagaC.delete(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_sag"));
		
		// Errore: exists_sagman: manga collegati a saga
		id = 1;
		log.debug("Start testSagaController.deleteTest(), id: {}", id);
		resp = sagaC.delete(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("exists_sagman"));

		
		// Normal workflow
		id = 3;
		log.debug("Start testSagaController.deleteTest(), id: {}", id);
		resp = sagaC.delete(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
	}
}
