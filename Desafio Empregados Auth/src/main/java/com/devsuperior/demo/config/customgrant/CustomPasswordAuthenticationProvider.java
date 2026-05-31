package com.devsuperior.demo.config.customgrant; // Declara o pacote onde a classe está localizada

// Importações de classes necessárias do Spring Security e OAuth2
import org.springframework.security.authentication.AuthenticationProvider; // Importa a interface AuthenticationProvider para provedores de autenticação
import org.springframework.security.core.Authentication; // Importa a interface Authentication para representar uma autenticação
import org.springframework.security.core.AuthenticationException; // Importa a exceção base para falhas de autenticação
import org.springframework.security.core.context.SecurityContextHolder; // Importa SecurityContextHolder para acessar e manipular o contexto de segurança
import org.springframework.security.core.userdetails.UserDetails; // Importa a interface UserDetails para representar detalhes do usuário
import org.springframework.security.core.userdetails.UserDetailsService; // Importa a interface UserDetailsService para carregar detalhes do usuário
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importa exceção para usuário não encontrado
import org.springframework.security.crypto.password.PasswordEncoder; // Importa a interface PasswordEncoder para codificação e verificação de senhas
import org.springframework.security.oauth2.core.*; // Importa classes core do OAuth2, como OAuth2Error, AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization; // Importa OAuth2Authorization para representar uma autorização OAuth2
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService; // Importa OAuth2AuthorizationService para gerenciar autorizações OAuth2
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType; // Importa OAuth2TokenType para tipos de token OAuth2
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken; // Importa OAuth2AccessTokenAuthenticationToken para representar um token de acesso autenticado
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken; // Importa OAuth2ClientAuthenticationToken para representar um cliente autenticado
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient; // Importa RegisteredClient para representar um cliente OAuth2 registrado
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder; // Importa AuthorizationServerContextHolder para acessar o contexto do servidor de autorização
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext; // Importa DefaultOAuth2TokenContext para construir o contexto do token
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext; // Importa OAuth2TokenContext para o contexto do token
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator; // Importa OAuth2TokenGenerator para gerar tokens OAuth2
import org.springframework.util.Assert; // Importa Assert para validação de argumentos

import java.security.Principal; // Importa Principal para representar o principal de segurança
import java.util.HashSet; // Importa HashSet para conjuntos
import java.util.Set; // Importa Set para coleções de elementos únicos
import java.util.stream.Collectors; // Importa Collectors para operações de stream

// Esta classe é um provedor de autenticação customizado para o fluxo de concessão "password" do OAuth2.
// Ela é responsável por autenticar o usuário e o cliente, e então gerar um token de acesso.
public class CustomPasswordAuthenticationProvider implements AuthenticationProvider {

	// URI de erro padrão conforme especificado na RFC 6749.
	private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

	// Serviço para gerenciar autorizações OAuth2 (salvar, buscar).
	private final OAuth2AuthorizationService authorizationService;

	// Serviço para carregar detalhes do usuário (username, password, authorities).
	private final UserDetailsService userDetailsService;

	// Gerador de tokens OAuth2 (responsável por criar o token de acesso).
	private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

	// Codificador de senhas para verificar a senha do usuário.
	private final PasswordEncoder passwordEncoder;

	// Campos temporários para armazenar o nome de usuário e senha durante o processo de autenticação.
	private String username = "";
	private String password = "";

	// Conjunto de escopos autorizados para o token de acesso.
	private Set<String> authorizedScopes = new HashSet<>();

	// Construtor do provedor de autenticação.
	// Recebe as dependências necessárias para realizar a autenticação e geração de tokens.
	public CustomPasswordAuthenticationProvider(OAuth2AuthorizationService authorizationService,
												OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
												UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

		// Validações para garantir que as dependências não sejam nulas.
		Assert.notNull(authorizationService, "authorizationService cannot be null");
		Assert.notNull(tokenGenerator, "TokenGenerator cannot be null");
		Assert.notNull(userDetailsService, "UserDetailsService cannot be null");
		Assert.notNull(passwordEncoder, "PasswordEncoder cannot be null");

		// Atribui as dependências aos campos da classe.
		this.authorizationService = authorizationService;
		this.tokenGenerator = tokenGenerator;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	// Método principal para realizar a autenticação.
	// Recebe um objeto Authentication (neste caso, CustomPasswordAuthenticationToken) e retorna um Authentication autenticado.
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		// Faz o cast do objeto Authentication para o tipo específico CustomPasswordAuthenticationToken.
		CustomPasswordAuthenticationToken customPasswordAuthenticationToken = (CustomPasswordAuthenticationToken) authentication;

		// Obtém o principal do cliente autenticado ou lança uma exceção se o cliente for inválido.
		OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthenticationToken);

		// Obtém o cliente registrado a partir do principal do cliente.
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

		// Extrai o nome de usuário e a senha do token de autenticação customizado.
		username = customPasswordAuthenticationToken.getUsername();
		password = customPasswordAuthenticationToken.getPassword();

