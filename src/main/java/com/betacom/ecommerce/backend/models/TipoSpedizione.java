package com.betacom.ecommerce.backend.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tipi_spedizione")
public class TipoSpedizione {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=100,
			nullable=false,
			unique=true
			)
	private String tipoSpedizione;
	
    @Column(name = "costo_spedizione", 
    		precision = 10, 
    		scale = 2)
    private BigDecimal costoSpedizione = BigDecimal.ZERO;
}
