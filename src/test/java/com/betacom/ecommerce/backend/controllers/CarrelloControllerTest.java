package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.ICarrelloServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CarrelloControllerTest {
	@Autowired
	private CarrelloController carC;
	@Autowired
	private IMessagesServices msgS;
	
	@MockitoSpyBean
	private ICarrelloServices carS;


	@Test
	public void testCarrelloController() throws Exception {
		findByIdSuccess();
		deleteCarrelloSuccess();
		createCarrello();
		addRow();
		updateRow();
		deleteRow();
		listCarrelli();
	}

	public void createCarrello() {
		log.debug("*** Test creazione Carrello ***");
		
		log.debug("* Expected: success *");
		CarrelloRequest req = new CarrelloRequest();
		req.setId_account(1);
		ResponseEntity<Response> resp = carC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_created"));
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad account id *");
		req = new CarrelloRequest();
		req.setId_account(0);
		resp = carC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing chart creation successfully ***");
	}
	
	public void addRow() {
		log.debug("*** Test aggiunta RigaCarrello a Carrello ***");
		
		log.debug("* Expected: success *");
		ResponseEntity<Response> resp = carC.addRow(2, "ISBN002", 1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_updated"));
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id*");
		resp = carC.addRow(0, "ISBN002", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad isbn *");
		resp = carC.addRow(2, "Kawabanga", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad copies number *");
		resp = carC.addRow(1, "ISBN001", 0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing row adding to the chart ***");
	}
	
	public void updateRow() {
		log.debug("*** Test aggiornamento riga carrello del Carrello ***");
		
		log.debug("* Expected: success - modifying the row *");
		ResponseEntity<Response> resp = carC.updateRow(2, 1, "ISBN002", 2);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_updated"));
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id *");
		resp = carC.updateRow(0, 1, "ISBN002", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad isbn *");
		resp = carC.updateRow(1, 1, "Kawabanga", 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("* Expected: success - removing the row due to insufficient copies number *");
		resp = carC.updateRow(2, 1, "ISBN002", 0);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
		log.debug("* Done *");
		
		log.debug("*** Finished testing row updating of the chart ***");
	}
	
	public void deleteRow() {
		log.debug("*** Test rimozione riga carrello dal Carrello ***");
		
		log.debug("* Expected - success *");
		ResponseEntity<Response> resp = carC.deleteRow(2, 1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to chart id *");
		resp = carC.deleteRow(0, 1);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing row deletion from the chart ***");
	}
	
	public void deleteCarrelloSuccess() {
		log.debug("*** Test delete Carrello - successo ***");
		
		log.debug("* Expected: success *");
		ResponseEntity<Response> resp = carC.delete(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id *");
		resp = carC.delete(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		log.debug("* Done *");
		
		log.debug("*** Finished testing chart deletion ***");
	}
	
	public void listCarrelli() throws Exception {
		log.debug("*** Test lista Carrelli ***");

		log.debug("* Expected: success - listAll *");
		ResponseEntity<Object> resp = carC.list(null);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		List<?> lC = (List<?>) b;
		assertThat(lC.size()).isGreaterThan(0);
		Assertions.assertThat(lC.getFirst()).isInstanceOf(CarrelloDTO.class);
		lC.forEach(c -> log.debug(c.toString()+'\n'));
		log.debug("* Done *");
		
		log.debug("* Expected: success - listAll (filteredList with empty list) *");
		List<String> isbn = new ArrayList<>();
		resp = carC.list(isbn);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
        b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		lC = (List<?>) b;
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		Assertions.assertThat(lC.getFirst()).isInstanceOf(CarrelloDTO.class);
		lC.forEach(c -> log.debug(c.toString()+'\n'));
		log.debug("* Done *");
		
		log.debug("* Expected: success - filteredList *");
		isbn.add("ISBN002");
		resp = carC.list(isbn);			
		log.debug("Resp body: {}", resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		lC = (List<?>) b;		
		Assertions.assertThat(lC.size()).isGreaterThan(0);
		Assertions.assertThat(lC.getFirst()).isInstanceOf(CarrelloDTO.class);
		lC.forEach(c -> log.debug(c.toString()+'\n'));
		log.debug("* Done *");
		
		log.debug("*** Finished testing list chart ***");
		
		String error = "generic error";
		doThrow(new RuntimeException(error)).when(carS).list(null);
		resp = carC.list(null);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	public void findByIdSuccess() {
		log.debug("*** Test ricerca per id Carrello - successo ***");
		
		log.debug("* Expected: success *");
		ResponseEntity<Object> resp = carC.findById(1);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isNotNull();
		Assertions.assertThat(b).isInstanceOf(CarrelloDTO.class);

		log.debug("* Done *");
		
		log.debug("* Expected: fail due to bad chart id *");
		resp = carC.findById(0);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Assertions.assertThat(resp.getBody().toString()).isNotEmpty();
	}
}
