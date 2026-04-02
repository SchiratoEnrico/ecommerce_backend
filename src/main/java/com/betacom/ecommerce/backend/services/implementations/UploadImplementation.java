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
			@Value("${app.upload.dir}") String uploadDir,
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
	
	
	private String controlUploadFile(MultipartFile file, Boolean unique) {
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
		
		if (!unique) {
			// meaning we are setting the defalut image
			return "default" + extension;
		}
		// Build unique name
		String uniqueName = originalName.substring(0, originalName.lastIndexOf("."))
				+ "-" + UUID.randomUUID().toString() + extension;

		return uniqueName;
	} 
	
	// devo tenerlo public/default per avere transactional
	@Transactional (rollbackFor = Exception.class)
	public void setAndSave(String upName, String isbn, Integer id) {
		String defaultImg = getDefaultFilename();
		if (id != null && id > 0) {
			Saga s = sagaR.findById(id).orElseThrow(() ->
					new MangaException("!exists_sag"));
			// rimuovo img preesistente se esiste 
			if (s.getImmagine() != null && !s.getImmagine().equals(defaultImg)) {
				removeImage(s.getImmagine());
			}
			
			s.setImmagine(upName);
			sagaR.save(s);
		}
		if (isbn != null && !isbn.isBlank()) {
			Manga m = mangaR.findByIsbn(isbn).orElseThrow(() ->
			new MangaException("!exists_man"));
			if (m.getImmagine() != null && !m.getImmagine().equals(defaultImg)) {
				removeImage(m.getImmagine());
			}
			m.setImmagine(upName);
			mangaR.save(m);
		}
	}
	
	@Override
	public String saveImage(MultipartFile file,  String isbn, Integer id) throws MangaException {
		log.debug("saveImage req, id {}, isbn: {}", id, isbn);
		if (id == null && (isbn == null || isbn.isBlank())) {
			throw new MangaException("null_idxs");
		}
		String upName = controlUploadFile(file, true);
		Path destinationFile = uploadPath.resolve(upName);
		
		try {
			Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
			setAndSave(upName, isbn, id);
		} catch (IOException e) {
			throw new MangaException("upsave_err");
		}
		return upName;
	}
	
		
	private void removePrevDefault() {
	    try {
	        Files.list(uploadPath)
	            .filter(p -> p.getFileName().toString().startsWith("default"))
	            .forEach(p -> {
	                try { Files.deleteIfExists(p); } 
	                catch (IOException e) { log.warn("Could not delete previous default: {}", p); }
	            });
	    } catch (IOException e) {
	        log.warn("Could not scan upload dir for default cleanup: {}", e.getMessage());
	    }
	}	

	@Override
	public String saveDefaultImage(MultipartFile file) throws MangaException {
		log.debug("saveDefaultImage req");

		String upName = controlUploadFile(file, false);
		Path destinationFile = uploadPath.resolve(upName);
		removePrevDefault();
		
		try {
			Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new MangaException("upsave_err");
		}
		return upName;
	}

	@Override
	public void removeImage(String filename) throws MangaException {
		if (filename == null || filename.isBlank()) return; 
		String defaultImg = getDefaultFilename();
		
		// controllo per non rimuovere immagine di default
		if (filename.equals(defaultImg)) return; 
		try {
			Path target = uploadPath.resolve(filename).normalize();
			if (!target.startsWith(uploadPath))
	            throw new MangaException("invalid_path");
			Files.deleteIfExists(target);
			
		} catch (IOException e) {
	        log.warn("Could not delete image {}: {}", filename, e.getMessage());
	        // solo warning altrimenti blocco delete entries
		}
		
	}

	private String getDefaultFilename() {
	    try {
	        return Files.list(uploadPath)
	                .filter(p -> p.getFileName().toString().startsWith("default"))
	                .map(p -> p.getFileName().toString())
	                .findFirst()
	                .orElse(null);
	    } catch (IOException e) {
	        log.error("Error listing files in uploadPath", e);
	        return null;
	    }
	}
	
	private String getFileName(String filename) {
		// check to return null if img does not exist
		if (filename == null || filename.isBlank()) {
			String myDefault = getDefaultFilename();
			
			//log.info("filename: {} myDefault: {}", filename, myDefault);
	        if (myDefault == null) {
	        	return null;
	        }
	        filename = myDefault;
	    }
		return filename;
	}
	
	@Override
	public String buildUrl(String filename) {
		
		filename = getFileName(filename);
		if (filename == null) {
			return null;
		}
	    Path filePath = uploadPath.resolve(filename);

	    if (!Files.isRegularFile(filePath)) {
		    log.info("image file not found/valid: {}", filePath.toAbsolutePath());
	        return null;
	    }

		return ServletUriComponentsBuilder.fromCurrentContextPath() // parte iniziale path: localhost ...
				.path("uploads/")
				.path(filename) // aggiunta filename
				.toUriString();
	}

}
