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

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // FONDAMENTALE: Fa il rollback del DB H2 dopo ogni test per evitare sovrapposizioni
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	// I servizi reali che ci servono per creare il Token
	@Autowired
	private JwtService jwtService; 

	@Autowired
	private UserDetailsService userDetailsService;

	private ObjectMapper objectMapper = new ObjectMapper();

	private AccountRequest buildAccountRequest() {
		return AccountRequest.builder()
				.username("anna.anna")
				.password("aaaA00.1!") 
				.email("a.anna@test.it")
				.ruolo("ADMIN")
				.build();
	}

	// ==========================================
	// METODO MAGICO PER IL TOKEN
	// ==========================================
	private String getBearerToken(String username) {
		UserDetails user = userDetailsService.loadUserByUsername(username);
		// Se il tuo metodo si chiama diversamente da "generateToken", cambialo qui:
		String token = jwtService.generateToken(user.getUsername()); 
		return "Bearer " + token;
	}


	// ==========================================
	// TEST ENDPOINT PUBBLICI (Senza Autenticazione)
	// ==========================================

	@Test
	public void createSuccess() throws Exception {
		log.debug("Begin create Account Test - Success");
		AccountRequest req = buildAccountRequest();

		mockMvc.perform(post("/rest/account/create").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento creato con successo"));
	}

	@Test
	public void createErrorNullUsername() throws Exception {
		log.debug("Begin create Account Test - Error null username");
		AccountRequest req = buildAccountRequest();
		req.setUsername(null);

		mockMvc.perform(post("/rest/account/create").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Username assente"));
	}

	@Test
	public void createPasswordInvalida() throws Exception {
		log.debug("Begin create Account Test - password invalida");
		AccountRequest req = buildAccountRequest();
		req.setPassword("aaaa001"); 

		mockMvc.perform(post("/rest/account/create").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest());
	}


	// ==========================================
	// TEST ENDPOINT PRIVATI (Solo ADMIN)
	// ==========================================

	@Test
	public void listSuccessAsAdmin() throws Exception {
		log.debug("Begin list() Account test - Success as Admin");
		
		String token = getBearerToken("AdminUser"); // Genera il token dell'Admin

		mockMvc.perform(get("/rest/account/list")
				.header("Authorization", token)) // Inserisce il token nell'header
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void listForbiddenAsUser() throws Exception {
		log.debug("Begin list() Account test - Forbidden as User");
		
		String token = getBearerToken("MarioRossi"); // Genera il token dello User

		mockMvc.perform(get("/rest/account/list")
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	}


	// ==========================================
	// TEST ENDPOINT CONDIVISI (Admin + Owner)
	// ==========================================

	@Test
	public void updateOwnerSuccess() throws Exception {
		log.debug("Begin update Account test - Owner updating himself");
		
		String token = getBearerToken("MarioRossi");

		AccountRequest req = AccountRequest.builder()
				.id(1)
				.username("MarioRossi") 
				.password("Aaa.012!")
				.email("user.nuova@test.com")
				.ruolo("USER")
				.build();

		mockMvc.perform(put("/rest/account/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Elemento aggiornato con successo"));
	}

	@Test
	public void updateForbiddenNotOwner() throws Exception {
		log.debug("Begin update Account test - User updating another user");
		
		String token = getBearerToken("MarioRossi"); // MarioRossi tenta l'aggiornamento

		AccountRequest req = AccountRequest.builder()
				.id(2)
				.username("AdminUser") // Su un altro utente
				.build();

		mockMvc.perform(put("/rest/account/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.msg").value("Accesso negato: puoi modificare solo il tuo profilo."));
	}

	@Test
	public void findByIdOwnerSuccess() throws Exception {
		log.debug("Begin findById test - Owner finds himself");
		
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/account/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void findByIdForbiddenNotOwner() throws Exception {
		log.debug("Begin findById test - User finds another user (Forbidden)");
		
		String token = getBearerToken("MarioRossi");

		mockMvc.perform(get("/rest/account/findById").param("id", "2")
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	}

	@Test
	public void findByIdAdminSuccess() throws Exception {
		log.debug("Begin findById test - Admin finds another user");
		
		String token = getBearerToken("AdminUser");

		mockMvc.perform(get("/rest/account/findById").param("id", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void deleteErrorNotExists() throws Exception {
		log.debug("Delete Account Test - Error Not Exists");
		
		String token = getBearerToken("AdminUser");

		mockMvc.perform(delete("/rest/account/delete/999").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value("Account assente"));
	}
	
	// ==========================================
		// TEST ENDPOINT: findByFilters (Solo ADMIN)
		// ==========================================

		@Test
		public void findByFiltersSuccessAsAdmin() throws Exception {
			log.debug("Begin findByFilters test - Success as Admin");
			
			String token = getBearerToken("AdminUser");

			// L'admin cerca gli utenti con ruolo "USER"
			mockMvc.perform(get("/rest/account/findByFilters")
					.param("ruolo", "USER")
					.header("Authorization", token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray());
		}

		@Test
		public void findByFiltersForbiddenAsUser() throws Exception {
			log.debug("Begin findByFilters test - Forbidden as User");
			
			String token = getBearerToken("MarioRossi");

			mockMvc.perform(get("/rest/account/findByFilters")
					.param("ruolo", "USER")
					.header("Authorization", token))
					.andExpect(status().isForbidden());
		}
		
		
		// ==========================================
		// TEST ENDPOINT: findByUsername (Admin + Owner)
		// ==========================================

		@Test
		public void findByUsernameOwnerSuccess() throws Exception {
			log.debug("Begin findByUsername test - Owner finds himself");
			
			String token = getBearerToken("MarioRossi");

			mockMvc.perform(get("/rest/account/findByUsername")
					.param("username", "MarioRossi")
					.header("Authorization", token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.username").value("MarioRossi"));
		}

		@Test
		public void findByUsernameAdminSuccess() throws Exception {
			log.debug("Begin findByUsername test - Admin finds User");
			
			String token = getBearerToken("AdminUser");

			mockMvc.perform(get("/rest/account/findByUsername")
					.param("username", "MarioRossi")
					.header("Authorization", token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.username").value("MarioRossi"));
		}

		@Test
		public void findByUsernameForbiddenNotOwner() throws Exception {
			log.debug("Begin findByUsername test - User finds another user");
			
			String token = getBearerToken("MarioRossi");

			// MarioRossi tenta di cercare i dati di AdminUser
			mockMvc.perform(get("/rest/account/findByUsername")
					.param("username", "AdminUser")
					.header("Authorization", token))
					.andExpect(status().isForbidden())
					.andExpect(content().string("Accesso negato: non puoi vedere i dati di altri utenti.")); // <--- MODIFICATO QUI
		}
		
		
		// ==========================================
		// TEST ENDPOINT: delete (Forbidden Not Owner)
		// ==========================================

		@Test
		public void deleteForbiddenNotOwner() throws Exception {
			log.debug("Begin delete test - User tries to delete Admin");
			
			String token = getBearerToken("MarioRossi");

			// Mario Rossi (id 1) prova a cancellare AdminUser (id 2)
			mockMvc.perform(delete("/rest/account/delete/2").with(csrf())
					.header("Authorization", token))
					.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.msg").value("Accesso negato: puoi eliminare solo il tuo account."));
		}
		
		
		
		// ==========================================
		// TEST ENDPOINT: DELETE SUCCESS (Ciclo Completo)
		// ==========================================

		@Test
		public void deleteSuccessReal() throws Exception {
			log.debug("Begin delete test - Real Success Lifecycle");
			
			// 1. Creiamo un utente "sacrificabile" (senza carrelli o ordini) tramite la API pubblica
			AccountRequest req = AccountRequest.builder()
					.username("UsaEGetta")
					.password("Aaa.012!")
					.email("usa@getta.it")
					.ruolo("USER")
					.build();

			mockMvc.perform(post("/rest/account/create").with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
					.andExpect(status().isOk());

			// 2. Recuperiamo l'ID di questo nuovo utente usando il token dell'Admin
			String adminToken = getBearerToken("AdminUser");
			
			String responseBody = mockMvc.perform(get("/rest/account/findByUsername")
					.param("username", "UsaEGetta")
					.header("Authorization", adminToken))
					.andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString();
					
			// Estraiamo l'ID dall'oggetto JSON di risposta usando Jackson
			com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
			Integer idDaCancellare = rootNode.path("id").asInt();

			// 3. Eseguiamo finalmente la DELETE con successo!
			mockMvc.perform(delete("/rest/account/delete/" + idDaCancellare).with(csrf())
					.header("Authorization", adminToken))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.msg").value("Elemento eliminato con successo"));
		}

		// ==========================================
		// TEST DEI CATCH (Bad Requests - 400)
		// ==========================================

		@Test
		public void updateErrorNotExistsCatch() throws Exception {
			log.debug("Begin update test - Catch Block (Not Exists)");
			
			String adminToken = getBearerToken("AdminUser");

			// Proviamo ad aggiornare un ID inesistente (es. 999) per far arrabbiare il Service
			AccountRequest req = AccountRequest.builder()
					.id(999)
					.username("Inesistente")
					.build();

			mockMvc.perform(put("/rest/account/update").with(csrf())
					.header("Authorization", adminToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
					.andExpect(status().isBadRequest())
					// Controlliamo che il controller ci restituisca il messaggio di errore dal DB
					.andExpect(jsonPath("$.msg").value("Account assente")); 
		}

		@Test
		public void findByIdErrorNotExistsCatch() throws Exception {
			log.debug("Begin findById test - Catch Block (Not Exists)");
			
			String adminToken = getBearerToken("AdminUser");

			// Attenzione: Nel tuo Controller, la findById restituisce direttamente e.getMessage() come Stringa, 
			// non usa r.setMsg(...) come le altre. Quindi verifichiamo il "content().string(...)"
			mockMvc.perform(get("/rest/account/findById").param("id", "999")
					.header("Authorization", adminToken))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("!exists_acc")); // O l'esatto codice/messaggio che lancia il tuo Service
		}

		@Test
		public void findByUsernameErrorNotExistsCatch() throws Exception {
			log.debug("Begin findByUsername test - Catch Block (Not Exists)");
			
			String adminToken = getBearerToken("AdminUser");

			mockMvc.perform(get("/rest/account/findByUsername")
					.param("username", "UtenteFantasma")
					.header("Authorization", adminToken))
					.andExpect(status().isBadRequest());
		}

		@Test
		public void createErrorDuplicateUsernameCatch() throws Exception {
			log.debug("Begin create test - Catch Block (Duplicate Username)");
			
			// Proviamo a creare un utente con lo username dell'Admin, che esiste già!
			AccountRequest req = AccountRequest.builder()
					.username("AdminUser") // Duplicato
					.password("Aaa.012!")
					.email("nuova@email.it")
					.ruolo("USER")
					.build();

			mockMvc.perform(post("/rest/account/create").with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.msg").value("Username già presente"));
		}
}