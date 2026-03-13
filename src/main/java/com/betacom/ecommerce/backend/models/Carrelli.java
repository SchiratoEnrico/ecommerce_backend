package com.betacom.ecommerce.backend.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="carrelli")
public class Carrelli {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne(
			mappedBy = "utenti",
			cascade = CascadeType.REMOVE
			)
	private Accounts account;
	
	@OneToMany(
			mappedBy = "utenti",
			fetch = FetchType.EAGER
			)
	private List<Manga> manga;
}
