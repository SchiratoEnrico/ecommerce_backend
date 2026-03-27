package com.betacom.ecommerce.backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.ecommerce.backend.dto.inputs.OrdineRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoPagamentoDTO;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IOrdineServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/ordine")
public class OrdineController {
	private final IOrdineServices ordS;
	private final IMessagesServices msgS;

	@PostMapping("/create")
    public ResponseEntity<Response> create(@RequestBody(required = true) OrdineRequest req) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            ordS.create(req);
            r.setMsg(msgS.get("rest_created"));
        } catch (MangaException e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

	@DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable(required = true) Integer id) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            ordS.delete(id);
            r.setMsg(msgS.get("rest_deleted"));
        } catch (MangaException e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

	@PutMapping("/update")
    public ResponseEntity<Response> update(@RequestBody(required = true) OrdineRequest req) {
		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            ordS.update(req);
            r.setMsg(msgS.get("rest_updated"));
        } catch (MangaException e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

/*	
	@GetMapping ("/list")
	public ResponseEntity<Object> list(){
		Object r = new Object();
		HttpStatus status = HttpStatus.OK;
		try {
            r = ordS.list();
        } catch (MangaException e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
	}
*/
	@GetMapping("/list")
	public ResponseEntity<Object> list(
	        @RequestParam(required = false) String username,
	        @RequestParam(required = false) String tipoPagamento,
	        @RequestParam(required = false) String tipoSpedizione,
	        @RequestParam(required = false) String statoOrdine,
	        @RequestParam(required = false) Integer anno,
	        @RequestParam(required = false) Integer mese,
	        @RequestParam(required = false) Integer giorno,
	        @RequestParam(required = false) List<String> isbns
	        ) {
	    Object r = new Object();
	    HttpStatus status = HttpStatus.OK;
	    try {
	        r = ordS.list(
	                username != null ? AccountDTO.builder().username(username).build() : null,
	                tipoPagamento != null ? TipoPagamentoDTO.builder().tipoPagamento(tipoPagamento).build() : null,
	                tipoSpedizione != null ? TipoSpedizioneDTO.builder().tipoSpedizione(tipoSpedizione).build() : null,
	                anno, mese, giorno,
	                statoOrdine != null ? StatoOrdineDTO.builder().statoOrdine(statoOrdine).build() : null,
	                isbns
	        );
	    } catch (Exception e) {
	        r = e.getMessage();
	        status = HttpStatus.BAD_REQUEST;
	    }
	    return ResponseEntity.status(status).body(r);
	}
	
	@GetMapping("/findById")
    public ResponseEntity<Object> findById(@RequestParam(required = true) Integer id) {
		Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        try {
            r = ordS.findById(id);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

}
