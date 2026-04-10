package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.betacom.ecommerce.backend.dto.inputs.GenereRequest;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IMailServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GenereControllerTest {
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMessagesServices msgS;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoSpyBean
    private IMailServices mailSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBearerToken(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(user.getUsername());
        return "Bearer " + token;
    }

	@Autowired
	private GenereController genC;
	
	@Test
	public void testGenereControllerAdmin() throws Exception {
		create();
		update();
		delete();
	}

	@Test
	public void testGenereControllerAny() {
		list();
		listById();
	}

	public void list() {
		log.debug("start list generi test");
		ResponseEntity<?> resp = genC.list();
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertThat(((List<?>) b).size()).isGreaterThan(0);
		Assertions.assertThat(((List<?>) b).getFirst()).isInstanceOf(GenereDTO.class);

	}
	
	public void listById() {
		log.debug("start list generi by id test");
		
		ResponseEntity<?> resp = genC.findById(1);
		Object b =  resp.getBody();
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assertions.assertThat(b).isInstanceOf(GenereDTO.class);
		GenereDTO g = (GenereDTO) b;
		assertEquals(g.getDescrizione(), "AZIONE");
		
		//error
		resp = genC.findById(99);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
	}
	
	public void create() throws Exception {
		String token = getBearerToken("AdminUser");
		GenereRequest req = new GenereRequest();
		req.setDescrizione(" comico");
		String msg = "rest_created";
		mockMvc.perform(post("/rest/genere/create").with(csrf())
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

		
		//errore duplicazione
		msg = "exists_gen";
		mockMvc.perform(post("/rest/genere/create").with(csrf())
		        .header("Authorization", token)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(req)))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
	}
	
	public void update() throws Exception {
		String token = getBearerToken("AdminUser");
		GenereRequest req = new GenereRequest();
		req.setId(5);
		req.setDescrizione(" sport");
		String msg = "rest_updated";
		mockMvc.perform(put("/rest/genere/update").with(csrf())
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

	    // update error id inesistente
	    req.setId(99);
	    msg = "!exists_gen";
		mockMvc.perform(put("/rest/genere/update").with(csrf())
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

	    // update error duplicato
	    req.setDescrizione(" azione");
	    req.setId(2);
	    msg = "exists_gen";
		mockMvc.perform(put("/rest/genere/update").with(csrf())
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		}
	
	public void delete() throws Exception {
		String token = getBearerToken("AdminUser");
		String msg = "rest_deleted";
		//delete a buon fine
		mockMvc.perform(MockMvcRequestBuilders.delete("/rest/genere/delete/6").with(csrf())
		        .header("Authorization", token))
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		
		//delete andato male id sbagliato
		msg = "!exists_gen";
		mockMvc.perform(MockMvcRequestBuilders.delete("/rest/genere/delete/99").with(csrf())
		        .header("Authorization", token))
        		.andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		
		//delete andato male ci sono manga collegati
		msg = "linked_man";
		mockMvc.perform(MockMvcRequestBuilders.delete("/rest/genere/delete/1").with(csrf())
		        .header("Authorization", token))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
		
	}
}
