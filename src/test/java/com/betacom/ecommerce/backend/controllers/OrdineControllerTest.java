package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.OrdineDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OrdineControllerTest {
	@Autowired
	private OrdineController ordC;
	@Autowired
	private IMessagesServices msgS;
	
	@Test
	public void testOrdineController() {
		createTest();
		updateTest();
		listOrdiniTest();
		findByIdTest();
		deleteTest();
	}
	
    private OrdineRequest getProva() {
        return OrdineRequest.builder()
        		.id(1)
                .account(1)
                .pagamento("PAYPAL")
                .spedizione("STANDARD")
                .data("2026-03-20")
                .stato("CREATED")
                .build();
    }

	private void createTest(){
		// Normal workflow
		OrdineRequest req = getProva();
		req.setId(null);
		
		log.debug("Start OrdineControllerTest.createTest(), req: {}", req);
		ResponseEntity<Response> resp = ordC.create(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_created"));

		// errore: account null
		req = getProva();
		req.setId(null);
		req.setAccount(null);
		log.debug("Start OrdineControllerTest.createTest(): error expected: null_acc, OrdineRequest {}", req);
		 resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_acc"));

		// errore: account non esistente
		req = getProva();
		req.setId(null);
		req.setAccount(99);
		log.debug("Start OrdineControllerTest.createTest(): error expected: !exists_acc, OrdineRequest {}", req);
		resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_acc"));

		// errore: pagamento null
		req = getProva();
		req.setId(null);
		req.setPagamento(null);
		log.debug("Start OrdineControllerTest.createTest(): error expected: null_pag, OrdineRequest {}", req);
		 resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_pag"));

		// errore: pagamento non esistente
		req = getProva();
		req.setId(null);
		req.setPagamento("MIO");
		log.debug("Start OrdineControllerTest.createTest(): error expected: !exists_pag, OrdineRequest {}", req);
		 resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_pag"));

		// errore: spedizione null
		req = getProva();
		req.setId(null);
		req.setSpedizione(null);
		log.debug("Start OrdineControllerTest.createTest(): error expected: null_spe, OrdineRequest {}", req);
		 resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_spe"));

		// errore: spedizione non esistente
		req = getProva();
		req.setId(null);
		req.setSpedizione("MIO");
		log.debug("Start OrdineControllerTest.createTest(): error expected: !exists_spe, OrdineRequest {}", req);
		resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_spe"));

		// errore: data null
		req = getProva();
		req.setId(null);
		req.setData(null);
		log.debug("Start OrdineControllerTest.createTest(): error expected: null_dat, OrdineRequest {}", req);
		resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_dat"));

		// errore: stato null
		req = getProva();
		req.setId(null);
		req.setStato(null);
		log.debug("Start OrdineControllerTest.createTest(): error expected: null_sta, OrdineRequest {}", req);
		resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_sta"));

		// errore: stato non esistente
		req = getProva();
		req.setId(null);
		req.setStato("MIO");
		log.debug("Start OrdineControllerTest.createTest(): error expected: !exists_sta, OrdineRequest {}", req);
		resp = ordC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_sta"));
	};

	private void updateTest(){
		// Normal workflow
		OrdineRequest req = getProva();
		log.debug("Start OrdineControllerTest.updateTest(), req: {}", req);
		ResponseEntity<Response> resp = ordC.update(req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Response r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_updated"));

		// errore: id null
		req = getProva();
		req.setId(null);
		log.debug("Start OrdineControllerTest.updateTest(): error expected: null_ord, OrdineRequest {}", req);
		 resp = ordC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_ord"));
		
		// errore: id non esistente
		req = getProva();
		req.setId(99);
		log.debug("Start OrdineControllerTest.updateTest(): error expected: !exists_ord, OrdineRequest {}", req);
		 resp = ordC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_ord"));

		// update invalido per campi
		// account invalido !exists_acc
		req = getProva();
		req.setAccount(99);
		log.debug("Start OrdineControllerTest.updateTest(): error expected: !exists_acc, OrdineRequest {}", req);
		resp = ordC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_acc"));

		// errore: pagamento non esistente
		req = getProva();
		req.setPagamento("MIO");
		log.debug("Start OrdineControllerTest.updateTest(): error expected: !exists_pag, OrdineRequest {}", req);
		 resp = ordC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_pag"));

		// errore: spedizione non esistente
		req = getProva();
		req.setSpedizione("MIO");
		log.debug("Start OrdineControllerTest.updateTest(): error expected: !exists_spe, OrdineRequest {}", req);
		resp = ordC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_spe"));

		// errore: data invalida
		req = getProva();
		req.setData("OGGI");
		log.debug("Start OrdineControllerTest.updateTest(): error expected: null_dat, OrdineRequest {}", req);
		resp = ordC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_dat"));

		// errore: stato non esistente
		req = getProva();
		req.setStato("MIO");
		log.debug("Start OrdineControllerTest.updateTest(): error expected: !exists_sta, OrdineRequest {}", req);
		resp = ordC.update(req);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = (Response)resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_sta"));

	};
/*
	private void listTest(){
		log.debug("Start OrdineControllerTest.listTest()");
		
		ResponseEntity<?> resp = ordC.list();
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(List.class);
		List<?> body = (List<?>) resp.getBody();
		if (body.size() > 0) {
			Assertions.assertThat(body.getFirst()).isInstanceOf(OrdineDTO.class);
		}
	};
*/
	@Test
    public void listOrdiniTest() {
        log.debug("start list ordini test");
        
        // Inizializzazione parametri di ricerca
        String username = null;
        String tipoPagamento = null;
        String tipoSpedizione = null;
        LocalDate data = null;
        String statoOrdine = null;
        List<String> isbns = new ArrayList<>();

        //Test Lista Vuota (senza filtri)
        List<OrdineDTO> lO = getLoadedList(username, tipoPagamento, tipoSpedizione, data, statoOrdine, isbns);
        assertThat(lO).isNotEmpty();
        
        // Test per Username
        username = "Mario";
        lO = getLoadedList(username, tipoPagamento, tipoSpedizione, data, statoOrdine, isbns);
        username = null;
        assertThat(lO).isNotEmpty();
        assertThat(lO.get(0).getAccount().getUsername()).containsIgnoringCase("mario");

        // Test per Tipo Pagamento
        tipoPagamento = "PayPal";
        lO = getLoadedList(username, tipoPagamento, tipoSpedizione, data, statoOrdine, isbns);
        tipoPagamento = null;
        assertThat(lO).isNotEmpty();
        assertThat(lO.get(0).getPagamento().getTipoPagamento()).isEqualToIgnoringCase("paypal");

        // Test per Tipo Spedizione
        tipoSpedizione = "Corriere";
        lO = getLoadedList(username, tipoPagamento, tipoSpedizione, data, statoOrdine, isbns);
        tipoSpedizione = null;
        assertThat(lO).isNotEmpty();
        assertThat(lO.get(0).getSpedizione().getTipoSpedizione()).isEqualToIgnoringCase("corriere");

        // Test per Stato Ordine
        statoOrdine = "Spedito";
        lO = getLoadedList(username, tipoPagamento, tipoSpedizione, data, statoOrdine, isbns);
        statoOrdine = null;
        assertThat(lO).isNotEmpty();
        assertThat(lO.get(0).getStato().getStatoOrdine()).isEqualToIgnoringCase("spedito");

        // Test per ISBN
        isbns.add("978-1234567890");
        lO = getLoadedList(username, tipoPagamento, tipoSpedizione, data, statoOrdine, isbns);
        isbns.clear();
        assertThat(lO).isNotEmpty();
        assertThat(lO.get(0).getRigheOrdine().stream()
            .anyMatch(r -> r.getManga().equals("978-1234567890"))).isTrue();

        // Test per Data
        data = LocalDate.of(2024, 5, 15);
        lO = getLoadedList(username, tipoPagamento, tipoSpedizione, data, statoOrdine, isbns);
        data = null;
        assertThat(lO).isNotEmpty();
        assertThat(lO.get(0).getData().getMonthValue()).isEqualTo(5);
        assertThat(lO.get(0).getData().getYear()).isEqualTo(2024);
    }
	
	@SuppressWarnings("unchecked")
	private List<OrdineDTO> getLoadedList(
	        String username,
	        String tipoPagamento,
	        String tipoSpedizione,
	        LocalDate data,
	        String statoOrdine,
	        List<String> isbns
	) {
		Integer anno = (data != null) ? data.getYear() : null;
	    Integer mese = (data != null) ? data.getMonthValue() : null;
	    Integer giorno = (data != null) ? data.getDayOfMonth() : null;
	    ResponseEntity<Object> resp = ordC.list(
	            username,
	            tipoPagamento,
	            tipoSpedizione,
	            statoOrdine,
	            anno,
	            mese,
	            giorno,
	            isbns
	    );

	    assertEquals(HttpStatus.OK, resp.getStatusCode());
	    Object b = resp.getBody();
	    Assertions.assertThat(b).isInstanceOf(List.class);

	    return (List<OrdineDTO>) b;
	}
	
	private void findByIdTest(){
		// Id error
		// null_ord
		Integer id = null;
		log.debug("Start OrdineControllerTest.findByIdTest(), error null_ord expected");
		ResponseEntity<?> resp = ordC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get("null_ord"), resp.getBody());

		// !exists_ord
		id = 99;
		log.debug("Start OrdineControllerTest.findByIdTest(), error expected");
		resp = ordC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get("!exists_ord"), resp.getBody());

		// Normal workflow
		id = 1;
		log.debug("Start OrdineControllerTest.findByIdTest()");
		resp = ordC.findById(id);
		Assertions.assertThat(resp.getBody()).isInstanceOf(OrdineDTO.class);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
	}
	

	private void deleteTest(){
		// !exists_ord
		Integer id = 99;
		log.debug("Start OrdineRequest.deleteTest(): error expected !exists_ord, invalid id: {}", id);
		ResponseEntity<Response> resp = ordC.delete(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_ord"));
		
		// null_ord
		id = null;
		log.debug("Start OrdineRequest.deleteTest(): error expected: null_ord, invalid id: {}", id);
		resp = ordC.delete(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("null_ord"));

		// Normal workflow
		id = 2;
		log.debug("Start OrdineRequest.deleteTest(), id: {}", id);
		resp = ordC.delete(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get("rest_deleted"));
	};
	
}
