package com.betacom.ecommerce.backend.dto.outputs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor 
@AllArgsConstructor
public class SagaDTO {
    private Integer id;
    private String nome;        // "One Piece"
    private String immagine;    // saga cover
    private String descrizione;
    private List<MangaDTO> manga;
    private List<AutoreDTO>  autori;
    private List<GenereDTO>  generi;

}
