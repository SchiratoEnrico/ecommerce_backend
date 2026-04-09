package com.betacom.ecommerce.backend.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
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
    private final IAccountRepository accountRepository; // Necessario per recuperare l'ID dall'username del token

    // --- METODI DI SUPPORTO ---
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    private Account getLoggedAccount(Principal principal) {
        return accountRepository.findByUsername(principal.getName()).orElse(null);
    }

    // --- ENDPOINT CREAZIONE ---
    @PostMapping("/create")
    public ResponseEntity<Response> create(@RequestBody(required = true) OrdineRequest req, Authentication auth, Principal principal) {
        Response r = new Response();
        HttpStatus status = HttpStatus.OK;

        // Forziamo l'ID dell'account a quello dell'utente loggato
        if (!isAdmin(auth)) {
            Account loggedAcc = getLoggedAccount(principal);
            if (loggedAcc == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // Sovrascriviamo l'ID mandato dal frontend con l'ID certo del token
            req.setAccount(loggedAcc.getId()); 
        }

        try {
            ordS.create(req);
            r.setMsg(msgS.get("rest_created"));
        } catch (Exception e) {
            r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }


    @GetMapping("/list")
    public ResponseEntity<Object> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String tipoPagamento,
            @RequestParam(required = false) String tipoSpedizione,
            @RequestParam(required = false) String statoOrdine,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) Integer mese,
            @RequestParam(required = false) Integer giorno,
            @RequestParam(required = false) List<String> isbns,
            Authentication auth, Principal principal 
            ) {
        Object r = new Object();
        HttpStatus status = HttpStatus.OK;
        
        // Se non è Admin, forziamo il filtro sullo username dell'utente loggato
        String targetUsername = isAdmin(auth) ? username : principal.getName();

        try {
            r = ordS.list(
                    targetUsername != null ? AccountDTO.builder().username(targetUsername).build() : null,
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
    public ResponseEntity<Object> findById(@RequestParam(required = true) Integer id, Authentication auth, Principal principal) {
        Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        if (!isAdmin(auth)) {
            Account loggedAcc = getLoggedAccount(principal);
            if (loggedAcc == null || !ordS.isOrdineOwnedByAccount(id, loggedAcc.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: questo ordine non è tuo.");
            }
        }

        try {
            r = ordS.findById(id);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

    // ENDPOINT SOLO ADMIN 
 	
    // inizio reso da fattua controller, qui solo admin
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/advanceStato")
    public ResponseEntity<Response> advanceStato(
            @RequestParam(required = true) Integer ordineId,
            @RequestParam(required = true) Integer statoId) {
        Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
            ordS.advanceStatoOrdine(ordineId, statoId);
            r.setMsg(msgS.get("rest_updated"));
        } catch (Exception e) {
            r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }
    
 	// Solo l'admin dovrebbe poter cancellare fisicamente un ordine dal DB
    @PreAuthorize("hasAuthority('ADMIN')")
 	@DeleteMapping("/delete/{id}")
     public ResponseEntity<Response> delete(@PathVariable(required = true) Integer id, @RequestParam(required = false) Boolean ripristinaCopie) {
 		Response r = new Response();
         HttpStatus status = HttpStatus.OK;
         try {
             ordS.delete(id, Boolean.TRUE.equals(ripristinaCopie));
             r.setMsg(msgS.get("rest_deleted"));
         } catch (Exception e) {
         	r.setMsg(msgS.get(e.getMessage()));
             status = HttpStatus.BAD_REQUEST;
         }
         return ResponseEntity.status(status).body(r);
     }

 	// Solo l'admin dovrebbe poter aggiornare liberamente un ordine (es. per cambiare lo stato in SPEDITO)
 	@PreAuthorize("hasAuthority('ADMIN')")
 	@PutMapping("/update")
     public ResponseEntity<Response> update(@RequestBody(required = true) OrdineRequest req) {

 		Response r = new Response();
         HttpStatus status = HttpStatus.OK;
         try {
             ordS.update(req);
             r.setMsg(msgS.get("rest_updated"));
         } catch (Exception e) {
         	r.setMsg(msgS.get(e.getMessage()));
             status = HttpStatus.BAD_REQUEST;
         }
         return ResponseEntity.status(status).body(r);
     }
}