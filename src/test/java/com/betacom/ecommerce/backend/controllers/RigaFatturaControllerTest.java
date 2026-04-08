package com.betacom.ecommerce.backend.controllers;
 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
import java.math.BigDecimal;
 
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
 
import com.betacom.ecommerce.backend.dto.inputs.RigaFatturaRequest;
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
public class RigaFatturaControllerTest {
 
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
    private RigaFatturaRequest buildRigaFatturaRequest() {
        return RigaFatturaRequest.builder()
                .idFattura(2)
                .isbn("ISBN002")
                .numeroCopie(2)
                .build();
    }
 
    // ==========================================
    // ASSERT HELPERS
    // ==========================================
    private void assertCreateError(String token, String msg, RigaFatturaRequest req) throws Exception {
        mockMvc.perform(post("/rest/riga_fattura/create").with(csrf())
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
    public void testRigaFatturaControllerAdmin() throws Exception {
        create();
        update();
        list();
        findById();
        findByIdError();
        deleteError();
        deleteTest();
    }
 
    @Test
    public void testRigaFatturaControllerAuthFails() throws Exception {
        String token = getBearerToken("UserUser");
        RigaFatturaRequest req = buildRigaFatturaRequest();
 
        // create forbidden
        mockMvc.perform(post("/rest/riga_fattura/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
 
        // update forbidden
        mockMvc.perform(put("/rest/riga_fattura/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
 
        // delete forbidden
        mockMvc.perform(delete("/rest/riga_fattura/delete/1").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isForbidden());
 
        // list forbidden
        mockMvc.perform(get("/rest/riga_fattura/list")
                .header("Authorization", token))
                .andExpect(status().isForbidden());
 
        // findById forbidden
        mockMvc.perform(get("/rest/riga_fattura/findById")
                .param("id", "1")
                .header("Authorization", token))
                .andExpect(status().isForbidden());
    }
 
    // ==========================================
    // CREATE
    // ==========================================
    public void create() throws Exception {
        log.debug("Begin create RigaFattura Test");
        String token = getBearerToken("AdminUser");
 
        // Normal workflow
        RigaFatturaRequest req = buildRigaFatturaRequest();
        mockMvc.perform(post("/rest/riga_fattura/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_created")));
 
        // null_isn
        req = buildRigaFatturaRequest();
        req.setIsbn(null);
        String msg = "null_isn";
        assertCreateError(token, msg, req);
    
        // null_qua
        req = buildRigaFatturaRequest();
        req.setIsbn("ISBN001");
        req.setNumeroCopie(null);
        msg = "null_qua";
        assertCreateError(token, msg, req);
 
        // null_fat
        req = buildRigaFatturaRequest();
        req.setIsbn("ISBN001");
        req.setIdFattura(null);
        msg = "null_fat";
        assertCreateError(token, msg, req);
    }
 
    // ==========================================
    // UPDATE
    // ==========================================
    public void update() throws Exception {
        log.debug("Begin update RigaFattura Test");
        String token = getBearerToken("AdminUser");
 
        RigaFatturaRequest req = RigaFatturaRequest.builder()
                .id(1)
                .idFattura(1)
                .isbn("ISBN002")
                .prezzoUnitario(new BigDecimal("14.50"))
                .numeroCopie(3)
                .build();
 
        mockMvc.perform(put("/rest/riga_fattura/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_updated")));
    }
 
    // ==========================================
    // LIST
    // ==========================================
    public void list() throws Exception {
        log.debug("Begin list RigaFattura Test");
        String token = getBearerToken("AdminUser");
 
        mockMvc.perform(get("/rest/riga_fattura/list")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());
    }
 
    // ==========================================
    // FIND BY ID
    // ==========================================
    public void findById() throws Exception {
        log.debug("Begin findById RigaFattura Test");
        String token = getBearerToken("AdminUser");
 
        mockMvc.perform(get("/rest/riga_fattura/findById")
                .param("id", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
 
    public void findByIdError() throws Exception {
        log.debug("Begin findById RigaFattura Test - error expected");
        String token = getBearerToken("AdminUser");
 
        mockMvc.perform(get("/rest/riga_fattura/findById")
                .param("id", "999")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }
 
    // ==========================================
    // DELETE
    // ==========================================
    public void deleteError() throws Exception {
        log.debug("Begin delete RigaFattura Test - error expected");
        String token = getBearerToken("AdminUser");
 
        String msg = "!exists_rig_fat";
        mockMvc.perform(delete("/rest/riga_fattura/delete/999").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }
 
    public void deleteTest() throws Exception {
        log.debug("Begin delete RigaFattura Test");
        String token = getBearerToken("AdminUser");
 
        String msg = "rest_deleted";
        mockMvc.perform(delete("/rest/riga_fattura/delete/1").with(csrf())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }
}
