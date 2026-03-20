package com.betacom.ecommerce.backend;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import com.betacom.ecommerce.backend.controllers.AccountControllerTest;
import com.betacom.ecommerce.backend.controllers.AnagraficaControllerTest;
import com.betacom.ecommerce.backend.controllers.CarrelloControllerTest;
import com.betacom.ecommerce.backend.controllers.TipoPagamentoControllerTest;


@Suite
@SelectClasses({

	AccountControllerTest.class,
	AnagraficaControllerTest.class,
//	CarrelloControllerTest.class,
	TipoPagamentoControllerTest.class
})


@SpringBootTest
class EcommerceBackendApplicationTests {
	@Test
	void contextLoads() {
	}
}
