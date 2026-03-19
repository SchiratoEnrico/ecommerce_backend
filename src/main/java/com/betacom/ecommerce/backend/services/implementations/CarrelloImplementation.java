package com.betacom.ecommerce.backend.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.ecommerce.backend.dto.inputs.CarrelloRequest;
import com.betacom.ecommerce.backend.dto.outputs.CarrelloDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.repositories.IAccountRepository;
import com.betacom.ecommerce.backend.repositories.ICarrelloRepository;
import com.betacom.ecommerce.backend.services.interfaces.ICarrelloServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import static com.betacom.ecommerce.backend.utilities.Mapper.buildCarrelloDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CarrelloImplementation implements ICarrelloServices{
	private final ICarrelloRepository carR;
	private final IAccountRepository accR;
	private final IMessagesServices msgS;
	
	@Transactional(rollbackFor=MangaException.class)
	@Override
	public Integer create(CarrelloRequest req) throws MangaException {
		Carrello car = new Carrello();
		Account acc = accR.findById(req.getId_account())
				.orElseThrow(() -> new MangaException("account_ntfnd"));
		acc.setCarrello(car);
		car.setAccount(acc);
		accR.save(acc);
		return carR.save(car).getId();
	}
	
	@Override
	public void delete(Integer id) throws MangaException {
		Carrello car = carR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
		
		Account acc = accR.findById(car.getAccount().getId())
				.orElseThrow();
		
		acc.setCarrello(null);
		car.setAccount(null);
		
		accR.save(acc);		
		carR.save(car);
		carR.delete(car);
	}
	 
	@Override
	public List<CarrelloDTO> list() {
		return buildCarrelloDTO(carR.findAll());
	}
	
	@Override
	public CarrelloDTO findById(Integer id) throws Exception {
		Carrello car = carR.findById(id)
				.orElseThrow(() -> new MangaException(msgS.get("carrello_ntfnd")));
		
		return buildCarrelloDTO(car);
	}
}
