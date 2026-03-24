package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.betacom.ecommerce.backend.dto.inputs.RigaFatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaFatturaDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RigaFatturaControllerTest {
	
	@Autowired
	private RigaFatturaController rigaC;
	@Autowired
	private IMessagesServices msgS;

	
	@Test
	public void testRigaFatturaController() {
		create();
        update();
        list();
        findById();
        findByIdError();
        deleteError();
        delete();
	}
	
	private RigaFatturaRequest buildRigaFatturaRequest() {
        return RigaFatturaRequest.builder()
                .idFattura(1)
                .isbn("978-3-16-148410-0")
                .titolo("Il Nome della Rosa")
                .autore("Umberto Eco")
                .prezzoUnitario(new BigDecimal("19.99"))
                .quantita(2)
                .build();
    }

	public void create() {
		log.debug("Begin create RigaFattura Test");

        RigaFatturaRequest req = buildRigaFatturaRequest();

        ResponseEntity<Response> re = rigaC.create(req);
        log.debug("Response body: {}", re.getBody().getMsg());
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        Response r = re.getBody();
        assertThat(r.getMsg()).isEqualTo("Elemento creato con successo");
        
        // null_isn
     	String msg = "null_isn";
     	req = buildRigaFatturaRequest();
     	req.setIsbn(null);
     	log.debug("Begin create Riga Fattura Test, error expected: {}", msg);
     	re = rigaC.create(req);
     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

     	// null_tit
     	msg = "null_tit";
     	req = buildRigaFatturaRequest();
     	req.setTitolo(null);
     	log.debug("Begin create Riga Fattura Test, error expected: {}", msg);
     	re = rigaC.create(req);
     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

     	// null_aut
     	msg = "null_aut";
     	req = buildRigaFatturaRequest();
     	req.setAutore(null);
     	log.debug("Begin create Riga Fattura Test, error expected: {}", msg);
     	re = rigaC.create(req);
     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

     	// null_pre
     	msg = "null_pre";
     	req = buildRigaFatturaRequest();
     	req.setPrezzoUnitario(null);
     	log.debug("Begin create Riga Fattura Test, error expected: {}", msg);
     	re = rigaC.create(req);
     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

        // null_pre
     	msg = "null_qua";
     	req = buildRigaFatturaRequest();
     	req.setQuantita(null);
     	log.debug("Begin create Riga Fattura Test, error expected: {}", msg);
     	re = rigaC.create(req);
     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));
     	
     	// null_fat
     	msg = "null_fat";
     	req = buildRigaFatturaRequest();
     	req.setIdFattura(null);
     	log.debug("Begin create Riga Fattura Test, error expected: {}", msg);
     	re = rigaC.create(req);
     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));
     	
     	
	}
	
	public void update() {
		log.debug("Begin update RigaFattura Test");

        RigaFatturaRequest req = RigaFatturaRequest.builder()
                .id(1)
                .idFattura(1)
                .isbn("978-0-7432-7356-5")
                .titolo("Il Gatto con Gli Stivali")
                .autore("Qualcuno Fantasioso")
                .prezzoUnitario(new BigDecimal("14.50"))
                .quantita(3)
                .build();

        ResponseEntity<Response> re = rigaC.update(req);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        Response r = re.getBody();
        assertThat(r.getMsg()).isEqualTo("Elemento aggiornato con successo");	
	
	}
	public void list() {
        log.debug("Begin list RigaFattura Test");

        ResponseEntity<Object> re = rigaC.list();
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> b = (List<?>) re.getBody();
        assertThat(b.size()).isGreaterThan(0);
        Assertions.assertThat(b.getFirst()).isInstanceOf(RigaFatturaDTO.class);
    }

	public void findById() {
        log.debug("Begin findById RigaFattura Test");

        ResponseEntity<Object> re = rigaC.findById(1);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object b = re.getBody();
        Assertions.assertThat(b).isInstanceOf(RigaFatturaDTO.class);
        assertThat(((RigaFatturaDTO) b).getId()).isEqualTo(1);
    }
	
	public void findByIdError() {
        log.debug("Begin findById RigaFattura Test - error expected");

        ResponseEntity<Object> re = rigaC.findById(999);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
	
	public void deleteError() {
        log.debug("Begin delete RigaFattura Test - error expected");

        ResponseEntity<Response> re = rigaC.delete(999);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Response r = re.getBody();
        assertThat(r.getMsg()).isEqualTo("Riga fattura non esistente");
    }
	
	public void delete() {
        log.debug("Begin delete RigaFattura Test");

        ResponseEntity<Response> re = rigaC.delete(1);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        Response r = re.getBody();
        assertThat(r.getMsg()).isEqualTo("Elemento eliminato con successo");
    }
	

}
