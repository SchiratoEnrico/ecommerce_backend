package com.betacom.ecommerce.backend.services.implementations;

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
import com.betacom.ecommerce.backend.services.interfaces.IMangaServices;
import com.betacom.ecommerce.backend.utilities.MangaUtils;

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
	
	@Override
	@Transactional
	public void create(MangaRequest req) throws MangaException {
		log.debug("Begin creating manga {}", req);
		
		MangaUtils.validateRequest(req, true);
		log.debug("Manga validated...");
		
		if(checkDuplicateManga(req.getIsbn()))
			throw new MangaException("exists_man");
		
		Manga m = MangaUtils.buildManga(new Manga(), req, true);
		
		List<Autore> lA = autRepo.findAllById(req.getAutori());
		m.setAutori(lA);
		
		if(lA.size()<req.getAutori().size())
			throw new MangaException("!exists_aut");
		
		List<Genere> lG = genRepo.findAllById(req.getGeneri());
		m.setGeneri(lG);
		
		if(lG.size()<req.getGeneri().size())
			throw new MangaException("!exists_gen");
		
		CasaEditrice c = casRepo.findById(req.getCasaEditrice())
				.orElseThrow(()-> new MangaException("!exists_ced"));
		
		mangaRepo.save(m);
		log.debug("manga saved in db successfully");
	}

	@Override
	@Transactional
	public void update(MangaRequest req) throws MangaException {
		log.debug("begin updating manga  isbn {}", req.getIsbn());
		
		MangaUtils.validateRequest(req, false);
		
		Manga m = mangaRepo.findById(req.getIsbn())
				.orElseThrow(()-> new MangaException("!exists_man"));
		
		MangaUtils.buildManga(m, req, false);
		
		log.debug("builded manga");
		
		if(checkDuplicateManga(m.getIsbn()))
			throw new MangaException("exists_man");
		
		mangaRepo.save(m);
		
		log.debug("manga updated successfully");
		
	}

	@Override
	public MangaDTO findByIsbn(String isbn) throws MangaException {
		log.debug("begin find manga by isbn {}", isbn);
		
		Manga m = mangaRepo.findById(isbn)
				.orElseThrow(()-> new MangaException("!exists_man"));
		
		return MangaUtils.buildMangaDTO(m);
	}

	@Override
	public List<MangaDTO> list() throws MangaException {
		log.debug("begin find all manga");
		
		List<Manga> lM = mangaRepo.findAll();
		
		return lM.stream().map(m->	MangaUtils.buildMangaDTO(m)).toList();
	}

	@Override
	public void delete(String isbn) throws MangaException {
		
	}

	private Boolean checkDuplicateManga(String isbn) {
		log.debug("checking duplicate manga {}", isbn);
		return mangaRepo.existsById(isbn);
	}
}
