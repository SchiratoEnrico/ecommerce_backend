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
import com.betacom.ecommerce.backend.dto.inputs.RigaCarrelloRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback automatico del DB H2 dopo ogni test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RigaCarrelloControllerTest {

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

	private RigaCarrelloRequest buildRigaCarrelloRequest() {
		RigaCarrelloRequest r = new RigaCarrelloRequest();
		r.setCarrelloId(1); // Il carrello 1 appartiene a Mario Rossi nel data.sql
		r.setManga("ISBN002");
		r.setNumeroCopie(1);
		return r;
	}

	// TEST ENDPOINT: CREATE

	@Test
	public void createSuccessOwner() throws Exception {
		log.debug("Begin create RigaCarrello Test - Success");
		String token = getBearerToken("MarioRossi"); 

		RigaCarrelloRequest req = buildRigaCarrelloRequest();

		mockMvc.perform(post("/rest/riga_carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento creato con successo"));
	}

	@Test
	public void createForbiddenNotOwner() throws Exception {
		log.debug("Begin create RigaCarrello Test - Forbidden");
		String token = getBearerToken("MarioRossi"); 

		RigaCarrelloRequest req = buildRigaCarrelloRequest();
		req.setCarrelloId(99); // Mario non possiede il carrello 99

		mockMvc.perform(post("/rest/riga_carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato."));
	}

	@Test
	public void createErrorsCatch() throws Exception {
		log.debug("Begin create RigaCarrello Test - Catch Errors");
		String token = getBearerToken("AdminUser"); // Admin per bypassare il blocco controller e testare il service

		// 1. Errore: CarrelloId null
		RigaCarrelloRequest req = buildRigaCarrelloRequest();
		req.setCarrelloId(null);
		mockMvc.perform(post("/rest/riga_carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Id carrello non può essere null"));

		// 2. Errore: Manga null
		req = buildRigaCarrelloRequest();
		req.setManga(null);
		mockMvc.perform(post("/rest/riga_carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Manga assente"));

		// 3. Errore: Carrello inesistente
		req = buildRigaCarrelloRequest();
		req.setCarrelloId(99);
		mockMvc.perform(post("/rest/riga_carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Carrello non trovato")); 

		// 4. Errore: Numero Copie 0
		req = buildRigaCarrelloRequest();
		req.setNumeroCopie(0);
		mockMvc.perform(post("/rest/riga_carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Quantità assente")); 
	}


	// TEST ENDPOINT: UPDATE

	@Test
	public void updateSuccessOwner() throws Exception {
		log.debug("Begin update RigaCarrello Test - Success");
		String token = getBearerToken("MarioRossi"); 

		// La riga 1 appartiene al carrello 1 (Mario Rossi)
		RigaCarrelloRequest req = buildRigaCarrelloRequest();
		req.setId(1);
		req.setCarrelloId(null); // Importante per l'update del carrello, deve essere null
		req.setNumeroCopie(5);

		mockMvc.perform(put("/rest/riga_carrello/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void updateZeroCopieTriggersDelete() throws Exception {
		log.debug("Begin update RigaCarrello Test - Zero Copie (Delete)");
		String token = getBearerToken("MarioRossi"); 

		// Se passiamo copie = 0, il Service elimina la riga e il controller restituisce "rest_deleted"
		RigaCarrelloRequest req = buildRigaCarrelloRequest();
		req.setId(1);
		req.setCarrelloId(null);
		req.setNumeroCopie(0);

		mockMvc.perform(put("/rest/riga_carrello/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void updateForbiddenNotOwner() throws Exception {
		log.debug("Begin update RigaCarrello Test - Forbidden");
		String token = getBearerToken("MarioRossi"); 

		RigaCarrelloRequest req = buildRigaCarrelloRequest();
		req.setId(99); // Riga inesistente / di un altro
		req.setCarrelloId(null);

		mockMvc.perform(put("/rest/riga_carrello/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato."));
	}

	@Test
	public void updateErrorsCatch() throws Exception {
		log.debug("Begin update RigaCarrello Test - Catch Errors");
		String token = getBearerToken("AdminUser"); 

		// 1. Errore: Modifica del Carrello ID non permessa
		RigaCarrelloRequest req = buildRigaCarrelloRequest();
		req.setId(1);
		req.setCarrelloId(2); // Farà scattare l'eccezione "id_chng"
		
		mockMvc.perform(put("/rest/riga_carrello/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Impossibile cambiare il carrello di una riga"));
	}


	// TEST ENDPOINT: DELETE

	@Test
	public void deleteSuccessOwner() throws Exception {
		log.debug("Begin delete RigaCarrello Test - Success");
		String token = getBearerToken("MarioRossi"); 

		// Eliminiamo la riga 2 dal DB 
		mockMvc.perform(delete("/rest/riga_carrello/delete/2").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void deleteForbiddenNotOwner() throws Exception {
		log.debug("Begin delete RigaCarrello Test - Forbidden");
		String token = getBearerToken("MarioRossi"); 

		mockMvc.perform(delete("/rest/riga_carrello/delete/99").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato."));
	}

	@Test
	public void deleteErrorNotExists() throws Exception {
		log.debug("Begin delete RigaCarrello Test - Error Not Exists");
		String token = getBearerToken("AdminUser"); // Usiamo Admin per arrivare al catch

		mockMvc.perform(delete("/rest/riga_carrello/delete/99").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Riga carrello non trovata"));
	}


	// TEST ENDPOINT: RECUPERO DATI

	@Test
	public void findByIdSuccessOwner() throws Exception {
		log.debug("Begin findById RigaCarrello test - Success");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/riga_carrello/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void findByIdForbiddenNotOwner() throws Exception {
		log.debug("Begin findById RigaCarrello test - Forbidden");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/riga_carrello/findById").param("id", "99")
				.header("Authorization", token))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Accesso negato."));
	}

	@Test
	public void listSuccessOwner() throws Exception {
		log.debug("Begin list RigaCarrello test - Success");
		String token = getBearerToken("MarioRossi"); 

		// Mario può listare le righe solo se passa esplicitamente il SUO chartId
		mockMvc.perform(get("/rest/riga_carrello/list").param("chartId", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void listForbiddenMissingChartId() throws Exception {
		log.debug("Begin list RigaCarrello test - Forbidden (Missing chartId)");
		String token = getBearerToken("MarioRossi"); 

		// Mario prova a fare list senza chartId (potenziale rischio dati altrui)
		mockMvc.perform(get("/rest/riga_carrello/list")
				.header("Authorization", token))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Accesso negato.")); 
	}
}