# <a href="https://imgbb.com/"><img src="https://i.ibb.co/S42fsBL4/Devsuperior-logo.png" alt="Devsuperior logo" border="0" width="300" height="100"></a> Java Spring Expert - Desafio Empregados Auth

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-brightgreen)
![OAuth2](https://img.shields.io/badge/OAuth2-Authorization-red)
![JWT](https://img.shields.io/badge/JWT-Authentication-blue)
![H2 Database](https://img.shields.io/badge/H2-Database-lightgrey)

## 👨‍💻 Desenvolvido por

**Marcos Shirafuchi**

- GitHub: https://github.com/marcosfshirafuchi
- Desenvolvedor Backend Java
- Formação Desenvolvedor Moderno - DevSuperior

---

## 📚 Sobre o Projeto

Este projeto foi desenvolvido como parte do curso **Java Spring Expert**, ministrado pelo professor **Nélio Alves**, na plataforma DevSuperior.

O objetivo do desafio é implementar um sistema de gerenciamento de funcionários e departamentos com autenticação, autorização baseada em perfis de usuário e validações de dados utilizando os recursos modernos do ecossistema Spring.

---

## 🎯 Objetivos do Desafio

- Autenticação com OAuth2
- Geração e validação de Token JWT
- Controle de acesso baseado em perfis
- Validação de dados com Bean Validation
- Tratamento global de exceções
- Segurança de APIs REST com Spring Security

---

## 🏗️ Modelo Conceitual

O sistema consiste em um gerenciamento de funcionários e departamentos com autenticação e autorização baseada em perfis de usuário.

<p align="center">
    <img src="https://i.ibb.co/fz3zbgbW/Chat-GPT-Image-31-de-mai-de-2026-22-38-48.png" alt="Modelo Conceitual Empregados Auth" width="900">
</p>

### Relacionamentos

#### User ↔ Role

Um usuário pode possuir um ou mais perfis de acesso:

- ROLE_ADMIN
- ROLE_OPERATOR

#### Employee ↔ Department

Cada funcionário pertence a um departamento, enquanto um departamento pode possuir vários funcionários.

---

## ✨ Funcionalidades

- Autenticação utilizando OAuth2
- Geração e validação de JWT
- Controle de acesso baseado em perfis
- Consulta de funcionários
- Atualização de funcionários
- Validação de dados com Bean Validation
- Tratamento global de exceções
- Proteção de endpoints com Spring Security

---

## 🔐 Regras de Controle de Acesso

Todas as rotas da aplicação são protegidas.

### Perfil ADMIN

Pode:

- Consultar recursos
- Atualizar recursos

### Perfil OPERATOR

Pode:

- Consultar recursos

Não pode:

- Alterar recursos

---

## ✅ Validações Implementadas

### Employee

- Nome obrigatório
- E-mail obrigatório e válido
- Departamento obrigatório

```java
@NotBlank
private String name;

@Email
@NotBlank
private String email;

@NotNull
private Department department;
```

---

## 🚀 Tecnologias Utilizadas

### Backend

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Authorization Server
- JWT

### Banco de Dados

- H2 Database

### Testes

- JUnit 5
- Mockito

### Ferramentas

- Maven
- Postman
- IntelliJ IDEA

---

## 🔑 Segurança

O projeto utiliza:

- Spring Security
- OAuth2 Authorization Server
- Resource Server
- JWT Token
- Controle de acesso por perfil (RBAC)

| Perfil | Permissões |
|---------|------------|
| ADMIN | Leitura e alteração |
| OPERATOR | Apenas leitura |

---

## 🔗 Endpoints Principais

| Método | Endpoint | Descrição |
|----------|----------|----------|
| GET | /employees | Lista funcionários |
| GET | /employees/{id} | Busca funcionário por ID |
| PUT | /employees/{id} | Atualiza funcionário |
| POST | /oauth2/token | Gera token JWT |

---

## 📂 Estrutura do Projeto

```text
src
├── main
│   ├── java
│   │   ├── config
│   │   ├── controllers
│   │   ├── dto
│   │   ├── entities
│   │   ├── repositories
│   │   ├── services
│   │   └── security
│   └── resources
└── test
    └── java
```

---

## ▶️ Como Executar o Projeto

```bash
git clone https://github.com/marcosfshirafuchi/Devsuperior-Empregados-Auth.git
cd Devsuperior-Empregados-Auth
mvn spring-boot:run
```

---

## 🧪 Executar os Testes

```bash
mvn test
```

---

## 📖 Aprendizados

Durante este desafio foram praticados conceitos fundamentais para aplicações corporativas modernas:

- Spring Security
- OAuth2 Authorization Server
- Resource Server
- JWT
- Controle de acesso baseado em perfis (RBAC)
- Bean Validation
- Tratamento global de exceções
- APIs REST seguras
- Testes automatizados
- Boas práticas com Spring Boot

---

## 🎓 Curso

Java Spring Expert

Professor: Nélio Alves

Plataforma DevSuperior

https://devsuperior.com.br

---

## ⭐ Agradecimento

Agradecimento ao professor Nélio Alves e à DevSuperior pela excelente formação em desenvolvimento Java e Spring Boot.
