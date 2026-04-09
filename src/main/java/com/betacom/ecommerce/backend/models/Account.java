package com.betacom.ecommerce.backend.models;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;

import com.betacom.ecommerce.backend.enums.Ruoli;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "accounts")
@ToString(exclude = {"anagrafiche", "carrello"})
@DynamicInsert //per poter usare Boolean in validated 
public class Account {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (length = 100,
			nullable = false,
			unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column (length = 100,
			nullable = false,
			unique = true)
	private String email;
	
	@Column(
			nullable = false,
			name = "data_creazione"
			)
	private LocalDateTime dataCreazione;
	
	@Enumerated(EnumType.STRING)
	@Column (nullable = false, length = 20)
	private Ruoli ruolo;
	
	@OneToMany(
	        mappedBy = "account",   
	        fetch = FetchType.LAZY,
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	    )
	private List<Anagrafica> anagrafiche;
	
	@OneToOne(
			mappedBy = "account",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true
			)
	private Carrello carrello;
	
	@Column (name="email_validation",
			nullable= false,
			columnDefinition = "BOOLEAN DEFAULT false")
	Boolean validated;
}