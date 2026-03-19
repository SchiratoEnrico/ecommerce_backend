package com.betacom.ecommerce.backend;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.ecommerce.backend.controllers.CarrelloControllerTest;
import com.betacom.ecommerce.backend.controllers.CasaEditriceControllerTest;
import com.betacom.ecommerce.backend.controllers.TipoSpedizioneControllerTest;

@Suite
@SelectClasses({
	CarrelloControllerTest.class,
	CasaEditriceControllerTest.class,
	TipoSpedizioneControllerTest.class
})
@SpringBootTest
class EcommerceBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
