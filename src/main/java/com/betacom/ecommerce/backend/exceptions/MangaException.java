package com.betacom.ecommerce.backend.exceptions;

@SuppressWarnings("serial")
public class MangaException extends RuntimeException{
	public MangaException() {
		super();
	}

	public MangaException(String msg) {
		super(msg);
	}
}
