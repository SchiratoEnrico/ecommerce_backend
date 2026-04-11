package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.dto.inputs.RigaCarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.RigaCarrello;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaCarrelloRepository;
import com.betacom.ecommerce.backend.services.interfaces.ICarrelloServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaCarrelloServices;
import com.betacom.ecommerce.backend.specification.CarrelloSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CarrelloImplementation implements ICarrelloServices{
	private final ICarrelloRepository carR;
	private final IAccountRepository accR;
	private final IMangaRepository manR;
	private final IMessagesServices msgS;
	private final IRigaCarrelloServices rcS;
	private final IRigaCarrelloRepository rcR;
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(CarrelloRequest req) throws MangaException {
		Carrello car = new Carrello();
		Account acc = accR.findById(req.getId_account())
				.orElseThrow(() -> new MangaException("account_ntfnd"));
		if (carR.findByAccountId(acc.getId()).isPresent()) {
			throw new MangaException("exists_car");
		}
		acc.setCarrello(car);
		car.setAccount(acc);
		
		// basta salvare la relazione dal lato proprietario e siamo apposto
		
		return carR.save(car).getId();
	}
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void addRow(Integer chartId, String isbn, Integer nCopie) throws MangaException {
		log.debug("Addong to chart {} row for isbn {} and number copies {}", chartId, isbn, nCopie);
		Carrello car = carR.findById(chartId)
				.orElseThrow(() -> new MangaException("carrello_ntfnd"));
		
		Optional<Manga> man = manR.findById(isbn);
		if (man.isEmpty())
			throw new MangaException("manga_ntfnd");
		
		RigaCarrelloRequest req = new RigaCarrelloRequest();
				
		for(RigaCarrello row : car.getRigheCarrello()) {
			if(row.getManga().getIsbn().equals(isbn)) {
	            Integer nuovoNCopie = row.getNumeroCopie() + nCopie;

	            // se tot < 0
	            if (nuovoNCopie <= 0) {
	                car.getRigheCarrello().remove(row); // orphanRemoval rimuove riga
	                carR.save(car);
	                return;
	            }

				req.setId(row.getId());
				req.setManga(isbn);
				req.setNumeroCopie(nuovoNCopie);
				rcS.update(req);
				
				return;
			}
		}
		
		// se isbn non è già in carrello e ncopie < 0 errore
	    if (nCopie <= 0)
	        throw new MangaException("!exists_qua");

		req.setCarrelloId(chartId);
		req.setManga(isbn);
		req.setNumeroCopie(nCopie);
		
		rcS.create(req);
		
		List<RigaCarrello> lRC = rcR.findAllByCarrelloId(car.getId());
		car.getRigheCarrello().clear();
		car.getRigheCarrello().addAll(lRC);
		carR.save(car);
	}
	
	@Override
	public void updateRow(Integer chartId, Integer rowId, String isbn, Integer nCopie) throws MangaException {
		Carrello car = carR.findById(chartId)
				.orElseThrow(() -> new MangaException("carrello_ntfnd"));
		
		Optional<Manga> man = manR.findById(isbn);
		if (man.isEmpty()) {
				throw new MangaException("manga_ntfnd");
		}		
		
		RigaCarrello target = car.getRigheCarrello().stream()
	            .filter(c -> c.getId().equals(rowId))
	            .findFirst()
	            .orElseThrow(() -> new MangaException("!exists_rcr"));
		
	    if (nCopie <= 0) {
	        car.getRigheCarrello().remove(target); // orphanRemoval
	        carR.save(car);
	        return;
	    }

	    RigaCarrelloRequest req = new RigaCarrelloRequest();
	    req.setId(target.getId());
		req.setManga(man.get().getIsbn());
		req.setNumeroCopie(nCopie);
		rcS.update(req);
		return;

	}

	@Transactional (rollbackFor = MangaException.class)
	@Override
	public void deleteRow(Integer chartId, Integer rowId) throws MangaException {
		Carrello car = carR.findById(chartId)
				.orElseThrow(() -> new MangaException("carrello_ntfnd"));
		
		RigaCarrello rc = rcR.findById(rowId).orElseThrow(() ->
				new MangaException("!exists_rca"));
		if (!car.getRigheCarrello().remove(rc)) throw new MangaException("rca_notin_ca"); // orphanremoval true
		
		carR.save(car);
	}
	
	@Transactional (rollbackFor = MangaException.class)
	@Override
	public void delete(Integer id) throws MangaException {
		Carrello car = carR.findById(id)
				.orElseThrow(() -> new MangaException("carrello_ntfnd")); 
		
		// NW INVECE DI DELETE CARRELLO	+ RICREO VUOTO
		// MOLTO PIÙ SEMPLICEMENTE SVUOTO CARRELLO
		// RISCHIOSO PER MECCANICHE ORPHANREMOVAL CON ACCOUNT

	    car.getRigheCarrello().clear();
	    carR.save(car);

//		Account acc = accR.findById(car.getAccount().getId())
//				.orElseThrow();
//
//		acc.setCarrello(null);
//		car.setAccount(null);
//		car.getRigheCarrello().clear(); 
//		
//		carR.delete(car);
//		carR.flush(); // forza la delete nel db subito
//		
//		Carrello nuovoCarrello = new Carrello();
//		nuovoCarrello.setAccount(acc);
//		carR.save(nuovoCarrello);
	}
	 
	@Override
	public List<CarrelloDTO> list(List<String> isbns) {
		Specification<Carrello> spec = Specification
				.where(CarrelloSpecifications.hasAnyMangaIds(isbns));
		List<Carrello> lC = carR.findAll(spec);
		return lC.stream()
				.map(c ->
				DtoBuilders.buildCarrelloDTO(
						c,
						Optional.ofNullable(c.getAccount()),
						Optional.empty() //Optional.ofNullable(c.getRigheCarrello())
						)
				).toList();
	}
	
	@Override
	public CarrelloDTO findById(Integer id) throws Exception {
		Carrello car = carR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
		
		return DtoBuilders.buildCarrelloDTO(
				car,
				Optional.ofNullable(car.getAccount()),
				Optional.ofNullable(car.getRigheCarrello())
				);
	}
	
	@Override
	public CarrelloDTO findByAccountId(Integer id) throws Exception{
		Carrello car = carR.findByAccountId(id)
				.orElseThrow(() -> new MangaException("carrello_ntfnd"));
		
		log.debug("Carrello: {}", car);
		
		return DtoBuilders.buildCarrelloDTO(
				car,
				Optional.ofNullable(car.getAccount()),
				Optional.ofNullable(car.getRigheCarrello())
				);
	}

	@Override
	public boolean isCartOwnedByAccount(Integer chartId, Integer accountId) {
		// Cerchiamo il carrello nel DB tramite il suo ID
		var carrelloOpt = carR.findById(chartId);
		
		// 2. Se il carrello non esiste, ovviamente non è suo
		if (carrelloOpt.isEmpty()) {
			return false;
		}
		
		//se c'è me lo prendo dall'optional
		var carrello = carrelloOpt.get();
		
		// e il carrello esiste ma non ha un account associato (non dovrebbe succedere, ma sicurezza prima di tutto)
		if (carrello.getAccount() == null) {
			return false;
		}
		
		//Confrontiamo l'ID dell'account proprietario del carrello con l'ID dell'utente loggato
		return carrello.getAccount().getId().equals(accountId);
	}
}
