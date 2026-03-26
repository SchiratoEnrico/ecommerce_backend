package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IAccountServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AccountControllerTest {
	
	@Autowired
	private AccountController accC;
	
	@Autowired
	private IMessagesServices msgS;
	
	@MockitoSpyBean
	private IAccountServices accS;
	
	
	@Test
	public void testAccountController() {
		create();
		createPasswordInvalida();
		createSenzaRuolo();
		createSenzaUsername();
		update();
		updateEmailDuplicata();
		updateUserNameDuplicato();
		updateErrorId();
		findById();
		findByIdError();
		deleteError();
		delete();
		list();
	}
	
	private AccountRequest buildAccountRequest() {
		return AccountRequest.builder()
				.username("   anna.anna   ")
				.password("aaaA00.1")
				.email("a.anna@test.it")
				.ruolo("ADMIN")
				.build();
	}
	
	public void create() {
		
		AccountRequest req = buildAccountRequest();
		
		log.debug("Begin create Account Test, req: {}", req);
		ResponseEntity<Response> re = accC.create(req);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento creato con successo");
		
		// null_usr
		String msg = "null_usr";
		req = buildAccountRequest();
		req.setUsername(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		// null_ema
		msg = "null_ema";
		req = buildAccountRequest();
		req.setEmail(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "null_pwd";
		req = buildAccountRequest();
		req.setPassword(null);
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "pwd_short";
		req = buildAccountRequest();
		req.setPassword("a12B!");
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "pwd_upper";
		req = buildAccountRequest();
		req.setPassword("a12bc!");
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "pwd_lower";
		req = buildAccountRequest();
		req.setPassword("A12BC!");
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "pwd_digit";
		req = buildAccountRequest();
		req.setPassword("ADEBc!");
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "pwd_special";
		req = buildAccountRequest();
		req.setPassword("A12BcD");
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "exists_usr";
		req = buildAccountRequest();
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

		msg = "exists_ema";
		req = buildAccountRequest();
		req.setUsername("jc.cj");
		log.debug("Begin create Account Test, error expected: {}", msg);
		re = accC.create(req);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
		Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	}
	
	public void update() {
	    log.debug("Begin update Account test");
	    AccountRequest req = AccountRequest.builder()
	            .id(1)
	            .username("user2")
	            .password("Aaa.012!")
	            .email("user@test.com")
	            .ruolo("USER")
	            .build();

	    ResponseEntity<Response> re = accC.update(req);
	    assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
	    Response r = re.getBody();
	    assertThat(r.getMsg()).isEqualTo("Elemento aggiornato con successo");
	}
	
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
	
	public void list() { 
		log.debug("Begin list() Account test");
		
		ResponseEntity<Object> re = accC.list();
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);		
        Object b = re.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		assertThat(((List<?>) b).size()).isGreaterThan(0);
		Assertions.assertThat(((List<?>) b).getFirst()).isInstanceOf(AccountDTO.class);
		
		log.debug("Test error in list account");
		String error = "generic error";
		doThrow(new RuntimeException(error)).when(accS).list();
		re = accC.list();
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	}
	
	public void findById() { 
		log.debug("Begin find by id tipoPagamento test");
		ResponseEntity<Object> re = accC.findById(1);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		Object b =  re.getBody();
		Assertions.assertThat(b).isInstanceOf(AccountDTO.class);
		assertThat(((AccountDTO) b).getId()).isEqualTo(1);
		
	}
	
	public void findByIdError() { 
		log.debug("Begin findById TipoPagamento test error");
		ResponseEntity<Object> re = accC.findById(100);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	public void deleteError() { 
		log.debug("Begin delete Account test error");
		
		ResponseEntity<Response> re = accC.delete(99);
		assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);	
		Response r = re.getBody();
		assertThat(r.getMsg()).isEqualTo("Account assente");
	}
	
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
	
	public void delete() {
		log.debug("Delete Account Test");
		ResponseEntity<Response> resp = accC.delete(2);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);	
		Response r =resp.getBody();
		assertThat(r.getMsg()).isEqualTo("Elemento eliminato con successo");
	}
	
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
