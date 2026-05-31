package com.devsuperior.demo.services; // Declara o pacote onde a classe de serviço está localizada

// Importações de classes utilitárias do Java


import com.devsuperior.demo.entities.Role; // Importa a entidade Role
import com.devsuperior.demo.entities.User; // Importa a entidade User
import com.devsuperior.demo.projections.UserDetailsProjection;
import com.devsuperior.demo.repositories.UserRepository; // Importa o repositório de User
import org.springframework.beans.factory.annotation.Autowired; // Importa a anotação @Autowired para injeção de dependência
import org.springframework.security.core.userdetails.UserDetails; // Importa a interface UserDetails do Spring Security
import org.springframework.security.core.userdetails.UserDetailsService; // Importa a interface UserDetailsService do Spring Security
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importa exceção para usuário não encontrado
import org.springframework.stereotype.Service; // Importa a anotação @Service para marcar a classe como um serviço Spring

import java.util.List; // Importa a interface List para coleções de objetos

// Importações de classes de projeção e DTOs (Data Transfer Objects)

// Anotação que marca esta classe como um componente de serviço do Spring
@Service
// Declaração da classe UserService, que implementa UserDetailsService para integração com o Spring Security
public class UserService implements UserDetailsService {

	// Injeção de dependência do UserRepository para operações de banco de dados com a entidade User
	@Autowired
	private UserRepository repository;

	// Implementação do método loadUserByUsername da interface UserDetailsService
	// Usado pelo Spring Security para carregar detalhes do usuário para autenticação
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Busca o usuário e suas roles pelo e-mail usando uma projeção
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		// Verifica se nenhum resultado foi encontrado
		if (result.size() == 0) {
			// Se o e-mail não for encontrado, lança uma exceção
			throw new UsernameNotFoundException("Email not found");
		}
		// Cria uma nova instância de User
		User user = new User();
		// Define o e-mail do usuário
		user.setEmail(username);
		// Define a senha do usuário (obtida da projeção)
		user.setPassword(result.get(0).getPassword());
		// Itera sobre os resultados da projeção para adicionar as roles ao usuário
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		// Retorna o objeto User (que implementa UserDetails)
		return user;
	}
}