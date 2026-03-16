package com.betacom.ecommerce.backend.services.implementations;

import org.springframework.stereotype.Service;

import com.betacom.ecommerce.backend.repositories.IMessaggiRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessagesImplementation implements IMessagesServices{

	private final IMessaggiRepository repM;

	@Override
	public String get(String code){
		
		String msg;
		try {
			msg = repM.findByCode(code).get().getMessaggio();
		} catch (Exception e) {
			return "Caricamento messaggio fallito, forse non lo hai nel database";
		}	
		return msg;
	}
}
