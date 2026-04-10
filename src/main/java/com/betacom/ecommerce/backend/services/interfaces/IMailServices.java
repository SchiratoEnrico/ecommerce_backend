package com.betacom.ecommerce.backend.services.interfaces;

import com.betacom.ecommerce.backend.dto.inputs.MailRequest;
import com.betacom.ecommerce.backend.exceptions.MangaException;

public interface IMailServices {
	void sendMail(MailRequest req) throws MangaException;
}
