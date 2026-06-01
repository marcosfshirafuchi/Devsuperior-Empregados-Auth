package com.devsuperior.demo.dto;

import com.devsuperior.demo.entities.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object)
 *
 * Esta classe é utilizada para transportar dados entre
 * as camadas da aplicação (Controller, Service e Cliente).
 *
 * O objetivo principal é:
 * - Evitar expor diretamente as entidades JPA;
 * - Controlar quais dados serão enviados e recebidos;
 * - Aplicar validações de entrada.
 */
public class EmployeeDTO {

	/**
	 * Identificador do funcionário.
	 *
	 * Normalmente é gerado automaticamente pelo banco de dados.
	 */
	private Long id;

	/**
	 * @NotBlank
	 *
	 * Valida que o campo:
	 * - Não é nulo;
	 * - Não está vazio ("");
	 * - Não contém apenas espaços em branco.
	 *
	 * Caso a validação falhe, será retornada
	 * a mensagem definida em "message".
	 */
	@NotBlank(message = "Campo requerido")
	private String name;

	/**
	 * @Email
	 *
	 * Valida se o valor informado possui
	 * formato de e-mail válido.
	 *
	 * Exemplos válidos:
	 * usuario@email.com
	 * teste@gmail.com
	 *
	 * Exemplos inválidos:
	 * usuario
	 * usuario@
	 * email.com
	 */
	@Email(message = "Email inválido")
	private String email;

	/**
	 * @NotNull
	 *
	 * Garante que o ID do departamento
	 * seja informado.
	 *
	 * Como o relacionamento é obrigatório,
	 * não podemos permitir valor nulo.
	 */
	@NotNull(message = "Campo requerido")
	private Long departmentId;

	/**
	 * Construtor padrão.
	 *
	 * Necessário para:
	 * - Serialização JSON;
	 * - Desserialização JSON;
	 * - Funcionamento do Jackson.
	 */
	public EmployeeDTO() {
	}

	/**
	 * Construtor com argumentos.
	 *
	 * Permite criar um DTO já preenchido.
	 *
	 * @param id ID do funcionário
	 * @param name Nome do funcionário
	 * @param email Email do funcionário
	 * @param departmentId ID do departamento
	 */
	public EmployeeDTO(Long id, String name, String email, Long departmentId) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.departmentId = departmentId;
	}

	/**
	 * Construtor que recebe uma entidade Employee.
	 *
	 * Realiza a conversão Entity -> DTO.
	 *
	 * Muito utilizado na camada Service
	 * para retornar dados ao Controller.
	 *
	 * @param entity Entidade Employee
	 */
	public EmployeeDTO(Employee entity) {

		// Copia o ID da entidade
		id = entity.getId();

		// Copia o nome da entidade
		name = entity.getName();

		// Copia o email da entidade
		email = entity.getEmail();

		/**
		 * Como Employee possui um relacionamento
		 * com Department, obtemos o ID do departamento
		 * associado ao funcionário.
		 */
		departmentId = entity.getDepartment().getId();
	}

	/**
	 * Retorna o ID do funcionário.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Define o ID do funcionário.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Retorna o nome do funcionário.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Define o nome do funcionário.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna o email do funcionário.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Define o email do funcionário.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Retorna o ID do departamento.
	 */
	public Long getDepartmentId() {
		return departmentId;
	}

	/**
	 * Define o ID do departamento.
	 */
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
}