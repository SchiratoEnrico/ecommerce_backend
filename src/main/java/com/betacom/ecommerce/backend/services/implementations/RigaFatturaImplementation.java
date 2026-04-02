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
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaFatturaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IRigaFatturaServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
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
        
//        if (Utils.isBlank(req.getAutore()))
//            throw new MangaException("null_aut");

        if (req.getPrezzoUnitario() == null ||
            req.getPrezzoUnitario().compareTo(BigDecimal.ZERO) <= 0)
            throw new MangaException("null_pre");

        if (req.getNumeroCopie() == null || req.getNumeroCopie() < 1)
            throw new MangaException("null_qua");

        RigaFattura r = new RigaFattura();
        r.setFattura(fat);
        r.setIsbn(Utils.normalize(req.getIsbn()));
        r.setTitolo(Utils.normalize(req.getTitolo()));
//        r.setAutore(Utils.normalize(req.getAutore()));
        r.setPrezzoUnitario(req.getPrezzoUnitario());
        r.setNumeroCopie(req.getNumeroCopie());
        r.setTotaleRiga(
        	    req.getPrezzoUnitario()
        	       .multiply(BigDecimal.valueOf(req.getNumeroCopie()))
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
            r.setFattura(fat);
        }

        if (!Utils.isBlank(req.getTitolo()))
            r.setTitolo(Utils.normalize(req.getTitolo()));

        if (!Utils.isBlank(req.getIsbn()))
            r.setIsbn(Utils.normalize(req.getIsbn()));

//      non credo ci interessi avere autore in fattura, abbiamo isbn  
//      if (!Utils.isBlank(req.getAutore()))
//            r.setAutore(Utils.normalize(req.getAutore()));

        if (req.getPrezzoUnitario() != null &&
            req.getPrezzoUnitario().compareTo(BigDecimal.ZERO) > 0)
            r.setPrezzoUnitario(req.getPrezzoUnitario());

        if (req.getNumeroCopie() != null && req.getNumeroCopie() >= 1)
            r.setNumeroCopie(req.getNumeroCopie());
        
     //ricalcola totaleRiga solo se entrambi i valori sono presenti sulla entity
        if (r.getPrezzoUnitario() != null && r.getNumeroCopie() != null) {
            r.setTotaleRiga(
                r.getPrezzoUnitario()
                 .multiply(BigDecimal.valueOf(r.getNumeroCopie()))
            );
        }
        Utils.ricalcolaTotale(r.getFattura());
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

        Fattura fat = r.getFattura();
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
                .map(r -> DtoBuilders.buildRigaFatturaDTO(r, fattR.findById(r.getFattura().getId())))
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
        return DtoBuilders.buildRigaFatturaDTO(r, fattR.findById(r.getFattura().getId()));
	}
	
	private RigaFattura mirrorRigaOrdine(RigaOrdine ro) {
		RigaFattura rf = new RigaFattura();

		rf.setIsbn(ro.getManga().getIsbn());
		rf.setTitolo(ro.getManga().getTitolo());
		rf.setPrezzoUnitario(ro.getPrezzo());
		rf.setNumeroCopie(ro.getNumeroCopie());
		rf.setTotaleRiga(rf.getPrezzoUnitario()
     	       .multiply(BigDecimal.valueOf(rf.getNumeroCopie())));
		return rf;
		
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void righeFatturaFromRigheOrdine(List<RigaOrdine> lO, Fattura f) {
		List<RigaFattura> lF = lO.stream()
									.map(ro -> mirrorRigaOrdine(ro))
									.toList();
		lF.forEach(rf -> {
			rf.setFattura(f);
			rigR.save(rf);
			f.getRighe().add(rf);
		});
		Utils.ricalcolaTotale(f);
        fattR.saveAndFlush(f);
		return;
	}
	
}
