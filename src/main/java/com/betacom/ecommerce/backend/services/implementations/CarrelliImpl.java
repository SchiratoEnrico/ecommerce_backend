package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.CarrelliReq;
import com.betacom.ecommerce.backend.dto.outputs.CarrelliDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Carrelli;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.repositories.ICarrelliRepository;
import com.betacom.ecommerce.backend.services.interfaces.ICarrelliServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.specification.CarrelliSpecifications;

import static com.betacom.ecommerce.backend.utilities.Mapper.buildCarrelliDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CarrelliImpl implements ICarrelliServices{
	private final ICarrelliRepository carR;
	private final IMessagesServices msgS;
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(CarrelliReq req) throws MangaException {
		return carR.save(new Carrelli()).getId();
	}
	
	@Override
	public void delete(Integer id) throws MangaException {
		Carrelli car = carR.findById(id)
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
	public List<CarrelliDTO> list(List<String> manga) {
	    List<String> isbns = (manga == null) ? List.of() : manga;

	    Specification<Carrelli> spec = CarrelliSpecifications.hasAnyMangaIds(isbns);
	    return buildCarrelliDTO(carR.findAll(spec));
	}
	
	@Override
	public CarrelliDTO findById(Integer id) throws Exception {
		Carrelli car = carR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
		
		return buildCarrelliDTO(car);
	}
}
