package com.betacom.ecommerce.backend.utilities;

import java.util.Map;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.betacom.ecommerce.backend.models.Account;
import com.betacom.ecommerce.backend.models.Anagrafica;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.Carrello;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Ordine;
import com.betacom.ecommerce.backend.models.TipoPagamento;
import com.betacom.ecommerce.backend.models.RigaOrdine;
import com.betacom.ecommerce.backend.models.TipoSpedizione;
import com.betacom.ecommerce.backend.models.StatoOrdine;

public class ObjectDTOMapper {}
/*
@Component
public class ObjectDTOMapper {
    private final Map<Class<?>, Function<Object, ?>> registry;

    public ObjectDTOMapper() {
        this.registry = Map.of(
        	Accounts.class, v -> DtoBuilders.buildAccountsDTO((Accounts) v, true),
        	Anagrafiche.class, v -> DtoBuilders.buildAnagraficheDTO((Anagrafiche) v, true),
        	Autori.class, v -> DtoBuilders.buildAutoriDTO((Autori) v, true),
        	Carrelli.class, v -> DtoBuilders.buildCarrelliDTO((Carrelli) v, true),
        	CaseEditrici.class, v -> DtoBuilders.buildCaseEditriciDTO((CaseEditrici) v, true),
        	Generi.class, v -> DtoBuilders.buildGeneriDTO((Generi) v, true),
        	Manga.class, v -> DtoBuilders.buildMangaDTO((Manga) v, true),
        	Ordini.class, v -> DtoBuilders.buildOrdiniDTO((Ordini) v, true),
        	Pagamenti.class, v -> DtoBuilders.buildPagamentiDTO((Pagamenti) v, true),
        	RigheOrdine.class, v -> DtoBuilders.buildRigheOrdineDTO((RigheOrdine) v, true),
        	Spedizioni.class, v -> DtoBuilders.buildSpedizioniDTO((Spedizioni) v, true),
        	StatiOrdine.class, v -> DtoBuilders.buildStatiOrdineDTO((StatiOrdine) v, true)
        );
    }

    public Object map(Object v) {
        return registry.get(v.getClass()).apply(v);
    }
}*/