package com.betacom.ecommerce.backend.controllers;
 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc // per autoconfiguraz test con richiesta web simulata
@ActiveProfiles("test") // per avere info da test/resources/application.properties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FatturaControllerTest {
	
    @Autowired // <-- @Autowired invece di final
    private MockMvc mockMvc;
    
    @Autowired
    private IMessagesServices msgS;

    // @MockitoSpyBean sostituisce classe reale con uno spy no-op
    @MockitoSpyBean
    private IFatturaServices fatS;
	 
	// I servizi  che ci servono per creare il Token
	@Autowired
	private JwtService jwtService; 

	@Autowired
	private UserDetailsService userDetailsService;

	private final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	
	// METODO  PER IL TOKEN
	private String getBearerToken(String username) {
		UserDetails user = userDetailsService.loadUserByUsername(username);
		String token = jwtService.generateToken(user.getUsername()); 
		return "Bearer " + token;
	}

	
	// ==========================================
	// TEST ENDPOINT CONDIVISI (Admin + Owner)
	// ==========================================
    @Test
    public void testFatturaControllerOwner()  throws Exception{
    	findById();
    	iniziaReso();  
    }

	public void findById() throws Exception {
        log.debug("Begin findById Fattura Test");
        String token = getBearerToken("MarioRossi");
		
        mockMvc.perform(get("/rest/fattura/findById")
        		.param("idFattura", "1")
        		.param("idAccount", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/rest/fattura/findById")
        		.param("idFattura", (String) null)
        		.param("idAccount", "1")
				.header("Authorization", token))
				.andExpect(status().isBadRequest());
        
        token = getBearerToken("UserUser");
        mockMvc.perform(get("/rest/fattura/findById")
        		.param("idFattura", "1")
        		.param("idAccount", "1")
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	}
	
	public void iniziaReso() throws Exception {
        log.debug("Begin iniziaReso Fattura Test");
        
        String token = getBearerToken("UserUser");
        mockMvc.perform(post("/rest/fattura/reso/inizia")
        		.header("Authorization", token)
        		.param("fatturaId", "1")
        		.param("accountId", "1")
        		)
				.andExpect(status().isForbidden());
        
        token = getBearerToken("MarioRossi");
        mockMvc.perform(post("/rest/fattura/reso/inizia")
				.header("Authorization", token)
        		.param("fatturaId", "1")
        		.param("accountId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value(msgS.get("reso_start")));
	}

	 //
	
	@Test
    public void testFatturaControllerAdmin() throws Exception {
        create();
        update();
        list();
        deleteTest();
    	authFails();
    }

    private FatturaRequest buildFatturaRequest() {
        return FatturaRequest.builder()
                .clienteNome("Mario")
                .clienteCognome("Rossi")
                .clienteEmail("mario.rossi@email.it")
                .clienteIndirizzo("Via Roma 1")
                .clienteCitta("Milano")
                .clienteCap("20100")
                .clienteProvincia("Milano")
                .clienteStato("Italia")
                .tipoPagamento("Carta di Credito")
                .tipoSpedizione("Standard")
                .ordineId(1)
                .righeFatturaRequest(List.of())
                .build();
    }

    private void assertCreateError(String msg, FatturaRequest req, String token) throws Exception {
	    mockMvc.perform(post("/rest/fattura/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value(msgS.get(msg)));

    }
    
    public void authFails() throws Exception {
	    String token = getBearerToken("UserUser");
	    FatturaRequest req = buildFatturaRequest();
        req.setId(1);
	    mockMvc.perform(post("/rest/fattura/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	    
	    token = getBearerToken("AdminUser");

    }
    
	public void create() throws Exception {
		log.debug("Begin create Fattura Test");
	    FatturaRequest req = buildFatturaRequest();	    
	    String token = getBearerToken("AdminUser");
	    mockMvc.perform(post("/rest/fattura/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value(msgS.get("rest_created")));
	    
	    // null nom
	    String msg = "null_nom";
	    req = buildFatturaRequest();
	    req.setClienteNome(null);
	    assertCreateError(msg, req, token);
	    
	 // null_cog
     	msg = "null_cog";
     	req = buildFatturaRequest();
     	req.setClienteCognome(null);
     	assertCreateError(msg, req, token);

     	// null_ema
     	msg = "null_ema";
     	req = buildFatturaRequest();
     	req.setClienteEmail(null);
     	assertCreateError(msg, req, token);
     	// null_ind
     	msg = "null_ind";
     	req = buildFatturaRequest();
     	req.setClienteIndirizzo(null);
     	assertCreateError(msg, req, token);
     	// null_cit
     	msg = "null_cit";
     	req = buildFatturaRequest();
     	req.setClienteCitta(null);
     	assertCreateError(msg, req, token);
     	// null_cap
     	msg = "null_cap";
     	req = buildFatturaRequest();
     	req.setClienteCap(null);
     	assertCreateError(msg, req, token);
     	// null_pro
    	msg = "null_pro";
     	req = buildFatturaRequest();
     	req.setClienteProvincia(null);
     	assertCreateError(msg, req, token);
     	// null_sta
    	msg = "null_sta";
     	req = buildFatturaRequest();
     	req.setClienteStato(null);
    	assertCreateError(msg, req, token);

     	// null_pag
     	msg = "null_pag";
     	req = buildFatturaRequest();
     	req.setTipoPagamento(null);
     	assertCreateError(msg, req, token);

     	// null_spe
     	msg = "null_spe";
     	req = buildFatturaRequest();
     	req.setTipoSpedizione(null);
     	assertCreateError(msg, req, token);
 	}

	
    private void assertUpdateError(String msg, FatturaRequest req, String token) throws Exception {
	    mockMvc.perform(put("/rest/fattura/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value(msgS.get(msg)));

    }

	public void update() throws Exception {
	    String token = getBearerToken("UserUser");
	    FatturaRequest req = buildFatturaRequest();
	    mockMvc.perform(put("/rest/fattura/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	    
	    token = getBearerToken("AdminUser");
	    String msg = "!exists_fat";
	    req.setId(99);
	    assertUpdateError(msg, req, token);
	    
	    msg = "!exists_pag";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setTipoPagamento("AA");
	    assertUpdateError(msg, req, token);
	    
	    msg = "!exists_spe";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setTipoSpedizione("AA");
	    assertUpdateError(msg, req, token);
	    
	    msg = "!exists_ord";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setOrdineId(99);
	    assertUpdateError(msg, req, token);
	    
	    msg = "rest_updated";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setClienteNome("Otto");
	    mockMvc.perform(put("/rest/fattura/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value(msgS.get(msg)));
	}
	
    public void deleteTest() throws Exception {
        log.debug("Begin delete Fattura Test");
        
	    String token = getBearerToken("UserUser");
        
	    mockMvc.perform(delete("/rest/fattura/delete/99").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	    
	    token = getBearerToken("AdminUser");
	    mockMvc.perform(delete("/rest/fattura/delete/999").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value(msgS.get("!exists_fat")));

	    mockMvc.perform(delete("/rest/fattura/delete/2").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value(msgS.get("rest_deleted")));

    }
    
        
    public void list() throws Exception{
    	String token = getBearerToken("UserUser");
	    mockMvc.perform(get("/rest/fattura/list").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	    
	    token = getBearerToken("AdminUser");
	    
		String numeroFattura = null;
		LocalDate from = null;
		LocalDate to = null;
		String clienteNome = null;
		String clienteCognome = null;
		String clienteEmail = null;
		String tipoPagamento = null;
		String tipoSpedizione = null;
		String statoFattura = null;
		Integer idOrdine = null;
		List<String> isbns = new ArrayList<String>();

		mockMvc.perform(get("/rest/fattura/list").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
    	
    }
    

}
