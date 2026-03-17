package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.CaseEditriciReq;
import com.betacom.ecommerce.backend.dto.outputs.CaseEditriciDTO;
import com.betacom.ecommerce.backend.models.CaseEditrici;
import com.betacom.ecommerce.backend.repositories.ICaseEditriciRepository;
import com.betacom.ecommerce.backend.services.interfaces.ICaseEditriciServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.specification.CaseEditriciSpecifications;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import static com.betacom.ecommerce.backend.utilities.Mapper.buildCaseEditriciDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CaseEditriciImpl implements ICaseEditriciServices{
	private final ICaseEditriciRepository caseR;
	private final IMessagesServices msgS;
	
	@Transactional (rollbackFor = MangaException.class)
	@Override
	public Integer create(CaseEditriciReq req) throws MangaException {
		if(req.getDescrizione()==null)
			throw new MangaException("Descrizione non caricata");
		if(req.getEmail()==null)
			throw new MangaException("Email non caricata");
		if(req.getIndirizzo()==null)
			throw new MangaException("Indirizzo non caricato");
		if(req.getNome()==null)
			throw new MangaException("Nome non caricato");
		
		CaseEditrici cas = new CaseEditrici();
		cas.setDescrizione(req.getDescrizione());
		cas.setEmail(req.getEmail());
		cas.setIndirizzo(req.getIndirizzo());
		cas.setNome(req.getNome());
		
		return caseR.save(cas).getId();
	}
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public void update(CaseEditriciReq req) throws MangaException {
		CaseEditrici cas = caseR.findById(req.getId())
				.orElseThrow(() -> new MangaException(msgS.get("case_ntfnd")));
		
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
		CaseEditrici cas = caseR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("case_ntfnd")));
		
		if(!cas.getManga().isEmpty())
			throw new MangaException(msgS.get("manga_exist"));
		
		caseR.delete(cas);
	}
	
	@Override
	public List<CaseEditriciDTO> list(String nome, String descrizione, String indirizzo, String email) throws Exception {
		Specification<CaseEditrici> spec = Specification
				.where(CaseEditriciSpecifications.nomeLike(nome))
	            .and(CaseEditriciSpecifications.descrizioneLike(descrizione))
	            .and(CaseEditriciSpecifications.indirizzoLike(indirizzo))
	            .and(CaseEditriciSpecifications.emailLike(email));
		
		return buildCaseEditriciDTO(caseR.findAll(spec));
	}
	
	@Override
	public CaseEditriciDTO findById(Integer id) throws Exception {
		CaseEditrici cas = caseR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("case_ntfnd")+id));

		return buildCaseEditriciDTO(cas);
	}
}
