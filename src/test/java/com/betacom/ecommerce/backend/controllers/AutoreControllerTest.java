package com.betacom.ecommerce.backend.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class AutoreControllerTest {
	@Autowired
    private AutoreController autC;

    @Test
    @Order(1)
    public void list() {
        log.debug("start list autori test");

        ResponseEntity<?> resp = autC.list();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
    }

    @Test
    @Order(2)
    public void listById() {
        log.debug("start list autore by id test");

        ResponseEntity<?> resp = autC.findById(1);

        assertEquals(HttpStatus.OK, resp.getStatusCode());

        AutoreDTO a = (AutoreDTO) resp.getBody();
        assertNotNull(a);


        assertNotNull(a.getId());
        assertNotNull(a.getNome());
        assertNotNull(a.getCognome());
        assertNotNull(a.getDescrizione());
        assertNotNull(a.getDataNascita());

        // test errore id inesistente
        resp = autC.findById(99);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @Order(3)
    public void create() {
        log.debug("start create autore test");

        AutoreRequest req = new AutoreRequest();
        req.setNome(" Eiichiroo ");
        req.setCognome(" Oda ");
        req.setDataNascita("01/01/1975");
        req.setDescrizione(" mangaka ");

        ResponseEntity<?> resp = autC.create(req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());

        // assumendo che l'autore creato prenda id=3
        resp = autC.findById(3);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        AutoreDTO a = (AutoreDTO) resp.getBody();
        assertNotNull(a);
        assertEquals("EIICHIROO", a.getNome());
        assertEquals("ODA", a.getCognome());
        assertEquals("MANGAKA", a.getDescrizione());
        assertEquals(LocalDate.of(1975, 1, 1), a.getDataNascita());

        // errore duplicazione
        ResponseEntity<?> resp1 = autC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp1.getStatusCode());
    }

    @Test
    @Order(4)
    public void update() {
        log.debug("start update autore test");

        // creo prima un autore che userò per il test di duplicazione
        AutoreRequest createReq = new AutoreRequest();
        createReq.setNome(" Naoki ");
        createReq.setCognome(" Urasawa ");
        createReq.setDataNascita("02/01/1960");
        createReq.setDescrizione(" autore seinen ");
        autC.create(createReq);

        // update corretto sull'autore con id=1
        AutoreRequest req = new AutoreRequest();
        req.setId(1);
        req.setNome(" Akira ");
        req.setDescrizione(" autore aggiornato ");

        ResponseEntity<?> resp = autC.update(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        resp = autC.findById(1);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        AutoreDTO a = (AutoreDTO) resp.getBody();
        assertNotNull(a);
        assertEquals("AKIRA", a.getNome());
        assertEquals("AUTORE AGGIORNATO", a.getDescrizione());

        // update errore id inesistente
        req = new AutoreRequest();
        req.setId(99);
        req.setNome(" Test ");
        resp = autC.update(req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // update errore duplicato
        // supponendo che l'autore creato sopra abbia id=3
        req = new AutoreRequest();
        req.setId(1);
        req.setNome("NAOKI");
        req.setCognome("URASAWA");
        req.setDataNascita("02/01/1960");

        resp = autC.update(req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // update errore request null
        resp = autC.update(null);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    @Order(5)
    public void delete() {
        log.debug("start delete autore test");

        // delete a buon fine
        // assumendo che id=3 sia l'autore creato in update e non collegato a manga
        ResponseEntity<?> resp = autC.delete(3);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        // delete id inesistente
        resp = autC.delete(99);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        // delete autore collegato a manga
        // assumendo che id=1 sia collegato a un manga nel db di test
        resp = autC.delete(1);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }
}
