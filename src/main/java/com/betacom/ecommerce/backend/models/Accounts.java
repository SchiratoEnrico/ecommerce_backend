package com.betacom.ecommerce.backend.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "accounts")
public class Accounts {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (length = 100,
			nullable = false)
	private String username;
	
	@Column (length = 100,
			nullable = false)
	private String email;
	
	@Column (nullable = false)
	private String ruolo;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_anagrafica")
	private List<Anagrafiche> anagrafiche;
	
	@OneToOne(fetch = FetchType.EAGER,
			mappedBy = "account")
	private Carrelli carrello;
	
	
}
