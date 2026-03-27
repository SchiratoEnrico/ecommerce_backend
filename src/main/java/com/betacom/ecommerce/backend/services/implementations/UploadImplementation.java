package com.betacom.ecommerce.backend.services.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Saga;
import com.betacom.ecommerce.backend.repositories.IMangaRepository;
import com.betacom.ecommerce.backend.repositories.ISagaRepository;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;
import com.betacom.ecommerce.backend.services.interfaces.IUploadServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadImplementation implements IUploadServices{

	private final Path uploadPath;
	private final IMessagesServices msgS;
	private final ISagaRepository sagaR;
	private final IMangaRepository mangaR;

	public UploadImplementation(
			@Value("${app.upload.dir:uploads}") String uploadDir,
			IMessagesServices msgS,
			ISagaRepository sagaR,
			IMangaRepository mangaR
			) {
		this.msgS = msgS;
		this.sagaR = sagaR;
		this.mangaR = mangaR;
		this.uploadPath = Paths.get(uploadDir)
					.toAbsolutePath() 
					.normalize();
		init();
	}
	
	private void init() {
		try {
			if (Files.notExists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
		} catch (IOException e) {
			throw new MangaException("udir_err");
		}
	}
	
	private String controlUploadFile(MultipartFile file) {
		Assert.isTrue(!file.isEmpty(), 
				() -> msgS.get("!exists_up")
		);
		String original = file.getOriginalFilename();
		String extension = "";
		String originalName = original.trim().replaceAll("\\s+", "_"); // replaace spaces
		
		// get extension
		extension = Optional.ofNullable(originalName)
						.filter(name -> name.contains("."))
						.map(name -> name.substring(name.lastIndexOf(".")))
						.orElse("");
		// Build unique name
		String uniqueName = originalName.substring(0, originalName.lastIndexOf("."))
				+ "-" + UUID.randomUUID().toString() + extension;

		return uniqueName;
	} 
	
	private void setAndSave(String upName, String isbn, Integer id) {
		if (id != null && id > 0) {
			Saga s = sagaR.findById(id).orElseThrow(() ->
					new MangaException("!exists_sag"));
			s.setImmagine(upName);
			sagaR.save(s);
		}
		if (isbn != null && !isbn.isBlank()) {
			Manga m = mangaR.findByIsbn(isbn).orElseThrow(() ->
			new MangaException("!exists_man"));
			m.setImmagine(upName);
			mangaR.save(m);
		}
	}
	
	@Transactional (rollbackFor = Exception.class)
	@Override
	public String saveImage(MultipartFile file,  String isbn, Integer id) throws MangaException {
		log.debug("saveImage req, id {}, isbn: {}", id, isbn);
		if (id == null && (isbn.isEmpty())) {
			throw new MangaException("null_idxs");
		}
		String upName = controlUploadFile(file);
		Path destinationFile = uploadPath.resolve(upName);
		
		try {
			Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
			setAndSave(upName, isbn, id);
		} catch (IOException e) {
			throw new MangaException("upsave_err");
		}
		return upName;
	}

	@Override
	public void removeImage(String filename) throws MangaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String buildUrl(String filename) {
		
		return ServletUriComponentsBuilder.fromCurrentContextPath() // parte iniziale path: localhost ...
				.path("/images/") // ci aspettiamo file in ./upload/images/
				.path(filename) // aggiunta filename
				.toUriString();
	}

}
