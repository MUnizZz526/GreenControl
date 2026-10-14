# GreenControl — API REST com Spring Boot e JPA

<div align="center">

### Monitoramento Inteligente de Rodovias & Gestão Operacional de Vegetação
**Challenge FIAP — Sprint 4: A Grande Virada Tecnológica (Spring Boot + Spring Data JPA + API REST)**

---

![Java](https://img.shields.io/badge/Java-17%20%7C%2021-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.x-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data-JPA-blue?style=for-the-badge)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Oracle Database](https://img.shields.io/badge/Oracle_Database-19c-red?style=for-the-badge&logo=oracle&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build_Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Status](https://img.shields.io/badge/Status-Concluído-success?style=for-the-badge)
![Sprint](https://img.shields.io/badge/Sprint-4-purple?style=for-the-badge)

</div>

---

## 1. Contexto e Evolução do Projeto

O **GreenControl** (desenvolvido no contexto do desafio MOTIVA) evoluiu ao longo de 4 etapas:

| Sprint | Escopo e Tecnologias |
| :--- | :--- |
| **Sprint 1** | Modelagem do Domínio base (`TrechoRodovia`, `EquipeManutencao`), encapsulamento e protótipo console. |
| **Sprint 2** | Motor de regras de negócio, classes abstratas (`IntervencaoOperacional`), interfaces (`MonitoravelViaIoT`), microclima e polimorfismo. |
| **Sprint 3** | Persistência Relacional com **JDBC puro**, pattern DAO, CRUD manual via `PreparedStatement` e gravação de relatórios no Oracle FIAP. |
| **Sprint 4 (Atual)** | **A Grande Virada Tecnológica**: Migração completa para **Spring Boot 4.1.x**, **Spring Data JPA**, **Driver Maven Oficial (`ojdbc17`)**, **API REST profissional**, DTOs, Bean Validation, tratamento global com `@ControllerAdvice` e testes com `@SpringBootTest` + `MockMvc`. |

### A Virada: De JDBC Puro para Spring Data JPA

```java
// Sprint 3 (JDBC puro): ~40 linhas por operação com Connection, PreparedStatement e try-catch-finally
Connection conn = ConexaoBD.getInstancia().conectar();
PreparedStatement stmt = conn.prepareStatement("INSERT INTO T_TRECHO_RODOVIA (...) VALUES (...)");
// mapeamento manual de parâmetros, executeUpdate, fechamento manual no finally...

// Sprint 4 (Spring Data JPA): 1 linha expressiva e desacoplada
trechoRepository.save(trecho);
```

---

## 2. Arquitetura em Camadas (Clean Architecture)

A aplicação adota estritamente a separação de responsabilidades em camadas unidirecionais:
$$\text{Controller} \longrightarrow \text{Service} \longrightarrow \text{Repository} \longrightarrow \text{Banco Oracle}$$

```text
src/
├── main/
│   ├── java/br/com/greencontrol/
│   │   ├── GreenControlApplication.java      # Ponto de entrada @SpringBootApplication
│   │   │
│   │   ├── controller/                       # Controladores REST (@RestController + JSON)
│   │   │   ├── EquipeManutencaoController.java
│   │   │   ├── TrechoRodoviaController.java
│   │   │   ├── IntervencaoOperacionalController.java
│   │   │   └── RelatorioPrioridadeController.java
│   │   │
│   │   ├── service/                          # Regras de Negócio (@Service)
│   │   │   ├── EquipeManutencaoService.java
│   │   │   ├── TrechoRodoviaService.java
│   │   │   ├── IntervencaoOperacionalService.java
│   │   │   └── RelatorioPrioridadeService.java # Motor de Prioridade como Serviço REST
│   │   │
│   │   ├── repository/                       # Repositórios (@Repository - Interfaces Spring Data)
│   │   │   ├── EquipeManutencaoRepository.java
│   │   │   ├── TrechoRodoviaRepository.java
│   │   │   ├── IntervencaoOperacionalRepository.java
│   │   │   └── RelatorioPrioridadeRepository.java
│   │   │
│   │   ├── model/                            # Entidades JPA (@Entity, @Table, @Id)
│   │   │   ├── EquipeManutencao.java
│   │   │   ├── TrechoRodovia.java
│   │   │   ├── IntervencaoOperacional.java
│   │   │   └── RelatorioPrioridade.java
│   │   │
│   │   ├── dto/                              # Contratos de API (Java Records + Bean Validation)
│   │   │   ├── EquipeRequestDTO.java
│   │   │   ├── EquipeResponseDTO.java
│   │   │   ├── TrechoRequestDTO.java
│   │   │   ├── TrechoResponseDTO.java
│   │   │   ├── IntervencaoRequestDTO.java
│   │   │   ├── IntervencaoResponseDTO.java
│   │   │   └── RelatorioResponseDTO.java
│   │   │
│   │   └── exception/                        # Tratamento Global de Erros (@RestControllerAdvice)
│   │       ├── GlobalExceptionHandler.java
│   │       ├── RecursoNaoEncontradoException.java
│   │       ├── RegraDeNegocioException.java
│   │       └── ErroRespostaDTO.java
│   │
│   └── resources/
│       └── application.properties            # Configurações do Spring Boot, HikariCP e Oracle FIAP
│
└── test/
    ├── java/br/com/greencontrol/             # Testes Automatizados (@SpringBootTest + MockMvc)
    │   ├── TrechoRodoviaControllerTest.java
    │   ├── EquipeManutencaoControllerTest.java
    │   └── RelatorioPrioridadeControllerTest.java
    └── resources/
        └── application.properties            # Configuração de testes com banco H2 em memória
```

---

## 3. Tabela Completa de Endpoints REST

### 3.1 Trechos de Rodovia (`/api/trechos`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/trechos` | Lista todos os trechos monitorados | `200 OK` | `500` |
| **GET** | `/api/trechos/{id}` | Busca trecho por ID | `200 OK` | `404 Not Found` |
| **POST** | `/api/trechos` | Cadastra novo trecho com validação | `201 Created` | `400 Bad Request` |
| **PUT** | `/api/trechos/{id}` | Atualiza dados do trecho | `200 OK` | `400 / 404` |
| **DELETE** | `/api/trechos/{id}` | Remove trecho do banco | `204 No Content` | `404 Not Found` |
| **POST** | `/api/trechos/{id}/simular-crescimento` | Simula crescimento dinâmico (IoT/microclima) | `200 OK` | `404 Not Found` |
| **GET** | `/api/trechos/clima?tipo=...` | Derived Query: filtra trechos por clima | `200 OK` | `500` |
| **GET** | `/api/trechos/vegetacao-minima?minimo=...` | Derived Query: filtra vegetação $\ge$ valor | `200 OK` | `500` |

### 3.2 Equipes de Manutenção (`/api/equipes`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/equipes` | Lista todas as equipes | `200 OK` | `500` |
| **GET** | `/api/equipes/{id}` | Busca equipe por ID | `200 OK` | `404 Not Found` |
| **POST** | `/api/equipes` | Cadastra nova equipe | `201 Created` | `400 Bad Request` |
| **PUT** | `/api/equipes/{id}` | Atualiza equipe | `200 OK` | `400 / 404` |
| **DELETE** | `/api/equipes/{id}` | Remove equipe | `204 No Content` | `404 Not Found` |
| **GET** | `/api/equipes/buscar?especialidade=...` | Derived Query: busca por especialidade | `200 OK` | `500` |

### 3.3 Intervenções Operacionais (`/api/intervencoes`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/intervencoes` | Lista histórico de intervenções | `200 OK` | `500` |
| **GET** | `/api/intervencoes/{id}` | Busca intervenção por ID | `200 OK` | `404 Not Found` |
| **POST** | `/api/intervencoes` | Registra serviço (aplica efeito no trecho) | `201 Created` | `400 / 404` |
| **PUT** | `/api/intervencoes/{id}` | Atualiza dados da intervenção | `200 OK` | `400 / 404` |
| **DELETE** | `/api/intervencoes/{id}` | Remove registro de intervenção | `204 No Content` | `404 Not Found` |
| **GET** | `/api/intervencoes/tipo?tipo=...` | Derived Query: filtra por tipo de serviço | `200 OK` | `500` |
| **GET** | `/api/intervencoes/trecho/{trechoId}` | Derived Query: lista intervenções por trecho | `200 OK` | `500` |

### 3.4 Motor de Prioridade & Relatórios (`/api/relatorios`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/relatorios` | Executa o motor sobre os trechos atuais e grava no Oracle | `201 Created` | `500` |
| **GET** | `/api/relatorios` | Lista o histórico completo de relatórios gerados | `200 OK` | `500` |
| **GET** | `/api/relatorios/periodo?inicio=...&fim=...` | Derived Query: consulta relatórios por intervalo | `200 OK` | `500` |

---

## 4. Derived Queries do Spring Data JPA

Cada repositório implementa métodos com **Derived Queries**, nos quais o Spring Data gera a consulta SQL automaticamente em tempo de execução com base na nomenclatura sem necessidade de SQL manual:

```java
// TrechoRodoviaRepository
List<TrechoRodovia> findByNivelVegetacaoGreaterThanEqual(Double minimo);
List<TrechoRodovia> findByTipoClimaIgnoreCase(String tipoClima);
List<TrechoRodovia> findByEquipeId(Long equipeId);

// EquipeManutencaoRepository
List<EquipeManutencao> findByNomeContainingIgnoreCase(String nome);
List<EquipeManutencao> findByEspecialidadeContainingIgnoreCase(String especialidade);

// IntervencaoOperacionalRepository
List<IntervencaoOperacional> findByTipoIntervencaoIgnoreCase(String tipoIntervencao);
List<IntervencaoOperacional> findByTrechoId(Long trechoId);

// RelatorioPrioridadeRepository
List<RelatorioPrioridade> findByDataGeracaoBetween(LocalDateTime inicio, LocalDateTime fim);
List<RelatorioPrioridade> findAllByOrderByDataGeracaoDesc();
```

---

## 5. Como Executar o Projeto

### Pré-requisitos
* **Java JDK 17** ou superior instalado (`java -version`).
* Conexão com o banco Oracle FIAP (`oracle.fiap.com.br:1521:ORCL`).

### 1. Clonar o repositório
```bash
git clone https://github.com/MUnizZz526/GreenControl.git
cd GreenControl
```

### 2. Executar a Aplicação (usando o Maven Wrapper embutido)
Não é necessário ter o Maven instalado no PATH! Utilize o Maven Wrapper já configurado:

* **No Windows (PowerShell / CMD):**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```

* **No Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```

A API iniciará no endereço `http://localhost:8080`.

### 3. Rodar a bateria de testes automatizados
```powershell
.\mvnw.cmd test
```
*Todos os testes utilizam banco H2 em memória em modo Oracle, validando regras de negócio, persistência e códigos HTTP de forma 100% isolada.*

---

## 6. Exemplos de Requisição com cURL

### 1. Criar um novo Trecho de Rodovia (`POST /api/trechos`)
```bash
curl -X POST http://localhost:8080/api/trechos \
  -H "Content-Type: application/json" \
  -d '{
    "quilometroInicial": 50.0,
    "quilometroFinal": 75.0,
    "nivelVegetacao": 82.5,
    "tipoClima": "Úmido",
    "idEquipe": 1
  }'
```

### 2. Buscar Trecho por ID (`GET /api/trechos/{id}`)
```bash
curl -X GET http://localhost:8080/api/trechos/1
```

### 3. Simular Crescimento IoT no Trecho (`POST /api/trechos/{id}/simular-crescimento`)
```bash
curl -X POST http://localhost:8080/api/trechos/1/simular-crescimento
```

### 4. Cadastrar Intervenção Operacional (`POST /api/intervencoes`)
```bash
curl -X POST http://localhost:8080/api/intervencoes \
  -H "Content-Type: application/json" \
  -d '{
    "tipoIntervencao": "RocadaMecanizada",
    "idTrecho": 1
  }'
```

### 5. Gerar Relatório de Prioridade no Oracle (`POST /api/relatorios`)
```bash
curl -X POST http://localhost:8080/api/relatorios
```

### 6. Consultar Relatórios por Período (`GET /api/relatorios/periodo`)
```bash
curl -X GET "http://localhost:8080/api/relatorios/periodo?inicio=2026-10-01&fim=2026-10-31"
```

### 7. Exemplo de Erro de Validação (`POST /api/trechos` com dados inválidos)
```bash
curl -X POST http://localhost:8080/api/trechos \
  -H "Content-Type: application/json" \
  -d '{
    "quilometroInicial": 10.0,
    "quilometroFinal": 20.0,
    "nivelVegetacao": -15.0,
    "tipoClima": ""
  }'
```

---

## 7. Evidências de Funcionamento (Registros Reais da API)

### Evidência 1: Inicialização do Spring Boot 4.1.x conectado ao Oracle FIAP
```text
2026-10-14T09:37:57.671-03:00  INFO 16504 --- [greencontrol] [main] com.zaxxer.hikari.pool.HikariPool : HikariPool-1 - Added connection oracle.jdbc.driver.T4CConnection@77eb607e
2026-10-14T09:37:57.673-03:00  INFO 16504 --- [greencontrol] [main] com.zaxxer.hikari.HikariDataSource: HikariPool-1 - Start completed.
Database JDBC URL [jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL]
Database driver: Oracle JDBC driver
Database dialect: OracleDialect
Tomcat started on port 8080 (http) with context path '/'
Started GreenControlApplication in 7.931 seconds
```

### Evidência 2: Criação de Trecho via POST (Status HTTP 201 Created)
```json
HTTP/1.1 201 Created
Location: http://localhost:8080/api/trechos/21
Content-Type: application/json

{
  "id": 21,
  "quilometroInicial": 50.0,
  "quilometroFinal": 75.0,
  "nivelVegetacao": 82.5,
  "tipoClima": "Úmido",
  "nivelPrioridade": "URGENTE",
  "idEquipe": 1,
  "nomeEquipe": "Equipe Alfa"
}
```

### Evidência 3: Simulação de Crescimento Dinâmico (IoT)
*Trecho com clima Úmido cresce +8.0cm: vegetação de 82.5cm vai para 90.5cm no Oracle:*
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 21,
  "quilometroInicial": 50.0,
  "quilometroFinal": 75.0,
  "nivelVegetacao": 90.5,
  "tipoClima": "Úmido",
  "nivelPrioridade": "URGENTE",
  "idEquipe": 1,
  "nomeEquipe": "Equipe Alfa"
}
```

### Evidência 4: Execução do Motor de Prioridade via REST (Status HTTP 201 Created)
*Analisa todos os trechos atuais e grava consolidação permanente na tabela `T_RELATORIO_PRIORIDADE`:*
```json
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 21,
  "qtUrgente": 2,
  "qtCritico": 6,
  "qtAtencao": 0,
  "qtNormal": 1,
  "resumo": "Análise de prioridade realizada para 9 trecho(s) monitorado(s).",
  "dataGeracao": "2026-10-14T09:40:43.923782"
}
```

### Evidência 5: Consulta de Relatórios por Período via Derived Query (`GET /api/relatorios/periodo`)
```json
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 21,
    "qtUrgente": 2,
    "qtCritico": 6,
    "qtAtencao": 0,
    "qtNormal": 1,
    "resumo": "Análise de prioridade realizada para 9 trecho(s) monitorado(s).",
    "dataGeracao": "2026-10-14T09:40:43.923782"
  }
]
```

### Evidência 6: Tratamento Global de Erro & Bean Validation (Status HTTP 400 Bad Request)
```json
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "timestamp": "2026-10-14T09:43:35.0203082",
  "status": 400,
  "erro": "Erro de Validação de Dados",
  "mensagem": "Um ou mais campos contêm valores inválidos.",
  "detalhes": [
    "nivelVegetacao: Nível de vegetação não pode ser negativo.",
    "tipoClima: Tipo de clima é obrigatório."
  ]
}
```

### Evidência 7: Exclusão com Sucesso (Status HTTP 204 No Content)
```text
DELETE /api/trechos/21
HTTP/1.1 204 No Content
```

### Evidência 8: Busca por ID Inexistente (Status HTTP 404 Not Found)
```json
GET /api/trechos/21
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "timestamp": "2026-10-14T09:44:17.7550945",
  "status": 404,
  "erro": "Recurso Não Encontrado",
  "mensagem": "Trecho com ID 21 não encontrado.",
  "detalhes": []
}
```

---

## 8. Conceitos em Foco (Perguntas de Reflexão Respondidas)

### 1. Por que o Repository é uma interface e não uma classe? Quem escreve a implementação e quando?
> **Resposta:**
> O Repository é uma interface porque ele define apenas o **contrato** de persistência da entidade. No ecossistema Spring Data JPA, **ninguém precisa escrever a classe de implementação manualmente**. O próprio framework Spring Data, durante o processo de inicialização da aplicação (tempo de execução / runtime), utiliza proxies dinâmicos do Java e reflexão para gerar automaticamente a classe concreta que implementa a interface e todos os métodos herdados do `JpaRepository` (como `save`, `findAll`, `findById`, `deleteById`), conectando-os diretamente ao `EntityManager` do Hibernate.

---

### 2. O pattern DAO da Sprint 3 "morreu" na migração ou só mudou de forma? Explique com as suas palavras.
> **Resposta:**
> O padrão DAO (Data Access Object) **não morreu, ele foi absorvido e automatizado pelo framework**. Na Sprint 3, tínhamos que escrever artesanalmente cada classe DAO (`TrechoRodoviaDAO`, `EquipeManutencaoDAO`), gerenciando conexões, `PreparedStatement`, parâmetros posicionais `?`, tratamento de `SQLException` e fechamento de `ResultSet` no bloco `finally`. Na Sprint 4, a responsabilidade de isolar a camada de acesso ao banco continua exatamente a mesma (o Controller e o Service não fazem queries SQL diretamente), porém a implementação repetitiva (boilerplate) agora é provida de forma transparente pelas interfaces `JpaRepository` do Spring Data.

---

### 3. Por que a validação de `nivelVegetacao >= 0` (Sprint 1) deve ficar no Service e não no Controller?
> **Resposta:**
> Porque `nivelVegetacao >= 0` é uma **invariante de domínio / regra de negócio**. O Controller tem como responsabilidade exclusiva o protocolo HTTP (receber a requisição, desserializar JSON, delegar e retornar o código HTTP adequado). Se a validação ficasse no Controller:
> 1. Ela teria que ser duplicada em qualquer novo ponto de entrada (por exemplo, se o sistema receber dados de mensageria RabbitMQ/Kafka, um batch job ou testes unitários de serviço).
> 2. O Controller ficaria acoplado a regras de cálculo e integridade ecológica da via.
> Ao manter a regra no `@Service` (e no modelo de domínio), garantimos que o sistema nunca persista um trecho inválido, independentemente de quem chamou o serviço.

---

### 4. No JDBC puro vocês escreviam SQL. Onde está o SQL do `findByTipo()`? Quem o gerou?
> **Resposta:**
> O SQL do método `findByTipo(String tipo)` (ou `findByTipoIntervencaoIgnoreCase`) não existe em nenhum arquivo do projeto em formato de string manual. Ele foi **gerado em tempo de execução pelo motor de Derived Queries do Spring Data JPA**. O framework analisa léxica e sintaticamente o nome do método (`findBy` + nome do atributo + palavras-chave como `IgnoreCase`, `GreaterThanEqual`, `Between`), traduz a intenção para a linguagem HQL/JPQL do Hibernate, e o Hibernate, por sua vez, converte o JPQL para o dialeto SQL nativo do banco configurado (`OracleDialect`), produzindo:
> ```sql
> select io.* from t_intervencao_operacional io where upper(io.tipo_intervencao) = upper(?)
> ```

---

## 9. Diferenciais e Bônus Implementados (+1.0)

| Bônus | Implementação | Pontuação |
| :--- | :--- | :---: |
| **DTOs desacoplados da entidade** | Criação de Java Records (`TrechoRequestDTO`, `TrechoResponseDTO`, etc.) separando o contrato da API do modelo JPA. | **+0,3** |
| **Bean Validation** | Validações com `@Valid`, `@NotNull`, `@NotBlank`, `@PositiveOrZero` e mensagens customizadas em português. | **+0,3** |
| **Tratamento Global de Erros** | `@RestControllerAdvice` na classe `GlobalExceptionHandler` interceptando 400, 404 e 500 com respostas padronizadas em `ErroRespostaDTO`. | **+0,2** |
| **Testes com SpringBootTest + MockMvc** | Bateria de testes automatizados (`TrechoRodoviaControllerTest`, `EquipeManutencaoControllerTest`, `RelatorioPrioridadeControllerTest`) executados com banco H2 em memória. | **+0,2** |
| **Total de Bônus Alcançado** | | **+1,0** |

---

## 10. Integrantes do Grupo

* **Cauã** — RM566527
* **Matheus** — RM562765
* **Lucas** — RM561607
* **Rafael** — RM563285
* **Henrique** — RM564699
* **Victor** — RM564804
