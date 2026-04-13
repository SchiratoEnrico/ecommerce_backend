package com.betacom.ecommerce.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.betacom.ecommerce.backend.services.interfaces.IFatturaServices;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AttivitaProgrammate {
	private final IFatturaServices fattS;
	
	// cron: accetta espressioni cron-like, stringhe con 
	// secondi minuti ora giorno_mese mese MON-SAT.
	// => ogni giorno a mezznotte
    @Scheduled(cron = "0 0 0 * * *")
    public void autoConfirmExpiredFatture() {
        fattS.autoConfirmExpired();
    }
}
