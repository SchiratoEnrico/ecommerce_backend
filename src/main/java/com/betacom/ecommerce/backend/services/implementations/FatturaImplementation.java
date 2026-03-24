package com.betacom.ecommerce.backend.services.implementations;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.FatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.FatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FatturaImplementation implements IFatturaServices{

	private final IFatturaRepository fattR;
	 
	@Override
    @Transactional(rollbackFor = Exception.class)
	public void create(FatturaRequest req) throws MangaException {
		log.debug("Create Fattura: {}", req);

		if (Utils.isBlank(req.getNumeroFattura()))
            throw new MangaException("null_num_fat");
		
        //validazione cliente
		if (Utils.isBlank(req.getClienteNome()))
            throw new MangaException("null_nom");

        if (Utils.isBlank(req.getClienteCognome()))
            throw new MangaException("null_cog");

        if (Utils.isBlank(req.getClienteEmail()))
            throw new MangaException("null_ema");

        if (Utils.isBlank(req.getClienteIndirizzo()))
            throw new MangaException("null_ind");

        if (Utils.isBlank(req.getClienteCitta()))
            throw new MangaException("null_cit");

        if (Utils.isBlank(req.getClienteCap()))
            throw new MangaException("null_cap");

        if (Utils.isBlank(req.getClienteProvincia()))
            throw new MangaException("null_pro");

        if (Utils.isBlank(req.getClienteStato()))
            throw new MangaException("null_sta");

        //validazione pag e sped
        if (Utils.isBlank(req.getTipoPagamento()))
            throw new MangaException("null_pag");

        if (Utils.isBlank(req.getTipoSpedizione()))
            throw new MangaException("null_spe");

        if (req.getRigheFatturaRequest() == null || req.getRigheFatturaRequest().isEmpty())
            throw new MangaException("null_rig_fat");

        Fattura fat = new Fattura();
        fat.setNumeroFattura(Utils.normalize(req.getNumeroFattura()));
        // Snapshot cliente
        fat.setClienteNome(Utils.normalize(req.getClienteNome()));
        fat.setClienteCognome(Utils.normalize(req.getClienteCognome()));
        fat.setClienteEmail(Utils.normalize(req.getClienteEmail()));
        fat.setClienteIndirizzo(Utils.normalize(req.getClienteIndirizzo()));
        fat.setClienteCitta(Utils.normalize(req.getClienteCitta()));
        fat.setClienteCap(Utils.normalize(req.getClienteCap()));
        fat.setClienteProvincia(Utils.normalize(req.getClienteProvincia()));
        fat.setClienteStato(Utils.normalize(req.getClienteStato()));

        // Pagamento e spedizione
        fat.setTipoPagamento(Utils.normalize(req.getTipoPagamento()));
        fat.setTipoSpedizione(Utils.normalize(req.getTipoSpedizione()));

        // Costi
        fat.setCostoSpedizione(req.getCostoSpedizione() != null 
        		? req.getCostoSpedizione()
                : BigDecimal.ZERO
        );
        fat.setTotale(fat.getCostoSpedizione()); // totale iniziale, le righe lo aggiorneranno

        fat.setNote(req.getNote());

        fattR.save(fat);
        
     }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(FatturaRequest req) throws MangaException {
		 log.debug("updating Fattura {}", req);
		 
	        Fattura fat = fattR.findById(req.getId()).orElseThrow(() ->
	                new MangaException("!exists_fat"));
	        // Dati fattura
	        if (!Utils.isBlank(req.getNumeroFattura()))
	            fat.setNumeroFattura(Utils.normalize(req.getNumeroFattura()));

	        // Snapshot cliente
	        if (!Utils.isBlank(req.getClienteNome()))
	            fat.setClienteNome(Utils.normalize(req.getClienteNome()));

	        if (!Utils.isBlank(req.getClienteCognome()))
	            fat.setClienteCognome(Utils.normalize(req.getClienteCognome()));

	        if (!Utils.isBlank(req.getClienteEmail()))
	            fat.setClienteEmail(Utils.normalize(req.getClienteEmail()));

	        if (!Utils.isBlank(req.getClienteIndirizzo()))
	            fat.setClienteIndirizzo(Utils.normalize(req.getClienteIndirizzo()));

	        if (!Utils.isBlank(req.getClienteCitta()))
	            fat.setClienteCitta(Utils.normalize(req.getClienteCitta()));

	        if (!Utils.isBlank(req.getClienteCap()))
	            fat.setClienteCap(Utils.normalize(req.getClienteCap()));

	        if (!Utils.isBlank(req.getClienteProvincia()))
	            fat.setClienteProvincia(Utils.normalize(req.getClienteProvincia()));

	        if (!Utils.isBlank(req.getClienteStato()))
	            fat.setClienteStato(Utils.normalize(req.getClienteStato()));

	        // Pagamento e spedizione
	        if (!Utils.isBlank(req.getTipoPagamento()))
	            fat.setTipoPagamento(Utils.normalize(req.getTipoPagamento()));

	        if (!Utils.isBlank(req.getTipoSpedizione()))
	            fat.setTipoSpedizione(Utils.normalize(req.getTipoSpedizione()));

	        // Costi
	        if (req.getCostoSpedizione() != null) {
	            fat.setCostoSpedizione(req.getCostoSpedizione());
	            Utils.ricalcolaTotale(fat); // aggiorna totale se cambia spedizione
	        }

	        if (!Utils.isBlank(req.getNote()))
	            fat.setNote(req.getNote());

	        fattR.save(fat);
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		 log.debug("removing Fattura with id {}", id);
	        if (id == null)
	            throw new MangaException("null_fat");

	        Fattura fat = fattR.findById(id).orElseThrow(() ->
	                new MangaException("!exists_fat"));
	        fattR.delete(fat);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FatturaDTO> list() {
		log.debug("Fattura list()");
        List<Fattura> lF = fattR.findAll();
        return lF.stream()
                .map(f -> DtoBuildres.buildFatturaDTO(f, true))
                .collect(Collectors.toList());

	}

	@Override
	@Transactional(readOnly = true)
	public FatturaDTO findById(Integer id) throws MangaException {
		log.debug("Fattura findById({})", id);

        if (id == null)
            throw new MangaException("null_fat");

        Fattura fat = fattR.findById(id).orElseThrow(() ->
                new MangaException("!exists_fat"));
        return DtoBuildres.buildFatturaDTO(fat, true);
    }

	

}
