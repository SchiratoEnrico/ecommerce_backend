package com.betacom.ecommerce.backend.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ImageControllerTest {
 
    @Autowired
    private MockMvc mockMvc;
 
    @Autowired
    private IMessagesServices msgS;
 
    @Autowired
    private JwtService jwtService;
 
    @Autowired
    private UserDetailsService userDetailsService;
 
    private String getBearerToken(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(user.getUsername());
        return "Bearer " + token;
    }
 
    // helper: fake 1x1 PNG byte array
    private byte[] fakePngBytes() {
        return new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG header
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };
    }
 
    private MockMultipartFile buildImageFile(String paramName, String filename) {
        return new MockMultipartFile(
                paramName, // filename
                filename, //originalFilename the original filename (as on the client's machine)
                "image/png",//contentType the content type (if known)
                fakePngBytes()//content the content of the file
        );
    }
 
    // ==========================================
    // TESTS
    // ==========================================
    @Test
    public void testImageControllerAdmin() throws Exception {
        uploadDefaultImage();
        uploadImage();
        getUrl();
        deleteImage();
    }
 
    @Test
    public void testImageControllerAuthFails() throws Exception {
        authFails();
    }
 
    // ==========================================
    // AUTH FAILS
    // ==========================================
    public void authFails() throws Exception {
        log.debug("Begin authFails Image Test");
        String token = getBearerToken("UserUser");
 
        MockMultipartFile file = buildImageFile("file", "test.png");
 
        // upload forbidden
        mockMvc.perform(multipart("/rest/immagini/upload").file(file).with(csrf())
                .header("Authorization", token)
                .param("isbn", "ISBN001"))
                .andExpect(status().isForbidden());
 
        // upload default forbidden
        mockMvc.perform(multipart("/rest/immagini/upload_default_img").file(file).with(csrf())
                .header("Authorization", token))
                .andExpect(status().isForbidden());
 
        // delete forbidden
        mockMvc.perform(delete("/rest/immagini/delete_image/test.png").with(csrf())
                .header("Authorization", token)
                .param("filename", "test.png"))
                .andExpect(status().isForbidden());
    }
 
    // ==========================================
    // UPLOAD DEFAULT IMAGE
    // ==========================================
    public void uploadDefaultImage() throws Exception {
        log.debug("Begin uploadDefaultImage Test");
        String token = getBearerToken("AdminUser");
 
        MockMultipartFile file = buildImageFile("file", "default.png");
 
        // Normal workflow
        mockMvc.perform(multipart("/rest/immagini/upload_default_img").file(file).with(csrf())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists());
 
        // invalid content type (not image)
        MockMultipartFile textFile = new MockMultipartFile(
                "file", "doc.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/rest/immagini/upload_default_img").file(textFile).with(csrf())
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get("upload_inv")));
 
        // empty file
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]);
        mockMvc.perform(multipart("/rest/immagini/upload_default_img").file(emptyFile).with(csrf())
                .header("Authorization", token))
                .andExpect(status().isInternalServerError());
    }
 
    // ==========================================
    // UPLOAD IMAGE (to manga or saga)
    // ==========================================
    public void uploadImage() throws Exception {
        log.debug("Begin uploadImage Test");
        String token = getBearerToken("AdminUser");
 
        MockMultipartFile file = buildImageFile("file", "manga_cover.png");
 
        // Normal workflow - upload to manga by isbn
        mockMvc.perform(multipart("/rest/immagini/upload").file(file).with(csrf())
                .header("Authorization", token)
                .param("isbn", "ISBN001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists());
 
        // Normal workflow - upload to saga by id
        MockMultipartFile file2 = buildImageFile("file", "saga_cover.png");
        mockMvc.perform(multipart("/rest/immagini/upload").file(file2).with(csrf())
                .header("Authorization", token)
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists());
 
        // null_idxs - no isbn and no id
        MockMultipartFile file3 = buildImageFile("file", "orphan.png");
        mockMvc.perform(multipart("/rest/immagini/upload").file(file3).with(csrf())
                .header("Authorization", token))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value(msgS.get("null_idxs")));
 
        // invalid content type
        MockMultipartFile textFile = new MockMultipartFile(
                "file", "doc.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/rest/immagini/upload").file(textFile).with(csrf())
                .header("Authorization", token)
                .param("isbn", "ISBN001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get("upload_inv")));
    }
 
    // ==========================================
    // GET URL (public)
    // ==========================================
    public void getUrl() throws Exception {
        log.debug("Begin getUrl Image Test");
        String token = getBearerToken("AdminUser");
 
        // existing filename
        mockMvc.perform(get("/rest/immagini/get_url")
                .param("filename", "default.png")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists());
 
        // non-existing filename
        mockMvc.perform(get("/rest/immagini/get_url")
                .param("filename", "nonexistent.png")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }
 
    // ==========================================
    // DELETE IMAGE
    // ==========================================
    public void deleteImage() throws Exception {
        log.debug("Begin deleteImage Test");
        String token = getBearerToken("AdminUser");
 
        // Normal workflow
        mockMvc.perform(delete("/rest/immagini/delete_image/somefile.png").with(csrf())
                .header("Authorization", token)
                .param("filename", "somefile.png"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get("img_deleted")));
    }
}
