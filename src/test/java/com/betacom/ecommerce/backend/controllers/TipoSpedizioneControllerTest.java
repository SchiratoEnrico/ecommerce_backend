package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import com.betacom.ecommerce.backend.dto.inputs.TipoSpedizioneRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback automatico del DB H2
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TipoSpedizioneControllerTest { 

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

	private TipoSpedizioneRequest buildTipoSpedizioneRequest() {
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setTipoSpedizione("Spedizione Express Test");
		req.setCostoSpedizione(new BigDecimal("9.99"));
		return req;
	}


	// TEST ENDPOINT: CREATE (Solo ADMIN)

	@Test
	public void createSuccessAdmin() throws Exception {
		log.debug("Begin create TipoSpedizione Test - Success");
		String token = getBearerToken("AdminUser"); 

		TipoSpedizioneRequest req = buildTipoSpedizioneRequest();

		mockMvc.perform(post("/rest/tipo_spedizione/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento creato con successo"));
	}

	@Test
	public void createForbiddenUser() throws Exception {
		log.debug("Begin create TipoSpedizione Test - Forbidden");
		String token = getBearerToken("MarioRossi"); // Ruolo USER non autorizzato

		mockMvc.perform(post("/rest/tipo_spedizione/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(buildTipoSpedizioneRequest())))
				.andExpect(status().isForbidden());
	}

	@Test
	public void createErrorCatchBlocks() throws Exception {
		log.debug("Begin create TipoSpedizione Test - Error Catch");
		String token = getBearerToken("AdminUser"); 

		// Errore: Costo null
		TipoSpedizioneRequest req = buildTipoSpedizioneRequest();
		req.setCostoSpedizione(null);

		mockMvc.perform(post("/rest/tipo_spedizione/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Prezzo assente/non valido"));
	}


	// TEST ENDPOINT: UPDATE (Solo ADMIN)

	@Test
	public void updateSuccessAdmin() throws Exception {
		log.debug("Begin update TipoSpedizione Test - Success");
		String token = getBearerToken("AdminUser"); 

		// Aggiorniamo la spedizione con ID 1 (Standard)
		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setId(1);
		req.setTipoSpedizione("Spedizione Standard Modificata");
		req.setCostoSpedizione(new BigDecimal("4.50"));

		mockMvc.perform(put("/rest/tipo_spedizione/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void updateForbiddenUser() throws Exception {
		log.debug("Begin update TipoSpedizione Test - Forbidden");
		String token = getBearerToken("MarioRossi");

		TipoSpedizioneRequest req = new TipoSpedizioneRequest();
		req.setId(1);
		req.setTipoSpedizione("Modifica vietata");

		mockMvc.perform(put("/rest/tipo_spedizione/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	}

	// TEST ENDPOINT: DELETE (Solo ADMIN)

	@Test
	public void deleteSuccessAdmin() throws Exception {
		log.debug("Begin delete TipoSpedizione Test - Real Lifecycle");
		String token = getBearerToken("AdminUser"); 

		//  Creiamo un tipo spedizione 
		TipoSpedizioneRequest req = buildTipoSpedizioneRequest();
		mockMvc.perform(post("/rest/tipo_spedizione/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());

		//. Troviamo l'id tramite list()
		String responseBody = mockMvc.perform(get("/rest/tipo_spedizione/list")
				.param("tipoSpedizione", "Spedizione Express Test")
				.header("Authorization", token))
				.andReturn().getResponse().getContentAsString();
				
		com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
		Integer idDaCancellare = rootNode.get(0).path("id").asInt();

		mockMvc.perform(delete("/rest/tipo_spedizione/delete/" + idDaCancellare).with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void deleteErrorOrderLinked() throws Exception {
		log.debug("Begin delete TipoSpedizione Test - Linked Order");
		String token = getBearerToken("AdminUser"); 

		// La spedizione 1 è  collegata all'ordine inserito nel data.sql
		mockMvc.perform(delete("/rest/tipo_spedizione/delete/1").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Tipo spedizione in uso: eliminazione bloccata"));
	}


	// TEST ENDPOINT: RECUPERO DATI (Pubblici)

	@Test
	public void listSuccess() throws Exception {
		log.debug("Begin list TipoSpedizione test");
		String token = getBearerToken("MarioRossi"); // Accesso consentito a tutti

		mockMvc.perform(get("/rest/tipo_spedizione/list")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void findByIdSuccess() throws Exception {
		log.debug("Begin findById TipoSpedizione test");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/tipo_spedizione/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}
	
	
		// TEST ERRORI: UPDATE


		@Test
		public void updateErrorNotFound() throws Exception {
			log.debug("Begin update TipoSpedizione Test - ID Not Found");
			String token = getBearerToken("AdminUser"); 

			// Proviamo a modificare un ID che non esiste
			TipoSpedizioneRequest req = new TipoSpedizioneRequest();
			req.setId(999);
			req.setTipoSpedizione("Inesistente");
			req.setCostoSpedizione(new BigDecimal("10.00"));

			mockMvc.perform(put("/rest/tipo_spedizione/update").with(csrf())
					.header("Authorization", token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.msg").value("Tipo spedizione non trovato")); 
		}

		@Test
		public void updateErrorNegativeCost() throws Exception {
			log.debug("Begin update TipoSpedizione Test - Negative Cost");
			String token = getBearerToken("AdminUser"); 

			// Proviamo a mettere un costo negativo
			TipoSpedizioneRequest req = new TipoSpedizioneRequest();
			req.setId(1);
			req.setTipoSpedizione("Standard");
			req.setCostoSpedizione(new BigDecimal("-5.00")); 

			mockMvc.perform(put("/rest/tipo_spedizione/update").with(csrf())
					.header("Authorization", token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.msg").value("Prezzo assente/non valido"));
		}


		// TEST ERRORI: FIND BY ID

		@Test
		public void findByIdErrorNotFound() throws Exception {
			log.debug("Begin findById TipoSpedizione Test - Not Found");
			String token = getBearerToken("MarioRossi");

			// Cerchiamo un ID inesistente. 
			mockMvc.perform(get("/rest/tipo_spedizione/findById").param("id", "999")
					.header("Authorization", token))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("Tipo spedizione non trovato")); 
		}
}