		UserDetails user = null;
		try {
			// Carrega os detalhes do usuário usando o UserDetailsService.
			user = userDetailsService.loadUserByUsername(username);
		} catch (UsernameNotFoundException e) {
			// Se o usuário não for encontrado, lança uma exceção de autenticação OAuth2.
			throw new OAuth2AuthenticationException("Invalid credentials");
		}

		// Verifica se a senha fornecida corresponde à senha armazenada (codificada) e se o nome de usuário corresponde.
		if (!passwordEncoder.matches(password, user.getPassword()) || !user.getUsername().equals(username)) {
			// Se as credenciais forem inválidas, lança uma exceção de autenticação OAuth2.
			throw new OAuth2AuthenticationException("Invalid credentials");
		}

		// Filtra as autoridades (roles) do usuário para incluir apenas aquelas que o cliente registrado tem permissão para solicitar.
		authorizedScopes = user.getAuthorities().stream()
				.map(scope -> scope.getAuthority()) // Mapeia GrantedAuthority para String (nome da role)
				.filter(scope -> registeredClient.getScopes().contains(scope)) // Filtra pelos escopos do cliente
				.collect(Collectors.toSet()); // Coleta em um conjunto

		//-----------Create a new Security Context Holder Context----------
		// Obtém o token de autenticação do cliente do contexto de segurança atual.
		OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

		// Cria um objeto CustomUserAuthorities com o nome de usuário e as autoridades do usuário autenticado.
		CustomUserAuthorities customPasswordUser = new CustomUserAuthorities(username, user.getAuthorities());

		// Define os detalhes do token de autenticação do cliente com o CustomUserAuthorities.
		oAuth2ClientAuthenticationToken.setDetails(customPasswordUser);

		// Cria um novo contexto de segurança vazio.
		var newcontext = SecurityContextHolder.createEmptyContext();

		// Define o token de autenticação do cliente no novo contexto.
		newcontext.setAuthentication(oAuth2ClientAuthenticationToken);

		// Define o novo contexto de segurança como o contexto atual.
		SecurityContextHolder.setContext(newcontext);

		//-----------TOKEN BUILDERS----------
		// Constrói o contexto do token OAuth2, que contém informações necessárias para a geração do token.
		DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
				.registeredClient(registeredClient) // Cliente registrado
				.principal(clientPrincipal) // Principal do cliente
				.authorizationServerContext(AuthorizationServerContextHolder.getContext()) // Contexto do servidor de autorização
				.authorizedScopes(authorizedScopes) // Escopos autorizados
				.authorizationGrantType(new AuthorizationGrantType("password")) // Tipo de concessão (password)
				.authorizationGrant(customPasswordAuthenticationToken); // Token de concessão customizado

		// Constrói o objeto OAuth2Authorization, que representa a autorização concedida.
		OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
				.attribute(Principal.class.getName(), clientPrincipal) // Adiciona o principal do cliente como atributo
				.principalName(clientPrincipal.getName()) // Define o nome do principal
				.authorizationGrantType(new AuthorizationGrantType("password")) // Tipo de concessão
				.authorizedScopes(authorizedScopes); // Escopos autorizados

		//-----------ACCESS TOKEN----------
		// Define o tipo de token como ACCESS_TOKEN e constrói o contexto final do token.
		OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();

		// Gera o token de acesso usando o tokenGenerator.
		OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);

		// Verifica se o token de acesso foi gerado com sucesso.
		if (generatedAccessToken == null) {
			// Se a geração falhar, lança uma exceção de erro do servidor.
			OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
					"The token generator failed to generate the access token.", ERROR_URI);
			throw new OAuth2AuthenticationException(error);
		}

		// Cria um objeto OAuth2AccessToken a partir do token gerado.
		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
				generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());

		// Se o token gerado for um ClaimAccessor (como um JWT), adiciona os claims ao objeto de autorização.
		if (generatedAccessToken instanceof ClaimAccessor) {
			authorizationBuilder.token(accessToken, (metadata) ->
					metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
		} else {
			// Caso contrário, adiciona o token de acesso diretamente.
			authorizationBuilder.accessToken(accessToken);
		}

		// Constrói o objeto OAuth2Authorization final.
		OAuth2Authorization authorization = authorizationBuilder.build();

		// Salva a autorização no serviço de autorização.
		this.authorizationService.save(authorization);

		// Retorna um OAuth2AccessTokenAuthenticationToken, indicando que o token de acesso foi gerado e o cliente autenticado.
		return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken);
	}

	@Override
	// Indica se este provedor de autenticação suporta o tipo de autenticação fornecido.
	public boolean supports(Class<?> authentication) {
		// Retorna true se o tipo de autenticação for CustomPasswordAuthenticationToken ou uma subclasse dele.
		return CustomPasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	// Método auxiliar estático para obter o principal do cliente autenticado.
	private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {

		OAuth2ClientAuthenticationToken clientPrincipal = null;
		// Verifica se o principal da autenticação é um OAuth2ClientAuthenticationToken.
		if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
			clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
		}
		// Se o principal do cliente não for nulo e estiver autenticado, retorna-o.
		if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
			return clientPrincipal;
		}
		// Caso contrário, lança uma exceção de cliente inválido.
		throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
	}
}