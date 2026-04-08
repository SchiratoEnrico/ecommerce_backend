package com.betacom.ecommerce.backend.services.implementations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.MangaRequest;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Fattura;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.Saga;
import com.betacom.ecommerce.backend.repositories.IAutoreRepository;
import com.betacom.ecommerce.backend.repositories.ICasaEditriceRepository;
import com.betacom.ecommerce.backend.repositories.IGenereRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaCarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IRigaOrdineRepository;
import com.betacom.ecommerce.backend.repositories.ISagaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMangaServices;
import com.betacom.ecommerce.backend.services.interfaces.IUploadServices;
import com.betacom.ecommerce.backend.specification.MangaSpecifications;
import com.betacom.ecommerce.backend.utilities.ImageDtoBuilders;
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
	private final ImageDtoBuilders imgB;
	private final IUploadServices uplS;
	
	
	@Override
	@Transactional
	public void create(MangaRequest req) throws MangaException {
		log.debug("Begin creating manga {}", req);

		ReqValidators.validateMangaRequest(req, true); 
		log.debug("Manga validated...");
		
		if(checkDuplicateManga(req.getIsbn()))
			throw new MangaException("exists_man");
				
		Manga m = ReqValidators.buildManga(new Manga(), req, true);
		
		log.debug("Autori -> " + req.getAutori());
		log.debug("Generi -> " + req.getGeneri());
		log.debug("Casa editrice -> " + req.getCasaEditrice());
		List<Autore> lA = autRepo.findAllById(req.getAutori());
		if(lA.size()<req.getAutori().size()) 
			throw new MangaException("!exists_aut");

		log.debug("Autori trovati -> " + lA);
		Set<Autore> sA = new HashSet<Autore>();
		Boolean added = sA.addAll(lA);
		if (added) {
			m.setAutori(sA);
		}
		
		
		List<Genere> lG = genRepo.findAllById(req.getGeneri());
		if (lG.size()<req.getGeneri().size())
			throw new MangaException("!exists_gen");

		Set<Genere> sG = new HashSet<Genere>();
		added = sG.addAll(lG);
		if (added) {
			m.setGeneri(sG);
		}

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
		Set<Autore> a = m.getAutori();
		Set<Genere> g = m.getGeneri();
		CasaEditrice c = m.getCasaEditrice();
		Saga s = m.getSaga();
		log.debug("Autori: {}\nGeneri: {}\nCasaEditrice: {},\nSaga: {}", isbn, a, g, c, s);

		return imgB.buildMangaDTO(m, Optional.ofNullable(c), Optional.ofNullable(a), Optional.ofNullable(g), Optional.ofNullable(s));
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
				.map(m-> imgB.buildMangaDTO(m,  
						Optional.empty(), 
						Optional.empty(), 
						Optional.empty(),
						Optional.empty()
						))
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

	    // delete manga azione admin => si riflette su righe carrello (User)
	    rigaCarRepo.deleteAllByMangaIsbn(key);
	    

	    for (Autore a : new ArrayList<>(m.getAutori())) {
	        a.getManga().remove(m);
	        m.getAutori().remove(a);
	    }

	    for (Genere g : new ArrayList<>(m.getGeneri())) {
	        g.getManga().remove(m);
	        m.getGeneri().remove(g);
	    }
	    
	    mangaRepo.saveAndFlush(m);
	    Saga saga = m.getSaga();

	    // cancello manga prima di saga: manga Owner
	    mangaRepo.delete(m);
	    uplS.removeImage(m.getImmagine());

	    if (saga != null 
	    	&& Boolean.TRUE.equals(saga.getProxy()) // controllo se sag proxy
	        && mangaRepo.findAllBySagaId(saga.getId()).isEmpty()) {
	        sagaRepo.delete(saga);
	    }
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
		s.setProxy(true);
		sagaRepo.save(s);
		
		return s;
	}
	
	private Manga validateSagaVol(Manga m, Integer vol, Integer sagaId) {
		log.debug("checking validity of provided saga: {}", sagaId);
		if (sagaId == null) {
			if (vol != null) {
				throw new MangaException("!exists_sag");
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
	
	@Transactional(rollbackFor = Exception.class)
    public void ripristinaNumeroCopie(Ordine o) {
        o.getRigheOrdine().forEach(r -> {
            Manga m = r.getManga();
            m.setNumeroCopie(m.getNumeroCopie() + r.getNumeroCopie());
            mangaRepo.save(m);
        });
    }

	@Transactional(rollbackFor = Exception.class)
	public void ripristinaNumeroCopie(Fattura f) {
        f.getRighe().forEach(rf -> {
        	Manga m = mangaRepo.findById(rf.getIsbn()).orElseThrow(() 
        			-> new MangaException("!exists_man"));
        	m.setNumeroCopie(m.getNumeroCopie() + rf.getNumeroCopie());
        	mangaRepo.save(m);
           });
		
	}

	@Transactional(rollbackFor = Exception.class)
	public void decrementaNumeroCopie(Ordine o) throws MangaException {
        o.getRigheOrdine().forEach(r -> {
        	Manga m = r.getManga();
        	Integer left = m.getNumeroCopie() - r.getNumeroCopie();
        	if (left < 0) {
        		throw new MangaException("!exists_ncopie");
        	}
        	m.setNumeroCopie(left);
        	mangaRepo.save(m);
           });
	}

	@Transactional(rollbackFor = Exception.class)
	public void decrementaNumeroCopie(Fattura f) throws MangaException {
		// TODO Auto-generated method stub
		f.getRighe().forEach(rf -> {
			Manga m = mangaRepo.findById(rf.getIsbn()).orElseThrow(() 
        			-> new MangaException("!exists_man"));
        	Integer left = m.getNumeroCopie() - rf.getNumeroCopie();
        	if (left < 0) {
        		throw new MangaException("!exists_ncopie");
        	}
        	m.setNumeroCopie(left);
        	mangaRepo.save(m);
           });
	}
}
