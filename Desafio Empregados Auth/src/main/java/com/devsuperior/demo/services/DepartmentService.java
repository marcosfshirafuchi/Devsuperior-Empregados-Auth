package com.devsuperior.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.devsuperior.demo.dto.DepartmentDTO;
import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.repositories.DepartmentRepository;

/**
 * @Service
 *
 * Indica que esta classe pertence à camada de serviço.
 *
 * Responsabilidades da camada Service:
 * - Implementar regras de negócio;
 * - Intermediar a comunicação entre Controller e Repository;
 * - Aplicar regras de segurança;
 * - Realizar transformações entre Entity e DTO.
 */
@Service
public class DepartmentService {

	/**
	 * @Autowired
	 *
	 * Injeta automaticamente uma instância do
	 * DepartmentRepository criada pelo Spring.
	 *
	 * O Repository é responsável pelo acesso ao banco de dados.
	 */
	@Autowired
	private DepartmentRepository repository;

	/**
	 * @PreAuthorize
	 *
	 * Define uma regra de autorização para o método.
	 *
	 * Somente usuários que possuam um dos papéis:
	 *
	 * ROLE_ADMIN
	 * ROLE_OPERATOR
	 *
	 * poderão executar esta operação.
	 *
	 * Caso o usuário não possua um desses papéis,
	 * o Spring Security retornará erro 403 (Forbidden).
	 *
	 * @return Lista de departamentos convertidos para DTO.
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
	public List<DepartmentDTO> findAll() {

		/**
		 * Busca todos os departamentos do banco.
		 *
		 * Sort.by("name")
		 * ordena os registros pelo atributo "name"
		 * em ordem crescente (A-Z).
		 *
		 * Exemplo:
		 *
		 * Financeiro
		 * Marketing
		 * Recursos Humanos
		 * Tecnologia
		 */
		List<Department> list = repository.findAll(Sort.by("name"));

		/**
		 * Stream API
		 *
		 * Converte cada Department da lista
		 * para um DepartmentDTO.
		 *
		 * Fluxo:
		 *
		 * List<Department>
		 *        ↓
		 * stream()
		 *        ↓
		 * map()
		 *        ↓
		 * List<DepartmentDTO>
		 *
		 * A expressão lambda:
		 *
		 * x -> new DepartmentDTO(x)
		 *
		 * significa:
		 *
		 * para cada departamento encontrado,
		 * crie um novo DTO correspondente.
		 */
		return list.stream()

				/**
				 * Converte cada Department em DepartmentDTO.
				 */
				.map(x -> new DepartmentDTO(x))

				/**
				 * Converte o Stream em uma List.
				 *
				 * Método disponível a partir do Java 16.
				 */
				.toList();
	}
}