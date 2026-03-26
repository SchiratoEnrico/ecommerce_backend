package com.betacom.ecommerce.backend.dto.inputs;

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
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SagaRequest {
    private Integer id;
    private String nome;        // "One Piece"
    private String immagine;    // saga cover
    private String descrizione;
    private List<String> manga;
}
