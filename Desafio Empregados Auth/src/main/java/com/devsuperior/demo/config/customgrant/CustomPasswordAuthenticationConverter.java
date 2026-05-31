package com.devsuperior.demo.config.customgrant; // Declara o pacote onde a classe está localizada

// Importações de classes necessárias
import jakarta.servlet.http.HttpServletRequest; // Importa a interface HttpServletRequest para lidar com requisições HTTP
import org.springframework.lang.Nullable; // Importa anotação para indicar que um parâmetro ou retorno pode ser nulo
import org.springframework.security.core.Authentication; // Importa a interface Authentication do Spring Security
import org.springframework.security.core.context.SecurityContextHolder; // Importa SecurityContextHolder para acessar o contexto de segurança
import org.springframework.security.oauth2.core.OAuth2AuthenticationException; // Importa exceção para erros de autenticação OAuth2
import org.springframework.security.oauth2.core.OAuth2ErrorCodes; // Importa códigos de erro padrão do OAuth2
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames; // Importa nomes de parâmetros padrão do OAuth2
import org.springframework.security.web.authentication.AuthenticationConverter; // Importa a interface AuthenticationConverter para converter requisições em objetos de autenticação
import org.springframework.util.LinkedMultiValueMap; // Importa implementação de MultiValueMap
import org.springframework.util.MultiValueMap; // Importa interface para mapas com múltiplos valores por chave
import org.springframework.util.StringUtils; // Importa utilitários para manipulação de strings

import java.util.*; // Importa classes utilitárias como Set, Map, Arrays, HashSet, HashMap

// Esta classe é um conversor de autenticação customizado para o fluxo de concessão "password" do OAuth2.
// Ela implementa a interface AuthenticationConverter para transformar uma HttpServletRequest em um objeto Authentication.
public class CustomPasswordAuthenticationConverter implements AuthenticationConverter {

	// Anotação que indica que o valor de retorno pode ser nulo.
	@Nullable
	@Override
	// Método principal para converter a requisição HTTP em um objeto de autenticação.
	public Authentication convert(HttpServletRequest request) {

		// Obtém o tipo de concessão (grant_type) da requisição.
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);

		// Se o tipo de concessão não for "password", este conversor não se aplica, retorna nulo.
		if (!"password".equals(grantType)) {
			return null;
		}

		// Extrai todos os parâmetros da requisição em um MultiValueMap.
		MultiValueMap<String, String> parameters = getParameters(request);

		// --- Validação e extração de parâmetros específicos do fluxo "password" ---

		// scope (OPTIONAL) - Escopo de acesso solicitado.
		String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
		// Verifica se o parâmetro 'scope' está presente e se há mais de um valor para ele, o que é inválido.
		if (StringUtils.hasText(scope) &&
				parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
			// Lança uma exceção de requisição inválida se a validação falhar.
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		// username (REQUIRED) - Nome de usuário para autenticação.
		String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
		// Verifica se o parâmetro 'username' está ausente ou se há mais de um valor para ele, o que é inválido.
		if (!StringUtils.hasText(username) ||
				parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
			// Lança uma exceção de requisição inválida se a validação falhar.
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		// password (REQUIRED) - Senha do usuário para autenticação.
		String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
		// Verifica se o parâmetro 'password' está ausente ou se há mais de um valor para ele, o que é inválido.
		if (!StringUtils.hasText(password) ||
				parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
			// Lança uma exceção de requisição inválida se a validação falhar.
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		// Conjunto para armazenar os escopos solicitados.
		Set<String> requestedScopes = null;
		// Se o escopo estiver presente na requisição, divide-o por espaços e adiciona ao conjunto.
		if (StringUtils.hasText(scope)) {
			requestedScopes = new HashSet<>(
					Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
		}

		// Mapa para armazenar parâmetros adicionais que não são padrão do OAuth2.
		Map<String, Object> additionalParameters = new HashMap<>();
		// Itera sobre todos os parâmetros da requisição.
		parameters.forEach((key, value) -> {
			// Se a chave não for 'grant_type' nem 'scope', adiciona o primeiro valor ao mapa de parâmetros adicionais.
			if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
					!key.equals(OAuth2ParameterNames.SCOPE)) {
				additionalParameters.put(key, value.get(0));
			}
		});

		// Obtém o principal de autenticação do cliente (que já foi autenticado anteriormente).
		Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

		// Retorna uma nova instância de CustomPasswordAuthenticationToken, contendo o principal do cliente,
		// os escopos solicitados e quaisquer parâmetros adicionais.
		return new CustomPasswordAuthenticationToken(clientPrincipal, requestedScopes, additionalParameters);
	}

	// Método auxiliar estático para extrair parâmetros de uma HttpServletRequest em um MultiValueMap.
	private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {

		// Obtém o mapa de parâmetros da requisição (Map<String, String[]>).
		Map<String, String[]> parameterMap = request.getParameterMap();
		// Cria um novo LinkedMultiValueMap com a capacidade inicial baseada no número de parâmetros.
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
		// Itera sobre o mapa de parâmetros.
		parameterMap.forEach((key, values) -> {
			// Se houver valores para a chave...
			if (values.length > 0) {
				// Adiciona cada valor individualmente ao MultiValueMap.
				for (String value : values) {
					parameters.add(key, value);
				}
			}
		});
		// Retorna o MultiValueMap preenchido.
		return parameters;
	}
}