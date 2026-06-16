# Reservas API

API REST simples para gerenciamento de salas e reservas.

O projeto foi desenvolvido com Spring Boot e possui endpoints para cadastrar/listar salas, criar/listar reservas, validar conflitos de horario e retornar erros em formato padronizado.

## Tecnologias

- Java 21
- Spring Boot 3.5.13
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- Flyway
- H2 Database
- Maven Wrapper

## Como rodar

Na raiz do projeto, execute:

```bash
.\mvnw.cmd spring-boot:run
```

A API sobe por padrao em:

```text
http://localhost:8080
```

Para rodar os testes:

```bash
.\mvnw.cmd test
```

## Banco de dados e dados iniciais

O projeto usa Flyway para criar o schema e inserir dados mock.

A migration principal esta em:

```text
src/main/resources/db/migration/V1__create_schema_and_seed_data.sql
```

Ao iniciar a aplicacao, sao criadas as tabelas:

- `salas`
- `reservas`

Tambem sao inseridas salas e reservas iniciais para testar a API.

## Autenticacao

As rotas de reservas usam Basic Auth.

Credenciais:

```text
usuario: admin
senha: 123456
```

As rotas de salas continuam publicas na configuracao atual.

## Endpoints principais

### Salas

Listar salas:

```http
GET /api/v1/salas
```

Buscar sala por ID:

```http
GET /api/v1/salas?id=1
```

Criar sala:

```http
POST /api/v1/salas/criar
```

Exemplo de corpo:

```json
{
  "nome": "Sala Reuniao C",
  "capacidade": 12,
  "localizacao": "Bloco 3"
}
```

### Reservas

Listar reservas:

```http
GET /api/v1/reservas
```

Criar reserva:

```http
POST /api/v1/reservas/criar
```

Exemplo de corpo:

```json
{
  "salaId": 1,
  "nomeSolicitante": "Maria Lima",
  "email": "maria.lima@example.com",
  "dataReserva": "2026-06-20",
  "horaInicio": "09:00:00",
  "horaFim": "10:00:00",
  "finalidade": "Reuniao de alinhamento"
}
```

Exemplo com `curl`:

```bash
curl.exe -i -u admin:123456 http://localhost:8080/api/v1/reservas
```

## Tratamento de erros

A API retorna erros em formato padronizado:

```json
{
  "timestamp": "2026-03-29T20:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ja existe reserva para esta sala no horario informado",
  "path": "/api/v1/reservas/criar"
}
```

Fluxos tratados:

- dados invalidos: `400 Bad Request`
- recurso nao encontrado: `404 Not Found`
- conflito de horario: `409 Conflict`
- acesso nao autorizado: `401 Unauthorized`

Documento detalhado:

```text
docs/tratamento-erros.md
```

## Documentacao adicional

Explicacao do Basic Auth e do `SecurityFilterChain`:

```text
docs/security-filter-chain.md
```

Explicacao dos fluxos de erro:

```text
docs/tratamento-erros.md
```

## Swagger

Com a aplicacao rodando, a documentacao interativa pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```
