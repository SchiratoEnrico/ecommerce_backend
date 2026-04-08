package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.security.JwtService; 
import com.betacom.ecommerce.backend.dto.inputs.TipoPagamentoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback automatico del DB H2 dopo ogni test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TipoPagamentoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService; 

	@Autowired
	private UserDetailsService userDetailsService;

	private ObjectMapper objectMapper = new ObjectMapper();


	// UTILITIES
	private String getBearerToken(String username) {
		UserDetails user = userDetailsService.loadUserByUsername(username);
		String token = jwtService.generateToken(user.getUsername()); 
		return "Bearer " + token;
	}

	private TipoPagamentoRequest buildTipoPagamentoRequest() {
		TipoPagamentoRequest req = new TipoPagamentoRequest();
		req.setTipoPagamento("Satispay");
		return req;
	}


	// TEST ENDPOINT: CREATE (Solo ADMIN)

	@Test
	public void createSuccessAdmin() throws Exception {
		log.debug("Begin create TipoPagamento Test - Success");
		String token = getBearerToken("AdminUser"); 

		TipoPagamentoRequest req = buildTipoPagamentoRequest();

		mockMvc.perform(post("/rest/tipo_pagamento/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento creato con successo"));
	}

	@Test
	public void createForbiddenUser() throws Exception {
		log.debug("Begin create TipoPagamento Test - Forbidden");
		String token = getBearerToken("MarioRossi"); // L'utente normale viene bloccato

		mockMvc.perform(post("/rest/tipo_pagamento/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(buildTipoPagamentoRequest())))
				.andExpect(status().isForbidden());
	}

	@Test
	public void createErrorCatchBlocks() throws Exception {
		log.debug("Begin create TipoPagamento Test - Catch Error");
		String token = getBearerToken("AdminUser"); 

		// Errore: Campo nullo
		TipoPagamentoRequest req = buildTipoPagamentoRequest();
		req.setTipoPagamento(null);

		mockMvc.perform(post("/rest/tipo_pagamento/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Tipo pagamento assente"));
	}


	// TEST ENDPOINT: UPDATE (Solo ADMIN)

	@Test
	public void updateSuccessAdmin() throws Exception {
		log.debug("Begin update TipoPagamento Test - Success");
		String token = getBearerToken("AdminUser"); 

		TipoPagamentoRequest req = new TipoPagamentoRequest();
		req.setId(1); // Id di default dal data.sql
		req.setTipoPagamento("Carta di Credito Modificata");

		mockMvc.perform(put("/rest/tipo_pagamento/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void updateErrorDuplicateCatch() throws Exception {
		log.debug("Begin update TipoPagamento Test - Duplicate Error");
		String token = getBearerToken("AdminUser"); 

		//  Creiamo un pagamento nuovo Contrassegno
		TipoPagamentoRequest nuovoPag = new TipoPagamentoRequest();
		nuovoPag.setTipoPagamento("Contrassegno");
		mockMvc.perform(post("/rest/tipo_pagamento/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(nuovoPag)))
				.andExpect(status().isOk());

		TipoPagamentoRequest reqUpdate = new TipoPagamentoRequest();
		reqUpdate.setId(1);
		reqUpdate.setTipoPagamento("Contrassegno");

		mockMvc.perform(put("/rest/tipo_pagamento/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqUpdate)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Tipo pagamento già presente"));
	}


	// TEST ENDPOINT: DELETE (Solo ADMIN)

	@Test
	public void deleteSuccessAdmin() throws Exception {
		log.debug("Begin delete TipoPagamento Test - Real Lifecycle");
		String token = getBearerToken("AdminUser"); 

		//. Creiamo un pagamento usa e getta
		TipoPagamentoRequest req = buildTipoPagamentoRequest();
		mockMvc.perform(post("/rest/tipo_pagamento/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());

		//  Ricerchiamo l'ID appena creato
		String responseBody = mockMvc.perform(get("/rest/tipo_pagamento/list")
				.header("Authorization", token))
				.andReturn().getResponse().getContentAsString();
				
		com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
		// Prendiamo l'ultimo elemento della lista (che è quello appena inserito)
		Integer idDaCancellare = rootNode.get(rootNode.size() - 1).path("id").asInt();

		mockMvc.perform(delete("/rest/tipo_pagamento/delete/" + idDaCancellare).with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void deleteErrorOrderLinked() throws Exception {
		log.debug("Begin delete TipoPagamento Test - Linked Order");
		String token = getBearerToken("AdminUser"); 

		// L'ID 1 è legato a un Ordine nel data.sql
		mockMvc.perform(delete("/rest/tipo_pagamento/delete/1").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Tipo pagamento in uso: eliminazione bloccata")); 
	}


	// TEST ENDPOINT: RECUPERO DATI (Pubblici)

	@Test
	public void listSuccess() throws Exception {
		log.debug("Begin list TipoPagamento test");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/tipo_pagamento/list")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void findByIdSuccess() throws Exception {
		log.debug("Begin findById TipoPagamento test");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/tipo_pagamento/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void findByIdErrorNotFound() throws Exception {
		log.debug("Begin findById TipoPagamento Test - Not Found");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/tipo_pagamento/findById").param("id", "999")
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Tipo pagamento non trovato")); 
	}
}