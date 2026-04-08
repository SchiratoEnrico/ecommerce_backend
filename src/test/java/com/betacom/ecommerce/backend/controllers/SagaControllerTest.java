package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.betacom.ecommerce.backend.dto.inputs.SagaRequest;
import com.betacom.ecommerce.backend.dto.outputs.SagaDTO;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SagaControllerTest {
    @Autowired
    private SagaController sagaC;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMessagesServices msgS;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String getBearerToken(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(user.getUsername());
        return "Bearer " + token;
    }

    // ==========================================
    // BUILD REQUEST
    // ==========================================
	private SagaRequest buildSagaRequest() {
		SagaRequest r = new SagaRequest();
		r.setNome("Ken Shiro");
		r.setDescrizione("Saga su formidabli guerrieri");
		return r;
	}
	
    // ==========================================
    // TESTS
    // ==========================================
    @Test
    public void testSagaControllerAdmin() throws Exception {
        createTest();
        updateTest();
        deleteTest();
    }
    
    @Test
    public void testSagaController() {
    	// chiunque => identici a prima
        listTest();
        findByIdTest();
    }
	
	private List<SagaDTO> getLoadedList(
			String casaEditriceNome,
			String autoreNome,
			String autoreCognome,
			String sagaNome,
			Integer sagaId,
			Integer casaEditriceId,
			Integer autoreId,
			List<Integer> generiId
			) {

    	ResponseEntity<?> resp = sagaC.list(
				casaEditriceNome,
				autoreNome,
				autoreCognome,
				sagaNome,
				sagaId,
				casaEditriceId,
				autoreId,
				generiId
        		);
    	// test per:
    	// linked_ord 
    	// linked_car
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);

    	return ((List<SagaDTO>) resp.getBody());
	}
	
	public void listTest() {
        log.debug("start list saga test");
		String casaEditriceNome = "";
		String autoreNome = "";
		String autoreCognome = "";
		String sagaNome = "";
		Integer sagaId = null;
		Integer casaEditriceId = null;
		Integer autoreId = null;
		List<Integer> generiId = new ArrayList<Integer>();

		// test vuoto
		List<SagaDTO> lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
		
    	// sagaNome
    	sagaNome = "piec";
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	sagaNome = "";
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// sagaNome
    	casaEditriceNome = "Shueis";
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceNome = "";
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// autoreNome
    	autoreNome = "iich";
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	autoreNome = "";
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// sagaId
    	sagaId= 1;
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	sagaId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// autoreId
    	autoreId= 1;
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	autoreId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// casaEditriceId
    	casaEditriceId= 1;
    	lS = getLoadedList(casaEditriceNome, autoreNome, autoreCognome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));

    	// generiId
    	generiId.add(2);
    	lS = getLoadedList(casaEditriceNome, autoreNome, sagaNome, autoreCognome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceId = null;
    	assertThat(lS.get(0).getNome().toLowerCase().contains("one piece"));
	}

public void findByIdTest() {
		// Id error
		Integer id = 99;
		String msg = "!exists_sag";
		log.debug("Start testSagaController.findByIdTest(), error expected");
		ResponseEntity<?> resp = sagaC.findById(id);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertEquals(msgS.get(msg), resp.getBody());

		// Normal workflow
		id = 1;
		log.debug("Start testSagaController.findByIdTest()");
		resp = sagaC.findById(id);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(resp.getBody()).isInstanceOf(SagaDTO.class);
	}
	
	    // ==========================================
	    // ASSERT HELPERS
	    // ==========================================
	private void assertCreateError(String token, String msg, SagaRequest req) throws Exception {
	      mockMvc.perform(post("/rest/saga/create").with(csrf())
	         .header("Authorization", token)
	             .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(req)))
	                .andExpect(status().isBadRequest())
	                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
	    }

	private void assertUpdateError(String token, String msg, SagaRequest req) throws Exception {
	        mockMvc.perform(put("/rest/saga/update").with(csrf())
	                .header("Authorization", token)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(req)))
	                .andExpect(status().isBadRequest())
	                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
	    }

	public void updateTest() throws Exception {
		SagaRequest r = buildSagaRequest();
		String msg = "!exists_sag";
		r.setId(99);
		String token = getBearerToken("AdminUser");
        assertUpdateError(token, msg, r);
		
		msg = "!exists_man";
		r = buildSagaRequest();
		r.setId(1);
		List<String> manga = new ArrayList<String>();
		manga.add("kutvg");
		r.setManga(manga);
        assertUpdateError(token, msg, r);

		msg = "rest_updated";
		r = buildSagaRequest();
		r.setId(1);
		r.setDescrizione("descrizione aggriornata");
		mockMvc.perform(put("/rest/saga/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
	}
	

	public void createTest() throws Exception {
		String msg = "null_req";
		String token = getBearerToken("AdminUser");
		SagaRequest r = null;
        //assertCreateError(token, msg, r);
		
		r = buildSagaRequest();
		r.setNome(null);
		msg = "null_snom";
        assertCreateError(token, msg, r);

		r = buildSagaRequest();
		r.setNome("One Piece");
		msg = "exists_sag";
        assertCreateError(token, msg, r);
		
		r = buildSagaRequest();
		r.setDescrizione(null);
		msg = "null_desc";
        assertCreateError(token, msg, r);
		
		r = buildSagaRequest();
		List<String> manga = new ArrayList<String>();
		manga.add("ABC");
		r.setManga(manga);
		msg = "!exists_man";
        assertCreateError(token, msg, r);
		
		r = buildSagaRequest();
		msg = "rest_created";
		manga = new ArrayList<String>();
		manga.add("ISBN001");
		mockMvc.perform(post("/rest/saga/create").with(csrf())
	         .header("Authorization", token)
	         .contentType(MediaType.APPLICATION_JSON)
	         .content(objectMapper.writeValueAsString(r)))
	         .andExpect(status().isOk())
	         .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		}

	public void deleteTest() throws Exception {
		String token = getBearerToken("AdminUser");
		String msg = "!exists_sag";

        mockMvc.perform(delete("/rest/saga/delete/99").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		
		// Errore: exists_sagman: manga collegati a saga
		msg = "exists_sagman";
        mockMvc.perform(delete("/rest/saga/delete/1").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

		
		// Normal workflow
		msg = "rest_deleted";
        mockMvc.perform(delete("/rest/saga/delete/3").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

	}
}
