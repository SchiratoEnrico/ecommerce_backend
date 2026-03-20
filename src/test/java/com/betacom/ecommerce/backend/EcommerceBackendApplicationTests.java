package com.betacom.ecommerce.backend;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.ecommerce.backend.controllers.CarrelloControllerTest;
import com.betacom.ecommerce.backend.controllers.CasaEditriceControllerTest;
import com.betacom.ecommerce.backend.controllers.OrdineControllerTest;
import com.betacom.ecommerce.backend.controllers.RigaOrdineControllerTest;
import com.betacom.ecommerce.backend.controllers.StatoOrdineControllerTest;
import com.betacom.ecommerce.backend.controllers.TipoSpedizioneControllerTest;

@Suite
@SelectClasses({
	StatoOrdineControllerTest.class,
	RigaOrdineControllerTest.class,
	OrdineControllerTest.class
})
//@SpringBootTest NON VA SE METTI @Suite(@Suite is JUnit platform-level, @SpringBootTest should be on individual test classes)

class EcommerceBackendApplicationTests {
	@Test
	void contextLoads() {
	}
}
