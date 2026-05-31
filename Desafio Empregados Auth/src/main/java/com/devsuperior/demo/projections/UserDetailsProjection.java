package com.devsuperior.demo.projections; // Declara o pacote onde a interface de projeção está localizada

// Esta interface é uma projeção (ou DTO de projeção) usada para buscar dados específicos
// de um usuário e suas roles (perfis de acesso) diretamente do banco de dados.
// Ela é útil para otimizar consultas, retornando apenas os campos necessários,
// especialmente para o processo de autenticação do Spring Security.
public interface UserDetailsProjection {
    // Método para obter o nome de usuário (geralmente o email)
    String getUsername();
    // Método para obter a senha do usuário
    String getPassword();
    // Método para obter o ID da role (perfil de acesso)
    Long getRoleId();
    // Método para obter o nome da autoridade/permissão (ex: "ROLE_ADMIN")
    String getAuthority();
}