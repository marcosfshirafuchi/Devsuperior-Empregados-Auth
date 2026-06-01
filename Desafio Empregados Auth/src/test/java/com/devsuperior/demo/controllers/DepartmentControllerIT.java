package com.devsuperior.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.demo.tests.TokenUtil;

/**
 * @SpringBootTest
 *
 * Carrega todo o contexto da aplicação.
 *
 * Diferente dos testes unitários, aqui o Spring inicializa:
 * - Controllers
 * - Services
 * - Repositories
 * - Security
 * - Banco de dados de teste
 *
 * Por isso este é um TESTE DE INTEGRAÇÃO.
 */
@SpringBootTest

/**
 * @AutoConfigureMockMvc
 *
 * Configura automaticamente o MockMvc.
 *
 * O MockMvc permite simular requisições HTTP
 * sem precisar iniciar um servidor Tomcat real.
 */
@AutoConfigureMockMvc

/**
 * @Transactional
 *
 * Cada teste executa dentro de uma transação.
 *
 * Ao final do teste:
 * - Todas as alterações são desfeitas (rollback).
 * - O banco volta ao estado original.
 *
 * Isso garante que um teste não interfira no outro.
 */
@Transactional
public class DepartmentControllerIT {

	/**
	 * MockMvc utilizado para simular chamadas HTTP.
	 *
	 * Exemplo:
	 * GET
	 * POST
	 * PUT
	 * DELETE
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Classe utilitária responsável por:
	 * - Fazer login via OAuth2
	 * - Obter Access Token JWT
	 * - Facilitar testes autenticados
	 */
	@Autowired
	private TokenUtil tokenUtil;

	/**
	 * Usuário com perfil OPERATOR.
	 */
	private String operatorUsername;
	private String operatorPassword;

	/**
	 * Usuário com perfil ADMIN.
	 */
	private String adminUsername;
	private String adminPassword;

	/**
	 * Executado antes de cada teste.
	 *
	 * Inicializa os dados necessários
	 * para autenticação dos usuários.
	 */
	@BeforeEach
	void setUp() throws Exception {

		operatorUsername = "ana@gmail.com";
		operatorPassword = "123456";

		adminUsername = "bob@gmail.com";
		adminPassword = "123456";
	}

	/**
	 * Cenário:
	 *
	 * Um usuário ADMIN realiza uma consulta
	 * para listar departamentos.
	 *
	 * Resultado esperado:
	 * - HTTP 200 OK
	 * - Lista ordenada por nome
	 */
	@Test
	public void findAllShouldReturnAllResourcesSortedByNameWhenAdminLogged() throws Exception {

		/**
		 * Obtém um token JWT válido para o usuário ADMIN.
		 */
		String accessToken =
				tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

		/**
		 * Simula uma requisição GET para /departments
		 * enviando o token JWT no cabeçalho Authorization.
		 */
		ResultActions result =
				mockMvc.perform(get("/departments")
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON));

		/**
		 * Verifica se a resposta retornou HTTP 200.
		 */
		result.andExpect(status().isOk());

		/**
		 * Verifica se os departamentos estão
		 * ordenados alfabeticamente.
		 */
		result.andExpect(jsonPath("$[0].name").value("Management"));
		result.andExpect(jsonPath("$[1].name").value("Sales"));
		result.andExpect(jsonPath("$[2].name").value("Training"));
	}

	/**
	 * Cenário:
	 *
	 * Um usuário OPERATOR realiza uma consulta
	 * para listar departamentos.
	 *
	 * Resultado esperado:
	 * - HTTP 200 OK
	 * - Lista ordenada por nome
	 *
	 * Isso ocorre porque o endpoint permite:
	 *
	 * ROLE_ADMIN
	 * ROLE_OPERATOR
	 */
	@Test
	public void findAllShouldReturnAllResourcesSortedByNameWhenEmployeeLogged() throws Exception {

		/**
		 * Obtém token JWT do usuário operador.
		 */
		String accessToken =
				tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);

		/**
		 * Executa a requisição autenticada.
		 */
		ResultActions result =
				mockMvc.perform(get("/departments")
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON));

		/**
		 * Verifica retorno HTTP 200.
		 */
		result.andExpect(status().isOk());

		/**
		 * Verifica a ordenação dos departamentos.
		 */
		result.andExpect(jsonPath("$[0].name").value("Management"));
		result.andExpect(jsonPath("$[1].name").value("Sales"));
		result.andExpect(jsonPath("$[2].name").value("Training"));
	}

	/**
	 * Cenário:
	 *
	 * Usuário não autenticado tenta acessar
	 * um endpoint protegido.
	 *
	 * Resultado esperado:
	 * HTTP 401 Unauthorized
	 */
	@Test
	public void findAllShouldReturn401WhenNoUserLogged() throws Exception {

		/**
		 * Executa a requisição sem enviar token.
		 */
		ResultActions result =
				mockMvc.perform(get("/departments")
						.contentType(MediaType.APPLICATION_JSON));

		/**
		 * Verifica se o Spring Security
		 * bloqueou o acesso.
		 */
		result.andExpect(status().isUnauthorized());
	}
}