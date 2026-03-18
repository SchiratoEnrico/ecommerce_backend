package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
import com.betacom.ecommerce.backend.services.interfaces.ICarrelloServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.specification.CarrelloSpecifications;

import static com.betacom.ecommerce.backend.utilities.Mapper.buildCarrelloDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CarrelloImplementation implements ICarrelloServices{
	private final ICarrelloRepository carR;
	private final IMessagesServices msgS;
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(CarrelloRequest req) throws MangaException {
		return carR.save(new Carrello()).getId();
	}
	
	@Override
	public void delete(Integer id) throws MangaException {
		Carrello car = carR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
		
		if(car.getAccount()!=null) 
			throw new MangaException("Esiste un account collegato a questo carrello");
		
		if(!car.getManga().isEmpty()) {
			car.getManga().removeAll(car.getManga());
			carR.save(car);
		}
		
		carR.delete(car);
		
	}
	 
	@Override
	public List<CarrelloDTO> list(List<String> manga) {
	    List<String> isbns = (manga == null) ? List.of() : manga;

	    Specification<Carrello> spec = CarrelloSpecifications.hasAnyMangaIds(isbns);
	    return buildCarrelloDTO(carR.findAll(spec));
	}
	
	@Override
	public CarrelloDTO findById(Integer id) throws Exception {
		Carrello car = carR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
		
		return buildCarrelloDTO(car);
	}
}
