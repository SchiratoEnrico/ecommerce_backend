package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.SpedizioniReq;
import com.betacom.ecommerce.backend.dto.outputs.SpedizioniDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Spedizioni;
import com.betacom.ecommerce.backend.repositories.ISpedizioniRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.ISpedizioniServices;
import com.betacom.ecommerce.backend.specification.SpedizioniSpecifications;
import static com.betacom.ecommerce.backend.utilities.Mapper.buildSpedizioniDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SpedizioniImpl implements ISpedizioniServices{
	private final ISpedizioniRepository speR;
	private final IMessagesServices msgS;

	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(SpedizioniReq req) throws MangaException {
		if(req.getTipoSpedizione()==null)
			throw new MangaException("Tipo spedizione non caricata");
		
		Spedizioni spe = new Spedizioni();
		spe.setTipoSpedizione(req.getTipoSpedizione());
		
		return speR.save(spe).getId();
	}

	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void update(SpedizioniReq req) throws MangaException {
		Spedizioni spe = speR.findById(req.getId())
				.orElseThrow(() -> new MangaException(msgS.get("spedizione_ntfnd")));
		
		if(req.getTipoSpedizione()!=null)
			spe.setTipoSpedizione(req.getTipoSpedizione());
		
		speR.save(spe);
	}

	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void delete(Integer id) throws MangaException {
		Spedizioni spe = speR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("spedizione_ntfnd")));
		
		speR.delete(spe);
	}

	@Override
	public List<SpedizioniDTO> list(String tipoSpedizione) throws Exception {
		Specification<Spedizioni> spec = Specification
				.where(SpedizioniSpecifications.tipoSpedizioneLike(tipoSpedizione));
		
		return buildSpedizioniDTO(speR.findAll(spec));
	}

	@Override
	public SpedizioniDTO findById(Integer id) throws Exception {
		Spedizioni spe = speR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("spedizione_ntfnd")));
		
		return buildSpedizioniDTO(spe);
	}

}
