package com.betacom.ecommerce.backend.utilities;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.betacom.ecommerce.backend.dto.outputs.AutoreDTO;
import com.betacom.ecommerce.backend.dto.outputs.GenereDTO;
import com.betacom.ecommerce.backend.dto.outputs.MangaDTO;
import com.betacom.ecommerce.backend.dto.outputs.SagaDTO;
import com.betacom.ecommerce.backend.models.Autore;
import com.betacom.ecommerce.backend.models.CasaEditrice;
import com.betacom.ecommerce.backend.models.Genere;
import com.betacom.ecommerce.backend.models.Manga;
import com.betacom.ecommerce.backend.models.Saga;
import com.betacom.ecommerce.backend.repositories.IAutoreRepository;
import com.betacom.ecommerce.backend.repositories.IGenereRepository;
import com.betacom.ecommerce.backend.services.interfaces.IUploadServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageDtoBuilders {
	private final IUploadServices uploS;
	private final IAutoreRepository autoR;
	private final IGenereRepository geneR;
	
	public MangaDTO buildMangaDTO(Manga m, Optional<CasaEditrice> c, Optional<Set<Autore>> lA, Optional<Set<Genere>> lG, Optional<Saga> s) {
        
		return MangaDTO.builder()
                .isbn(m.getIsbn())
                .titolo(m.getTitolo())
                .immagine(uploS.buildUrl(m.getImmagine()))
                .sagaVol(m.getSagaVol())
                .numeroCopie(m.getNumeroCopie())
                .prezzo(m.getPrezzo())
                .dataPubblicazione(m.getDataPubblicazione())
				.casaEditrice(c.isPresent()?
						DtoBuilders.buildCasaEditriceDTO(c.get()) : null)
				.autori(
						lA.isPresent() ?
						lA.get().stream()
							.map(a -> DtoBuilders.buildAutoreDTO(a, Optional.empty())).toList() :
								null
						)
				.generi(
						lG.isPresent() ?
								lG.get().stream()
									.map(g -> DtoBuilders.buildGenereDTO(g, Optional.empty())).toList() :
										null
						)
				.saga(s.isPresent()?
						buildSagaDTO(s.get(), Optional.empty()): null)
                .build();
    }

	public SagaDTO buildSagaDTO(Saga s, Optional<List<Manga>> mangaList) {
	    List<Manga> lM = mangaList.orElse(null);
	    return SagaDTO.builder()
	            .id(s.getId())
	            .nome(s.getNome())
	            .descrizione(s.getDescrizione())
	            .immagine(uploS.buildUrl(s.getImmagine()))
	            .manga(lM != null
	                ? lM.stream()
	                    .map(m -> buildMangaDTO(
	                        m,
	                        Optional.empty(),
	                        Optional.empty(),
	                        Optional.empty(),
	                        Optional.empty()))  // empty = no saga back-ref → no recursion
	                    .toList()
	                : null)
	            .autori(lM != null ? computeAutori(lM) : null)
	            .generi(lM != null ? computeGeneri(lM) : null)
	            .build();
	}
	
	private List<AutoreDTO> computeAutori(List<Manga> manga) {
		//List<Autore> = 
		Set<Integer> ids = manga.stream()
		        .flatMap(m -> autoR.findAllByMangaIsbn(m.getIsbn()).stream())
		        .map(a -> a.getId())
		        .collect(Collectors.toSet());
		return autoR.findAllById(ids).stream()
				.map(a -> DtoBuilders.buildAutoreDTO(a, Optional.empty()))
				.collect(Collectors.toList());
	}

	private List<GenereDTO> computeGeneri(List<Manga> manga) {
		Set<Integer> ids = manga.stream()
		        .flatMap(m -> geneR.findAllByMangaIsbn(m.getIsbn()).stream())
		        .map(a -> a.getId())
		        .collect(Collectors.toSet());
	    return geneR.findAllById(ids).stream()
	    			.map(g -> DtoBuilders.buildGenereDTO(g, Optional.empty()))
	    			.toList();
	}
}
