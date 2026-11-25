# E-commerce Whitelabel (Java + Spring Boot + Flutter)

Plataforma de e-commerce **multi-tenant whitelabel** onde múltiplas lojas operam sobre a mesma infraestrutura compartilhada, com isolamento lógico por domínio (`X-Client-Domain`), identidade visual dinâmica (nome, logo e cor primária carregados em tempo real pelo frontend), autenticação JWT vinculada ao tenant e catálogo unificado de produtos agregando múltiplos fornecedores externos.

---

## Stack Tecnológica

### Backend (`./src`)
* **Java 21** & **Spring Boot 3**
* **Spring Security** & **JWT (`jjwt`)** — autenticação stateless com claims de `userId`, `email` e `clientId`
* **Spring Data JPA / Hibernate** — persistência relacional com isolamento por tenant
* **PostgreSQL 16** & **Flyway** — versionamento de schema e seed inicial dos tenants e fornecedores
* **SpringDoc OpenAPI (Swagger UI)** — documentação interativa e exportação de collection
* **Docker Compose** — orquestração do banco PostgreSQL

### Frontend (`./flutter_app`)
* **Flutter (Web & Mobile)** com **BLoC (`flutter_bloc`)**
* **Dio** — interceptor HTTP injetando automaticamente `X-Client-Domain` e `Authorization: Bearer <token>`
* **GetIt** — injeção de dependências e arquitetura limpa (`core`, `data`, `domain`, `presentation`)
* **Material 3 Dinâmico** — geração automática da paleta de cores (`ColorScheme.fromSeed`) a partir da configuração da loja

---

## Arquitetura Multi-Tenant & Fluxo de Requisição

1. **Resolução de Tenant (`TenantFilter`):** Intercepta todas as requisições HTTP, lê o header `X-Client-Domain`, valida o cliente ativo no PostgreSQL e armazena o contexto na `ThreadLocal` (`TenantContext`).
2. **Identidade Visual Dinâmica (`GET /client/config`):** Endpoint público que devolve `name`, `logoUrl`, `primaryColor` e os fornecedores habilitados da loja antes mesmo do login.
3. **Autenticação Isolada (`POST /auth/login` e `POST /auth/register`):** Usuários são cadastrados e autenticados dentro do escopo do `client_id` ativo (o mesmo e-mail pode existir em lojas diferentes sem colisão).
4. **Agregação de Catálogo (`GET /products`):** Consulta em paralelo os fornecedores configurados para o tenant (`BRASIL_PROVIDER` e/ou `EUROPEAN_PROVIDER`), normaliza os payloads heterogêneos e aplica filtros por nome, categoria e faixa de preço.

---

## Modelagem de Dados (DER)

```mermaid
erDiagram
    CLIENTS ||--o{ USERS : "possui"
    CLIENTS ||--o{ CLIENT_PROVIDERS : "configura"

    CLIENTS {
        uuid id PK
        varchar name
        varchar domain UK
        varchar logo_url
        varchar primary_color
        boolean active
    }

    USERS {
        uuid id PK
        uuid client_id FK
        varchar name
        varchar email
        varchar password_hash
        varchar role
    }

    CLIENT_PROVIDERS {
        uuid id PK
        uuid client_id FK
        varchar provider_type
        varchar base_url
        boolean active
    }