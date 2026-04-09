package com.betacom.ecommerce.backend.dto.inputs;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdineRequest {
	private Integer id;
	private Integer account;
	private Integer pagamentoId;
	private Integer spedizioneId;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate data;

	private String stato;
	private Integer anagrafica;

	private List<RigaOrdineRequest> righeOrdineRequest;
}
