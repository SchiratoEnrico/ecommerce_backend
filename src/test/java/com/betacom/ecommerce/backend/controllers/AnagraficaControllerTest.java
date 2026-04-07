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
import com.betacom.ecommerce.backend.dto.inputs.AnagraficaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // FONDAMENTALE: Fa il rollback del DB H2 dopo ogni test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AnagraficaControllerTest {

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

	private AnagraficaRequest buildAnagraficaRequest(Integer idAccount) {
		return AnagraficaRequest.builder()
				.idAccount(idAccount)
				.nome("Anna")
				.cognome("Verdi")
				.stato("Italia")
				.citta("Sesto Fiorentino")
				.provincia("Firenze")
				.cap("12345")
				.via("Via Trento 1")
				.predefinito(true)
				.build();
	}


	// TEST ENDPOINT: CREATE

	@Test
	public void createSuccessAsOwner() throws Exception {
		log.debug("Begin create Anagrafica Test - Success as Owner");
		String token = getBearerToken("MarioRossi"); // id account 1

		// Mario Rossi crea un'anagrafica per se stesso (idAccount = 1)
		AnagraficaRequest req = buildAnagraficaRequest(1);

		mockMvc.perform(post("/rest/anagrafica/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento creato con successo"));
	}

	@Test
	public void createForbiddenNotOwner() throws Exception {
		log.debug("Begin create Anagrafica Test - Forbidden Not Owner");
		String token = getBearerToken("MarioRossi"); // id account 1

		// Mario Rossi prova a creare un'anagrafica per l'Admin (idAccount = 2)
		AnagraficaRequest req = buildAnagraficaRequest(2);

		mockMvc.perform(post("/rest/anagrafica/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: puoi creare indirizzi solo per il tuo account."));
	}

	@Test
	public void createErrorsNullFieldsCatch() throws Exception {
		log.debug("Begin create Anagrafica Test - Errors Null Fields");
		String token = getBearerToken("MarioRossi");

		// Test Nome Assente
		AnagraficaRequest req = buildAnagraficaRequest(1);
		req.setNome(null);

		mockMvc.perform(post("/rest/anagrafica/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Nome assente"));
				
		// Test Stato Assente
		req.setNome("Anna");
		req.setStato(null);

		mockMvc.perform(post("/rest/anagrafica/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Stato assente"));
	}


	// TEST ENDPOINT: UPDATE

	@Test
	public void updateOwnerSuccess() throws Exception {
		log.debug("Begin update Anagrafica test - Success as Owner");
		String token = getBearerToken("MarioRossi");

		// Modifichiamo l'anagrafica di default di Mario Rossi (id = 1)
		AnagraficaRequest req = buildAnagraficaRequest(1);
		req.setId(1);
		req.setCitta("Modena"); // Modifica

		mockMvc.perform(put("/rest/anagrafica/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void updateForbiddenNotOwner() throws Exception {
		log.debug("Begin update Anagrafica test - Forbidden Not Owner");
		String token = getBearerToken("MarioRossi");

		// Mario prova ad aggiornare un ID che non possiede 
		AnagraficaRequest req = buildAnagraficaRequest(1);
		req.setId(99); 

		mockMvc.perform(put("/rest/anagrafica/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: non puoi modificare questo indirizzo."));
	}

	@Test
	public void updateErrorNotExistsCatch() throws Exception {
		log.debug("Begin update Anagrafica test - Catch (Not Exists) as Admin");

		String token = getBearerToken("AdminUser");

		AnagraficaRequest req = buildAnagraficaRequest(1);
		req.setId(99); 

		mockMvc.perform(put("/rest/anagrafica/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Anagrafica assente"));
	}



	@Test
	public void deleteSuccessReal() throws Exception {
		log.debug("Begin delete Anagrafica test - Real Lifecycle");
		String adminToken = getBearerToken("AdminUser");

		// 1. Creiamo un'anagrafica libera per l'admin
		AnagraficaRequest req = buildAnagraficaRequest(2);
		mockMvc.perform(post("/rest/anagrafica/create").with(csrf())
				.header("Authorization", adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());

		// 2. Troviamo il suo ID cercando le anagrafiche dell'account 2 (Admin)
		String responseBody = mockMvc.perform(get("/rest/anagrafica/findByAccountId").param("id", "2")
				.header("Authorization", adminToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
				
		com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
		Integer idDaCancellare = rootNode.get(0).path("id").asInt(); // Prende il primo elemento dell'array

		// 3. Eseguiamo la DELETE
		mockMvc.perform(delete("/rest/anagrafica/delete/" + idDaCancellare).with(csrf())
				.header("Authorization", adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void deleteForbiddenNotOwner() throws Exception {
		log.debug("Begin delete Anagrafica test - Forbidden Not Owner");
		String token = getBearerToken("MarioRossi");

		// Mario prova a eliminare un'anagrafica che non gli appartiene (es. id inesistente 99 o di altri)
		mockMvc.perform(delete("/rest/anagrafica/delete/99").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: non puoi eliminare questo indirizzo."));
	}


	// TEST ENDPOINT: FINDBYID & FINDBYACCOUNTID

	@Test
	public void findByIdOwnerSuccess() throws Exception {
		log.debug("Begin findById test - Owner Success");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/anagrafica/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void findByIdForbiddenNotOwner() throws Exception {
		log.debug("Begin findById test - Forbidden Not Owner");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/anagrafica/findById").param("id", "99")
				.header("Authorization", token))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Accesso negato: indirizzo non tuo."));
	}
	
	@Test
	public void findByAccountIdOwnerSuccess() throws Exception {
		log.debug("Begin findByAccountId test - Owner Success");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/anagrafica/findByAccountId").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void findByAccountIdForbiddenNotOwner() throws Exception {
		log.debug("Begin findByAccountId test - Forbidden Not Owner");
		String token = getBearerToken("MarioRossi");

		// Mario (id 1) cerca le anagrafiche dell'Admin (id 2)
		mockMvc.perform(get("/rest/anagrafica/findByAccountId").param("id", "2")
				.header("Authorization", token))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Accesso negato: puoi vedere solo i tuoi indirizzi."));
	}


	// TEST ENDPOINT: LIST (Solo Admin)

	@Test
	public void listSuccessAsAdmin() throws Exception {
		log.debug("Begin list() Anagrafica test - Success as Admin");
		String token = getBearerToken("AdminUser"); 

		mockMvc.perform(get("/rest/anagrafica/list")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void listForbiddenAsUser() throws Exception {
		log.debug("Begin list() Anagrafica test - Forbidden as User");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/anagrafica/list")
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	}
}