package com.betacom.ecommerce.backend.services.implementations;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.RigaFatturaRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaFatturaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.RigaFattura;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaFatturaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IRigaFatturaServices;
import com.betacom.ecommerce.backend.utilities.DtoBuildres;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RigaFatturaImplementation implements IRigaFatturaServices{
	
	private final IRigaFatturaRepository rigR;
	private final IFatturaRepository fattR;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(RigaFatturaRequest req) throws MangaException {
		log.debug("creating RigaFattura {}", req);
		
		 if (req.getIdFattura() == null)         
		        throw new MangaException("null_fat");

        Fattura fat = fattR.findById(req.getIdFattura()).orElseThrow(() ->
                new MangaException("!exists_fat"));

        if (Utils.isBlank(req.getIsbn()))
            throw new MangaException("null_isn");
        
        if (Utils.isBlank(req.getTitolo()))
            throw new MangaException("null_tit");
        
        if (Utils.isBlank(req.getAutore()))
            throw new MangaException("null_aut");

        if (req.getPrezzoUnitario() == null ||
            req.getPrezzoUnitario().compareTo(BigDecimal.ZERO) <= 0)
            throw new MangaException("null_pre");

        if (req.getQuantita() == null || req.getQuantita() < 1)
            throw new MangaException("null_qua");

        RigaFattura r = new RigaFattura();
        r.setIdFattura(fat);
        r.setIsbn(Utils.normalize(req.getIsbn()));
        r.setTitolo(Utils.normalize(req.getTitolo()));
        r.setAutore(Utils.normalize(req.getAutore()));
        r.setPrezzoUnitario(req.getPrezzoUnitario());
        r.setQuantita(req.getQuantita());
        r.setTotaleRiga(
        	    req.getPrezzoUnitario()
        	       .multiply(BigDecimal.valueOf(req.getQuantita()))
        	);
        fat.getRighe().add(r);
        Utils.ricalcolaTotale(fat);
        fattR.save(fat);
	}
	
	 
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(RigaFatturaRequest req) throws MangaException {
		log.debug("updating RigaFattura {}", req);

        if (req.getId() == null)
            throw new MangaException("null_rig");
        RigaFattura r = rigR.findById(req.getId()).orElseThrow(() ->
                new MangaException("!exists_rig_fat"));

        if (req.getId() != null) {
            Fattura fat = fattR.findById(req.getIdFattura()).orElseThrow(() ->
                    new MangaException("!exists_fat"));
            r.setIdFattura(fat);
        }

        if (!Utils.isBlank(req.getTitolo()))
            r.setTitolo(Utils.normalize(req.getTitolo()));

        if (!Utils.isBlank(req.getIsbn()))
            r.setIsbn(Utils.normalize(req.getIsbn()));

        if (!Utils.isBlank(req.getAutore()))
            r.setAutore(Utils.normalize(req.getAutore()));

        if (req.getPrezzoUnitario() != null &&
            req.getPrezzoUnitario().compareTo(BigDecimal.ZERO) > 0)
            r.setPrezzoUnitario(req.getPrezzoUnitario());

        if (req.getQuantita() != null && req.getQuantita() >= 1)
            r.setQuantita(req.getQuantita());
        
     //ricalcola totaleRiga solo se entrambi i valori sono presenti sulla entity
        if (r.getPrezzoUnitario() != null && r.getQuantita() != null) {
            r.setTotaleRiga(
                r.getPrezzoUnitario()
                 .multiply(BigDecimal.valueOf(r.getQuantita()))
            );
        }
        Utils.ricalcolaTotale(r.getIdFattura());
        rigR.save(r);
    }
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("removing RigaFattura with id {}", id);

        if (id == null)
            throw new MangaException("null_rig");

        RigaFattura r = rigR.findById(id).orElseThrow(() ->
                new MangaException("!exists_rig_fat"));

        Fattura fat = r.getIdFattura();
        fat.getRighe().remove(r);         // orphanRemoval=true la cancella
        Utils.ricalcolaTotale(fat);
        fattR.saveAndFlush(fat);      
        rigR.delete(r);
		
	}
	@Override
	@Transactional(readOnly = true)
	public List<RigaFatturaDTO> list() {
		log.debug("RigaFattura list()");
        List<RigaFattura> lR = rigR.findAll();
        return lR.stream()
                .map(r -> DtoBuildres.buildRigaFatturaDTO(r, true))
                .collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public RigaFatturaDTO findById(Integer id) throws MangaException {
		log.debug("RigaFattura findById({})", id);

        if (id == null)
            throw new MangaException("null_rig");

        RigaFattura r = rigR.findById(id).orElseThrow(() ->
                new MangaException("!exists_rig"));
        return DtoBuildres.buildRigaFatturaDTO(r, true);
	}
	
}
