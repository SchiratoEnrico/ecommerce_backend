package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.AnagraficaRequest;
import com.betacom.ecommerce.backend.dto.outputs.AnagraficaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.IAnagraficaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IAnagraficaServices;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnagraficaImplementation implements IAnagraficaServices{

	private final IAnagraficaRepository repAna;
	private final IAccountRepository repAcc;
	
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void create(AnagraficaRequest req) throws MangaException {
	    log.debug("Create Account", req);
	    
	    //check sull'account
	    Account acc = repAcc.findById(req.getIdAccount()).orElseThrow(()-> new MangaException("!exists_acc"));
	    
	    if (Utils.isBlank(req.getNome()))
	        throw new MangaException("null_nom");

	    if (Utils.isBlank(req.getCognome()))
	        throw new MangaException("null_cog");

	    if (Utils.isBlank(req.getStato()))
	        throw new MangaException("null_sta");

	    if (Utils.isBlank(req.getCitta()))
	        throw new MangaException("null_cit");

	    if (Utils.isBlank(req.getProvincia()))
	        throw new MangaException("null_pro");

	    if (Utils.isBlank(req.getCap()))
	        throw new MangaException("null_cap");

	    if (Utils.isBlank(req.getVia()))
	        throw new MangaException("null_via");

	    if (req.getPredefinito() == null)
	        throw new MangaException("null_pre");

	    Anagrafica an = new Anagrafica();
	    an.setNome(Utils.normalize(req.getNome()));
	    an.setCognome(Utils.normalize(req.getCognome()));
	    an.setStato(Utils.normalize(req.getStato()));
	    an.setCitta(Utils.normalize(req.getCitta()));
	    an.setProvincia(Utils.normalize(req.getProvincia()));
	    an.setCap(Utils.normalize(req.getCap()));
	    an.setVia(Utils.normalize(req.getVia()));
	    an.setPredefinito(false);
	    an.setAccount(acc);

	    repAna.save(an);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void delete(Integer id) throws MangaException {
		log.debug("Delete Account, id: {}", id);
        
		Anagrafica ana = repAna.findById(id)
				.orElseThrow(() -> new MangaException("null_ana"));
		repAna.delete(ana);
	}

	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(AnagraficaRequest req) throws MangaException {
	    log.debug("Update Account: ", req);

	    Anagrafica ana = repAna.findById(req.getId())
	            .orElseThrow(() -> new MangaException("null_ana"));

	    if (!Utils.isBlank(req.getNome()))
	        ana.setNome(Utils.normalize(req.getNome()));

	    if (!Utils.isBlank(req.getCognome()))
	        ana.setCognome(Utils.normalize(req.getCognome()));

	    if (!Utils.isBlank(req.getStato()))
	        ana.setStato(Utils.normalize(req.getStato()));

	    if (!Utils.isBlank(req.getCitta()))
	        ana.setCitta(Utils.normalize(req.getCitta()));

	    if (!Utils.isBlank(req.getProvincia()))
	        ana.setProvincia(Utils.normalize(req.getProvincia()));

	    if (req.getCap() != null)
	        ana.setCap(req.getCap());

	    if (!Utils.isBlank(req.getVia()))
	        ana.setVia(Utils.normalize(req.getVia()));

	    if (req.getPredefinito() != null)
	        ana.setPredefinito(req.getPredefinito());
	    
	    repAna.save(ana);
	}

	@Override
	public List<AnagraficaDTO> list() {
	    log.debug("findAll() Anagrafica");

	    return repAna.findAll().stream()
	            .map(a -> DtoBuilders.buildAnagraficaDTO(a))
	            .toList();
	}

	@Override
	public AnagraficaDTO findById(Integer id) throws MangaException {
	    log.debug("findById() Anagrafica {}", id);

	    Anagrafica a = repAna.findById(id)
	            .orElseThrow(() -> new MangaException("!exists_ana"));

	    return DtoBuilders.buildAnagraficaDTO(a);
	}

	@Override
	public List<AnagraficaDTO> findByAccountId(Integer id) throws MangaException {
		log.debug("findind anagrafiche di account {}", id);
		
		List<Anagrafica> lA = repAna.findByAccountId(id);
		
		return DtoBuilders.buildAnagraficaDTO(lA);
	}
	
}
