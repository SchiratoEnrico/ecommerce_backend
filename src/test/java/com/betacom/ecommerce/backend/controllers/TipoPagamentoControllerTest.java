package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TipoPagamentoControllerTest {
	
	@Autowired
	private TipoPagamentoController pagC;
	
	@Test
	public void testPagamentoController() {
		create();
		update();
		findById();
		list();
		findByIdError();
		updateErrorId();
		deleteError();
		createVuoto();
		delete();
	}
	
	public void create() {
		log.debug("Begin create TipoPagamento Test");
		
		TipoPagamentoRequest req = new TipoPagamentoRequest();
				req.setTipoPagamento("CONTANTI");
		
		ResponseEntity<Response> re = pagC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento creato con successo");	
	}
	
	public void update() { 
		log.debug("Begin update Tipo Pagamento Test");
		
		TipoPagamentoRequest req = TipoPagamentoRequest.builder()
				.id(1)
				.tipoPagamento("ONLINE")
				.build();
		
		ResponseEntity<Response> re = pagC.update(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento aggiornato con successo");
		log.info("Tipo pagamento: " + req.getTipoPagamento());
	}
	
	public void findById() { 
		
		log.debug("Begin find by id tipoPagamento test");
		
		ResponseEntity<Object> re = pagC.findById(1);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(re.getBody()).isInstanceOf(TipoPagamentoDTO.class);
		TipoPagamentoDTO b = (TipoPagamentoDTO) re.getBody();
		assertThat(b.getId()).isEqualTo(1);
	}
	
	public void list() { 
		
		log.debug("Begin list() Tipo Pagamento test");
		
		ResponseEntity<Object> re = pagC.list();
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object b = re.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		List<?> l = (List<?>) b; 
		assertThat(l.size()).isGreaterThan(0);
		Assertions.assertThat(l.getFirst()).isInstanceOf(TipoPagamentoDTO.class);	
	}

	public void findByIdError() { 
		
		log.debug("Begin findById TipoPagamento test error");
		
		ResponseEntity<Object> re = pagC.findById(100);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			
	}
	
	public void updateErrorId() { 
		
		log.debug("Begin update bici test error id");
		
		TipoPagamentoRequest req = TipoPagamentoRequest.builder()
				.id(99)
				.build();
		
		ResponseEntity<Response> re = pagC.update(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		Response r = re.getBody();
		
		assertThat(r.getMsg()).isEqualTo("Tipo pagamento assente");	
	}
	
	public void deleteError() { 
		
		log.debug("Begin delete test error");
		
		ResponseEntity<Response> re = pagC.delete(99);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);	
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Tipo pagamento assente");
	}
	
	public void createVuoto() {
		log.debug("Create TipoPagamento Vuoto Test");
		
		TipoPagamentoRequest req = TipoPagamentoRequest.builder()
				.tipoPagamento("        ")
				.build();
		
		ResponseEntity<Response> re = pagC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	public void delete() {
		log.debug("Delete Pagamento Test");
		ResponseEntity<Response> resp = pagC.delete(2);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);	
		Response r =resp.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento eliminato con successo");
	}
	
}
