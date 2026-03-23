package com.betacom.ecommerce.backend;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.betacom.ecommerce.backend.controllers.AccountControllerTest;
import com.betacom.ecommerce.backend.controllers.AnagraficaControllerTest;
import com.betacom.ecommerce.backend.controllers.AutoreControllerTest;
import com.betacom.ecommerce.backend.controllers.CarrelloControllerTest;
import com.betacom.ecommerce.backend.controllers.CasaEditriceControllerTest;
import com.betacom.ecommerce.backend.controllers.GenereControllerTest;
import com.betacom.ecommerce.backend.controllers.MangaControllerTest;
import com.betacom.ecommerce.backend.controllers.OrdineControllerTest;
import com.betacom.ecommerce.backend.controllers.RigaOrdineControllerTest;
import com.betacom.ecommerce.backend.controllers.StatoOrdineControllerTest;
import com.betacom.ecommerce.backend.controllers.TipoPagamentoControllerTest;
import com.betacom.ecommerce.backend.controllers.TipoSpedizioneControllerTest;


@Suite
@SelectClasses({
	AccountControllerTest.class,
	AnagraficaControllerTest.class,
	AutoreControllerTest.class,
	CasaEditriceControllerTest.class,
	GenereControllerTest.class,
	MangaControllerTest.class,
	CarrelloControllerTest.class,
	OrdineControllerTest.class,
	RigaOrdineControllerTest.class,
	StatoOrdineControllerTest.class,
	TipoPagamentoControllerTest.class,
	TipoSpedizioneControllerTest.class
})


//@SpringBootTest
class EcommerceBackendApplicationTests {
	//@Test
	void contextLoads() {
	}
}
