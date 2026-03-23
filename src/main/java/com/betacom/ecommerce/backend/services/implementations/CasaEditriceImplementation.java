package com.betacom.ecommerce.backend.services.implementations;

import static com.betacom.ecommerce.backend.utilities.Mapper.buildCaseEditriciDTO;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.CasaEditriceRequest;
import com.betacom.ecommerce.backend.dto.outputs.CasaEditriceDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.repositories.ICasaEditriceRepository;
import com.betacom.ecommerce.backend.services.interfaces.ICasaEditriceServices;
import com.betacom.ecommerce.backend.specification.CasaEditriceSpecifications;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CasaEditriceImplementation implements ICasaEditriceServices{
	private final ICasaEditriceRepository caseR;
	
	@Transactional (rollbackFor = MangaException.class)
	@Override
	public Integer create(CasaEditriceRequest req) throws MangaException {
		if(req.getDescrizione()==null)
			throw new MangaException("null_des");
		if(req.getEmail()==null)
			throw new MangaException("null_ema");
		if(req.getIndirizzo()==null)
			throw new MangaException("null_ind");
		if(req.getNome()==null)
			throw new MangaException("null_nom");
		
		CasaEditrice cas = new CasaEditrice();
		cas.setDescrizione(req.getDescrizione());
		cas.setEmail(req.getEmail());
		cas.setIndirizzo(req.getIndirizzo());
		cas.setNome(req.getNome());
		
		return caseR.save(cas).getId();
	}
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void update(CasaEditriceRequest req) throws MangaException {
		CasaEditrice cas = caseR.findById(req.getId())
				.orElseThrow(() -> new MangaException("case_ntfnd"));
		
		if(req.getDescrizione()!=null)
			cas.setDescrizione(req.getDescrizione());
		if(req.getEmail()!=null)
			cas.setEmail(req.getEmail());
		if(req.getIndirizzo()!=null)
			cas.setIndirizzo(req.getIndirizzo());
		if(req.getNome()!=null)
			cas.setNome(req.getNome());
		
		caseR.save(cas);
	}
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void delete(Integer id) throws MangaException {
		CasaEditrice cas = caseR.findById(id)
				.orElseThrow(() -> new MangaException("case_ntfnd"));
		
		if(caseR.existsByIdAndMangaIsNotEmpty(id))
			throw new MangaException("casa_man");
		
		caseR.delete(cas); 
	}
	
	@Override
	public List<CasaEditriceDTO> list(String nome, String descrizione, String indirizzo, String email) throws Exception {
		Specification<CasaEditrice> spec = Specification
				.where(CasaEditriceSpecifications.nomeLike(nome))
	            .and(CasaEditriceSpecifications.descrizioneLike(descrizione))
	            .and(CasaEditriceSpecifications.indirizzoLike(indirizzo))
	            .and(CasaEditriceSpecifications.emailLike(email));
		
		return buildCaseEditriciDTO(caseR.findAll(spec));
	}
	
	@Override
	@Transactional
	public CasaEditriceDTO findById(Integer id) throws Exception {
		CasaEditrice cas = caseR.findById(id)
				.orElseThrow(() -> new MangaException("case_ntfnd"));

		return buildCaseEditriciDTO(cas);
	}
}
