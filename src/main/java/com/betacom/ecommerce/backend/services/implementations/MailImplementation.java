package com.betacom.ecommerce.backend.services.implementations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.betacom.ecommerce.backend.dto.inputs.MailRequest;
import com.betacom.ecommerce.backend.exceptions.MangaException;
import com.betacom.ecommerce.backend.services.interfaces.IMailServices;
import com.betacom.ecommerce.backend.services.interfaces.IMessagesServices;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailImplementation implements IMailServices{
	
	@Value("${mail.sender}")
	private String from;
	
	
	
	private final JavaMailSender mailSender;
	
	
	@Override
	public void sendMail(MailRequest req) throws MangaException {
		log.debug("sendMail []", req);
		
		if (req.getTo() == null || req.getOggetto() == null || req.getBody() == null)
			throw new MangaException("mail_error");
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mimeMessage,true, "UTF-8");
			helper.setTo(req.getTo());
			helper.setFrom(from);
			helper.setSubject(req.getOggetto());
			helper.setText(req.getBody(), true);
		} catch (MessagingException e) {
			throw new MangaException("mail_error");
		}	
		mailSender.send(mimeMessage);
		log.debug("dopo  send");	
	}
}
