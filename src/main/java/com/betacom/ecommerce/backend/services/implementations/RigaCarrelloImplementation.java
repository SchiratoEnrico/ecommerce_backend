package com.betacom.ecommerce.backend.services.implementations;

import static com.betacom.ecommerce.backend.utilities.DtoBuilders.buildRigaCarrelloDTO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.RigaCarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaCarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.RigaCarrello;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.IRigaCarrelloRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaCarrelloServices;
import com.betacom.ecommerce.backend.specification.RigaCarrelloSpecifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RigaCarrelloImplementation implements IRigaCarrelloServices {
	private final IRigaCarrelloRepository rcR;
	private final IMessagesServices msgS;
	private final ICarrelloRepository carR;
	private final IMangaRepository manR;
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(RigaCarrelloRequest req) throws MangaException{
		if(req==null)
			throw new MangaException("null_crq");
		log.debug("Request of creation: {}", req);
		if(req.getCarrelloId()==null)
			throw new MangaException("null_cri");
		if(req.getManga()==null)
			throw new MangaException("null_man");
		if(req.getNumeroCopie()==null || req.getNumeroCopie()<=0)
			throw new MangaException("null_qua");
		
		RigaCarrello rc = new RigaCarrello();
		Carrello car = carR.findById(req.getCarrelloId())
				.orElseThrow(() -> new MangaException(msgS.get("!exists_car")));
		
		rc.setCarrello(car);
		Manga m = manR.findByIsbn(req.getManga()).orElseThrow(
				() -> new MangaException("!exists_man"));
		rc.setManga(m);
		rc.setNumeroCopie(req.getNumeroCopie());
		
		List<RigaCarrello> rC = car.getRigheCarrello();
		rC.add(rc);
		
		carR.save(car); //Testare se viene creata doppia, in caso levare questa
		
		return rcR.save(rc).getId();
	}

	@Override
	public void update(RigaCarrelloRequest req) throws MangaException {
		RigaCarrello rc = rcR.findById(req.getId())
				.orElseThrow(() -> new MangaException(msgS.get("!exists_rcr")));
		
		if(req.getCarrelloId()!=null)
			throw new MangaException("id_chng");
		if(req.getManga()!=null) {
			Manga m = manR.findByIsbn(req.getManga()).orElseThrow(
					() -> new MangaException("!exists_man"));
			rc.setManga(m);
		}
		if(req.getNumeroCopie()!=null) {
			if(req.getNumeroCopie()<=0) {
				rcR.delete(rc);
				return;
			}
			else
				rc.setNumeroCopie(req.getNumeroCopie());
		}
			
		rcR.save(rc);
	}

	@Override
	public void delete(Integer id) throws MangaException {
		RigaCarrello rc = rcR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("!exists_rcr")));
		rcR.delete(rc);
	}
	
	private RigaCarrelloDTO builderCall(RigaCarrello rc) {
		Optional<Manga> m = manR.findByIsbn(rc.getManga().getIsbn());
		return buildRigaCarrelloDTO(rc, m);
	}
	
	@Override
	public List<RigaCarrelloDTO> list(Integer chartId, String isbn, Integer nCopie) throws Exception {
		Specification<RigaCarrello> spec = Specification
				.where(RigaCarrelloSpecifications.chartIdLike(chartId))
				.and(RigaCarrelloSpecifications.mangaLike(isbn))
				.and(RigaCarrelloSpecifications.hasAtLeast(nCopie));
		
		List<RigaCarrello> lrC = rcR.findAll(spec);
		
		return lrC.stream()
				.map(rC -> builderCall(rC))
				.toList();
	}

	@Override
	public RigaCarrelloDTO findById(Integer id) throws Exception {
		RigaCarrello rc = rcR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("!exists_rcr")));
		Optional<Manga> m = manR.findByIsbn(rc.getManga().getIsbn());
		return buildRigaCarrelloDTO(rc, m);
	}
	
	public Boolean isRigaCarrelloOwnedByAccount(Integer rigaId, Integer accountId) {
	    var rigaOpt = rcR.findById(rigaId);
	    if (rigaOpt.isEmpty() || rigaOpt.get().getCarrello() == null || rigaOpt.get().getCarrello().getAccount() == null) {
	        return false;
	    }
	    return rigaOpt.get().getCarrello().getAccount().getId().equals(accountId);
	}
}
