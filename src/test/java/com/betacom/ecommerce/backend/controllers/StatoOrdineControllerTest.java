package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.StatoOrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional //Fa il rollback del DB dopo ogni test 
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StatoOrdineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMessagesServices msgS;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private StatoOrdineController statC;

    private String getBearerToken(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(user.getUsername());
        return "Bearer " + token;
    }

    // TEST FUNCTIONS
	@Test
	public void testStatoOrdineControllerAdmin() throws Exception {
		createTest();
		updateTest();
		deleteTest();
	}
	
	@Test
	public void testStatoOrdineControllerAny() {
		listTest();
		findByIdTest();
	}

	public void listTest() {
		log.debug("Start StatoOrdineControllerTest.listTest()");
		
		ResponseEntity<?> resp = statC.list();
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(List.class);
		List<?> body = (List<?>) resp.getBody();
		assertThat(body.size()).isGreaterThan(0);
		Assertions.assertThat(body.getFirst()).isInstanceOf(StatoOrdineDTO.class);
	}
	
	public void findByIdTest() {
		// Id error
		Integer id = 99;
		log.debug("Start StatoOrdineControllerTest.findByIdTest(), error expected");
		ResponseEntity<?> resp = statC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get("!exists_sta"), resp.getBody());

		// Normal workflow
		id = 1;
		log.debug("Start StatoOrdineControllerTest.findByIdTest()");
		resp = statC.findById(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(StatoOrdineDTO.class);
		}

    // ==========================================
    // ASSERT HELPERS
    // ==========================================
    private void assertCreateError(String token, String msg, StatoOrdineRequest req) throws Exception {
        mockMvc.perform(post("/rest/stato_ordine/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }

    private void assertUpdateError(String token, String msg, StatoOrdineRequest req) throws Exception {
        mockMvc.perform(put("/rest/stato_ordine/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }

	// ADMIN TEST FUNCTIONS
    
	public void createTest() throws Exception {
		log.debug("Start StatoOrdineControllerTest.createTest()");

		// Normal workflow
		String token = getBearerToken("AdminUser");
		String msg = "rest_created";

		StatoOrdineRequest req = new StatoOrdineRequest();
		req.setStatoOrdine("trallallero");
		mockMvc.perform(post("/rest/stato_ordine/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

		// errore: null statoOrdine
		msg = "null_sta";
		req = new StatoOrdineRequest();
		assertCreateError(token, msg, req);
		
		// errore: blank statoOrdine
		req = new StatoOrdineRequest();
		req.setStatoOrdine("");
		msg = "null_sta";
		assertCreateError(token, msg, req);

		// errore: duplicate statoOrdine
		req = new StatoOrdineRequest();
		req.setStatoOrdine("trallallero");
		msg = "exists_sta";
		assertCreateError(token, msg, req);
	}

	public void updateTest() throws Exception {
		log.debug("Start StatoOrdineControllerTest.createTest()");
		// Normal workflow
		
		String token = getBearerToken("AdminUser");
		String msg = "rest_updated";
		StatoOrdineRequest req = new StatoOrdineRequest();
		req.setStatoOrdine("in conSegna");
		req.setId(2);
		mockMvc.perform(put("/rest/stato_ordine/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));


		// errore: duplicate statoOrdine
		req = new StatoOrdineRequest();
		req.setId(1);
		req.setStatoOrdine("in conSegna");
		msg = "exists_sta";
		assertUpdateError(token, msg, req);

		// errore: id sbagliato
		req = new StatoOrdineRequest();
		req.setId(100);
		req.setStatoOrdine("spedito");
		msg = "!exists_sta";
		assertUpdateError(token, msg, req);

		// errore: stato null
		req = new StatoOrdineRequest();
		req.setId(1);
		msg = "null_sta";
		assertUpdateError(token, msg, req);
	}

	public void deleteTest() throws Exception {
		log.debug("Start StatoOrdineControllerTest.deleteTest());");

		// errore: id non trovato in db/non valido
		Integer id = 99;
		log.debug("error expected, invalid id: {}", id);
		String token = getBearerToken("AdminUser");
		String msg = "!exists_sta";
		mockMvc.perform(delete("/rest/stato_ordine/delete/99").with(csrf())
					.header("Authorization", token))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		
		msg = "rest_deleted";
		mockMvc.perform(delete("/rest/stato_ordine/delete/3").with(csrf())
					.header("Authorization", token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		
	}
}
