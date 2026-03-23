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

import com.betacom.ecommerce.backend.dto.inputs.AnagraficaRequest;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AnagraficaControllerTest {
	
	@Autowired
	private AnagraficaController anaC;
	@Autowired
	private IMessagesServices msgS;

	@Test
	public void testAnagraficaController() {
		create();
		update();
		list();
		findById();
		findByIdError();
		deleteError();
		createNomeVuoto();
		delete();
		createCognomeVuoto();
	}

	private AnagraficaRequest buildAnagraficaRequest() {
		return AnagraficaRequest.builder()
				.nome("ANNA")
				.cognome("VERDI ")
				.stato("ITALIA     ")
				.citta("    Sesto Fiorentino")
				.provincia("Firenze")
				.cap("12345   ")
				.via("   Via Tento ")
				.predefinito(true)
				.build();
	}
	
	public void create() {
		log.debug("Begin create Anagrafica Test");
		
		AnagraficaRequest req = buildAnagraficaRequest();
					
		ResponseEntity<Response> re = anaC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento creato con successo");	
	
		// null_sta
		String msg = "null_sta";
		req = buildAnagraficaRequest();
		req.setStato(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = anaC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_cit
		msg = "null_cit";
		req = buildAnagraficaRequest();
		req.setCitta(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = anaC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_pro
		msg = "null_pro";
		req = buildAnagraficaRequest();
		req.setProvincia(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = anaC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_cap
		msg = "null_cap";
		req = buildAnagraficaRequest();
		req.setCap(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = anaC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_via
		msg = "null_via";
		req = buildAnagraficaRequest();
		req.setVia(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = anaC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_pre
		msg = "null_pre";
		req = buildAnagraficaRequest();
		req.setPredefinito(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = anaC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));
	}
	
	public void update() { 
		log.debug("Begin update Anagrafiche test");
		AnagraficaRequest req = AnagraficaRequest.builder()
				.id(1)
				.nome("Maria")
				.cognome("Federico")
				.stato("ITALIA     ")
				.citta("Modena")
				.provincia("Mirandola")
				.cap("00099")
				.via("Viale Venezia")
				.predefinito(true)
	            .build();
		
		ResponseEntity<Response> re = anaC.update(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento aggiornato con successo");
	}

	public void list() { 
		log.debug("Begin list() Account test");
		
		ResponseEntity<Object> re = anaC.list();
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<?> b = (List<?>) re.getBody();
		assertThat(b.size()).isGreaterThan(0);
		Assertions.assertThat(b.getFirst()).isInstanceOf(AnagraficaDTO.class);
		
	}
	
	public void findById() { 
		
		log.debug("Begin find by id tipoPagamento test");
		ResponseEntity<Object> re = anaC.findById(1);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Object b =  re.getBody();
		Assertions.assertThat(b).isInstanceOf(AnagraficaDTO.class);
		assertThat(((AnagraficaDTO) b).getId()).isEqualTo(1);
		
	}
	
	public void findByIdError() { 
		log.debug("Begin findById TipoPagamento test error");
		
		ResponseEntity<Object> re = anaC.findById(100);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	public void deleteError() { 
		
		log.debug("Begin delete anagrafica test error");
		
		ResponseEntity<Response> re = anaC.delete(99);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);	
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Anagrafica assente");
	}
	
	public void delete() {
		log.debug("Delete Test");
		ResponseEntity<Response> resp = anaC.delete(1);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);	
		Response r =resp.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento eliminato con successo");
	}
	
	public void createNomeVuoto() {
		log.debug("Begin create Anagrafica Test Error");
		
		AnagraficaRequest req = AnagraficaRequest.builder()
				.cognome("Bianchi ")
				.stato("ITALIA     ")
				.citta(" Firenze")
				.provincia("Pontassieve")
				.cap("12347   ")
				.via("   Via Firenze ")
				.predefinito(true)
				.build();
					
		ResponseEntity<Response> re = anaC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Nome assente");	
	}

	public void createCognomeVuoto() {
		log.debug("Begin create Anagrafica Test Error");
		
		AnagraficaRequest req = AnagraficaRequest.builder()
				.nome("Aurora")
				.stato("ITALIA")
				.citta(" Firenze")
				.provincia("Pontassieve")
				.cap("12347   ")
				.via("   Via Firenze ")
				.predefinito(true)
				.build();
					
		ResponseEntity<Response> re = anaC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Cognome assente");	
	}
	
	
}
