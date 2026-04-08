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
import org.springframework.test.web.servlet.MockMvc;

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
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
public class OrdineControllerTest {

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
    private OrdineRequest buildOrdineRequest() {
        return OrdineRequest.builder()
                .id(1)
                .account(1)
                .pagamento("PAYPAL")
                .spedizione("STANDARD")
                .data(LocalDate.of(2026, 03, 20))
                .anagrafica(1)
                .build();
    }

    // ==========================================
    // ASSERT HELPERS
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

    // ==========================================
    // TESTS
    // ==========================================
    @Test
    public void testOrdineControllerAdmin() throws Exception {
        create();
        update();
        listOrdini();
        findById();
        deleteTest();
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
        req.setPagamento(null);
        msg = "null_pag";
        assertCreateError(token, msg, req);

        // !exists_pag
        req = buildOrdineRequest();
        req.setId(null);
        req.setPagamento("MIO");
        msg = "!exists_pag";
        assertCreateError(token, msg, req);

        // null_spe
        req = buildOrdineRequest();
        req.setId(null);
        req.setSpedizione(null);
        msg = "null_spe";
        assertCreateError(token, msg, req);

        // !exists_spe
        req = buildOrdineRequest();
        req.setId(null);
        req.setSpedizione("MIO");
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
        req.setPagamento("MIO");
        msg = "!exists_pag";
        assertUpdateError(token, msg, req);

        // !exists_spe
        req = buildOrdineRequest();
        req.setSpedizione("MIO");
        msg = "!exists_spe";
        assertUpdateError(token, msg, req);

        // !exists_sta
        req = buildOrdineRequest();
        req.setStato("MIO");
        msg = "!exists_sta";
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
                .param("id", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        // !exists_ord
        mockMvc.perform(get("/rest/ordine/findById")
                .param("id", "99")
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
}