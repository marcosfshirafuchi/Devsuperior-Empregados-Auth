package com.devsuperior.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.demo.dto.EmployeeDTO;
import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.entities.Employee;
import com.devsuperior.demo.repositories.EmployeeRepository;

/**
 * @Service
 * Indica que esta classe pertence à camada de serviço (Service Layer).
 *
 * A camada de serviço é responsável por:
 * - Implementar as regras de negócio da aplicação;
 * - Fazer a comunicação entre Controller e Repository;
 * - Controlar transações;
 * - Aplicar regras de segurança.
 */
@Service
public class EmployeeService {

	/**
	 * @Autowired
	 * Realiza a injeção de dependência do EmployeeRepository.
	 *
	 * O Spring cria automaticamente uma instância do repositório
	 * e a disponibiliza para uso nesta classe.
	 */
	@Autowired
	private EmployeeRepository repository;

	/**
	 * @PreAuthorize
	 * Define uma regra de autorização baseada em papéis (roles).
	 *
	 * Somente usuários com os papéis:
	 * ROLE_ADMIN ou ROLE_OPERATOR
	 * podem acessar este método.
	 *
	 * @Transactional(readOnly = true)
	 * Indica que a transação é apenas para leitura.
	 *
	 * Benefícios:
	 * - Melhor desempenho;
	 * - Evita alterações acidentais no banco;
	 * - Otimiza o gerenciamento da transação.
	 *
	 * @param pageable Objeto que contém informações de paginação:
	 *                 número da página, quantidade de registros,
	 *                 ordenação etc.
	 *
	 * @return Página contendo EmployeeDTO.
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
	@Transactional(readOnly = true)
	public Page<EmployeeDTO> findAll(Pageable pageable) {

		// Busca todos os funcionários de forma paginada
		Page<Employee> page = repository.findAll(pageable);

		/**
		 * O método map converte cada Employee da página
		 * para um EmployeeDTO.
		 *
		 * x -> new EmployeeDTO(x)
		 *
		 * É uma expressão lambda equivalente a:
		 *
		 * Employee employee = x;
		 * return new EmployeeDTO(employee);
		 */
		return page.map(x -> new EmployeeDTO(x));
	}

	/**
	 * Apenas usuários com ROLE_ADMIN podem inserir funcionários.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")

	/**
	 * @Transactional
	 * Indica que este método executa operações de escrita.
	 *
	 * Caso ocorra alguma exceção durante o processo,
	 * o Spring realiza rollback automaticamente,
	 * desfazendo as alterações no banco.
	 */
	@Transactional
	public EmployeeDTO insert(EmployeeDTO dto) {

		// Cria uma nova entidade Employee vazia
		Employee entity = new Employee();

		// Copia o nome recebido do DTO para a entidade
		entity.setName(dto.getName());

		// Copia o e-mail recebido do DTO para a entidade
		entity.setEmail(dto.getEmail());

		/**
		 * Cria uma referência para o departamento.
		 *
		 * Neste caso, apenas o ID é necessário.
		 *
		 * O JPA utilizará esse ID como chave estrangeira
		 * ao salvar o funcionário.
		 *
		 * O segundo parâmetro (null) representa o nome
		 * do departamento, que não é necessário aqui.
		 */
		entity.setDepartment(new Department(dto.getDepartmentId(), null));

		/**
		 * Salva a entidade no banco de dados.
		 *
		 * Após o save:
		 * - O ID é gerado automaticamente;
		 * - A entidade passa a estar gerenciada pelo JPA.
		 */
		entity = repository.save(entity);

		/**
		 * Converte a entidade salva para DTO
		 * antes de retornar para a camada Controller.
		 *
		 * Isso evita expor diretamente as entidades JPA.
		 */
		return new EmployeeDTO(entity);
	}
}