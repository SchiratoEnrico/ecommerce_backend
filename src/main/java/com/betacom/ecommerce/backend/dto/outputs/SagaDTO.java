package com.betacom.ecommerce.backend.dto.outputs;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class SagaDTO {
    private Integer id;
    private String nome;        // "One Piece"
    private String immagine;    // saga cover
    private String descrizione;
    private List<MangaDTO> manga;
}
