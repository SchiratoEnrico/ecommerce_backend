package com.betacom.ecommerce.backend.services.interfaces;

import java.util.List;

import com.betacom.ecommerce.backend.dto.inputs.AccountRequest;
import com.betacom.ecommerce.backend.dto.outputs.AccountDTO;
import com.betacom.ecommerce.backend.exceptions.MangaException;


public interface IAccountServices {
	
    public void create(AccountRequest req) throws MangaException;
	
    public void delete(Integer id) throws MangaException;
    
    public void update(AccountRequest req, boolean isAdmin) throws MangaException;
    
    public List<AccountDTO> list();
    
    AccountDTO findById(Integer id) throws MangaException;
    
    AccountDTO findByUsername(String username) throws MangaException;
    
    List<AccountDTO> findByFilters(AccountRequest req) throws MangaException;

    public void sendValidation(String username) throws MangaException;
	public void emailValidate(String username) throws MangaException;
	
	Boolean isAccountValidated(Integer accountId) throws MangaException;

	
	public void requestPasswordReset(String email) throws MangaException;
	public void resetPassword(String token, String newPassword) throws MangaException;
}
