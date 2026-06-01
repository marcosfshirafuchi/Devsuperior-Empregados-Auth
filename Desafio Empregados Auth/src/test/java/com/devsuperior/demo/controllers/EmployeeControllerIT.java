package com.devsuperior.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.devsuperior.demo.dto.EmployeeDTO;
import com.devsuperior.demo.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @SpringBootTest
 *
 * Carrega todo o contexto da aplicação para testes.
 *
 * Diferente de um teste unitário, aqui o Spring inicializa:
 * - Controllers
 * - Services
 * - Repositories
 * - Banco de dados
 * - Spring Security
 * - OAuth2/JWT
 *
 * Por isso este é um TESTE DE INTEGRAÇÃO.
 */
@SpringBootTest

/**
 * Configura automaticamente o MockMvc.
 *
 * O MockMvc permite simular requisições HTTP
 * sem precisar subir um servidor real.
 */
@AutoConfigureMockMvc

/**
 * Executa cada teste dentro de uma transação.
 *
 * Ao final de cada teste o Spring executa rollback,
 * garantindo que o banco volte ao estado original.
 */
@Transactional
public class EmployeeControllerIT {

	/**
	 * Ferramenta utilizada para simular chamadas HTTP.
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Responsável por converter objetos Java em JSON
	 * e JSON em objetos Java.
	 */
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Classe utilitária utilizada para obter
	 * tokens JWT válidos durante os testes.
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
	 * Inicializa os usuários utilizados
	 * para autenticação.
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
	 * Usuário OPERATOR tenta inserir funcionário.
	 *
	 * O endpoint exige ROLE_ADMIN.
	 *
	 * Resultado esperado:
	 * HTTP 403 Forbidden
	 */
	@Test
	public void insertShouldReturn403WhenOperatorLogged() throws Exception {

		// Obtém token JWT do usuário operador
		String accessToken =
				tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);

		// Cria DTO válido
		EmployeeDTO dto =
				new EmployeeDTO(null, "Joaquim", "joaquim@gmail.com", 1L);

		// Converte DTO para JSON
		String jsonBody = objectMapper.writeValueAsString(dto);

		// Executa requisição POST
		ResultActions result =
				mockMvc.perform(post("/employees")
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		// Verifica retorno 403
		result.andExpect(status().isForbidden());
	}

	/**
	 * Cenário:
	 *
	 * Usuário não autenticado tenta inserir funcionário.
	 *
	 * Resultado esperado:
	 * HTTP 401 Unauthorized
	 */
	@Test
	public void insertShouldReturn401WhenNoUserLogged() throws Exception {

		EmployeeDTO dto =
				new EmployeeDTO(null, "Joaquim", "joaquim@gmail.com", 1L);

		String jsonBody = objectMapper.writeValueAsString(dto);

		ResultActions result =
				mockMvc.perform(post("/employees")
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		// Verifica ausência de autenticação
		result.andExpect(status().isUnauthorized());
	}

	/**
	 * Cenário:
	 *
	 * Usuário ADMIN envia dados válidos.
	 *
	 * Resultado esperado:
	 * HTTP 201 Created
	 *
	 * O funcionário deve ser criado com sucesso.
	 */
	@Test
	public void insertShouldInsertResourceWhenAdminLoggedAndCorrectData() throws Exception {

		String accessToken =
				tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

		EmployeeDTO dto =
				new EmployeeDTO(null, "Joaquim", "joaquim@gmail.com", 1L);

		String jsonBody = objectMapper.writeValueAsString(dto);

		ResultActions result =
				mockMvc.perform(post("/employees")
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		// Verifica criação do recurso
		result.andExpect(status().isCreated());

		// Verifica se o ID foi gerado
		result.andExpect(jsonPath("$.id").exists());

		// Verifica os dados retornados
		result.andExpect(jsonPath("$.name").value("Joaquim"));
		result.andExpect(jsonPath("$.email").value("joaquim@gmail.com"));
		result.andExpect(jsonPath("$.departmentId").value(1L));
	}

	/**
	 * Cenário:
	 *
	 * Nome informado contém apenas espaços.
	 *
	 * DTO possui:
	 *
	 * @NotBlank(message = "Campo requerido")
	 *
	 * Resultado esperado:
	 * HTTP 422 Unprocessable Entity
	 */
	@Test
	public void insertShouldReturn422WhenAdminLoggedAndBlankName() throws Exception {

		String accessToken =
				tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

		EmployeeDTO dto =
				new EmployeeDTO(null, "   ", "joaquim@gmail.com", 1L);

		String jsonBody = objectMapper.writeValueAsString(dto);

		ResultActions result =
				mockMvc.perform(post("/employees")
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());

		// Verifica erro de validação do campo name
		result.andExpect(jsonPath("$.errors[0].fieldName").value("name"));
		result.andExpect(jsonPath("$.errors[0].message").value("Campo requerido"));
	}

	/**
	 * Cenário:
	 *
	 * Email inválido.
	 *
	 * DTO possui:
	 *
	 * @Email(message = "Email inválido")
	 *
	 * Resultado esperado:
	 * HTTP 422
	 */
	@Test
	public void insertShouldReturn422WhenAdminLoggedAndInvalidEmail() throws Exception {

		String accessToken =
				tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

		EmployeeDTO dto =
				new EmployeeDTO(null, "Joaquim", "joaquim@", 1L);

		String jsonBody = objectMapper.writeValueAsString(dto);

		ResultActions result =
				mockMvc.perform(post("/employees")
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());

		// Verifica erro no campo email
		result.andExpect(jsonPath("$.errors[0].fieldName").value("email"));
		result.andExpect(jsonPath("$.errors[0].message").value("Email inválido"));
	}

	/**
	 * Cenário:
	 *
	 * DepartmentId não informado.
	 *
	 * DTO possui:
	 *
	 * @NotNull(message = "Campo requerido")
	 *
	 * Resultado esperado:
	 * HTTP 422
	 */
	@Test
	public void insertShouldReturn422WhenAdminLoggedAndNullDepartment() throws Exception {

		String accessToken =
				tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

		EmployeeDTO dto =
				new EmployeeDTO(null, "Joaquim", "joaquim@gmail.com", null);

		String jsonBody = objectMapper.writeValueAsString(dto);

		ResultActions result =
				mockMvc.perform(post("/employees")
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());

		// Verifica erro no campo departmentId
		result.andExpect(jsonPath("$.errors[0].fieldName").value("departmentId"));
		result.andExpect(jsonPath("$.errors[0].message").value("Campo requerido"));
	}
}