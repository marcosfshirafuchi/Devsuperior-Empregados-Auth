package com.devsuperior.demo.config; // Declara o pacote onde a classe está localizada

// Importações necessárias para a configuração do Spring
import org.springframework.context.annotation.Bean; // Importa a anotação @Bean para declarar um bean gerenciado pelo Spring
import org.springframework.context.annotation.Configuration; // Importa a anotação @Configuration para indicar que esta classe é uma fonte de definições de beans
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importa a implementação BCryptPasswordEncoder para codificação de senhas
import org.springframework.security.crypto.password.PasswordEncoder; // Importa a interface PasswordEncoder do Spring Security

// Anotação que indica que esta classe contém métodos que definem beans para o contexto de aplicação do Spring
@Configuration
public class AppConfig { // Declara a classe de configuração da aplicação

	// Anotação que indica que o método abaixo produz um bean a ser gerenciado pelo contêiner Spring
	@Bean
	// Método que define e retorna uma instância de PasswordEncoder
	PasswordEncoder passwordEncoder() {
		// Retorna uma nova instância de BCryptPasswordEncoder, que é uma implementação robusta para hash de senhas
		return new BCryptPasswordEncoder();
	}
}