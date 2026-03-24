package com.betacom.ecommerce.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.betacom.ecommerce.backend.dto.inputs.RigaCarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaCarrelloDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class RigaCarrelloControllerTest {
	@Autowired
	private RigaCarrelloController rowC;
	@Autowired
	private IMessagesServices msgS;
	
	@Test
	public void testRigaCarrelloController() {
		createTest();
		updateTest();
		listTest();
		findByIdTest();
		deleteTest();
	}

    private RigaCarrelloRequest getProva() {
    	RigaCarrelloRequest r = new RigaCarrelloRequest();
        r.setCarrelloId(1);
        r.setManga("ISBN002");
        r.setNumeroCopie(1);
        return r;
    }
    	
	public void listTest() {
		log.debug("Start RigaCarrelloControllerTest.listTest()");
		
		ResponseEntity<?> resp = rowC.list();

		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(List.class);
		List<?> body = (List<?>) resp.getBody();
		if (body.size() > 0) {
			Assertions.assertThat(body.getFirst()).isInstanceOf(RigaCarrelloDTO.class);
		}
	}
	
	public void findByIdTest() {
		// Id error
		Integer id = 99;
		log.debug("Start RigaCarrelloControllerTest.findByIdTest(), error expected");
		ResponseEntity<?> resp = rowC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get("!exists_rcr"), resp.getBody());

		// Normal workflow
		id = 1;
		log.debug("Start RigaCarrelloControllerTest.findByIdTest()");
		resp = rowC.findById(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(RigaCarrelloDTO.class);
		}

	public void createTest() {
		// Normal workflow
		String msg = "rest_created";
		log.debug("Start RigaCarrelloControllerTest.createTest()");
		RigaCarrelloRequest req = getProva();
		ResponseEntity<Response> resp = rowC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));
		
		// null request: null_crq
		msg = "null_crq";
		resp = rowC.create(null);
		log.debug("Start RigaCarrelloControllerTest.createTest(): error expected, RigaCarrelloRequest null");
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

		// errore: id Carrello null
		msg = "null_cri";
		req = getProva();
		req.setCarrelloId(null);
		resp = rowC.create(req);
		log.debug("Start RigaCarrelloControllerTest.createTest(): error expected, RigaCarrelloRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));
		
		// errore: manga null
		msg = "null_man";
		req = getProva();
		req.setManga(null);
		resp = rowC.create(req);
		log.debug("Start RigaCarrelloControllerTest.createTest(): error expected, RigaCarrelloRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

		// errore: manga null
		msg = "null_qua";
		req = getProva();
		req.setNumeroCopie(null);
		resp = rowC.create(req);
		log.debug("Start RigaCarrelloControllerTest.createTest(): error expected, RigaCarrelloRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

		// errore: id Ordine non esistente
		msg = "!exists_car";
		req = getProva();
		req.setCarrelloId(99);
		resp = rowC.create(req);
		log.debug("Start RigaCarrelloControllerTest.createTest(): error expected, RigaCarrelloRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));
		
		// errore: ISBN invalido, !exsits_man
		msg = "!exists_man";
		req = getProva();
		req.setManga("AAABBBB");
		log.debug("Start RigaCarrelloControllerTest.createTest(): error expected, RigaCarrelloRequest\n\t\t\t{}", req);
		resp = rowC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));
	}

	public void updateTest() {
		// Normal workflow
		String msg = "rest_updated";
		RigaCarrelloRequest req = getProva();
		req.setId(1);
		req.setCarrelloId(null);
		log.debug("Start RigaCarrelloControllerTest.updateTest(), req: {}", req);
		ResponseEntity<Response> resp = rowC.update(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

		// errore: id carrello != null
		msg = "id_chng";
		req = getProva();
		req.setId(1);
		log.debug("Start RigaCarrelloControllerTest.updateTest(): error expected, RigaCarrelloRequest {}", req);
		resp = rowC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

		// errore: id sbagliato
		msg = "!exists_rcr";
		req = new RigaCarrelloRequest();
		req.setId(100);
		req.setCarrelloId(null);
		resp = rowC.update(req);
		log.debug("Start RigaCarrelloControllerTest.updateTest(): error expected, RigaCarrelloRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

		// errore: isbn non valido
		msg = "!exists_man";
		req = new RigaCarrelloRequest();
		req.setId(1);
		req.setCarrelloId(null);
		req.setManga("WEWEWEW");
		resp = rowC.update(req);
		log.debug("Start RigaCarrelloControllerTest.updateTest(): error expected, RigaCarrelloRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));
	}
	
	public void deleteTest() {
		// errore: id non trovato in db/non valido
		Integer id = 99;
		log.debug("Start RigaCarrelloControllerTest.deleteTest(): error expected, invalid id: {}", id);
		ResponseEntity<Response> resp = rowC.delete(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_rcr"));
		
		// Normal workflow
		id = 1;
		log.debug("Start RigaCarrelloControllerTest.deleteTest(), id: {}", id);
		resp = rowC.delete(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
	}
}
