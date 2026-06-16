# Tratamento de erros da API

Este documento explica o fluxo de tratamento de erros implementado na API `reservas-api`.

O objetivo e fazer a API devolver sempre uma resposta JSON padronizada quando algo der errado.

## Formato padrao

Todos os erros tratados seguem este formato:

```json
{
  "timestamp": "2026-03-29T20:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ja existe reserva para esta sala no horario informado",
  "path": "/api/v1/reservas/criar"
}
```

Campos:

- `timestamp`: data e hora em que o erro foi gerado.
- `status`: codigo HTTP numerico.
- `error`: nome padrao do status HTTP.
- `message`: mensagem explicando o problema.
- `path`: rota chamada pelo cliente.

## Arquivos principais

O tratamento foi organizado no pacote:

```text
src/main/java/br/com/garcia/reservas_api/exceptions
```

Principais classes:

- `ApiErrorResponse`: define o formato padrao do JSON de erro.
- `GlobalExceptionHandler`: captura erros do controller/service e transforma em resposta padronizada.
- `RecursoNaoEncontradoException`: representa erro `404 Not Found`.
- `ConflitoHorarioException`: representa erro `409 Conflict` para choque de horarios.
- `RegraNegocioException`: representa erro `409 Conflict` para outras regras de negocio.
- `ApiAuthenticationEntryPoint`: padroniza erro `401 Unauthorized` do Spring Security.
- `ApiAccessDeniedHandler`: padroniza erro `403 Forbidden`.

## Fluxo 1: dados invalidos

Status retornado:

```text
400 Bad Request
```

Quando acontece:

- campo obrigatorio nao foi enviado;
- campo foi enviado com formato invalido;
- JSON enviado esta quebrado;
- parametro da URL tem tipo invalido.

Exemplo de requisicao invalida:

```http
POST /api/v1/reservas/criar
Authorization: Basic admin:123456
Content-Type: application/json

{}
```

Como `ReservaRequestDTO` possui validacoes como `@NotNull`, `@NotBlank`, `@Email` e `@AssertTrue`, o Spring valida o corpo antes de chamar o service.

Se houver erro, o fluxo e:

1. A requisicao chega no controller.
2. O Spring tenta validar o DTO anotado com `@Valid`.
3. A validacao falha.
4. O Spring lanca `MethodArgumentNotValidException`.
5. `GlobalExceptionHandler` captura a excecao.
6. A API responde com `400 Bad Request` no formato padrao.

Exemplo de resposta:

```json
{
  "timestamp": "2026-03-29T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "salaId: A sala e obrigatoria; nomeSolicitante: O nome do solicitante e obrigatorio",
  "path": "/api/v1/reservas/criar"
}
```

## Fluxo 2: recurso nao encontrado

Status retornado:

```text
404 Not Found
```

Quando acontece:

- buscar sala por ID inexistente;
- criar reserva para uma sala que nao existe;
- buscar/cancelar reserva inexistente em metodos do service.

Exemplo:

```http
POST /api/v1/reservas/criar
Authorization: Basic admin:123456
Content-Type: application/json

{
  "salaId": 999,
  "nomeSolicitante": "Maria Lima",
  "email": "maria.lima@example.com",
  "dataReserva": "2026-06-20",
  "horaInicio": "09:00:00",
  "horaFim": "10:00:00",
  "finalidade": "Reuniao"
}
```

Fluxo:

1. O controller recebe a requisicao.
2. O service tenta buscar a sala pelo `salaId`.
3. O repository nao encontra registro.
4. O service lanca `RecursoNaoEncontradoException`.
5. `GlobalExceptionHandler` captura a excecao.
6. A API responde com `404 Not Found`.

Exemplo de resposta:

```json
{
  "timestamp": "2026-03-29T20:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Sala nao encontrada",
  "path": "/api/v1/reservas/criar"
}
```

## Fluxo 3: conflito de horario

Status retornado:

```text
409 Conflict
```

Quando acontece:

- ja existe uma reserva ativa para a mesma sala;
- a reserva existente esta na mesma data;
- os horarios se sobrepoem.

Exemplo:

Ja existe no banco:

```text
Sala: 1
Data: 2026-06-17
Inicio: 09:00
Fim: 10:00
Status: ATIVA
```

Cliente tenta criar:

