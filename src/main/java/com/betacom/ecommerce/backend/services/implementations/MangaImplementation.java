package com.betacom.ecommerce.backend.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.repositories.IAutoreRepository;
import com.betacom.ecommerce.backend.repositories.ICasaEditriceRepository;
import com.betacom.ecommerce.backend.repositories.IGenereRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaCarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMangaServices;
import com.betacom.ecommerce.backend.utilities.MangaUtils;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MangaImplementation implements IMangaServices{
	
	private final IMangaRepository mangaRepo;
	private final IAutoreRepository autRepo;
	private final IGenereRepository genRepo;
	private final ICasaEditriceRepository casRepo;
	private final IRigaOrdineRepository rigaOrdineRepo;
	private final IRigaCarrelloRepository rigaCarRepo;
	
	@Override
	@Transactional
	public void create(MangaRequest req) throws MangaException {
		log.debug("Begin creating manga {}", req);
		
		MangaUtils.validateRequest(req, true); 
		log.debug("Manga validated...");
		
		if(checkDuplicateManga(req.getIsbn()))
			throw new MangaException("exists_man");
		
		Manga m = MangaUtils.buildManga(new Manga(), req, true);
		
		
		log.debug("Autori -> " + req.getAutori());
		log.debug("Generi -> " + req.getGeneri());
		log.debug("Casa editrice -> " + req.getCasaEditrice());
		List<Autore> lA = autRepo.findAllById(req.getAutori());
		log.debug("Autori trovati -> " + lA);
		m.setAutori(new ArrayList<Autore>(lA));
		
		if(lA.size()<req.getAutori().size()) 
			throw new MangaException("!exists_aut");
		
		List<Genere> lG = genRepo.findAllById(req.getGeneri());
		m.setGeneri(new ArrayList<Genere>(lG));
		
		if(lG.size()<req.getGeneri().size())
			throw new MangaException("!exists_gen");
		
		CasaEditrice c = casRepo.findById(req.getCasaEditrice())
				.orElseThrow(()-> new MangaException("!exists_ced"));
		m.setCasaEditrice(c);
		
		log.debug(m.toString());
		mangaRepo.save(m);
		log.debug("manga saved in db successfully");
	}

	@Override
	@Transactional
	public void update(MangaRequest req) throws MangaException {
	    log.debug("begin updating manga  isbn {}", req);

	    MangaUtils.validateRequest(req, false);

	    Manga m = mangaRepo.findById(req.getIsbn())
	            .orElseThrow(() -> new MangaException("!exists_man"));

	    MangaUtils.buildManga(m, req, false);

	    mangaRepo.save(m);

	    log.debug("manga updated successfully");
	}

	@Override
	@Transactional(readOnly = true)
	public MangaDTO findByIsbn(String isbn) throws MangaException {
		log.debug("begin find manga by isbn {}", isbn);
		Manga m = mangaRepo.findByIsbn(Utils.normalize(isbn))
				.orElseThrow(()-> new MangaException("!exists_man"));
		
		return MangaUtils.buildMangaDTO(m);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MangaDTO> list() throws MangaException {
		log.debug("begin find all manga");
		
		List<Manga> lM = mangaRepo.findAll();
		
		return lM.stream().map(m->	MangaUtils.buildMangaDTO(m)).toList();
	}

	@Override
	@Transactional(rollbackFor = MangaException.class)
	public void delete(String isbn) throws MangaException {
	    String key = Utils.normalize(isbn);
	    log.debug("begin delete manga isbn {}", key);

	    Manga m = mangaRepo.findById(key)
	            .orElseThrow(() -> new MangaException("!exists_man"));

	    if (rigaOrdineRepo.existsByMangaIsbn(key)) {
	        log.debug("manga {} linked to righe ordine", key);
	        throw new MangaException("linked_ord");
	    }

	    if (rigaCarRepo.existsByMangaIsbn(key)) {
	        log.debug("manga {} linked to carrello", key);
	        throw new MangaException("linked_car");
	    }

	    for (Autore a : new ArrayList<>(m.getAutori())) {
	        a.getManga().remove(m);
	        m.getAutori().remove(a);
	    }

	    for (Genere g : new ArrayList<>(m.getGeneri())) {
	        g.getManga().remove(m);
	        m.getGeneri().remove(g);
	    }

	    mangaRepo.saveAndFlush(m);
	    mangaRepo.delete(m);

	    log.debug("manga deleted successfully");
	}

	private Boolean checkDuplicateManga(String isbn) {
		log.debug("checking duplicate manga {}", isbn);
		return mangaRepo.existsById(isbn);
	}
}
