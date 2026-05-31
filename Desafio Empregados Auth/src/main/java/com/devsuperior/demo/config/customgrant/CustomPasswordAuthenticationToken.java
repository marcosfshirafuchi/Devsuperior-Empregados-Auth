package com.devsuperior.demo.config.customgrant; // Declara o pacote onde a classe está localizada

// Importações de classes necessárias
import org.springframework.lang.Nullable; // Importa anotação para indicar que um parâmetro ou retorno pode ser nulo
import org.springframework.security.core.Authentication; // Importa a interface Authentication do Spring Security
import org.springframework.security.oauth2.core.AuthorizationGrantType; // Importa AuthorizationGrantType para definir o tipo de concessão
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken; // Importa a classe base para tokens de autenticação de concessão OAuth2

import java.util.Collections; // Importa utilitários para coleções, como criar conjuntos imutáveis
import java.util.HashSet; // Importa HashSet para conjuntos
import java.util.Map; // Importa Map para mapas
import java.util.Set; // Importa Set para coleções de elementos únicos

// Esta classe representa um token de autenticação customizado para o fluxo de concessão "password" do OAuth2.
// Ela estende OAuth2AuthorizationGrantAuthenticationToken, que é a base para tokens de autenticação de concessão.
public class CustomPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

	// Identificador de versão para serialização, importante para compatibilidade.
	private static final long serialVersionUID = 1L;

	// Campo para armazenar o nome de usuário fornecido na requisição.
	private final String username;
	// Campo para armazenar a senha fornecida na requisição.
	private final String password;
	// Conjunto de escopos (permissões) solicitados pelo cliente.
	private final Set<String> scopes;

	// Construtor da classe CustomPasswordAuthenticationToken.
	// Recebe o principal do cliente, os escopos solicitados e parâmetros adicionais.
	public CustomPasswordAuthenticationToken(Authentication clientPrincipal,
											 @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {

		// Chama o construtor da classe pai (OAuth2AuthorizationGrantAuthenticationToken).
		// Define o tipo de concessão como "password" e passa o principal do cliente e os parâmetros adicionais.
		super(new AuthorizationGrantType("password"), clientPrincipal, additionalParameters);

		// Extrai o nome de usuário dos parâmetros adicionais.
		this.username = (String) additionalParameters.get("username");
		// Extrai a senha dos parâmetros adicionais.
		this.password = (String) additionalParameters.get("password");
		// Inicializa o conjunto de escopos. Se 'scopes' for nulo, usa um conjunto vazio.
		// Cria um conjunto imutável para garantir que não possa ser modificado após a criação.
		this.scopes = Collections.unmodifiableSet(
				scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
	}

	// Método getter para obter o nome de usuário.
	public String getUsername() {
		return this.username;
	}

	// Método getter para obter a senha.
	public String getPassword() {
		return this.password;
	}

	// Método getter para obter o conjunto de escopos solicitados.
	public Set<String> getScopes() {
		return this.scopes;
	}
}