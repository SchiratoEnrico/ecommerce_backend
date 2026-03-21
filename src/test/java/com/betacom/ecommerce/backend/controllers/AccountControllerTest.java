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
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AccountControllerTest {
	
	@Autowired
	private AccountController accC;
	
	@Test
	@Order(9)
	public void create() {
		log.debug("Begin create Account Test");
		
		AccountRequest req = AccountRequest.builder()
				.username("   anna.anna   ")
				.password("aaaA00.1")
				.email("a.anna@test.it")
				.ruolo("ADMIN")
				.build();
		
		ResponseEntity<Response> re = accC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento creato con successo");	
	}
	
	@Test
	@Order(1)
	public void update() {
	    log.debug("Begin update Account test");
	    AccountRequest req = AccountRequest.builder()
	            .id(1)
	            .username("user2")
	            .password("Aaa.012!")
	            .email("user@test.com")
	            .build();

	    ResponseEntity<Response> re = accC.update(req);
	    assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
	    Response r = re.getBody();
	    assertThat(r.getMsg()).isEqualTo("Elemento aggiornato con successo");
	}
	
	@Test
	@Order(2)
	public void updateEmailDuplicata() { 
		log.debug("Begin update Account test");
		AccountRequest req = AccountRequest.builder()
				.id(1)
	            .email("admin@email.com")
	            .build();
		
		ResponseEntity<Response> re = accC.update(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Email già presente");
			
	}
	
	@Test
	@Order(3)
	public void updateUserNameDuplicato() { 
		log.debug("Begin update Account test");
		AccountRequest req = AccountRequest.builder()
				.id(1)
				.username("AdminUser")
	            .build();
		
		ResponseEntity<Response> re = accC.update(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Username già presente");
			
	}
	
	@Test
	@Order(4)
	public void updateErrorId() { 
		log.debug("Begin Account update error id");
		
		AccountRequest req = AccountRequest.builder()
				.id(99)
				.username("Alessio")
				.build();
		
		ResponseEntity<Response> re = accC.update(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Account assente");	
	}
	
	@Test
	@Order(5)
	public void list() { 
		log.debug("Begin list() Account test");
		
		ResponseEntity<Object> re = accC.list();
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<AccountDTO> b = (List<AccountDTO>) re.getBody();
		assertThat(b.size()).isGreaterThan(0);	
	}
	
	@Test
	@Order(6)
	public void findById() { 
		log.debug("Begin find by id tipoPagamento test");
		ResponseEntity<Object> re = accC.findById(1);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		AccountDTO b = (AccountDTO) re.getBody();
		assertThat(b.getId()).isEqualTo(1);
		
	}
	
	@Test
	@Order(7)
	public void findByIdError() { 
		log.debug("Begin findById TipoPagamento test error");
		ResponseEntity<Object> re = accC.findById(100);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@Order(8)
	public void deleteError() { 
		log.debug("Begin delete Account test error");
		
		ResponseEntity<Response> re = accC.delete(99);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);	
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Account assente");
	}
	
	@Test
	@Order(10)
	public void createPasswordInvalida() {
	    log.debug("Begin create Account Test - password invalida");

	    AccountRequest req = AccountRequest.builder()
	            .username("test.user")
	            .password("aaaa001")  // manca maiuscola e carattere speciale
	            .email("test.user@test.it")
	            .ruolo("USER")
	            .build();

	    ResponseEntity<Response> re = accC.create(req);
	    assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@Order(11)
	public void delete() {
		log.debug("Delete Account Test");
		ResponseEntity<Response> resp = accC.delete(2);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);	
		Response r =resp.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento eliminato con successo");
	}
	
	@Test
	@Order(12)
	public void createSenzaRuolo() {
		log.debug("Begin create Account Test");
		
		AccountRequest req = AccountRequest.builder()
				.username("bruna.2")
				.password("bbbA00.1")
				.email("a.bruna@test.it")
				.build();
		
		ResponseEntity<Response> re = accC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Ruolo assente");	
	}
	@Test
	@Order(12)
	public void createSenzaUsername() {
		log.debug("Begin create Account Test");
		
		AccountRequest req = AccountRequest.builder()
				.password("bbbA00.1")
				.email("a.bruna@test.it")
				.ruolo("ADMIN")
				.build();
		
		ResponseEntity<Response> re = accC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Username assente");	
	}
}
