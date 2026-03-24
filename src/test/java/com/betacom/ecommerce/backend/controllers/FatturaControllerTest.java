package com.betacom.ecommerce.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.assertj.core.api.Assertions;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FatturaControllerTest {
	
	 @Autowired
	 private FatturaController fatC;
	 @Autowired
	 private IMessagesServices msgS;
	 
	 @Test
     public void testFatturaController() {
        create();
        update();
        list();
        findById();
        findByIdError();
        deleteError();
        delete();
    }

    private FatturaRequest buildFatturaRequest() {
        return FatturaRequest.builder()
                .numeroFattura("FAT-2024-001")
                .clienteNome("Mario")
                .clienteCognome("Rossi")
                .clienteEmail("mario.rossi@email.it")
                .clienteIndirizzo("Via Roma 1")
                .clienteCitta("Milano")
                .clienteCap("20100")
                .clienteProvincia("Milano")
                .clienteStato("Italia")
                .tipoPagamento("Carta di Credito")
                .tipoSpedizione("Standard")
                .costoSpedizione(new BigDecimal("4.99"))
                .righeFatturaRequest(List.of("978-3-16-148410-0"))
                .build();
        }

		public void create() {
		    log.debug("Begin create Fattura Test");
		
		    FatturaRequest req = buildFatturaRequest();
		
		    ResponseEntity<Response> re = fatC.create(req);
		    assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
		    Response r = re.getBody();
		    assertThat(r.getMsg()).isEqualTo("Elemento creato con successo");
		
	        // null_num_fat
	     	String msg = "null_num_fat";
	     	req = buildFatturaRequest();
	     	req.setNumeroFattura(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_nom
	     	msg = "null_nom";
	     	req = buildFatturaRequest();
	     	req.setClienteNome(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_cog
	     	msg = "null_cog";
	     	req = buildFatturaRequest();
	     	req.setClienteCognome(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_ema
	     	msg = "null_ema";
	     	req = buildFatturaRequest();
	     	req.setClienteEmail(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_ind
	     	msg = "null_ind";
	     	req = buildFatturaRequest();
	     	req.setClienteIndirizzo(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_cit
	     	msg = "null_cit";
	     	req = buildFatturaRequest();
	     	req.setClienteCitta(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_cap
	     	msg = "null_cap";
	     	req = buildFatturaRequest();
	     	req.setClienteCap(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_pro
	     	msg = "null_pro";
	     	req = buildFatturaRequest();
	     	req.setClienteProvincia(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_cap
	     	msg = "null_cap";
	     	req = buildFatturaRequest();
	     	req.setClienteCap(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_sta
	     	msg = "null_sta";
	     	req = buildFatturaRequest();
	     	req.setClienteStato(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));

	     	// null_pag
	     	msg = "null_pag";
	     	req = buildFatturaRequest();
	     	req.setTipoPagamento(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));


	     	// null_spe
	     	msg = "null_spe";
	     	req = buildFatturaRequest();
	     	req.setTipoSpedizione(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));
	     	
	     	// null_rig
	     	msg = "null_rig_fat";
	     	req = buildFatturaRequest();
	     	req.setRigheFatturaRequest(null);
	     	log.debug("Begin create Fattura Test, error expected: {}", msg);
	     	re = fatC.create(req);
	     	assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	     	Assertions.assertThat(re.getBody().getMsg()).isEqualTo(msgS.get(msg));
		
		}

	    public void update() {
	        log.debug("Begin update Fattura Test");
	
	        FatturaRequest req = FatturaRequest.builder()
	                .id(1)
	                .numeroFattura("FAT-2024-001-UPD")
	                .clienteNome("Luigi")
	                .clienteCognome("Bianchi")
	                .clienteEmail("luigi.bianchi@email.it")
	                .clienteIndirizzo("Via Venezia 5")
	                .clienteCitta("Roma")
	                .clienteCap("00100")
	                .clienteProvincia("Roma")
	                .clienteStato("Italia")
	                .tipoPagamento("PayPal")
	                .tipoSpedizione("Espresso")
	                .costoSpedizione(new BigDecimal("7.99"))
	                .righeFatturaRequest(List.of("978-0-7432-7356-5"))
	                .build();
	
	        ResponseEntity<Response> re = fatC.update(req);
	        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
	        Response r = re.getBody();
	        assertThat(r.getMsg()).isEqualTo("Elemento aggiornato con successo");
	    }

    public void list() {
        log.debug("Begin list Fattura Test");

        ResponseEntity<Object> re = fatC.list();
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> b = (List<?>) re.getBody();
        assertThat(b.size()).isGreaterThan(0);
        Assertions.assertThat(b.getFirst()).isInstanceOf(FatturaDTO.class);
    }

    public void findById() {
        log.debug("Begin findById Fattura Test");

        ResponseEntity<Object> re = fatC.findById(1);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object b = re.getBody();
        Assertions.assertThat(b).isInstanceOf(FatturaDTO.class);
        assertThat(((FatturaDTO) b).getId()).isEqualTo(1);
    }

    public void findByIdError() {
        log.debug("Begin findById Fattura Test - error expected");

        ResponseEntity<Object> re = fatC.findById(999);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    public void deleteError() {
        log.debug("Begin delete Fattura Test - error expected");

        ResponseEntity<Response> re = fatC.delete(999);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Response r = re.getBody();
        assertThat(r.getMsg()).isEqualTo(msgS.get("!exists_fat"));
    }

    public void delete() {
        log.debug("Begin delete Fattura Test");

        ResponseEntity<Response> re = fatC.delete(1);
        assertThat(re.getStatusCode()).isEqualTo(HttpStatus.OK);
        Response r = re.getBody();
        assertThat(r.getMsg()).isEqualTo("Elemento eliminato con successo");
    }

}
