package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.TipoSpedizioneRequest;
import com.betacom.ecommerce.backend.dto.outputs.TipoSpedizioneDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.TipoSpedizione;
import com.betacom.ecommerce.backend.repositories.ITipoSpedizioneRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.ITipoSpedizioneServices;
import com.betacom.ecommerce.backend.specification.SpedizioneSpecifications;
import com.betacom.ecommerce.backend.utilities.DtoBuilders;
import com.betacom.ecommerce.backend.utilities.Utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TipoSpedizioneImplementation implements ITipoSpedizioneServices{
	private final ITipoSpedizioneRepository speR;
	private final IMessagesServices msgS;

	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(TipoSpedizioneRequest req) throws MangaException {
		if(req.getTipoSpedizione()==null)
			throw new MangaException("Tipo spedizione non caricata");
		
		if (speR.findByTipoSpedizione(req.getTipoSpedizione().trim().toUpperCase()).isPresent())
	        throw new MangaException("exists_spe");
		
		TipoSpedizione spe = new TipoSpedizione();
		spe.setTipoSpedizione(Utils.normalize(req.getTipoSpedizione()));
		
		return speR.save(spe).getId();
	}

	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void update(TipoSpedizioneRequest req) throws MangaException {
		TipoSpedizione spe = speR.findById(req.getId())
				.orElseThrow(() -> new MangaException(msgS.get("spedizione_ntfnd")));
		
		if (!Utils.isBlank(req.getTipoSpedizione())) {
	        Optional<TipoSpedizione> byTipoSped = speR.findByTipoSpedizione(req.getTipoSpedizione().trim().toUpperCase());
	        
	        if (byTipoSped.isPresent() && !byTipoSped.get().getId().equals(req.getId()))
	            throw new MangaException("exists_spe");
	        	
	        spe.setTipoSpedizione(req.getTipoSpedizione().trim().toUpperCase());
	    }
		
		speR.save(spe);
	}

	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void delete(Integer id) throws MangaException {
		TipoSpedizione spe = speR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("spedizione_ntfnd")));
		
		speR.delete(spe);
	}

	@Override
	public List<TipoSpedizioneDTO> list(String tipoSpedizione) throws Exception {
		Specification<TipoSpedizione> spec = Specification
				.where(SpedizioneSpecifications.tipoSpedizioneLike(tipoSpedizione));
		List<TipoSpedizione> lS = speR.findAll(spec);
		return lS.stream()
				.map(s -> DtoBuilders.buildTipoSpedizioneDTO(s))
				.toList();
	}

	@Override
	public TipoSpedizioneDTO findById(Integer id) throws Exception {
		TipoSpedizione spe = speR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("spedizione_ntfnd")));
		
		return DtoBuilders.buildTipoSpedizioneDTO(spe);
	}

}
