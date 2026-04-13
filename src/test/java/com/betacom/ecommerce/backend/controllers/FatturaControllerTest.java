package com.betacom.ecommerce.backend.controllers;
 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.StatoOrdineDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.repositories.IStatoOrdineRepository;
import com.betacom.ecommerce.backend.security.JwtService;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.services.interfaces.IMailServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc // per autoconfiguraz test con richiesta web simulata
@ActiveProfiles("test") // per avere info da test/resources/application.properties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FatturaControllerTest {
	
    @Autowired // <-- @Autowired invece di final
    private MockMvc mockMvc;
    
    @Autowired
    private IMessagesServices msgS;

    @Autowired
    private IStatoOrdineRepository statR;

    // @MockitoSpyBean sostituisce classe reale con uno spy no-op
    @MockitoSpyBean
    private IFatturaServices fatS;
	 
	// I servizi  che ci servono per creare il Token
	@Autowired
	private JwtService jwtService; 

	@Autowired
	private UserDetailsService userDetailsService;

    @MockitoSpyBean
    private IMailServices mailSender;

	private final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	
	// METODO  PER IL TOKEN
	private String getBearerToken(String username) {
		UserDetails user = userDetailsService.loadUserByUsername(username);
		String token = jwtService.generateToken(user.getUsername()); 
		return "Bearer " + token;
	}

	
	// ==========================================
	// TEST ENDPOINT CONDIVISI (Admin + Owner)
	// ==========================================
    @Test
    public void testFatturaControllerOwner()  throws Exception{
    	findById();
    	// NW inizio reso testato dopo
    	//iniziaReso();  
    }

	public void findById() throws Exception {
        log.debug("Begin findById Fattura Test");
        String token = getBearerToken("MarioRossi");
		
        mockMvc.perform(get("/rest/fattura/findById")
        		.param("idFattura", "1")
        		.param("idAccount", "1")
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/rest/fattura/findById")
        		.param("idFattura", (String) null)
        		.param("idAccount", "1")
				.header("Authorization", token))
				.andExpect(status().isBadRequest());
        
        token = getBearerToken("UserUser");
        mockMvc.perform(get("/rest/fattura/findById")
        		.param("idFattura", "1")
        		.param("idAccount", "1")
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	}
	
	
	@Test
    public void testFatturaControllerAdmin() throws Exception {
        create();
        update();
        list();
        deleteTest();
    	authFails();
    	resoPipeline();
    }

    private FatturaRequest buildFatturaRequest() {
    	/*
    	 * NW in data.sql:
    	 * tipoPagamentoId: 1 - PAYPAL, 2 - CARTA DI CREDITO, 3 - BONIFICO
    	 * tipi_spedizioneId: 1 - standard, 2 - express, 3 - gratuita 
    	 */

        return FatturaRequest.builder()
                .clienteNome("Mario")
                .clienteCognome("Rossi")
                .clienteEmail("mario.rossi@email.it")
                .clienteIndirizzo("Via Roma 1")
                .clienteCitta("Milano")
                .clienteCap("20100")
                .clienteProvincia("Milano")
                .clienteStato("Italia")
                .tipoPagamentoId(2)
                .tipoSpedizioneId(1)
                .ordineId(1)
                .righeFatturaRequest(List.of())
                .build();
    }

	// mappa di transizion possibili
	private static final Map<String, List<String>> ALLOWED_TRANSITIONS = Map.of(
			"PAGATO",          List.of("LAVORAZIONE", "ANNULLATA"),
			"LAVORAZIONE",     List.of("SPEDITO"),
			"SPEDITO",         List.of("CONSEGNATO"),
			"CONSEGNATO",      List.of("CONFERMATO", "RICHIESTA_RESO"),
			"CONFERMATO",      List.of(),                                  // terminale: possibilità reso scaduto
			"RICHIESTA_RESO",  List.of("RICONSEGNATO", "RIFIUTATO"),
			"RICONSEGNATO",    List.of("RIMBORSATO"),                      // manga restituito fisicamente
			"RIMBORSATO",      List.of(),                                  // terminale
			"RIFIUTATO",       List.of(),                                  // terminale: reso respinto
			"ANNULLATA",       List.of()                                   // terminale: ordine cancellato
		);

    /// DTO builder per check get_next_allowed_states
    private List<StatoOrdineDTO> getNextStates(String statoOrdine) {
    	log.debug("Stato ordine input: {}", statoOrdine);
    	List<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(statoOrdine, List.of());
    	log.debug("Stati ordine permessi:");
    	for (String s: allowed) {
        	log.debug("\t{}", s);
    	}
    	log.debug("Stati ordine permessi:", allowed);
    	List<StatoOrdineDTO> lS = allowed.stream()
                .map(s -> s.equals("ANNULLATA")?
						statR.findByStatoOrdine("CANCELLATO")
							.orElseThrow(() -> new MangaException())
						:
						statR.findByStatoOrdine(s)
							.orElseThrow(() -> new MangaException())
                		)
    			.map(s -> DtoBuilders.buildStatoOrdineDTO(s))
    			.toList();
    	for (StatoOrdineDTO s: lS) {
        	log.debug("\tstato concesso: {}", s);
    	}
    	return lS;
    }
    
    private void assertNextStates(String token, String fatturaId, String currentState) throws Exception {
        List<StatoOrdineDTO> expected = getNextStates(currentState);

        var result = mockMvc.perform(get("/rest/fattura/get_next_allowed_states")
                .param("idFattura", fatturaId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expected.size()));

        for (int i = 0; i < expected.size(); i++) {
            result.andExpect(jsonPath("$[" + i + "].id").value(expected.get(i).getId()))
                  .andExpect(jsonPath("$[" + i + "].statoOrdine").value(expected.get(i).getStatoOrdine()));
        }
    }

    
	// qui testo anche getNextAllowedStates
    public void resoPipeline() throws Exception {
        log.debug("Begin reso pipeline Fattura Test");
        String token = getBearerToken("AdminUser");
        String userToken = getBearerToken("UserUser");
        String ownerToken = getBearerToken("MarioRossi");

        // rifiuta forbidden per non-admin
        mockMvc.perform(put("/rest/fattura/reso/rifiuta").with(csrf())
                .param("fatturaId", "1")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
        
        //conferma forbidden per non-admin
        mockMvc.perform(put("/rest/fattura/reso/conferma").with(csrf())
                .param("fatturaId", "1")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
        // rimborsa forbidden per non-admin
        mockMvc.perform(put("/rest/fattura/reso/rimborso").with(csrf())
                .param("fatturaId", "1")
                .param("ripristinaCopie", "false")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());

        
        // fattura 1 in CONSEGNATO
        // invalid: conferma reso su fattura non in RICHIESTA_RESO
        String msg = "stato_fat_invalid";
        mockMvc.perform(put("/rest/fattura/reso/conferma").with(csrf())
        		.header("Authorization", token)
        		.param("fatturaId", "1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // invalid: rimborsa fattura con stato != RESTITUITO
        mockMvc.perform(put("/rest/fattura/reso/rimborso").with(csrf())
                .param("fatturaId", "1")
                .param("ripristinaCopie", "false")
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // !exists_fat
        msg = "!exists_fat";
        mockMvc.perform(put("/rest/fattura/reso/conferma").with(csrf())
                .param("fatturaId", "999")
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        // !owner
        msg = "!owner";
        mockMvc.perform(post("/rest/fattura/reso/inizia").with(csrf())
                .param("fatturaId", "1")
                .param("accountId", "1")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        String curState = "CONSEGNATO";
        String fatturaId = ((Integer) 1).toString();
        assertNextStates(ownerToken, fatturaId, curState);

        // inizia reso
        msg = "reso_start";
        mockMvc.perform(post("/rest/fattura/reso/inizia").with(csrf())
                .param("fatturaId", "1")
                .param("accountId", "1")
                .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));


        curState = "RICHIESTA_RESO";
        assertNextStates(ownerToken, fatturaId, curState);

        // conferma: RICHIESTA_RESO → RICONSEGNATO
        msg = "reso_conf";
        mockMvc.perform(put("/rest/fattura/reso/conferma").with(csrf())
                .param("fatturaId", "1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        curState = "RICONSEGNATO";
        assertNextStates(token, fatturaId, curState);

        // rimborsa: RICONSEGNATO → RIMBORSATO (copie ripristinate)
        msg = "reso_rimb";
        mockMvc.perform(put("/rest/fattura/reso/rimborso").with(csrf())
                .param("fatturaId", "1")
                .param("ripristinaCopie", "true")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
        
        
        
        // su fattura 3 test per respingere reso:
        // porto fattura 3 in stati consegnato:
        // agisco su ordine id = 3 e lo porto fino a consegnato
        msg = "ord_adv";
        fatturaId = ((Integer) 3).toString();
        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "2") // PAGATO
                .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        curState = "PAGATO";
        assertNextStates(token, fatturaId, curState);

        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "3") // LAVORAZIONE
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        curState = "LAVORAZIONE";
        assertNextStates(token, fatturaId, curState);

        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "4") // SPEDITO
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));

        curState = "SPEDITO";
        assertNextStates(token, fatturaId, curState);

        mockMvc.perform(put("/rest/ordine/avanza_stato_ordine").with(csrf())
                .param("ordineId", "3")
                .param("statoId", "5") // CONSEGNATO
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));
        
        curState = "CONSEGNATO";
        assertNextStates(token, fatturaId, curState);

        // inizia reso
        msg = "reso_start";
        mockMvc.perform(post("/rest/fattura/reso/inizia").with(csrf())
                .param("fatturaId", fatturaId)
                .param("accountId", "1")
                .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));


        curState = "RICHIESTA_RESO";
        assertNextStates(ownerToken, fatturaId, curState);

        msg = "reso_ref";
        mockMvc.perform(put("/rest/fattura/reso/rifiuta").with(csrf())
                .param("fatturaId", fatturaId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(msgS.get(msg)));


        curState = "RIFIUTATO";
        assertNextStates(ownerToken, fatturaId, curState);

    }
    

    private void assertCreateError(String msg, FatturaRequest req, String token) throws Exception {
	    mockMvc.perform(post("/rest/fattura/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value(msgS.get(msg)));

    }
    
    public void authFails() throws Exception {
	    String token = getBearerToken("UserUser");
	    FatturaRequest req = buildFatturaRequest();
        req.setId(1);
	    mockMvc.perform(post("/rest/fattura/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	    
	    token = getBearerToken("AdminUser");

    }
    
	public void create() throws Exception {
		log.debug("Begin create Fattura Test");
	    FatturaRequest req = buildFatturaRequest();	    
	    String token = getBearerToken("AdminUser");
	    mockMvc.perform(post("/rest/fattura/create").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value(msgS.get("rest_created")));
	    
	    // null nom
	    String msg = "null_nom";
	    req = buildFatturaRequest();
	    req.setClienteNome(null);
	    assertCreateError(msg, req, token);
	    
	 // null_cog
     	msg = "null_cog";
     	req = buildFatturaRequest();
     	req.setClienteCognome(null);
     	assertCreateError(msg, req, token);

     	// null_ema
     	msg = "null_ema";
     	req = buildFatturaRequest();
     	req.setClienteEmail(null);
     	assertCreateError(msg, req, token);
     	// null_ind
     	msg = "null_ind";
     	req = buildFatturaRequest();
     	req.setClienteIndirizzo(null);
     	assertCreateError(msg, req, token);
     	// null_cit
     	msg = "null_cit";
     	req = buildFatturaRequest();
     	req.setClienteCitta(null);
     	assertCreateError(msg, req, token);
     	// null_cap
     	msg = "null_cap";
     	req = buildFatturaRequest();
     	req.setClienteCap(null);
     	assertCreateError(msg, req, token);
     	// null_pro
    	msg = "null_pro";
     	req = buildFatturaRequest();
     	req.setClienteProvincia(null);
     	assertCreateError(msg, req, token);
     	// null_sta
    	msg = "null_sta";
     	req = buildFatturaRequest();
     	req.setClienteStato(null);
    	assertCreateError(msg, req, token);

     	// null_pag
     	msg = "null_pag";
     	req = buildFatturaRequest();
     	req.setTipoPagamentoId(null);
     	assertCreateError(msg, req, token);

     	// null_spe
     	msg = "null_spe";
     	req = buildFatturaRequest();
     	req.setTipoSpedizioneId(null);
     	assertCreateError(msg, req, token);
 	}

	
    private void assertUpdateError(String msg, FatturaRequest req, String token) throws Exception {
	    mockMvc.perform(put("/rest/fattura/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value(msgS.get(msg)));

    }

	public void update() throws Exception {
	    String token = getBearerToken("UserUser");
	    FatturaRequest req = buildFatturaRequest();
	    req.setId(1);

	    mockMvc.perform(put("/rest/fattura/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	    
	    token = getBearerToken("AdminUser");
	    String msg = "!exists_fat";
	    req.setId(99);
	    assertUpdateError(msg, req, token);
	    
	    msg = "!exists_pag";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setTipoPagamentoId(99);
	    assertUpdateError(msg, req, token);
	    
	    msg = "!exists_spe";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setTipoSpedizioneId(99);
	    assertUpdateError(msg, req, token);
	    
	    msg = "!exists_ord";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setOrdineId(99);
	    assertUpdateError(msg, req, token);
	    
	    msg = "rest_updated";
	    req = buildFatturaRequest();
	    req.setId(1);
	    req.setClienteNome("Otto");
	    mockMvc.perform(put("/rest/fattura/update").with(csrf())
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value(msgS.get(msg)));
	}
	
    public void deleteTest() throws Exception {
        log.debug("Begin delete Fattura Test");
        
	    String token = getBearerToken("UserUser");
        
	    mockMvc.perform(delete("/rest/fattura/delete/99").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	    
	    token = getBearerToken("AdminUser");
	    mockMvc.perform(delete("/rest/fattura/delete/999").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").value(msgS.get("!exists_fat")));

	    mockMvc.perform(delete("/rest/fattura/delete/2").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value(msgS.get("rest_deleted")));

    }
    
        
    public void list() throws Exception{
    	// nw in delete rimuovo 2
    	// Forbidden
    	String token = getBearerToken("UserUser");
	    mockMvc.perform(get("/rest/fattura/list").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isForbidden());
	    

    	// no filtri
	    token = getBearerToken("AdminUser");
		mockMvc.perform(get("/rest/fattura/list").with(csrf())
				.header("Authorization", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
    	

	    
		String numeroFattura = "FAT-1-A1B2C3D4";
        mockMvc.perform(get("/rest/fattura/list")
                .param("numeroFattura", numeroFattura)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

		String from = LocalDate.now().minusDays(1).toString();
        mockMvc.perform(get("/rest/fattura/list")
                .param("from", from)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());


        String to = LocalDate.now().toString();
        mockMvc.perform(get("/rest/fattura/list")
                .param("to", to)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

		String clienteNome = "Mario";
        mockMvc.perform(get("/rest/fattura/list")
                .param("clienteNome", clienteNome)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

		String clienteCognome = "Rossi";
        mockMvc.perform(get("/rest/fattura/list")
                .param("clienteCognome", clienteCognome)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());


		String clienteEmail = "mario.rossi@email.com";
        mockMvc.perform(get("/rest/fattura/list")
                .param("clienteEmail", clienteEmail)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());


		String tipoPagamento = "PAYPAL";
        mockMvc.perform(get("/rest/fattura/list")
                .param("tipoPagamento", tipoPagamento)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

		String tipoSpedizione = "STANDARD";
        mockMvc.perform(get("/rest/fattura/list")
                .param("tipoSpedizione", tipoSpedizione)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        String statoFattura = "CONSEGNATO";
        mockMvc.perform(get("/rest/fattura/list")
                .param("statoFattura", statoFattura)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        Integer idOrdine = 1;
        mockMvc.perform(get("/rest/fattura/list")
                .param("idOrdine", idOrdine.toString())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/rest/fattura/list")
                .param("isbns", "ISBN001")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

    }
    

}
