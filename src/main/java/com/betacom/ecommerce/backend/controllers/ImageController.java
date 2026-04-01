package com.betacom.ecommerce.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.betacom.ecommerce.backend.services.interfaces.IUploadServices;
import com.betacom.ecommerce.backend.response.Response;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/immagini")
public class ImageController {

	private final IUploadServices uploS;
	private final IMessagesServices msgS;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping(value="/upload", consumes="multipart/form-data")
	public ResponseEntity<Response> uploadImage(
			@RequestParam(required = true) MultipartFile file,
			@RequestParam(required = false) String isbn,
			@RequestParam(required = false) Integer id			
			) {
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		try {
			if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
				r.setMsg(msgS.get("upload_inv"));
				return ResponseEntity.badRequest()
						.body(r);
			}
			r.setMsg(uploS.saveImage(file, isbn, id));
			return ResponseEntity.status(status)
					.body(r);
		} catch (Exception e) {
			r.setMsg(msgS.get(e.getMessage()));
			return ResponseEntity.internalServerError()
					.body(r);
		}
	}
	
	@GetMapping("/get_url")
	public ResponseEntity<Response> getUrl(@RequestParam(required = true) String filename) {
		Response r = new Response();
		HttpStatus status = HttpStatus.OK;
		r.setMsg(msgS.get(uploS.buildUrl(filename)));
		return ResponseEntity.status(status)
				.body(r);
	}
}
