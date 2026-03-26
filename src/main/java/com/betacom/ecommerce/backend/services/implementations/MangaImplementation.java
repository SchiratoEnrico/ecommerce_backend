package com.betacom.ecommerce.backend.services.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Saga;
import com.betacom.ecommerce.backend.repositories.IAutoreRepository;
import com.betacom.ecommerce.backend.repositories.ICasaEditriceRepository;
import com.betacom.ecommerce.backend.repositories.IGenereRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaCarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.repositories.ISagaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMangaServices;
import com.betacom.ecommerce.backend.specification.MangaSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.ReqValidators;
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
	private final ISagaRepository sagaRepo;
	
	@Override
	@Transactional
	public void create(MangaRequest req) throws MangaException {
		log.debug("Begin creating manga {}", req);
		// da aggiungere null_sag
		ReqValidators.validateMangaRequest(req, true); 
		log.debug("Manga validated...");
		
		if(checkDuplicateManga(req.getIsbn()))
			throw new MangaException("exists_man");
				
		Manga m = ReqValidators.buildManga(new Manga(), req, true);
		
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
		
		// throws !exists_sag, exists_sagvol, null_sagvol
		m = validateSagaVol(m, req.getSagaVol(), req.getSaga());

		log.debug(m.toString());
		mangaRepo.save(m);
		log.debug("manga saved in db successfully");
	}

	@Override
	@Transactional
	public void update(MangaRequest req) throws MangaException {
	    log.debug("begin updating manga  isbn {}", req);

	    ReqValidators.validateMangaRequest(req, false);

	    Manga m = mangaRepo.findById(req.getIsbn())
	            .orElseThrow(() -> new MangaException("!exists_man"));

	    ReqValidators.buildManga(m, req, false);
	    if (req.getSaga() != null || req.getSagaVol() != null) {
	    	m = validateSagaVol(m,
				req.getSagaVol()==null?
						m.getSagaVol():req.getSagaVol(),
				req.getSaga()==null?
						m.getSaga().getId():req.getSaga()
						);
		}
	    mangaRepo.save(m);

	    log.debug("manga updated successfully");
	}

	@Override
	@Transactional(readOnly = true)
	public MangaDTO findByIsbn(String isbn) throws MangaException {
		log.debug("begin find manga by isbn {}", isbn);
		Manga m = mangaRepo.findByIsbn(Utils.normalize(isbn))
				.orElseThrow(()-> new MangaException("!exists_man"));
		List<Autore> a = m.getAutori();
		List<Genere> g = m.getGeneri();
		CasaEditrice c = m.getCasaEditrice();
		Saga s = m.getSaga();
		return DtoBuilders.buildMangaDTO(m, Optional.ofNullable(c), Optional.ofNullable(a), Optional.ofNullable(g), Optional.ofNullable(s));
	}

	private MangaDTO callBuilder(Manga m) {
		if (m.getSaga() == null) {
			
		}
		return DtoBuilders.buildMangaDTO(m,  
				Optional.ofNullable(m.getCasaEditrice()), 
				Optional.ofNullable(m.getAutori()), 
				Optional.ofNullable(m.getGeneri()),
				Optional.ofNullable(m.getSaga())
				);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MangaDTO> list(
			String titolo,
			String casaEditriceNome,
			String autoreNome,
			String sagaNome,
			Integer sagaId,
			Integer casaEditriceId,
			Integer autoreId,
			List<Integer> generiId
			) throws MangaException {
		log.debug("begin userlist manga");
		
		Specification<Manga> spec = Specification
		        .where(MangaSpecifications.distinct())
				.and(MangaSpecifications.sagaIdEquals(sagaId))
				.and(MangaSpecifications.casaEditriceIdEquals(casaEditriceId))
				.and(MangaSpecifications.autoreIdEquals(autoreId))
				.and(MangaSpecifications.generiIdEqual(generiId))
				.and(MangaSpecifications.titoloLike(titolo))
				.and(MangaSpecifications.casaEditriceNomeLike(casaEditriceNome))
				.and(MangaSpecifications.autoreNomeLike(autoreNome))
				.and(MangaSpecifications.sagaNomeLike(sagaNome))
				;
		List<Manga> lM = mangaRepo.findAll(spec);
		return lM.stream()
				.map(m-> callBuilder(m))
				.toList();
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
	
	private Saga createProxySaga(Manga m){
		Saga s = new Saga();
		s.setNome(m.getTitolo());
		s.setDescrizione(m.getTitolo());
		s.setImmagine(m.getImmagine());
		sagaRepo.save(s);
		
		return s;
	}
	
	private Manga validateSagaVol(Manga m, Integer vol, Integer sagaId) {
		log.debug("checking validity of provided saga: {}", sagaId);
		if (sagaId == null) {
			if (vol != null) {
				
			}
			Saga s = createProxySaga(m);
			m.setSaga(s);
			m.setSagaVol(1);
			return m;
		}
		
		Saga s = sagaRepo.findById(sagaId).orElseThrow(() ->
				new MangaException("!exists_sag"));
		
		if (vol == null) {
			throw new MangaException("null_sagvol");
		}
		log.debug("checking validity of new volume saga insertion, vol {}", vol);
		if (mangaRepo.existsBySagaIdAndSagaVol(sagaId, vol)) {
			throw new MangaException("exists_sagvol");
		}
		
		m.setSaga(s);
		m.setSagaVol(vol);
		return m;
	}
}
