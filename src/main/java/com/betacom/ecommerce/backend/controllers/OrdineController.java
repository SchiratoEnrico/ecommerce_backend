package com.betacom.ecommerce.backend.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.StatoOrdine;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
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
    private final ICarrelloRepository carrelloRepository; // Necessario per recuperare l'ID dall'username del token
    private final IStatoOrdineRepository statoR;
    // --- METODI DI SUPPORTO ---
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    private Account getLoggedAccount(Principal principal) {
        return accountRepository.findByUsername(principal.getName()).orElse(null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
    @GetMapping("/last_created")
    public ResponseEntity<Object> getLastCreated(Authentication auth, Principal principal) {
        try {
            Account loggedAcc = getLoggedAccount(principal);
            if (loggedAcc == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
            return ResponseEntity.ok(ordS.getUltimoPendente(loggedAcc.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msgS.get(e.getMessage()));
        }
    }

    // --- ENDPOINT CREAZIONE ---
    // endpoints condivisi admin/account owner e verificato
    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
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
    

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
    @GetMapping("/findById")
    public ResponseEntity<Object> findById(@RequestParam(required = true) Integer idOrdine, Authentication auth, Principal principal) {
        Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        if (!isAdmin(auth)) {
            Account loggedAcc = getLoggedAccount(principal);
            if (loggedAcc == null || !ordS.isOrdineOwnedByAccount(idOrdine, loggedAcc.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: questo ordine non è tuo.");
            }
        }

        try {
            r = ordS.findById(idOrdine);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
    @GetMapping("/get_next_allowed_states")
    public ResponseEntity<Object> getNextAllowedStates(@RequestParam(required = true) Integer idOrdine, Authentication auth, Principal principal) {
        Object r = new Object();
        HttpStatus status = HttpStatus.OK;

        if (!isAdmin(auth)) {
            Account loggedAcc = getLoggedAccount(principal);
            if (loggedAcc == null || !ordS.isOrdineOwnedByAccount(idOrdine, loggedAcc.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msgS.get("!owned_ord"));
            }
        }

        try {
            r = ordS.getNextAllowedStates(idOrdine);
        } catch (Exception e) {
            r = msgS.get(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VERIFIED_USER')")
    @PostMapping("/create_ordine_from_carrello")
    public ResponseEntity<Response> createOrdineFromCarrello(
    		@RequestParam(required = true) Integer carrelloId,
    		@RequestParam(required = true) Integer anagraficaId,
    		@RequestParam(required = true) Integer tipoPagamentoId,
    		@RequestParam(required = true) Integer tipoSpedizioneId,
            Authentication auth, Principal principal 
    		) {
        Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        if (!isAdmin(auth)) {
            Account loggedAcc = getLoggedAccount(principal);
            Optional<Carrello> carr = carrelloRepository.findById(carrelloId);
            if (carr.isEmpty()) {
            	r.setMsg(msgS.get("!exists_carr"));
            	return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
            }
            
            Integer accCarrelloId = carr.get().getAccount().getId();
            if (loggedAcc == null || !accCarrelloId.equals(loggedAcc.getId())) {
            	r.setMsg(msgS.get("!owned_carr"));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(r);
            }
        }
        
        try {
        	ordS.createOrdineFromCarrello(carrelloId, anagraficaId, tipoPagamentoId, tipoSpedizioneId);
        	r.setMsg(msgS.get("rest_created"));

        } catch (MangaException e){
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(r);
    }

    
    // ENDPOINT SOLO ADMIN 
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
 	
    // inizio reso da fattua controller, qui solo admin
 	@PutMapping("/avanza_stato_ordine")
     public ResponseEntity<Response> avanzaStatoOrdine (
    		 @RequestParam(required = true) Integer ordineId, 
    		 @RequestParam(required = true) Integer statoId,
             Authentication auth, Principal principal 
    		 ) {
 		Response r = new Response();
        HttpStatus status = HttpStatus.OK;
        try {
        	
        	if (!isAdmin(auth)) {
        		// controllo se richiedente non admin è possessore del carrello
        		Account loggedAcc = getLoggedAccount(principal);
        		if (loggedAcc == null || !ordS.isOrdineOwnedByAccount(ordineId, loggedAcc.getId()))
        			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        	
                // User può solo fare: PAGATO,  CANCELLATO (pre-lavorazione) o RICHIESTA_RESO
                StatoOrdine target = statoR.findById(statoId)
                    .orElseThrow(() -> new MangaException("!exists_sta"));
                String targetName = target.getStatoOrdine();

                if (!List.of("PAGATO", "CANCELLATO", "RICHIESTA_RESO").contains(targetName))
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            ordS.advanceStatoOrdine(ordineId, statoId);
            r.setMsg(msgS.get("ord_adv"));
        } catch (Exception e) {
        	r.setMsg(msgS.get(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(r);

    }

}