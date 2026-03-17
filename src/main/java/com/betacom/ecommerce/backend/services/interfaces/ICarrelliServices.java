package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.CarrelliReq;
import com.betacom.ecommerce.backend.dto.outputs.CarrelliDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface ICarrelliServices {
	Integer create(CarrelliReq req) throws MangaException;
	void delete(Integer id) throws MangaException;
	
	List<CarrelliDTO> list(List<String> manga) throws Exception;
	CarrelliDTO findById(Integer id) throws Exception;
}
