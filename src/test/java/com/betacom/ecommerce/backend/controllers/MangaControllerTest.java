package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public class MangaControllerTest {

    @Autowired
    private MangaController mangaC;
    
    @Autowired
	private IMessagesServices msgS;

	@Test
	public void testMangaController() {
		list();
		findByIsbn();
		create();
		createFail();
		update();
		updateFail();
		delete();
	}

    public void list() {
        log.debug("start list manga test");
		String titolo = "";
		String casaEditriceNome = "";
		String autoreNome = "";
		String sagaNome = "";
		Integer sagaId = null;
		Integer casaEditriceId = null;
		Integer autoreId = null;
		List<Integer> generiId = new ArrayList<Integer>();

		// vuoto
    	List<MangaDTO> lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	// titolo
    	titolo = "one";
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	titolo = "";
    	assertThat(lM.get(0).getTitolo().toLowerCase().startsWith(titolo));

    	// casaEditriceNome
    	casaEditriceNome = "Shueis";
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceNome = "";
    	assertThat(lM.get(0).getTitolo().toLowerCase().contains("one piece"));

    	// autoreNome
    	autoreNome = "iich";
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	autoreNome = "";
    	assertThat(lM.get(0).getTitolo().toLowerCase().contains("one piece"));

    	// sagaNome
    	sagaNome = "piec";
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	sagaNome = "";
    	assertThat(lM.get(0).getTitolo().toLowerCase().contains("one piece"));

    	// sagaId
    	sagaId= 1;
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	sagaId = null;
    	assertThat(lM.get(0).getTitolo().toLowerCase().contains("one piece"));

    	// casaEditriceId
    	casaEditriceId= 1;
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	casaEditriceId = null;
    	assertThat(lM.get(0).getTitolo().toLowerCase().contains("one piece"));

    	// autoreId
    	autoreId= 1;
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	autoreId = null;
    	assertThat(lM.get(0).getTitolo().toLowerCase().contains("one piece"));

    	// genereId
    	generiId.add(2);
    	lM = getLoadedList(titolo, casaEditriceNome, autoreNome, sagaNome, sagaId, casaEditriceId, autoreId, generiId);
    	generiId = null;
    	assertThat(lM.get(0).getTitolo().toLowerCase().contains("one piece"));}


    public void findByIsbn() {
        log.debug("start find manga by isbn test");

        ResponseEntity<?> resp = mangaC.findById("ISBN001");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
        assertNotNull(b);
		Assertions.assertThat(b).isInstanceOf(MangaDTO.class);

        MangaDTO m = (MangaDTO) b;
        assertEquals("ISBN001", m.getIsbn());
        assertEquals("One Piece Vol.1", m.getTitolo());
        assertEquals(LocalDate.of(1997, 7, 22), m.getDataPubblicazione());
        assertEquals(new BigDecimal("9.99"), m.getPrezzo());
        assertEquals("img1.jpg", m.getImmagine());
        assertEquals(100, m.getNumeroCopie());

        assertNotNull(m.getCasaEditrice());
        assertNotNull(m.getAutori());
        assertNotNull(m.getGeneri());

        // errore isbn inesistente
        resp = mangaC.findById("INESISTENTE");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    public void create() {
        log.debug("start create manga test");

        MangaRequest req = new MangaRequest();
        req.setIsbn(" isbn003 ");
        req.setTitolo(" bleach ");
        req.setDataPubblicazione("2001-08-07");
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
    
    private MangaRequest getMangaRequest() {
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
        return req;
    }

    public void createFail() {
        log.debug("start create manga fail test");

        MangaRequest req =  getMangaRequest();

        // duplicato isbn già esistente nel data.sql
        req.setIsbn("ISBN001");

        ResponseEntity<?> resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // autore inesistente
        req =  getMangaRequest();
        req.setAutori(List.of(99));
        resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // genere inesistente
        req =  getMangaRequest();
        req.setGeneri(List.of(99));
        resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // casa editrice inesistente
        req =  getMangaRequest();
        req.setCasaEditrice(99);
        resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // request null
        resp = mangaC.create(null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        
        // casa editrice inesistente
        req =  getMangaRequest();
        req.setCasaEditrice(99);
        resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // troppi autori
        req =  getMangaRequest();
        req.setAutori(Arrays.asList(1, 2, 3, 4));
        resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // autori non esistenti
        req =  getMangaRequest();
        req.setAutori(Arrays.asList(1, 99));
        resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // generi non esistenti
        req =  getMangaRequest();
        req.setGeneri(Arrays.asList(1, 99));
        resp = mangaC.create(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
    
    public void update() {
        log.debug("start update manga test");

        MangaRequest req = new MangaRequest();
        req.setIsbn("ISBN001");
        req.setTitolo(" NARUTO SHIPPUDEN ");
        req.setDataPubblicazione("2001-01-01");
        req.setPrezzo(new BigDecimal("7.90"));
        req.setNumeroCopie(120);

        ResponseEntity<?> resp = mangaC.update(req);

        // con l'implementazione attuale questo va in BAD_REQUEST
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    public void updateFail() {
        log.debug("start update manga fail test");

        MangaRequest req = new MangaRequest();

        // isbn inesistente
        req.setIsbn("NONESISTE");
        req.setTitolo("TEST");

        ResponseEntity<?> resp = mangaC.update(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        // request null
        resp = mangaC.update(null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
    
    
    @SuppressWarnings("unchecked")
	private List<MangaDTO> getLoadedList(
			String titolo,
			String casaEditriceNome,
			String autoreNome,
			String sagaNome,
			Integer sagaId,
			Integer casaEditriceId,
			Integer autoreId,
			List<Integer> generiId

			) {

    	ResponseEntity<?> resp = mangaC.list(
				titolo,
				casaEditriceNome,
				autoreNome,
				sagaNome,
				sagaId,
				casaEditriceId,
				autoreId,
				generiId
        		);
    	// test per:
    	// linked_ord 
    	// linked_car
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Object b = resp.getBody();
		Assertions.assertThat(b).isInstanceOf(List.class);

    	return ((List<MangaDTO>) resp.getBody());
    }
    
    public void delete() {
		// errore: id non trovato in db/non valido
    	String msg = "!exists_man";
		String isbn = "ABC!123";
		log.debug("Start StatoOrdineControllerTest.deleteTest(): error expected: {}, invalid id: {}", msg, isbn);
		ResponseEntity<Response> resp = mangaC.delete(isbn);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		Response r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

		// linked_ord
		msg = "linked_ord";
		isbn = "ISBN001";
		log.debug("Start StatoOrdineControllerTest.deleteTest(), expected error: {} id: {}", msg, isbn);
		resp = mangaC.delete(isbn);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));
		
		// linked_car
		msg = "linked_car";
		isbn = "ISBN002";
		log.debug("Start StatoOrdineControllerTest.deleteTest(), expected error: {} id: {}", msg, isbn);
		resp = mangaC.delete(isbn);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));
		
		// normal workflow
		msg = "rest_deleted";
		List<MangaDTO> lM = getLoadedList("", "", "", "", null, null, null, null);
		isbn = lM.get(lM.size() - 1).getIsbn();
		log.debug("Start StatoOrdineControllerTest.deleteTest(), expected success, id: {}", msg, isbn);
		resp = mangaC.delete(isbn);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		r = resp.getBody();
		Assertions.assertThat(r.getMsg()).isEqualTo(msgS.get(msg));

    }
}