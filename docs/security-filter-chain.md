# SecurityFilterChain no reservas-api

Este documento explica, de forma basica e detalhada, a implementacao de seguranca atual do projeto `reservas-api`.

O arquivo principal e:

```text
src/main/java/br/com/garcia/reservas_api/config/SecurityConfig.java
```

## Objetivo da configuracao

A classe `SecurityConfig` configura o Spring Security para proteger os endpoints de reservas usando HTTP Basic Auth.

Na pratica, isso significa:

- algumas rotas exigem usuario e senha;
- outras rotas continuam publicas;
- a autenticacao acontece antes da requisicao chegar no controller;
- o controller nao precisa ter codigo extra para ficar protegido.

## O que é SecurityFilterChain

`SecurityFilterChain` e a cadeia de filtros de seguranca do Spring Security.

Antes de uma requisicao chegar em um controller, ela passa por essa cadeia. Nessa etapa, o Spring pode verificar regras como:

- a rota acessada precisa de login?
- o usuario enviou credenciais validas?
- qual tipo de autenticacao sera usado?
- a requisicao pode continuar ou deve retornar erro `401 Unauthorized`?

No projeto, o metodo abaixo cria essa cadeia:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/reservas", "/api/v1/reservas/**").authenticated()
                    .anyRequest().permitAll())
            .httpBasic(Customizer.withDefaults())
            .build();
}
```

## Explicacao por partes

### `@Configuration`

```java
@Configuration
```

Indica que a classe possui configuracoes que devem ser carregadas pelo Spring.

Nesse caso, a classe declara beans relacionados a seguranca da aplicacao.

### `@EnableWebSecurity`

```java
@EnableWebSecurity
```

Habilita a configuracao de seguranca web do Spring Security.

Em projetos Spring Boot modernos, muitas configuracoes ja sao ativadas automaticamente, mas essa anotacao deixa explicito que esta classe controla regras de seguranca web.

### `@Bean`

```java
@Bean
```

Indica que o objeto retornado pelo metodo deve ser gerenciado pelo Spring.

No caso do `SecurityFilterChain`, o Spring usa esse bean para saber quais regras aplicar nas requisicoes HTTP.

## CSRF desabilitado

```java
.csrf(csrf -> csrf.disable())
```

Essa linha desabilita a protecao CSRF.

CSRF e uma protecao importante em aplicacoes web que usam sessao e formularios renderizados no navegador.

Para uma API REST simples usando Basic Auth, como neste projeto, é comum desabilitar CSRF para evitar bloqueio em requisicoes `POST`, `PUT` e `DELETE` feitas por ferramentas como Postman, Insomnia, Swagger ou `curl`.

## Regras de autorizacao

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/reservas", "/api/v1/reservas/**").authenticated()
        .anyRequest().permitAll())
```

Essa parte define quais rotas precisam de autenticacao.

### Rotas protegidas

```java
.requestMatchers("/api/v1/reservas", "/api/v1/reservas/**").authenticated()
```

Essa regra diz que as rotas abaixo exigem usuario e senha:

```text
GET  /api/v1/reservas
POST /api/v1/reservas/criar
```

Isso funciona porque o `ReservaController` usa o prefixo:

```java
@RequestMapping("/api/v1/reservas")
```

E o endpoint de criacao usa:

```java
@PostMapping("/criar")
```

Portanto, o caminho completo fica:

```text
/api/v1/reservas/criar
```

Como a configuracao possui `"/api/v1/reservas/**"`, esse endpoint tambem fica protegido.

### Rotas publicas

```java
.anyRequest().permitAll()
```

Essa regra diz que qualquer rota que nao bateu com a regra anterior sera publica.

No estado atual do projeto, isso significa que rotas como as de salas continuam sem exigir login:

```text
GET  /api/v1/salas
GET  /api/v1/salas?id=1
POST /api/v1/salas/criar
```

Se a intencao fosse proteger toda a API, a regra poderia ser alterada para:

```java
.requestMatchers("/api/v1/**").authenticated()
.anyRequest().permitAll()
```

## Basic Auth

```java
.httpBasic(Customizer.withDefaults())
```

Essa linha habilita HTTP Basic Auth.

Nesse tipo de autenticacao, o cliente envia usuario e senha em cada requisicao protegida.

Exemplo de credenciais usadas no projeto:

```text
usuario: admin
senha: 123456
```

Quando a requisicao nao possui credenciais validas, o Spring Security bloqueia antes de chegar no controller e retorna `401 Unauthorized`.

## Usuario em memoria

```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails usuario = User
            .withUsername("admin")
            .password(passwordEncoder().encode("123456"))
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(usuario);
}
```

Esse metodo cria um usuario em memoria.

Isso quer dizer que:

- o usuario não esta salvo no banco de dados;
- ele existe apenas enquanto a aplicacao esta rodando;
- é uma abordagem simples para estudo, testes e demonstracoes;
- nao é a melhor abordagem para uma aplicacao real em prod.

O usuario criado é:

```text
username: admin
password: 123456
role: USER
```

## Criptografia da senha

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

O `PasswordEncoder` define como a senha sera codificada.

Neste projeto, a senha `123456` nao é guardada como texto puro dentro do usuario. Ela é passada por:

```java
passwordEncoder().encode("123456")
```

Isso gera uma versao criptografada usando BCrypt.

O Spring Security compara a senha enviada na requisicao com essa versao codificada.

## Fluxo de uma requisicao protegida

Exemplo:

```text
GET /api/v1/reservas
```

Fluxo:

1. A requisicao chega na aplicacao.
2. Antes do `ReservaController`, ela passa pelo `SecurityFilterChain`.
3. O Spring verifica se `/api/v1/reservas` precisa de autenticacao.
4. Como precisa, o Spring procura credenciais Basic Auth na requisicao.
5. Se nao houver usuario e senha, retorna `401 Unauthorized`.
6. Se houver usuario e senha invalidos, retorna `401 Unauthorized`.
7. Se as credenciais forem `admin:123456`, a requisicao continua.
8. A requisicao finalmente chega no metodo do `ReservaController`.

## Como testar

Com a aplicacao rodando em `localhost:8080`, uma requisicao sem autenticacao deve ser bloqueada:

```bash
curl.exe -i http://localhost:8080/api/v1/reservas
```

Resultado esperado:

```text
HTTP/1.1 401
```

Com usuario e senha:

```bash
curl.exe -i -u admin:123456 http://localhost:8080/api/v1/reservas
```

Resultado esperado:

```text
HTTP/1.1 200
```

O status final pode variar se houver outro erro de regra de negocio ou banco de dados, mas a autenticacao sera aceita.

## Precisa alterar o controller?

Não.

Para esse caso, a protecao esta correta apenas na classe `SecurityConfig`.

O controller nao precisa receber anotacoes extras para essa regra funcionar, porque o Spring Security filtra a requisicao antes dela chegar no controller.

So faria sentido adicionar algo no controller se o projeto precisasse de regras mais especificas, como:

```java
@PreAuthorize("hasRole('ADMIN')")
```

Esse tipo de anotacao seria usado para controlar permissao por metodo, por papel de usuario ou por regra mais detalhada.

## Pontos de atencao

A implementacao atual e boa para aprendizado e demonstracao, mas possui limites importantes:

- a senha esta fixa no codigo;
- existe apenas um usuario em memoria;
- apenas as rotas de reservas estao protegidas;
- as rotas de salas continuam publicas;
- Basic Auth deve ser usado com HTTPS em ambientes reais;
- para producao, o ideal seria buscar usuarios em banco de dados ou usar outro mecanismo de autenticacao.

## Resumo

O `SecurityFilterChain` atual:

- desabilita CSRF;
- protege `/api/v1/reservas` e subrotas;
- deixa as demais rotas publicas;
- habilita HTTP Basic Auth;
- usa um usuario em memoria chamado `admin`;
- codifica a senha com BCrypt;
- nao exige nenhuma alteracao no `ReservaController`.