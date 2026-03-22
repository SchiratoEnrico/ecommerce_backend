package com.betacom.ecommerce.backend.services.implementations;

import static com.betacom.ecommerce.backend.utilities.Mapper.buildRigaCarrelloDTO;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.RigaCarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.RigaCarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.RigaCarrello;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
import com.betacom.ecommerce.backend.repositories.IRigaCarrelloRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IRigaCarrelloServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RigaCarrelloImplementation implements IRigaCarrelloServices {
	private final IRigaCarrelloRepository rcR;
	private final IMessagesServices msgS;
	private final ICarrelloRepository carR;
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(RigaCarrelloRequest req) throws MangaException{
		if(req==null)
			throw new MangaException("RigaCarelloRequest cannot be null");
		if(req.getCarrelloId()==null)
			throw new MangaException("Carrello Id cannot be null");
		if(req.getManga()==null)
			throw new MangaException("Manga cannot be null");
		if(req.getNumeroCopie()==null || req.getNumeroCopie()<=0)
			throw new MangaException("Numero copie cannot be null or lesser than 1");
		
		RigaCarrello rc = new RigaCarrello();
		Carrello car = carR.findById(req.getCarrelloId())
				.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
		
		rc.setCarrello(car);
		rc.setManga(req.getManga());
		rc.setNumeroCopie(req.getNumeroCopie());
		
		List<RigaCarrello> rC = car.getRigheCarrello();
		rC.add(rc);
		
		carR.save(car);
		
		return rcR.save(rc).getId();
	}

	@Override
	public void update(RigaCarrelloRequest req) throws MangaException {
		RigaCarrello rc = rcR.findById(req.getId())
				.orElseThrow(() -> new MangaException(msgS.get("riga_carrello_ntfnd")));
		
		if(req.getCarrelloId()!=null)
			throw new MangaException("Cannot change the chart of a chart row");
		if(req.getManga()!=null) 
			rc.setManga(req.getManga());
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
				.orElseThrow(() -> new MangaException(msgS.get("riga_carrello_ntfnd")));
		rcR.delete(rc);
	}

	@Override
	public List<RigaCarrelloDTO> list() throws Exception {
		List<RigaCarrello> lrC = rcR.findAll();
		return buildRigaCarrelloDTO(lrC);
	}

	@Override
	public RigaCarrelloDTO findById(Integer id) throws Exception {
		RigaCarrello rc = rcR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("riga_carrello_ntfnd")));
		
		return buildRigaCarrelloDTO(rc);
	}
}
