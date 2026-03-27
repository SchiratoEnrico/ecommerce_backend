package com.betacom.ecommerce.backend.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IUploadServices {
	String saveImage(MultipartFile filename, String isbn, Integer id) throws MangaException;
	void removeImage(String filename) throws MangaException;
	String buildUrl(String filename);
}
