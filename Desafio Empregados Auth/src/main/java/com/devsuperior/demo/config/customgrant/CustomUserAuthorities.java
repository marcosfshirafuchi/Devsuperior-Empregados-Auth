package com.devsuperior.demo.config.customgrant; // Declara o pacote onde a classe está localizada

// Importações de classes necessárias
import org.springframework.security.core.GrantedAuthority; // Importa a interface GrantedAuthority do Spring Security para representar permissões

import java.util.Collection; // Importa a interface Collection para coleções de objetos

// Esta classe é um DTO (Data Transfer Object) ou um objeto de valor
// usado para encapsular o nome de usuário e suas autoridades (permissões/roles)
// de forma customizada, especialmente para ser usado no contexto do Spring Security
// e do Spring Authorization Server.
public class CustomUserAuthorities {

	// Campo para armazenar o nome de usuário.
	private String username;
	// Campo para armazenar uma coleção de autoridades (permissões) concedidas ao usuário.
	// O '<? extends GrantedAuthority>' indica que pode ser uma coleção de qualquer tipo
	// que estenda GrantedAuthority.
	private Collection<? extends GrantedAuthority> authorities;

	// Construtor da classe CustomUserAuthorities.
	// Inicializa o nome de usuário e a coleção de autoridades.
	public CustomUserAuthorities(String username, Collection<? extends GrantedAuthority> authorities) {
		this.username = username;
		this.authorities = authorities;
	}

	// Método getter para obter o nome de usuário.
	public String getUsername() {
		return username;
	}

	// Método getter para obter a coleção de autoridades do usuário.
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}