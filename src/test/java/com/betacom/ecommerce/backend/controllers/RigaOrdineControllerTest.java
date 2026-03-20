
package com.betacom.ecommerce.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.RigaOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaOrdineDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
//@Transactional // 🔥 rollback after test → no DB pollution
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RigaOrdineControllerTest {

	@Autowired
	private RigaOrdineController rowC;
	@Autowired
	private IMessagesServices msgS;
	
	@Test
	public void testRigaOrdineController() {
		createTest();
		updateTest();
		listTest();
		findByIdTest();
		deleteTest();
	}
	
    private RigaOrdineRequest getProva() {
        return RigaOrdineRequest.builder()
                .idOrdine(1)
                .manga("ISBN002")
                .numeroCopie(1)
                .build();
    }
    
    private void printList() {
		ResponseEntity<?> resp = rowC.list();
		List<?> body = (List<?>) resp.getBody();
		
		if (body.size() > 0) {
			body.forEach((rigaOrdineDTO -> log.debug(rigaOrdineDTO.toString())));
		}
    }
	
	public void listTest() {
		log.debug("Start RigaOrdineControllerTest.listTest()");
		
		ResponseEntity<?> resp = rowC.list();
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(List.class);
		List<?> body = (List<?>) resp.getBody();
		if (body.size() > 0) {
			Assertions.assertThat(body.getFirst()).isInstanceOf(RigaOrdineDTO.class);
		}
	}
	
	public void findByIdTest() {
		// Id error
		Integer id = 99;
		log.debug("Start RigaOrdineControllerTest.findByIdTest(), error expected");
		ResponseEntity<?> resp = rowC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get("!exists_row"), resp.getBody());

		// Normal workflow
		id = 1;
		log.debug("Start RigaOrdineControllerTest.findByIdTest()");
		resp = rowC.findById(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(RigaOrdineDTO.class);
		}

	public void createTest() {
		// Normal workflow
		log.debug("Start RigaOrdineControllerTest.createTest()");
		RigaOrdineRequest req = getProva();
		req.setId(null);
		ResponseEntity<Response> resp = rowC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_created"));

		// errore: id Ordine null
		req.setIdOrdine(null);
		resp = rowC.create(req);
		log.debug("Start RigaOrdineControllerTest.createTest(): error expected, RigaOrdineRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_ord"));
		
		// errore: id Ordine non esistente
		req.setIdOrdine(99);
		resp = rowC.create(req);
		log.debug("Start RigaOrdineControllerTest.createTest(): error expected, RigaOrdineRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_ord"));
		
		// errore: ISBN null
		req = getProva();
		req.setManga(null);
		log.debug("Start RigaOrdineControllerTest.createTest(): error expected, RigaOrdineRequest\n\t\t\t{}", req);
		printList();
		resp = rowC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_man"));

		// errore: ISBN non esistente
		req = getProva();
		req.setManga("LALALALAL");
		log.debug("Start RigaOrdineControllerTest.createTest(): error expected, RigaOrdineRequest {}", req);
		resp = rowC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_man"));
	}

	public void updateTest() {
		// Normal workflow
		RigaOrdineRequest req = getProva();
		req.setId(1);
		log.debug("Start RigaOrdineRequest.updateTest(), req: {}", req);

		ResponseEntity<Response> resp = rowC.update(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_updated"));

		// errore: id sbagliato
		req = new RigaOrdineRequest();
		req.setId(100);
		resp = rowC.update(req);
		log.debug("Start RigaOrdineRequest.updateTest(): error expected, RigaOrdineRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_row"));

		// errore: id Ordine non valido
		req = new RigaOrdineRequest();
		req.setId(1);
		req.setIdOrdine(100);
		resp = rowC.update(req);
		log.debug("Start RigaOrdineRequest.updateTest(): error expected, RigaOrdineRequest duplicato");
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_ord"));

		// errore: isbn non valido
		req = new RigaOrdineRequest();
		req.setId(1);
		req.setManga("WEWEWEW");
		resp = rowC.update(req);
		log.debug("Start RigaOrdineRequest.updateTest(): error expected, RigaOrdineRequest {}", req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_man"));
	}
	
	public void deleteTest() {
		// errore: id non trovato in db/non valido
		Integer id = 99;
		log.debug("Start RigaOrdineRequest.deleteTest(): error expected, invalid id: {}", id);
		ResponseEntity<Response> resp = rowC.delete(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_row"));
		
		// Normal workflow
		id = 1;
		log.debug("Start RigaOrdineRequest.deleteTest(), id: {}", id);
		resp = rowC.delete(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
	}
}
