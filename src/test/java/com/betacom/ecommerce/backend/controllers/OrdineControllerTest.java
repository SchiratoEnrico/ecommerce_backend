package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IMailServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OrdineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMessagesServices msgS;
    
    @Autowired
    private IStatoOrdineRepository statR;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoSpyBean
    private IMailServices mailSender;

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
    private OrdineRequest buildOrdineRequest() {
    	/*
    	 * NW in data.sql:
    	 * tipoPagamentoId: 1 - PAYPAL, 2 - CARTA DI CREDITO, 3 - BONIFICO
    	 * tipi_spedizioneId: 1 - standard, 2 - express, 3 - gratuita 
    	 */

        return OrdineRequest.builder()
                .id(1)
                .account(1)
                .pagamentoId(1)
                .spedizioneId(1)
                .data(LocalDate.of(2026, 03, 20))
                .anagrafica(1)
                .build();
    }

    // ==========================================
    // ASSERT AND HELPERS
    // ==========================================
    private void assertCreateError(String token, String msg, OrdineRequest req) throws Exception {
        mockMvc.perform(post("/rest/ordine/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }

    private void assertUpdateError(String token, String msg, OrdineRequest req) throws Exception {
        mockMvc.perform(put("/rest/ordine/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }

	private static final java.util.Map<String, List<String>> ALLOWED_ORDINE = java.util.Map.of(
			"CREATO",      List.of("PAGATO", "CANCELLATO"),
			"PAGATO",      List.of("LAVORAZIONE", "CANCELLATO"),
			"LAVORAZIONE", List.of("SPEDITO"),
			"SPEDITO",     List.of("CONSEGNATO"),
			"CONSEGNATO",  List.of("RICHIESTA_RESO")
		);

    /// DTO builder per check get_next_allowed_states
    private List<StatoOrdineDTO> getNextStates(String statoOrdine) {
    	List<String> allowed = ALLOWED_ORDINE.getOrDefault(statoOrdine, List.of());
    	List<StatoOrdineDTO> lS = allowed.stream()
                .map(s -> statR.findByStatoOrdine(s))
                .filter(s -> s.isPresent())
    			.map(s -> DtoBuilders.buildStatoOrdineDTO(s.get()))
    			.toList();
    	log.debug("Stato ordine input: {}", statoOrdine);
    	for (StatoOrdineDTO s: lS) {
        	log.debug("\tstato concesso: {}", s);
    	}
    	return lS;
    }
    
    private void assertNextStates(String token, String ordineId, String currentState) throws Exception {
        List<StatoOrdineDTO> expected = getNextStates(currentState);

        var result = mockMvc.perform(get("/rest/ordine/get_next_allowed_states")
                .param("idOrdine", ordineId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expected.size()));

        for (int i = 0; i < expected.size(); i++) {
            result.andExpect(jsonPath("$[" + i + "].id").value(expected.get(i).getId()))
                  .andExpect(jsonPath("$[" + i + "].statoOrdine").value(expected.get(i).getStatoOrdine()));
        }
    }

    // ==========================================
    // TESTS
    // ==========================================
    @Test
    public void testOrdineControllerAdmin() throws Exception {
        create();
        update();
        listOrdini();
        findById();
        advanceStato();
        deleteTest();
        buildFromCarrello();
        //getNextAllowedStates testato in advanceStato()
    }

    public void buildFromCarrello() throws Exception {
    	Integer carrelloId = 1;
    	Integer anagraficaId = 1;
    	Integer tipoPagamentoId = 1;
    	Integer tipoSpedizioneId = 1;
        log.debug("Begin buildFromCarrello Test");
        String ownertoken = getBearerToken("MarioRossi");

        //normal workflow
        mockMvc.perform(post("/rest/ordine/create_ordine_from_carrello").with(csrf())
                .header("Authorization", ownertoken)
        		.param("carrelloId", carrelloId.toString())
        		.param("anagraficaId", anagraficaId.toString())
        		.param("tipoPagamentoId", tipoPagamentoId.toString())
        		.param("tipoSpedizioneId", tipoSpedizioneId.toString())
        		)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.msg").value(msgS.get("rest_created")));
    
        //not owner
        String token = getBearerToken("UserUser");
        String msg = "!owned_carr";
        mockMvc.perform(post("/rest/ordine/create_ordine_from_carrello").with(csrf())
                .header("Authorization", token)
        		.param("carrelloId", carrelloId.toString())
        		.param("anagraficaId", anagraficaId.toString())
        		.param("tipoPagamentoId", tipoPagamentoId.toString())
        		.param("tipoSpedizioneId", tipoSpedizioneId.toString())
        		)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        msg = "!exists_carr";
        carrelloId = 99;
        mockMvc.perform(post("/rest/ordine/create_ordine_from_carrello").with(csrf())
                .header("Authorization", ownertoken)
        		.param("carrelloId", carrelloId.toString())
        		.param("anagraficaId", anagraficaId.toString())
        		.param("tipoPagamentoId", tipoPagamentoId.toString())
        		.param("tipoSpedizioneId", tipoSpedizioneId.toString())
        		)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }


    // ==========================================
    // CREATE
    // ==========================================
    public void create() throws Exception {
        log.debug("Begin create Ordine Test");
        String token = getBearerToken("AdminUser");

        // Normal workflow
        OrdineRequest req = buildOrdineRequest();
        req.setId(null);
        mockMvc.perform(post("/rest/ordine/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_created")));

        // null_acc
        req = buildOrdineRequest();
        req.setId(null);
        req.setAccount(null);
        String msg = "null_acc";
        assertCreateError(token, msg, req);

        // !exists_acc
        req = buildOrdineRequest();
        req.setId(null);
        req.setAccount(99);
        msg = "!exists_acc";
        assertCreateError(token, msg, req);

        // null_pag
        req = buildOrdineRequest();
        req.setId(null);
        req.setPagamentoId(null);
        msg = "null_pag";
        assertCreateError(token, msg, req);

        // !exists_pag
        req = buildOrdineRequest();
        req.setId(null);
        req.setPagamentoId(99);
        msg = "!exists_pag";
        assertCreateError(token, msg, req);

        // null_spe
        req = buildOrdineRequest();
        req.setId(null);
        req.setSpedizioneId(null);
        msg = "null_spe";
        assertCreateError(token, msg, req);

        // !exists_spe
        req = buildOrdineRequest();
        req.setId(null);
        req.setSpedizioneId(99);
        msg = "!exists_spe";
        assertCreateError(token, msg, req);

        // null_dat
        req = buildOrdineRequest();
        req.setId(null);
        req.setData(null);
        msg = "null_dat";
        assertCreateError(token, msg, req);

    }

    // ==========================================
    // UPDATE (ADMIN only)
    // ==========================================
    public void update() throws Exception {
        log.debug("Begin update Ordine Test");
        String token = getBearerToken("AdminUser");

        // Normal workflow
        OrdineRequest req = buildOrdineRequest();
        mockMvc.perform(put("/rest/ordine/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_updated")));

        // forbidden for non-admin
        String userToken = getBearerToken("UserUser");
        mockMvc.perform(put("/rest/ordine/update").with(csrf())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        // null_ord
        req = buildOrdineRequest();
        req.setId(null);
        String msg = "null_ord";
        assertUpdateError(token, msg, req);

        // !exists_ord
        req = buildOrdineRequest();
        req.setId(99);
        msg = "!exists_ord";
        assertUpdateError(token, msg, req);

        // !exists_acc
        req = buildOrdineRequest();
        req.setAccount(99);
        msg = "!exists_acc";
        assertUpdateError(token, msg, req);

        // !exists_pag
        req = buildOrdineRequest();
        req.setPagamentoId(99);
        msg = "!exists_pag";
        assertUpdateError(token, msg, req);

        // !exists_spe
        req = buildOrdineRequest();
        req.setSpedizioneId(99);
        msg = "!exists_spe";
        assertUpdateError(token, msg, req);

    }

    // ==========================================
    // LIST
    // ==========================================
    public void listOrdini() throws Exception {
        log.debug("Begin list Ordine Test");
        String token = getBearerToken("AdminUser");

        // no filters
        mockMvc.perform(get("/rest/ordine/list")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // username filter
        mockMvc.perform(get("/rest/ordine/list")
                .param("username", "Mario")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // tipoPagamento filter
        mockMvc.perform(get("/rest/ordine/list")
                .param("tipoPagamento", "PayPal")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // tipoSpedizione filter
        mockMvc.perform(get("/rest/ordine/list")
                .param("tipoSpedizione", "Corriere")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // statoOrdine filter
        mockMvc.perform(get("/rest/ordine/list")
                .param("statoOrdine", "Spedito")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // isbns filter
        mockMvc.perform(get("/rest/ordine/list")
                .param("isbns", "978-1234567890")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // date filter
        mockMvc.perform(get("/rest/ordine/list")
                .param("anno", "2024")
                .param("mese", "5")
                .param("giorno", "15")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ==========================================
    // FIND BY ID
    // ==========================================
    public void findById() throws Exception {
        log.debug("Begin findById Ordine Test");
        String token = getBearerToken("AdminUser");

        // Normal workflow
        mockMvc.perform(get("/rest/ordine/findById")
                .param("idOrdine", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        // !exists_ord
        mockMvc.perform(get("/rest/ordine/findById")
                .param("idOrdine", "99")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // DELETE (ADMIN only)
    // ==========================================
    public void deleteTest() throws Exception {
        log.debug("Begin delete Ordine Test");
        String token = getBearerToken("AdminUser");

        // forbidden for non-admin
        String userToken = getBearerToken("UserUser");
        mockMvc.perform(delete("/rest/ordine/delete/99").with(csrf())
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());

        // !exists_ord
        String msg = "!exists_ord";
        mockMvc.perform(delete("/rest/ordine/delete/99").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // null_ord
        msg = "null_ord";
        mockMvc.perform(delete("/rest/ordine/delete/").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isNotFound());

        // Normal workflow
        msg = "rest_deleted";
        mockMvc.perform(delete("/rest/ordine/delete/2").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }
        
    public void advanceStato() throws Exception {
        log.debug("Begin advanceStato Ordine Test");

        /*
         * Stati:
         * 1 - CREATO
         * 2 - PAGATO
         * 3 - LAVORAZIONE
         * 4 - SPEDITO
         * 5 - CONSEGNATO
         * 6 - CANCELLATO
         * 7 - RICHIESTA_RESO
         * 
         * Ordini: 1: CONSEGNATO, account MarioRossi
         * 		   2: LAVORAZIONE, account MarioRossi
         * 		   3: CREATO, account MarioRossi
         * 
         */
        String token = getBearerToken("AdminUser");
        String userToken = getBearerToken("UserUser");
        // forbidden: non admin nè owner
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "1")
                .param("statoId", "2")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());

        // null_ord
        String msg = "null_ord";
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("statoId", "2")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());

        // null_sta
        msg = "null_sta";
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "1")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());

        // !exists_ord
        msg = "!exists_ord";
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "99")
                .param("statoId", "2")
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // !exists_sta
        msg = "!exists_sta";
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "1")
                .param("statoId", "99")
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // invalida: CONSEGNATO → SPEDITO (skip)
        msg = "ord_transition_invalid";
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "1")
                .param("statoId", "4") // SPEDITO
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        String ownerToken = getBearerToken("MarioRossi");
        String curState = "CONSEGNATO";
        String ordineId = ((Integer) 1).toString();
        assertNextStates(ownerToken, ordineId, curState);

        // valida: CREATO → PAGATO da parte di owner, id 3
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "2") // PAGATO
                .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("ord_adv")));

        curState = "PAGATO";
        ordineId = ((Integer) 3).toString();
        assertNextStates(ownerToken, ordineId, curState);

        // passo a LAVORAZIONE
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "3") // LAVORAZIONE
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("ord_adv")));

        curState = "LAVORAZIONE";
        assertNextStates(token, ordineId, curState);


        // invalida: LAVORAZIONE → CANCELLATO
        msg = "ord_transition_invalid";
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "6") // CANCELLATO
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // continuo pipeline: LAVORAZIONE → SPEDITO → CONSEGNATO
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "4") // SPEDITO
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("ord_adv")));

        curState = "SPEDITO";
        assertNextStates(token, ordineId, curState);

        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "5") // CONSEGNATO
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("ord_adv")));

        curState = "CONSEGNATO";
        assertNextStates(token, ordineId, curState);

        // test CANCELLATO su  ordine 2 (ancora su CREATO)
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "2")
                .param("statoId", "6") // CANCELLATO
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("ord_adv")));

        curState = "CANCELLATO";
        ordineId = ((Integer) 2).toString();
        assertNextStates(token, ordineId, curState);

        // already cancelled → ord_canc
        msg = "ord_canc";
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "2")
                .param("statoId", "2")
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }

}