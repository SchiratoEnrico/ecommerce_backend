package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // FONDAMENTALE: Fa il rollback del DB H2 dopo ogni test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CarrelloControllerTest {

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


	// TEST ENDPOINT: CREATE

	@Test
	public void createSuccessAsAdmin() throws Exception {
		log.debug("Begin create Carrello Test - Success");
		String token = getBearerToken("AdminUser"); 

		CarrelloRequest req = new CarrelloRequest();
		req.setId_account(2);

		mockMvc.perform(post("/rest/carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andDo(print()) 
				.andExpect(status().isOk());
	}

	@Test
	public void createForbiddenNotOwner() throws Exception {
		log.debug("Begin create Carrello Test - Forbidden");
		String token = getBearerToken("MarioRossi"); 

		CarrelloRequest req = new CarrelloRequest();
		req.setId_account(2); // Mario prova a creare il carrello per l'Admin

		mockMvc.perform(post("/rest/carrello/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: non puoi creare carrello per altri"));
	}

	
	// TEST ENDPOINT: ADD ROW

	@Test
	public void addRowOwnerSuccess() throws Exception {
		log.debug("Begin addRow test - Owner Success");
		String token = getBearerToken("MarioRossi");

		// Aggiungiamo 1 copia di ISBN001 al carrello 1
		mockMvc.perform(put("/rest/carrello/addRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "1")
				.param("isbn", "ISBN001")
				.param("nCopie", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void addRowForbiddenNotOwner() throws Exception {
		log.debug("Begin addRow test - Forbidden");
		String token = getBearerToken("MarioRossi");

		// Mario prova ad aggiungere a un carrello non suo (es. id 99)
		mockMvc.perform(put("/rest/carrello/addRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "99")
				.param("isbn", "ISBN001")
				.param("nCopie", "1"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: non puoi modificare il carrello di un altro utente."));
	}

	@Test
	public void addRowErrorMangaNotFound() throws Exception {
		log.debug("Begin addRow test - Error Manga Not Found");
		String token = getBearerToken("AdminUser"); // Usiamo l'admin per superare il check di proprietà e testare il Service

		mockMvc.perform(put("/rest/carrello/addRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "1")
				.param("isbn", "ISBN_FALSO")
				.param("nCopie", "1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Manga non trovato")); 
	}

	// TEST ENDPOINT: UPDATE ROW

	@Test
	public void updateRowOwnerSuccess() throws Exception {
		log.debug("Begin updateRow test - Owner Success");
		String token = getBearerToken("MarioRossi");

		// Nel DB la riga 1 è l'ISBN001. La aggiorniamo a 5 copie
		mockMvc.perform(put("/rest/carrello/updateRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "1")
				.param("rowId", "1")
				.param("isbn", "ISBN001")
				.param("nCopie", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void updateRowDeleteZeroCopie() throws Exception {
		log.debug("Begin updateRow test - Delete on 0 copie");
		String token = getBearerToken("MarioRossi");

		// Se nCopie = 0, il controller restituisce il messaggio di eliminazione
		mockMvc.perform(put("/rest/carrello/updateRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "1")
				.param("rowId", "1")
				.param("isbn", "ISBN001")
				.param("nCopie", "0"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void updateRowForbiddenNotOwner() throws Exception {
		log.debug("Begin updateRow test - Forbidden");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(put("/rest/carrello/updateRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "99")
				.param("rowId", "1")
				.param("isbn", "ISBN001")
				.param("nCopie", "5"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: non puoi modificare questo carrello."));
	}


	// TEST ENDPOINT: DELETE ROW

	@Test
	public void deleteRowOwnerSuccess() throws Exception {
		log.debug("Begin deleteRow test - Owner Success");
		String token = getBearerToken("MarioRossi");

		// Eliminiamo la riga 2 (Dragon Ball Vol.1) dal carrello
		mockMvc.perform(delete("/rest/carrello/deleteRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "1")
				.param("rowId", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
	}

	@Test
	public void deleteRowForbiddenNotOwner() throws Exception {
		log.debug("Begin deleteRow test - Forbidden");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(delete("/rest/carrello/deleteRow").with(csrf())
				.header("Authorization", token)
				.param("chartId", "99")
				.param("rowId", "1"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: non puoi eliminare righe da questo carrello."));
	}


	// TEST ENDPOINT: DELETE CARRELLO
	@Test
	public void deleteCarrelloAdminSuccess() throws Exception {
		log.debug("Begin delete Carrello test - Admin Success");
		String token = getBearerToken("AdminUser"); 

		mockMvc.perform(delete("/rest/carrello/delete/1").with(csrf())
				.header("Authorization", token))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void deleteCarrelloForbiddenUser() throws Exception {
		log.debug("Begin delete Carrello test - Forbidden as User");
		String token = getBearerToken("MarioRossi"); 

		mockMvc.perform(delete("/rest/carrello/delete/1").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	}


	// TEST ENDPOINT: RECUPERO DATI

	@Test
	public void listSuccessAsAdmin() throws Exception {
		log.debug("Begin list() Carrello test - Success as Admin");
		String token = getBearerToken("AdminUser");

		mockMvc.perform(get("/rest/carrello/list")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void listSuccessWithIsbnsFilter() throws Exception {
		log.debug("Begin list() Carrello test - Filtered");
		String token = getBearerToken("AdminUser");

		mockMvc.perform(get("/rest/carrello/list")
				.param("isbns", "ISBN001", "ISBN002") // Passiamo una lista
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void findByAccountIdOwnerSuccess() throws Exception {
		log.debug("Begin findByAccountId test - Owner Success");
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/carrello/findByAccountId")
				.param("id", "1") // ID account di MarioRossi
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void findByAccountIdForbiddenNotOwner() throws Exception {
		log.debug("Begin findByAccountId test - Forbidden");
		String token = getBearerToken("MarioRossi");

		// Mario prova a vedere il carrello dell'account 2 (Admin)
		mockMvc.perform(get("/rest/carrello/findByAccountId")
				.param("id", "2")
				.header("Authorization", token))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Accesso negato: puoi visualizzare solo il tuo carrello."));
	}
}