```text
Sala: 1
Data: 2026-06-17
Inicio: 09:30
Fim: 10:30
```

Existe conflito porque `09:30` ate `10:30` cruza com `09:00` ate `10:00`.

Fluxo:

1. O controller recebe a criacao da reserva.
2. O service busca a sala.
3. O service verifica se a sala esta ativa.
4. O service consulta o repository para saber se existe reserva ativa sobreposta.
5. Se existir, o service lanca `ConflitoHorarioException`.
6. `GlobalExceptionHandler` captura a excecao.
7. A API responde com `409 Conflict`.

Exemplo de resposta:

```json
{
  "timestamp": "2026-03-29T20:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ja existe reserva para esta sala no horario informado",
  "path": "/api/v1/reservas/criar"
}
```

## Fluxo 4: acesso nao autorizado

Status retornado:

```text
401 Unauthorized
```

Quando acontece:

- cliente tenta acessar rota protegida sem Basic Auth;
- cliente envia usuario ou senha invalidos.

Exemplo:

```http
GET /api/v1/reservas
```

Essa rota e protegida no `SecurityConfig`:

```java
.requestMatchers("/api/v1/reservas", "/api/v1/reservas/**").authenticated()
```

Fluxo:

1. A requisicao chega na aplicacao.
2. Antes do controller, ela passa pelos filtros do Spring Security.
3. O Spring verifica que `/api/v1/reservas` exige autenticacao.
4. Como nao ha credenciais validas, o controller nem e chamado.
5. `ApiAuthenticationEntryPoint` monta a resposta padronizada.
6. A API responde com `401 Unauthorized`.

Exemplo de resposta:

```json
{
  "timestamp": "2026-03-29T20:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Acesso nao autorizado. Envie credenciais Basic Auth validas.",
  "path": "/api/v1/reservas"
}
```

## Fluxo extra: acesso negado

Status retornado:

```text
403 Forbidden
```

Esse fluxo acontece quando o usuario esta autenticado, mas nao tem permissao para executar uma operacao.

Hoje o projeto usa apenas uma role simples (`USER`), entao esse caso pode nao aparecer nos endpoints atuais. Mesmo assim, o handler foi criado para manter o contrato padronizado se regras por perfil forem adicionadas depois.

## Resumo do mapa de erros

| Cenario | Excecao/handler | Status |
| --- | --- | --- |
| Dados invalidos no DTO | `MethodArgumentNotValidException` | `400 Bad Request` |
| JSON invalido | `HttpMessageNotReadableException` | `400 Bad Request` |
| Parametro invalido | `MethodArgumentTypeMismatchException` | `400 Bad Request` |
| Recurso inexistente | `RecursoNaoEncontradoException` | `404 Not Found` |
| Conflito de horario | `ConflitoHorarioException` | `409 Conflict` |
| Regra de negocio | `RegraNegocioException` | `409 Conflict` |
| Sem Basic Auth valido | `ApiAuthenticationEntryPoint` | `401 Unauthorized` |
| Sem permissao | `ApiAccessDeniedHandler` | `403 Forbidden` |

## Como testar rapidamente

Sem autenticacao:

```bash
curl.exe -i http://localhost:8080/api/v1/reservas
```

Com autenticacao e dados invalidos:

```bash
curl.exe -i -u admin:123456 -H "Content-Type: application/json" -d "{}" http://localhost:8080/api/v1/reservas/criar
```

Com sala inexistente:

```bash
curl.exe -i -u admin:123456 -H "Content-Type: application/json" -d "{\"salaId\":999,\"nomeSolicitante\":\"Maria Lima\",\"email\":\"maria.lima@example.com\",\"dataReserva\":\"2026-06-20\",\"horaInicio\":\"09:00:00\",\"horaFim\":\"10:00:00\",\"finalidade\":\"Reuniao\"}" http://localhost:8080/api/v1/reservas/criar
```

Com conflito de horario usando dados mock do Flyway:

```bash
curl.exe -i -u admin:123456 -H "Content-Type: application/json" -d "{\"salaId\":1,\"nomeSolicitante\":\"Maria Lima\",\"email\":\"maria.lima@example.com\",\"dataReserva\":\"2026-06-17\",\"horaInicio\":\"09:30:00\",\"horaFim\":\"10:30:00\",\"finalidade\":\"Reuniao\"}" http://localhost:8080/api/v1/reservas/criar
```
