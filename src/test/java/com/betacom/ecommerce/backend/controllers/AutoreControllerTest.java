package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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
import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // FONDAMENTALE: Fa il rollback del DB H2 dopo ogni test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AutoreControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService; 
 
	@Autowired
	private UserDetailsService userDetailsService;

	// Inizializziamo l'ObjectMapper col modulo per le date (LocalDate), essenziale per Autore
	private ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


	// UTILITIES

	private String getBearerToken(String username) {
		UserDetails user = userDetailsService.loadUserByUsername(username);
		String token = jwtService.generateToken(user.getUsername()); 
		return "Bearer " + token;
	}

	private AutoreRequest buildAutoreRequest() {
		return AutoreRequest.builder()
				.nome("Hajime")
				.cognome("Isayama")
				.dataNascita(LocalDate.of(1986, 8, 29))
				.descrizione("Autore di Attack on Titan")
				.build();
	}


	// TEST ENDPOINT: CREATE (Solo ADMIN)

	@Test
	public void createSuccessAsAdmin() throws Exception {
		log.debug("Begin create Autore Test - Success as Admin");
		String token = getBearerToken("AdminUser"); 

		AutoreRequest req = buildAutoreRequest();

		mockMvc.perform(post("/rest/autore/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento creato con successo"));
	}

	@Test
	public void createForbiddenAsUser() throws Exception {
		log.debug("Begin create Autore Test - Forbidden as User");
		String token = getBearerToken("MarioRossi"); 

		AutoreRequest req = buildAutoreRequest();

		mockMvc.perform(post("/rest/autore/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	}

	@Test
	public void createErrorDuplicateCatch() throws Exception {
		log.debug("Begin create Autore Test - Error Duplicate");
		String token = getBearerToken("AdminUser"); 

		// 1. Creiamo un nuovo autore tramite API 
		AutoreRequest req = AutoreRequest.builder()
				.nome("Kentaro")
				.cognome("Miura")
				.dataNascita(LocalDate.of(1966, 7, 11))
				.descrizione("Autore di Berserk")
				.build();

		mockMvc.perform(post("/rest/autore/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk()); // La prima volta deve funzionare

		// 2. Ripetiamo ESATTAMENTE la stessa chiamata
		mockMvc.perform(post("/rest/autore/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Autore già presente")); 
	}


	// TEST ENDPOINT: UPDATE (Solo ADMIN)

	@Test
	public void updateSuccessAsAdmin() throws Exception {
		log.debug("Begin update Autore test - Success as Admin");
		String token = getBearerToken("AdminUser");

		// Modifichiamo l'autore 1 (Akira Toriyama)
		AutoreRequest req = AutoreRequest.builder()
				.id(1)
				.nome("Akira")
				.cognome("Toriyama")
				.dataNascita(LocalDate.of(1955, 4, 5))
				.descrizione("Nuova descrizione aggiornata")
				.build();

		mockMvc.perform(put("/rest/autore/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void updateForbiddenAsUser() throws Exception {
		log.debug("Begin update Autore test - Forbidden as User");
		String token = getBearerToken("MarioRossi");

		AutoreRequest req = buildAutoreRequest();
		req.setId(1);

		mockMvc.perform(put("/rest/autore/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	}

	
	// TEST ENDPOINT: DELETE (Solo ADMIN)

	@Test
	public void deleteSuccessReal() throws Exception {
		log.debug("Begin delete Autore test - Real Lifecycle");
		String adminToken = getBearerToken("AdminUser");

		// 1. Creiamo un autore  (senza manga collegati)
		AutoreRequest req = buildAutoreRequest();
		mockMvc.perform(post("/rest/autore/create").with(csrf())
				.header("Authorization", adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());

		// 2. Lo cerchiamo tramite findByFilters per ottenerne l'ID
		String responseBody = mockMvc.perform(get("/rest/autore/findByFilters")
				.param("nome", "Hajime")
				.param("cognome", "Isayama")
				.header("Authorization", adminToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
				
		com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
		Integer idDaCancellare = rootNode.get(0).path("id").asInt();

		// 3. Eseguiamo la DELETE 
		mockMvc.perform(delete("/rest/autore/delete").param("id", idDaCancellare.toString()).with(csrf())
				.header("Authorization", adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void deleteForbiddenAsUser() throws Exception {
		log.debug("Begin delete Autore test - Forbidden as User");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(delete("/rest/autore/delete").param("id", "1").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	}

	@Test
	public void deleteErrorLinkedMangaCatch() throws Exception {
		log.debug("Begin delete Autore test - Linked Manga Error");
		String token = getBearerToken("AdminUser");

		// Proviamo a cancellare l'Autore 1 che ha un manga collegato (ISBN002 nel data.sql)
		mockMvc.perform(delete("/rest/autore/delete").param("id", "1").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Elemento collegato a manga: eliminazione bloccata")); 
	}


	// TEST ENDPOINT: RECUPERO DATI (Pubblici/Utenti Autenticati)

	@Test
	public void listSuccess() throws Exception {
		log.debug("Begin list() Autore test - Success");
		String token = getBearerToken("MarioRossi"); // Accessibile a tutti 

		mockMvc.perform(get("/rest/autore/list")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void findByIdSuccess() throws Exception {
		log.debug("Begin findById Autore test - Success");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/autore/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nome").value("Akira"));
	}

	@Test
	public void findByFiltersSuccess() throws Exception {
		log.debug("Begin findByFilters Autore test - Success");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/autore/findByFilters")
				.param("nome", "Eiichiro")
				.param("cognome", "Oda")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].nome").value("Eiichiro"));
	}
}