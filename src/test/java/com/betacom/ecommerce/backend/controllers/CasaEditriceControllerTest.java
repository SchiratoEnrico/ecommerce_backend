package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.betacom.ecommerce.backend.dto.inputs.CasaEditriceRequest;
import com.betacom.ecommerce.backend.dto.outputs.CasaEditriceDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.ICasaEditriceServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CasaEditriceControllerTest {
	@Autowired 
	private CasaEditriceController casC;
	
	@Autowired 
	private IMessagesServices msgS;
	
	@MockitoSpyBean
	private ICasaEditriceServices casS;

	@Test
	public void testCasaEditriceController() throws Exception {
		createCasa();
		findCasaByIdSuccesso();
		findCasaByIdErrore();
		updateCasaSuccess();
		updateCasaError();
		listCase();
		deleteCasaSuccess();
		deleteCasaError();
	}

	private CasaEditriceRequest buildCasaEditriceRequest() {
		CasaEditriceRequest req = new CasaEditriceRequest();
		req.setDescrizione("String");
		req.setEmail("Email");
		req.setIndirizzo("Indiririzzo");
		req.setNome("Nome");
		return req;
	}
	public void createCasa() {
		log.debug("*** Test creazione Case Editrice ***");
		CasaEditriceRequest req = buildCasaEditriceRequest();
		ResponseEntity<Response> resp = casC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_created"));
	
		// null_des
		String msg = "null_des";
		req = buildCasaEditriceRequest();
		req.setDescrizione(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		ResponseEntity<Response> re = casC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_ema
		msg = "null_ema";
		req = buildCasaEditriceRequest();
		req.setEmail(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = casC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_ind
		msg = "null_ind";
		req = buildCasaEditriceRequest();
		req.setIndirizzo(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = casC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));
		
		// null_nom
		msg = "null_nom";
		req = buildCasaEditriceRequest();
		req.setNome(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = casC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	}
	
	public void findCasaByIdSuccesso() {
		log.debug("*** Test ricerca Casa Editrice per id - successo");
		ResponseEntity<Object> resp = casC.findById(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Object b =  resp.getBody();
		Assertions.assertThat(b).isNotNull();

		Assertions.assertThat(b).isInstanceOf(CasaEditriceDTO.class);
		assertThat(((CasaEditriceDTO) b).getId()).isEqualTo(1);
	}
				
	public void findCasaByIdErrore() {
		log.debug("*** Test ricerca Casa Editrice per id - errore ***");
		ResponseEntity<Object> resp = casC.findById(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Assertions.assertThat(resp.getBody().toString()).isNotEmpty();
	}
	
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
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_updated"));
	}
	
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
	
	public void deleteCasaSuccess() {
		log.debug("*** Test delete Casa Editrice ***");
		
		log.debug("* Expected: fail due to manga attached not empty *");
		ResponseEntity<Response> resp = casC.delete(1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: success *");
		resp = casC.delete(2);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
		log.debug("* Done *");
	}
	
	public void deleteCasaError() {
		log.debug("*** Test delete Casa Editrice - errore ***");
		
		ResponseEntity<Response> resp = casC.delete(0);
		
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}

	public void listCase() throws Exception {
		log.debug("*** Test list Case ***");
		
		log.debug("* list: no params *");
		ResponseEntity<?> resp = casC.list(null, null, null, null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		List<?> lC = (List<?>) b;
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		Assertions.assertThat(((List<?>) b).getFirst()).isInstanceOf(CasaEditriceDTO.class);
		lC.forEach(c -> c.toString());
		
		log.debug("* list: with params *");
		resp = casC.list("JPOP", null, null, null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		lC = (List<?>) b;
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		Assertions.assertThat(((List<?>) b).getFirst()).isInstanceOf(CasaEditriceDTO.class);
		lC.forEach(c -> c.toString());
		
		String error = "generic error";
		doThrow(new RuntimeException(error)).when(casS).list(null, null, null, null);
		resp = casC.list(null, null, null, null);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
}
