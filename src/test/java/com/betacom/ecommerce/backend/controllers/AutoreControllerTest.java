package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.betacom.ecommerce.backend.dto.inputs.AutoreRequest;
import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;
import com.betacom.ecommerce.backend.services.interfaces.IAutoreServices;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public class AutoreControllerTest {
	@Autowired
    private AutoreController autC;
	
	@MockitoSpyBean
	private IAutoreServices	autS;

	@Test
	public void testAutoreController() throws Exception{
		listById();
		create();
		update();
		delete();
		list();
	}

	public void list() throws Exception{
        log.debug("start list autori test");

        ResponseEntity<?> resp = autC.list();
		Assertions.assertThat(resp.getBody()).isInstanceOf(List.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);
		assertThat(((List<?>) b).size()).isGreaterThan(0);
		Assertions.assertThat(((List<?>) b).getFirst()).isInstanceOf(AutoreDTO.class);
        assertNotNull(resp.getBody());
        
        String error = "generic error";
        doThrow(new RuntimeException(error)).when(autS).list();
        resp = autC.list();
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    public void listById() {
        log.debug("start list autore by id test");

        ResponseEntity<?> resp = autC.findById(1);

        assertEquals(HttpStatus.OK, resp.getStatusCode());

        Object b = (AutoreDTO) resp.getBody();
        Assertions.assertThat(b).isInstanceOf(AutoreDTO.class);
        AutoreDTO a = (AutoreDTO) b;
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

    public void create() {
        log.debug("start create autore test");

        AutoreRequest req = new AutoreRequest();
        req.setNome(" Hirohiko ");
        req.setCognome(" Araki ");
        req.setDataNascita("1960-06-07");
        req.setDescrizione(" Le bizzarre avventure di JoJo ");

        ResponseEntity<?> resp = autC.create(req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());

        // assumendo che l'autore creato prenda id=3
        resp = autC.findById(3);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        AutoreDTO a = (AutoreDTO) resp.getBody();
        assertNotNull(a);
        assertEquals("HIROHIKO", a.getNome());
        assertEquals("ARAKI", a.getCognome());
        assertEquals("LE BIZZARRE AVVENTURE DI JOJO", a.getDescrizione());
        assertEquals(LocalDate.of(1960, 6, 7), a.getDataNascita());

        // errore duplicazione
        ResponseEntity<?> resp1 = autC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp1.getStatusCode());
    }

    public void update() {
        log.debug("start update autore test");

        // creo prima un autore che userò per il test di duplicazione
        AutoreRequest createReq = new AutoreRequest();
        createReq.setNome(" Naoki ");
        createReq.setCognome(" Urasawa ");
        createReq.setDataNascita("1960-01-02");
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
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // update errore duplicato
        // supponendo che l'autore creato sopra abbia id=3
        req = new AutoreRequest();
        req.setId(1);
        req.setNome("NAOKI");
        req.setCognome("URASAWA");
        req.setDataNascita("1960-01-02");

        resp = autC.update(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // update errore request null
        resp = autC.update(null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    public void delete() {
        log.debug("start delete autore test");

        // delete a buon fine
        // assumendo che id=3 sia l'autore creato in update e non collegato a manga
        ResponseEntity<?> resp = autC.delete(3);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        // delete id inesistente
        resp = autC.delete(99);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // delete autore collegato a manga
        // assumendo che id=1 sia collegato a un manga nel db di test
        resp = autC.delete(1);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}
