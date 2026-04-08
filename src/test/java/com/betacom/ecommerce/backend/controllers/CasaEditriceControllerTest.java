package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
import com.betacom.ecommerce.backend.dto.inputs.CasaEditriceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback automatico del DB H2 dopo ogni test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CasaEditriceControllerTest {

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

	private CasaEditriceRequest buildCasaEditriceRequest() {
		CasaEditriceRequest req = new CasaEditriceRequest();
		req.setNome("Edizioni Test");
		req.setDescrizione("Casa editrice di prova per i test");
		req.setIndirizzo("Via del Test 1, Milano");
		req.setEmail("info@edizionitest.it");
		return req;
	}


	// TEST ENDPOINT: CREATE (Solo ADMIN)

	@Test
	public void createSuccessAdmin() throws Exception {
		log.debug("Begin create CasaEditrice Test - Success");
		String token = getBearerToken("AdminUser"); 

		CasaEditriceRequest req = buildCasaEditriceRequest();

		mockMvc.perform(post("/rest/casa_editrice/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento creato con successo"));
	}

	@Test
	public void createForbiddenUser() throws Exception {
		log.debug("Begin create CasaEditrice Test - Forbidden");
		String token = getBearerToken("MarioRossi"); 

		mockMvc.perform(post("/rest/casa_editrice/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(buildCasaEditriceRequest())))
				.andExpect(status().isForbidden());
	}

	@Test
	public void createErrorCatchBlocks() throws Exception {
		log.debug("Begin create CasaEditrice Test - Null Fields Catch");
		String token = getBearerToken("AdminUser"); 

		// Errore 1: Nome nullo (null_nom)
		CasaEditriceRequest req = buildCasaEditriceRequest();
		req.setNome(null);

		mockMvc.perform(post("/rest/casa_editrice/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Nome assente")); 

		// Errore 2: Duplicato (exists_casa)
		CasaEditriceRequest reqDup = buildCasaEditriceRequest();
		// Inseriamo prima l'originale
		mockMvc.perform(post("/rest/casa_editrice/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDup)))
				.andExpect(status().isOk());

		// Proviamo a inserirne una con lo stesso nome ma case diverso (il service usa ignoreCase)
		reqDup.setNome("edizioni TEST"); 
		mockMvc.perform(post("/rest/casa_editrice/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDup)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Casa editrice già presente")); 
	}


	// TEST ENDPOINT: UPDATE (Solo ADMIN)

	@Test
	public void updateSuccessAdmin() throws Exception {
		log.debug("Begin update CasaEditrice Test - Success");
		String token = getBearerToken("AdminUser"); 

		// Aggiorniamo la casa editrice 1 dal data.sql
		CasaEditriceRequest req = new CasaEditriceRequest();
		req.setId(1);
		req.setNome("Nuovo Nome Editore");
		req.setEmail("nuovaemail@test.it");

		mockMvc.perform(put("/rest/casa_editrice/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}


	// TEST ENDPOINT: DELETE (Solo ADMIN)

	@Test
	public void deleteSuccessAdmin() throws Exception {
		log.debug("Begin delete CasaEditrice Test - Real Lifecycle");
		String token = getBearerToken("AdminUser"); 

		// 1. Creiamo una casa editrice sacrificabile
		CasaEditriceRequest req = buildCasaEditriceRequest();
		mockMvc.perform(post("/rest/casa_editrice/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());

		// 2. Troviamola tramite list 
		String responseBody = mockMvc.perform(get("/rest/casa_editrice/list")
				.param("nome", "Edizioni Test")
				.header("Authorization", token))
				.andReturn().getResponse().getContentAsString();
				
		com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
		Integer idDaCancellare = rootNode.get(0).path("id").asInt();

		// 3. Eseguiamo la delete
		mockMvc.perform(delete("/rest/casa_editrice/delete/" + idDaCancellare).with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void deleteErrorLinkedMangaCatch() throws Exception {
		log.debug("Begin delete CasaEditrice Test - Linked Manga");
		String token = getBearerToken("AdminUser"); 

		// La casa editrice con ID 1 nel DB di test ha dei manga associati.
		mockMvc.perform(delete("/rest/casa_editrice/delete/1").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Casa editrice ha manga allegati: eliminazione bloccata")); 
	}


	// TEST ENDPOINT: RECUPERO DATI (Pubblici)

	@Test
	public void listSuccess() throws Exception {
		log.debug("Begin list CasaEditrice test");
		String token = getBearerToken("MarioRossi"); 

		mockMvc.perform(get("/rest/casa_editrice/list")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void findByIdSuccess() throws Exception {
		log.debug("Begin findById CasaEditrice test");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/casa_editrice/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}
	
	

		// TEST ERRORI: UPDATE

		@Test
		public void updateErrorNotFound() throws Exception {
			log.debug("Begin update CasaEditrice Test - ID Not Found");
			String token = getBearerToken("AdminUser"); 

			// Proviamo a modificare un ID che non esiste nel DB
			CasaEditriceRequest req = new CasaEditriceRequest();
			req.setId(999);
			req.setNome("Editore Fantasma");

			mockMvc.perform(put("/rest/casa_editrice/update").with(csrf())
					.header("Authorization", token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.msg").value("Casa editrice non trovata")); 
		}

		@Test
		public void updateErrorDuplicateName() throws Exception {
			log.debug("Begin update CasaEditrice Test - Duplicate Name");
			String token = getBearerToken("AdminUser"); 

			// 1. Creiamo una seconda casa editrice "Mondadori"
			CasaEditriceRequest req2 = new CasaEditriceRequest();
			req2.setNome("Mondadori");
			req2.setEmail("info@mondadori.it");
			req2.setIndirizzo("Milano");
			req2.setDescrizione("Grande Editore");
			
			mockMvc.perform(post("/rest/casa_editrice/create").with(csrf())
					.header("Authorization", token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req2)))
					.andExpect(status().isOk());

			// 2. Proviamo ad aggiornare l'ID 1 (che nel data.sql è "Panini") 
			// rinominandolo "Mondadori" (che ora esiste già)
			CasaEditriceRequest reqUpdate = new CasaEditriceRequest();
			reqUpdate.setId(1);
			reqUpdate.setNome("Mondadori");

			mockMvc.perform(put("/rest/casa_editrice/update").with(csrf())
					.header("Authorization", token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqUpdate)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.msg").value("Casa editrice già presente")); 
		}


		// TEST ERRORI: FIND BY ID
		@Test
		public void findByIdErrorNotFound() throws Exception {
			log.debug("Begin findById CasaEditrice Test - Not Found");
			String token = getBearerToken("MarioRossi");

			// Cerchiamo un ID inesistente
			mockMvc.perform(get("/rest/casa_editrice/findById").param("id", "999")
					.header("Authorization", token))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("Casa editrice non trovata")); 
		}
}