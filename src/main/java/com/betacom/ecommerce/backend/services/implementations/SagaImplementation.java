package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.SagaRequest;
import com.betacom.ecommerce.backend.dto.outputs.SagaDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Saga;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.ISagaRepository;
import com.betacom.ecommerce.backend.services.interfaces.ISagaServices;
import com.betacom.ecommerce.backend.services.interfaces.IUploadServices;
import com.betacom.ecommerce.backend.specification.SagaSpecifications;
import com.betacom.ecommerce.backend.utilities.ImageDtoBuilders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaImplementation implements  ISagaServices {
	private final ISagaRepository sagaRepo;
	private final IMangaRepository mangaRepo;
	private final ImageDtoBuilders imgB;
	private final IUploadServices uploS;

	/*
	 * Throws:
	 * - null_snom: req.getNome() == null
	 * - exists_sag: saga già esistente
	 * - null_desc: descrizione non inserita
	 * - !exists_man: manga in lista isbn non esiste
	 */
	@Override
	@Transactional (rollbackFor = Exception.class)
	public Integer create(SagaRequest req) throws MangaException {
		log.debug("Begin creating saga {}", req);
		if (req == null) {
	        throw new MangaException("null_req");
	    }

		String myNome= null;
		
		Saga s = new Saga();
		if (req.getNome() != null && !req.getNome().isEmpty()) {
			myNome = req.getNome().trim();
		} else {
			throw new MangaException("null_snom");
		}
		
		Optional<Saga> dup = sagaRepo.findByNomeIgnoreCase(myNome);
		if (dup.isEmpty()) {
			s.setNome(myNome); 
		} else {
			throw new MangaException("exists_sag");
		}		
		
		if (req.getDescrizione() == null) {
			throw new MangaException("null_desc");
		}
		s.setDescrizione(req.getDescrizione());
		
		s.setProxy(false);
		// salvo saga prima di aggiungere eventuali manga
		// manga owner, mi serve sagaID

		sagaRepo.save(s);
		
		if (req.getManga() != null && !req.getManga().isEmpty()) {
			List<Manga> lM = req.getManga().stream()
					.map(isbn -> mangaRepo.findById(isbn).orElseThrow(() ->
						new MangaException("!exists_man"))
						)
					.toList();
			lM.forEach(m -> {
				m.setSaga(s);
				mangaRepo.save(m);
			});
		}
		return s.getId();
	}
	/*
	 * Throws:
	 * - !exists_sag Saga non esistente
	 * 
	 */
	@Override
	@Transactional (rollbackFor = Exception.class)
	public void update(SagaRequest req) throws MangaException {
		log.debug("Begin updating saga {}", req);
		Saga s = sagaRepo.findById(req.getId()).orElseThrow(() ->
					new MangaException("!exists_sag")
					);

		if (req.getNome() != null && !req.getNome().isEmpty()) {
			String myNome = req.getNome().trim();
			Optional<Saga> dup = sagaRepo.findByNomeIgnoreCase(myNome);
			if (dup.isEmpty() || dup.get().getId().equals(s.getId())) {
				s.setNome(myNome);
			}
		}
		
		if (req.getDescrizione() != null && !req.getDescrizione().isEmpty()) {
			s.setDescrizione(req.getDescrizione());
		}
		
		
		if (req.getManga() != null && !req.getManga().isEmpty()) {
			List<String> present = s.getManga().stream()
									.map(m -> m.getIsbn())
									.collect(Collectors.toList());
			
			List<Manga> toadd = req.getManga().stream()
					.filter(isbn -> !present.contains(isbn))
					.map(isbn -> mangaRepo.findById(isbn).orElseThrow(() ->
						new MangaException("!exists_man"))
						).toList();
			toadd.forEach(m -> {
				s.getManga().add(m);
				m.setSaga(s);
			    mangaRepo.save(m);
				});
		}
		
		if (req.getImmagine() != null && !req.getImmagine().isEmpty()) {
			s.setImmagine(req.getImmagine());
		}
		sagaRepo.save(s);
	}

	@Override
	public void delete(Integer id) throws MangaException {
		log.debug("deleting saga with id {}", id);
		Saga s = sagaRepo.findById(id).orElseThrow(() ->
					new MangaException("!exists_sag")
					);
		List<Manga> lM = mangaRepo.findAllBySagaId(id);
		if (lM.size() > 0) {
			throw new MangaException("exists_sagman");
		}
		uploS.removeImage(s.getImmagine());
		sagaRepo.delete(s);
	}
	
	@Override
	public List<SagaDTO> list(
			String sagaNome,
			String casaEditriceNome,
			String autoreNome,
			String autoreCognome,
			Integer sagaId,
			Integer casaEditriceId,
			Integer autoreId,
			List<Integer> generiId
			) {
		
		Specification<Saga> spec = Specification
		        .where(SagaSpecifications.distinct())
				.and(SagaSpecifications.sagaIdEquals(sagaId))
				.and(SagaSpecifications.casaEditriceIdEquals(casaEditriceId))
				.and(SagaSpecifications.autoreIdEquals(autoreId))
				.and(SagaSpecifications.generiIdEqual(generiId))
				.and(SagaSpecifications.casaEditriceNomeLike(casaEditriceNome))
				.and(SagaSpecifications.autoreNomeLike(autoreNome))
				.and(SagaSpecifications.autoreCognomeLike(autoreCognome))
				.and(SagaSpecifications.sagaNomeLike(sagaNome))
				;
		List<Saga> lS = sagaRepo.findAll(spec);
		
		return lS.stream()
				.map(s -> imgB.buildSagaDTO(s, Optional.empty()))
				.collect(Collectors.toList());
	}

	@Override
	public SagaDTO findById(Integer id) throws MangaException {
		log.debug("List saga with id {}", id);
		Saga s = sagaRepo.findById(id).orElseThrow(() ->
					new MangaException("!exists_sag")
					);
		List<Manga> lM = mangaRepo.findAllBySagaId(id);
		return imgB.buildSagaDTO(s, Optional.ofNullable(lM));
	}
}
