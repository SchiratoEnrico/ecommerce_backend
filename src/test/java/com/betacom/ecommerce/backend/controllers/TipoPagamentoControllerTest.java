package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TipoPagamentoControllerTest {
	
	@Autowired
	private TipoPagamentoController pagC;
	
	@Test
	@Order(1)
	public void create() {
		log.debug("Begin create TipoPagamento Test");
		
		TipoPagamentoRequest req = new TipoPagamentoRequest();
				req.setTipoPagamento("CONTANTI");
		
		ResponseEntity<Response> re = pagC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento creato con successo");	
	}
	
	@Test
	@Order(2)
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
	
	@Test
	@Order(3)
	public void findById() { 
		
		log.debug("Begin find by id tipoPagamento test");
		
		ResponseEntity<Object> re = pagC.findById(1);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		TipoPagamentoDTO b = (TipoPagamentoDTO) re.getBody();
		assertThat(b.getId()).isEqualTo(1);
	}
	
	@Test
	@Order(4)
	public void list() { 
		
		log.debug("Begin list() Tipo Pagamento test");
		
		ResponseEntity<Object> re = pagC.list();
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		List<TipoPagamentoDTO> b = (List<TipoPagamentoDTO>) re.getBody();
		assertThat(b.size()).isGreaterThan(0);
		
	}

	@Test
	@Order(5)
	public void findByIdError() { 
		
		log.debug("Begin findById TipoPagamento test error");
		
		ResponseEntity<Object> re = pagC.findById(100);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			
	}
	
	@Test
	@Order(6)
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
	
	
	@Test
	@Order(7)
	public void deleteError() { 
		
		log.debug("Begin delete test error");
		
		ResponseEntity<Response> re = pagC.delete(99);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);	
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Tipo pagamento assente");
	}
	
	
	@Test
	@Order(8)
	public void createVuoto() {
		log.debug("Create TipoPagamento Vuoto Test");
		
		TipoPagamentoRequest req = TipoPagamentoRequest.builder()
				.tipoPagamento("        ")
				.build();
		
		ResponseEntity<Response> re = pagC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@Order(9)
	public void delete() {
		log.debug("Delete Pagamento Test");
		ResponseEntity<Response> resp = pagC.delete(2);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);	
		Response r =resp.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento eliminato con successo");
	}
	
}
