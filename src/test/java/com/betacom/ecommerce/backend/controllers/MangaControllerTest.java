package com.betacom.ecommerce.backend.controllers;
 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
import java.math.BigDecimal;
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
 
import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IMailServices;
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
public class MangaControllerTest {
 
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
    private MangaRequest buildMangaRequestValid() {
        MangaRequest req = new MangaRequest();
        req.setIsbn("ISBN004");
        req.setTitolo("TEST");
        req.setDataPubblicazione(LocalDate.of(2020, 01, 01));
        req.setPrezzo(new BigDecimal("5.00"));
        req.setImmagine("IMG.JPG");
        req.setNumeroCopie(5);
        req.setCasaEditrice(1);
        req.setGeneri(List.of(1));
        req.setAutori(List.of(1));
        return req;
    }
 
    // ==========================================
    // ASSERT HELPERS
    // ==========================================
    private void assertCreateError(String token, String msg, MangaRequest req) throws Exception {
        mockMvc.perform(post("/rest/manga/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }
 
    private void assertUpdateError(String token, String msg, MangaRequest req) throws Exception {
        mockMvc.perform(put("/rest/manga/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }
 
    // ==========================================
    // TEST ADMIN
    // ==========================================
    @Test
    public void testMangaControllerAdmin() throws Exception {
        list();
        findByIsbn();
        create();
        createFail();
        update();
        updateFail();
        deleteTest();
    }
 
    // ==========================================
    // TEST AUTH FAILS
    // ==========================================
    @Test
    public void testMangaControllerAuthFails() throws Exception {
        String token = getBearerToken("UserUser");
        MangaRequest req = buildMangaRequestValid();
 
        // create forbidden for non-admin
        mockMvc.perform(post("/rest/manga/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
 
        // update forbidden for non-admin
        mockMvc.perform(put("/rest/manga/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
 
        // delete forbidden for non-admin
        mockMvc.perform(delete("/rest/manga/delete").with(csrf())
                .param("id", "ISBN001")
                .header("Authorization", token))
                .andExpect(status().isForbidden());
    }
 
    // ==========================================
    // LIST (public)
    // ==========================================
    public void list() throws Exception {
        log.debug("Begin list Manga Test");
        String token = getBearerToken("AdminUser");
 
        // no filters
        mockMvc.perform(get("/rest/manga/list")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // titolo filter
        mockMvc.perform(get("/rest/manga/list")
                .param("titolo", "one")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // casaEditriceNome filter
        mockMvc.perform(get("/rest/manga/list")
                .param("casaEditriceNome", "Shueis")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // autoreNome filter
        mockMvc.perform(get("/rest/manga/list")
                .param("autoreNome", "iich")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // sagaNome filter
        mockMvc.perform(get("/rest/manga/list")
                .param("sagaNome", "piec")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // sagaId filter
        mockMvc.perform(get("/rest/manga/list")
                .param("sagaId", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // casaEditriceId filter
        mockMvc.perform(get("/rest/manga/list")
                .param("casaEditriceId", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // autoreId filter
        mockMvc.perform(get("/rest/manga/list")
                .param("autoreId", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
 
        // generiId filter
        mockMvc.perform(get("/rest/manga/list")
                .param("generiId", "2")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
 
    // ==========================================
    // FIND BY ISBN
    // ==========================================
    public void findByIsbn() throws Exception {
        log.debug("Begin findByIsbn Manga Test");
        String token = getBearerToken("AdminUser");
 
        mockMvc.perform(get("/rest/manga/findByIsbn")
                .param("id", "ISBN001")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("ISBN001"))
                .andExpect(jsonPath("$.titolo").value("One Piece Vol.1"))
                .andExpect(jsonPath("$.prezzo").value(9.99))
                // here will get default img: http://localhost/uploads/default.png
                // cause controls on image will return null, in dto builders -> default
                //.andExpect(jsonPath("$.immagine").value("http://localhost/uploads/default.jpg"))
                .andExpect(jsonPath("$.numeroCopie").value(100))
                .andExpect(jsonPath("$.casaEditrice").isNotEmpty())
                .andExpect(jsonPath("$.autori").isNotEmpty())
                .andExpect(jsonPath("$.generi").isNotEmpty());
 
        // isbn inesistente
        mockMvc.perform(get("/rest/manga/findByIsbn")
                .param("id", "INESISTENTE")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }
 
    // ==========================================
    // CREATE
    // ==========================================
    public void create() throws Exception {
        log.debug("Begin create Manga Test");
        String token = getBearerToken("AdminUser");
 
        MangaRequest req = new MangaRequest();
        req.setIsbn(" isbn003 ");
        req.setTitolo(" bleach ");
        req.setDataPubblicazione(LocalDate.of(2001, 8, 07));
        req.setPrezzo(new BigDecimal("8.50"));
        req.setImmagine(" bleach.jpg ");
        req.setNumeroCopie(30);
        req.setCasaEditrice(1);
        req.setGeneri(List.of(1));
        req.setAutori(List.of(2));
 
        mockMvc.perform(post("/rest/manga/create").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_created")));
 
        // verify created
        mockMvc.perform(get("/rest/manga/findByIsbn")
                .param("id", "ISBN003")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("ISBN003"));
    }
 
    // ==========================================
    // CREATE FAIL
    // ==========================================
    public void createFail() throws Exception {
        log.debug("Begin createFail Manga Test");
        String token = getBearerToken("AdminUser");
 
        // isbn duplicato
        MangaRequest req = buildMangaRequestValid();
        req.setIsbn("ISBN001");
        String msg = "exists_man";
        assertCreateError(token, msg, req);
 
        // autore inesistente
        req = buildMangaRequestValid();
        req.setAutori(List.of(99));
        msg = "!exists_aut";
        assertCreateError(token, msg, req);
 
        // genere inesistente
        req = buildMangaRequestValid();
        req.setGeneri(List.of(99));
        msg = "!exists_gen";
        assertCreateError(token, msg, req);
 
        // casa editrice inesistente
        req = buildMangaRequestValid();
        req.setCasaEditrice(99);
        msg = "!exists_ced";
        assertCreateError(token, msg, req);
 
        // troppi autori
        req = buildMangaRequestValid();
        req.setAutori(List.of(1, 2, 3, 4));
        msg = "!exists_aut";
        assertCreateError(token, msg, req);
 
        // autori con uno non esistente
        req = buildMangaRequestValid();
        req.setAutori(List.of(1, 99));
        msg = "!exists_aut";
        assertCreateError(token, msg, req);
 
        // generi con uno non esistente
        req = buildMangaRequestValid();
        req.setGeneri(List.of(1, 99));
        msg = "!exists_gen";
        assertCreateError(token, msg, req);
    }
 
    // ==========================================
    // UPDATE
    // ==========================================
    public void update() throws Exception {
        log.debug("Begin update Manga Test");
        String token = getBearerToken("AdminUser");
 
        MangaRequest req = new MangaRequest();
        req.setIsbn("ISBN001");
        req.setTitolo(" NARUTO SHIPPUDEN ");
        req.setDataPubblicazione(LocalDate.of(2001, 01, 01));
        req.setPrezzo(new BigDecimal("7.90"));
        req.setNumeroCopie(120);
 
        mockMvc.perform(put("/rest/manga/update").with(csrf())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("rest_updated")));
    }
 
    // ==========================================
    // UPDATE FAIL
    // ==========================================
    public void updateFail() throws Exception {
        log.debug("Begin updateFail Manga Test");
        String token = getBearerToken("AdminUser");
 
        // isbn inesistente
        MangaRequest req = new MangaRequest();
        req.setIsbn("NONESISTE");
        req.setTitolo("TEST");
        String msg = "!exists_man";
        assertUpdateError(token, msg, req);
    }
 
    // ==========================================
    // DELETE
    // ==========================================
    public void deleteTest() throws Exception {
        log.debug("Begin delete Manga Test");
        String token = getBearerToken("AdminUser");
 
        // isbn inesistente
        String msg = "!exists_man";
        String isbn = "ABC!123";
        mockMvc.perform(delete("/rest/manga/delete").with(csrf())
                .param("id", isbn)
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
 
        // linked_ord
        msg = "linked_ord";
        isbn = "ISBN001";
        mockMvc.perform(delete("/rest/manga/delete").with(csrf())
                .param("id", isbn)
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
  
        // normal workflow - delete last manga
        msg = "rest_deleted";
        isbn = "ISBN003";
        mockMvc.perform(delete("/rest/manga/delete").with(csrf())
                .param("id", isbn)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
    }
}
 
