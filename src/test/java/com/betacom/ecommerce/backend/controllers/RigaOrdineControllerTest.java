package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.betacom.ecommerce.backend.dto.inputs.RigaOrdineRequest;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IMailServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaOrdineServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RigaOrdineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMessagesServices msgS;

    @MockitoSpyBean
    private IRigaOrdineServices rowS;

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
    private RigaOrdineRequest buildRigaOrdineRequest() {
        return RigaOrdineRequest.builder()
                .idOrdine(2)
                .manga("ISBN002")
                .numeroCopie(1)
                .build();
    }

    // ==========================================
    // ASSERT HELPERS
    // ==========================================
    private void assertCreateError(String token, String msg, RigaOrdineRequest req) throws Exception {
        mockMvc.perform(post("/rest/riga_ordine/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }

    private void assertUpdateError(String token, String msg, RigaOrdineRequest req) throws Exception {
        mockMvc.perform(put("/rest/riga_ordine/update").with(csrf())
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
    public void testRigaOrdineControllerAdmin() throws Exception {
        create();
        update();
        listRigaOrdine();
        findById();
        deleteTest();
    }

    @Test
    public void testRigaOrdineControllerAuthFails() throws Exception {
        String token = getBearerToken("UserUser");
        RigaOrdineRequest req = buildRigaOrdineRequest();

        // create forbidden for non-admin
        mockMvc.perform(post("/rest/riga_ordine/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        // update forbidden for non-admin
        req.setId(1);
        mockMvc.perform(put("/rest/riga_ordine/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        // delete forbidden for non-admin
        mockMvc.perform(delete("/rest/riga_ordine/delete/1").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // CREATE
    // ==========================================
    public void create() throws Exception {
        log.debug("Begin create RigaOrdine Test");
        String token = getBearerToken("AdminUser");

        // Normal workflow
        RigaOrdineRequest req = buildRigaOrdineRequest();
        req.setId(null);
        mockMvc.perform(post("/rest/riga_ordine/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_created")));

        // null_ord
        req = buildRigaOrdineRequest();
        req.setIdOrdine(null);
        String msg = "null_ord";
        assertCreateError(token, msg, req);

        // !exists_ord
        req = buildRigaOrdineRequest();
        req.setIdOrdine(99);
        msg = "!exists_ord";
        assertCreateError(token, msg, req);

        // null_man
        req = buildRigaOrdineRequest();
        req.setManga(null);
        msg = "null_man";
        assertCreateError(token, msg, req);

        // !exists_man
        req = buildRigaOrdineRequest();
        req.setManga("LALALALAL");
        msg = "!exists_man";
        assertCreateError(token, msg, req);
    }

    // ==========================================
    // UPDATE
    // ==========================================
    public void update() throws Exception {
        log.debug("Begin update RigaOrdine Test");
        String token = getBearerToken("AdminUser");

        // Normal workflow
        RigaOrdineRequest req = buildRigaOrdineRequest();
        req.setId(3);
        mockMvc.perform(put("/rest/riga_ordine/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_updated")));
        
        req = buildRigaOrdineRequest();
        req.setId(2);
        String msg = "exists_ro";
        assertUpdateError(token, msg, req);

        // !exists_row
        msg = "!exists_ro";
        req = buildRigaOrdineRequest();
        req.setId(100);
        assertUpdateError(token, msg, req);

        // !exists_ord
        req = buildRigaOrdineRequest();
        req.setId(1);
        req.setIdOrdine(100);
        msg = "!exists_ord";
        assertUpdateError(token, msg, req);

        // !exists_man
        req = buildRigaOrdineRequest();
        req.setId(1);
        req.setManga("WEWEWEW");
        msg = "!exists_man";
        assertUpdateError(token, msg, req);
    }

    // ==========================================
    // LIST
    // ==========================================
    public void listRigaOrdine() throws Exception {
        log.debug("Begin list RigaOrdine Test");
        String token = getBearerToken("AdminUser");

        // no params
        mockMvc.perform(get("/rest/riga_ordine/list")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        // with idOrdine
        mockMvc.perform(get("/rest/riga_ordine/list")
                .param("idOrdine", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());
    }

    // ==========================================
    // FIND BY ID
    // ==========================================
    public void findById() throws Exception {
        log.debug("Begin findById RigaOrdine Test");
        String token = getBearerToken("AdminUser");

        // !exists_row
        mockMvc.perform(get("/rest/riga_ordine/findById")
                .param("id", "99")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());

        // Normal workflow
        mockMvc.perform(get("/rest/riga_ordine/findById")
                .param("id", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ==========================================
    // DELETE
    // ==========================================
    public void deleteTest() throws Exception {
        log.debug("Begin delete RigaOrdine Test");
        String token = getBearerToken("AdminUser");

        // !exists_row
        String msg = "!exists_row";
        mockMvc.perform(delete("/rest/riga_ordine/delete/99").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // Normal workflow
        msg = "rest_deleted";
        mockMvc.perform(delete("/rest/riga_ordine/delete/1").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }
}