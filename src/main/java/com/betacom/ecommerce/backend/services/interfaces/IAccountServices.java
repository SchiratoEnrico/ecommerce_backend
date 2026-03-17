package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;


public interface IAccountServices {
	
    public void create(AccountRequest req) throws MangaException;
	
    public void delete(Integer id) throws MangaException;
    
    public void update(AccountRequest req) throws MangaException;
    
    public List<AccountDTO> list();
    
    AccountDTO findById(Integer id) throws MangaException;

}
