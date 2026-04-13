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
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.RigaFattura;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.repositories.IFatturaRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
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
	private final IMangaRepository mangaR;
	
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(RigaFatturaRequest req) throws MangaException {
		log.debug("creating RigaFattura {}", req);
		
		 if (req.getIdFattura() == null)         
		        throw new MangaException("null_fat");

        Fattura fat = fattR.findById(req.getIdFattura()).orElseThrow(() ->
                new MangaException("!exists_fat"));
        List<String> isbns = fat.getRighe().stream()
        		.map(r -> r.getIsbn()).toList();

        if (Utils.isBlank(req.getIsbn()))
            throw new MangaException("null_isn");
        Manga m = mangaR.findByIsbn(Utils.normalize(req.getIsbn()))
        		.orElseThrow(() -> new MangaException("!exists_man"));

        if (isbns.contains(m.getIsbn())) {
        	throw new MangaException("exists_rfa");
        }
        
        if (req.getNumeroCopie() == null || req.getNumeroCopie() < 1)
            throw new MangaException("null_qua");

        RigaFattura r = new RigaFattura();
        r.setFattura(fat);
        r.setIsbn(m.getIsbn());
        r.setTitolo(m.getTitolo());
        r.setPrezzoUnitario(m.getPrezzo());
        if (req.getPrezzoUnitario() != null) {
        	r.setPrezzoUnitario(req.getPrezzoUnitario());
        }
        
        r.setNumeroCopie(req.getNumeroCopie());
		Integer left = m.getNumeroCopie() - req.getNumeroCopie();
		m.setNumeroCopie(left);
		mangaR.save(m);

        r.setTotaleRiga(
        	    r.getPrezzoUnitario()
        	       .multiply(BigDecimal.valueOf(r.getNumeroCopie()))
        	);
        
        List<RigaFattura> lR = fat.getRighe();
        
        if (!lR.contains(r)) {
        	fat.getRighe().add(r);
        }
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

        if (req.getIdFattura() != null) {
            Fattura fat = fattR.findById(req.getIdFattura()).orElseThrow(() ->
                    new MangaException("!exists_fat"));
            r.setFattura(fat);
        }

        if (!Utils.isBlank(req.getIsbn())) {
        	Manga m = mangaR.findByIsbn(Utils.normalize(req.getIsbn())).orElseThrow(() ->
        			new MangaException("!exists_man"));
            r.setIsbn(m.getIsbn());
            r.setTitolo(m.getTitolo());
            r.setPrezzoUnitario(m.getPrezzo());
            
        }
        if (req.getPrezzoUnitario() != null &&
            req.getPrezzoUnitario().compareTo(BigDecimal.ZERO) > 0)
            r.setPrezzoUnitario(req.getPrezzoUnitario());

        if (req.getNumeroCopie() != null && req.getNumeroCopie() >= 1) {
        	if (req.getNumeroCopie() != r.getNumeroCopie()) {
        		Manga m = mangaR.findByIsbn(Utils.normalize(r.getIsbn())).orElseThrow(() ->
						new MangaException("!exists_man"));
        		Integer d = req.getNumeroCopie() - r.getNumeroCopie();
        		Integer left = m.getNumeroCopie() - d;
        		m.setNumeroCopie(left);
        		r.setNumeroCopie(req.getNumeroCopie());
        		mangaR.save(m);
        	}
        }
        
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
	public RigaFattura saveOrUpdateFromRigaOrdine(RigaOrdine ro, Fattura f) {
		String isbn = ro.getManga().getIsbn();
		List<RigaFattura> lRF = f.getRighe();
		List<String> foundIsbsns = lRF.stream()
				.map(rf -> rf.getIsbn())
				.toList();
		RigaFattura rfUpdate = mirrorRigaOrdine(ro);

		if (foundIsbsns.contains(isbn)) {
			// ottengo id di riga fattura per update
			Integer myId = lRF.stream()
					.filter(rf -> rf.getIsbn().equals(rfUpdate.getIsbn()))
					.map(rf -> rf.getId())
					.findFirst().get();
			rfUpdate.setId(myId);
			rfUpdate.setFattura(f);
			RigaFattura saved = rigR.save(rfUpdate);
			return saved;
		}
		
		rfUpdate.setFattura(f);
		RigaFattura saved = rigR.save(rfUpdate);
		return saved;
	}

	
	@Transactional(rollbackFor = Exception.class)
	public void righeFatturaFromRigheOrdine(List<RigaOrdine> lO, Fattura f) {
		if (lO.size()>0) {
			List<RigaFattura> lF =  lO.stream()
				.map(ro -> saveOrUpdateFromRigaOrdine(ro, f))
				.collect(Collectors.toList());

			List<String> newIsbns = lF.stream()
		            .map(rf -> rf.getIsbn())
		            .toList();
	        List<RigaFattura> toKeep = f.getRighe().stream()
	                .filter(rf -> !newIsbns.contains(rf.getIsbn()))
	                .collect(Collectors.toList());

	        f.getRighe().clear();
	        f.getRighe().addAll(toKeep);
	        f.getRighe().addAll(lF);

	        Utils.ricalcolaTotale(f);
	        fattR.saveAndFlush(f);
		}
		return;
	}
	
}
