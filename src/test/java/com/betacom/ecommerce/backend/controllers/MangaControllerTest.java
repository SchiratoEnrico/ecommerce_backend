package com.betacom.ecommerce.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public class MangaControllerTest {

    @Autowired
    private MangaController mangaC;

    @Test
    @Order(1)
    public void list() {
        log.debug("start list manga test");

        ResponseEntity<?> resp1 = mangaC.list();

        assertEquals(HttpStatus.OK, resp1.getStatusCode());
        assertNotNull(resp1.getBody());
    }

    @Test
    @Order(2)
    public void findByIsbn() {
        log.debug("start find manga by isbn test");

        ResponseEntity<?> resp = mangaC.findById("ISBN1");
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        MangaDTO m = (MangaDTO) resp.getBody();
        assertNotNull(m);
        assertEquals("ISBN1", m.getIsbn());
        assertEquals("NARUTO", m.getTitolo());
        assertEquals(LocalDate.of(1999, 9, 21), m.getDataPubblicazione());
        assertEquals(new BigDecimal("6.90"), m.getPrezzo());
        assertEquals("NARUTO.JPG", m.getImmagine());
        assertEquals(100, m.getNumeroCopie());

        assertNotNull(m.getCasaEditrice());
        assertNotNull(m.getAutori());
        assertNotNull(m.getGeneri());

        // errore isbn inesistente
        resp = mangaC.findById("INESISTENTE");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @Order(3)
    public void create() {
        log.debug("start create manga test");

        MangaRequest req = new MangaRequest();
        req.setIsbn(" isbn003 ");
        req.setTitolo(" bleach ");
        req.setDataPubblicazione("07/08/2001");
        req.setPrezzo(new BigDecimal("8.50"));
        req.setImmagine(" bleach.jpg ");
        req.setNumeroCopie(30);
        req.setCasaEditrice(1);
        req.setGeneri(List.of(1));
        req.setAutori(List.of(2));

        ResponseEntity<?> resp = mangaC.create(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        resp = mangaC.findById("ISBN003");
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        MangaDTO m = (MangaDTO) resp.getBody();
        assertNotNull(m);
        assertEquals("ISBN003", m.getIsbn());
        assertEquals("BLEACH", m.getTitolo());
        assertEquals(LocalDate.of(2001, 8, 7), m.getDataPubblicazione());
        assertEquals(new BigDecimal("8.50"), m.getPrezzo());
        assertEquals("BLEACH.JPG", m.getImmagine());
        assertEquals(30, m.getNumeroCopie());

        assertNotNull(m.getCasaEditrice());
        assertNotNull(m.getAutori());
        assertNotNull(m.getGeneri());
    }

    @Test
    @Order(4)
    public void createFail() {
        log.debug("start create manga fail test");

        MangaRequest req = new MangaRequest();
        req.setIsbn("ISBN004");
        req.setTitolo("TEST");
        req.setDataPubblicazione("2020-01-01");
        req.setPrezzo(new BigDecimal("5.00"));
        req.setImmagine("IMG.JPG");
        req.setNumeroCopie(5);
        req.setCasaEditrice(1);
        req.setGeneri(List.of(1));
        req.setAutori(List.of(1));

        // duplicato isbn già esistente nel data.sql
        MangaRequest dup = new MangaRequest();
        dup.setIsbn("ISBN1");
        dup.setTitolo("ALTRO TITOLO");
        dup.setDataPubblicazione("2020-01-01");
        dup.setPrezzo(new BigDecimal("5.00"));
        dup.setImmagine("IMG.JPG");
        dup.setNumeroCopie(5);
        dup.setCasaEditrice(1);
        dup.setGeneri(List.of(1));
        dup.setAutori(List.of(1));

        ResponseEntity<?> resp = mangaC.create(dup);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // autore inesistente
        req.setAutori(List.of(99));
        resp = mangaC.create(req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // genere inesistente
        req.setAutori(List.of(1));
        req.setGeneri(List.of(99));
        resp = mangaC.create(req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // casa editrice inesistente
        req.setGeneri(List.of(1));
        req.setCasaEditrice(99);
        resp = mangaC.create(req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // request null
        resp = mangaC.create(null);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    @Order(5)
    public void update() {
        log.debug("start update manga test");

        MangaRequest req = new MangaRequest();
        req.setIsbn("ISBN1");
        req.setTitolo(" NARUTO SHIPPUDEN ");
        req.setDataPubblicazione("01/01/2001");
        req.setPrezzo(new BigDecimal("7.90"));
        req.setNumeroCopie(120);

        ResponseEntity<?> resp = mangaC.update(req);

        // con l'implementazione attuale questo va in conflict
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @Order(6)
    public void updateFail() {
        log.debug("start update manga fail test");

        MangaRequest req = new MangaRequest();

        // isbn inesistente
        req.setIsbn("NONESISTE");
        req.setTitolo("TEST");

        ResponseEntity<?> resp = mangaC.update(req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // request null
        resp = mangaC.update(null);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }
}