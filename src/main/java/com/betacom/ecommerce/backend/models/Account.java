package com.betacom.ecommerce.backend.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
	
	@Column (nullable = false)
	private String ruolo;
	
	@OneToMany(fetch = FetchType.EAGER,
			cascade = CascadeType.ALL
			) 
	@JoinColumn(name = "id_account")
	private List<Anagrafica> anagrafiche;
	
	@OneToOne(
			mappedBy = "account"
			)
	private Carrello carrello;
}