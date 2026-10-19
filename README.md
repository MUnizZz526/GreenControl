# GreenControl — API REST com Spring Boot e JPA

<div align="center">

### Monitoramento Inteligente de Rodovias & Gestão Operacional de Vegetação
**Challenge FIAP — Sprint 4: A Grande Virada Tecnológica (Spring Boot + Spring Data JPA + API REST)**

---

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data-JPA-blue?style=for-the-badge)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Oracle Database](https://img.shields.io/badge/Oracle-Database-red?style=for-the-badge&logo=oracle&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build_Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Sprint](https://img.shields.io/badge/Sprint-4-purple?style=for-the-badge)

**Homologação local:** Java 17 + H2 · 35 testes aprovados · 9 requisições HTTP verificadas.

**Oracle FIAP:** integração e migração pendentes de homologação.

</div>

---

## 1. Contexto e Evolução do Projeto

O **GreenControl**, desenvolvido no contexto do desafio **MOTIVA** da FIAP, apoia o monitoramento da vegetação em rodovias e o planejamento das equipes de manutenção. O sistema acompanha trechos, registra intervenções e gera relatórios de prioridade a partir da altura da vegetação.

| Sprint | Escopo e Tecnologias |
| :--- | :--- |
| **Sprint 1** | Modelagem do domínio, encapsulamento, classes `TrechoRodovia` e `EquipeManutencao` e protótipo console. |
| **Sprint 2** | Crescimento por clima, motor de prioridades, interface `MonitoravelViaIoT`, classe abstrata de intervenção e polimorfismo. |
| **Sprint 3** | Persistência no Oracle com JDBC puro, DAOs, `PreparedStatement`, CRUD e histórico de relatórios. Fontes preservadas em `legacy-sprint3/`, fora do build Maven atual. |
| **Sprint 4 (Atual)** | **Spring Boot 4.1.1**, **Java 17**, Spring Data JPA e API REST; quatro entidades com sequences dedicadas, DTOs, Bean Validation, Services transacionais, proteção de relacionamentos, erros padronizados, filtro de período corrigido e **35 testes aprovados** com H2. |

### A Virada: De JDBC Puro para Spring Data JPA

Na Sprint 3, cada operação exigia preparar o SQL, preencher parâmetros, executar o comando e administrar os recursos JDBC. Na Sprint 4, o Service utiliza o contrato do Repository, enquanto Spring Data JPA e Hibernate administram a persistência.

```java
// Sprint 3: comparação didática com o acesso JDBC preservado no legado.
Connection conn = ConexaoBD.getInstancia().conectar();
try (PreparedStatement stmt = conn.prepareStatement(
        "INSERT INTO T_EQUIPE_MANUTENCAO (NOME, ESPECIALIDADE) VALUES (?, ?)")) {
    stmt.setString(1, equipe.nome());
    stmt.setString(2, equipe.especialidade());
    stmt.executeUpdate();
}

// Sprint 4: operação de persistência delegada ao Repository pelo Service.
equipeRepository.save(equipe);
```

O exemplo JDBC representa o legado: o código ativo em `src/main/java/` não utiliza JDBC manual, SQL em Controllers/Services ou console como interface principal. O driver Oracle **`ojdbc17`** é resolvido pelo Maven, sem dependência de um JAR local.

A apresentação original do grupo, elaborada por **Cauã Muniz**, é mantida nesta documentação, incorporando as correções técnicas e as evidências consolidadas por **Matheus Morelli** em 19/10/2026.

---

## 2. Arquitetura em Camadas

A aplicação separa o protocolo HTTP, as regras de negócio e o acesso aos dados:

```text
Controller → Service → Repository → Banco
                                    ├── Oracle FIAP: integração prevista
                                    └── H2: testes isolados e homologação local
```

| Camada | Implementação | Responsabilidade |
| :--- | :--- | :--- |
| **Controller** | `@RestController`, `ResponseEntity`, `@Valid` e parâmetros validados | Receber requisições, delegar ao Service e definir status e cabeçalhos HTTP. |
| **Service** | `@Service`, validação de domínio e `@Transactional` | Aplicar regras e efeitos, validar vínculos e delimitar transações; consultas utilizam `readOnly = true` quando apropriado. |
| **Repository** | Interfaces `JpaRepository` e derived queries | Abstrair persistência e consultas, sem JDBC manual no código ativo. |
| **Model** | Entidades JPA e estratégias | Representar o domínio persistido e comportamentos como crescimento, intervenções e classificação. |
| **DTO** | Records de requisição e resposta | Separar contratos JSON das entidades e validar as entradas. |
| **Exception / Validation** | `GlobalExceptionHandler`, `ErroRespostaDTO`, `OracleText` e `OracleTextValidator` | Padronizar erros e respeitar as restrições do schema legado. |

A injeção de dependências ocorre por construtor. Com `spring.jpa.open-in-view=false`, os DTOs são montados dentro das transações dos Services, sem depender de consultas durante a serialização da resposta.

### 2.1 Estrutura Atual de Diretórios

```text
GreenControl/
├── pom.xml                                      # Java 17 e Boot 4.1.1
├── mvnw / mvnw.cmd                              # Maven Wrapper
├── .mvn/wrapper/maven-wrapper.properties
├── .env.example                                 # Apenas placeholders
├── greencontrol-criacao.sql                      # Somente schema novo
├── greencontrol-dados.sql                        # Carga opcional antes da migração
├── legacy-sprint3/                               # Histórico, fora do build atual
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/
│   │   │       └── com/
│   │   │           └── greencontrol/
│   │   │               ├── controller/
│   │   │               │   ├── EquipeManutencaoController.java
│   │   │               │   ├── IntervencaoOperacionalController.java
│   │   │               │   ├── RelatorioPrioridadeController.java
│   │   │               │   └── TrechoRodoviaController.java
│   │   │               ├── dto/
│   │   │               │   ├── EquipeRequestDTO.java
│   │   │               │   ├── EquipeResponseDTO.java
│   │   │               │   ├── IntervencaoRequestDTO.java
│   │   │               │   ├── IntervencaoResponseDTO.java
│   │   │               │   ├── RelatorioResponseDTO.java
│   │   │               │   ├── TrechoRequestDTO.java
│   │   │               │   └── TrechoResponseDTO.java
│   │   │               ├── exception/
│   │   │               │   ├── ErroRespostaDTO.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── RecursoNaoEncontradoException.java
│   │   │               │   └── RegraDeNegocioException.java
│   │   │               ├── model/
│   │   │               │   ├── EfeitoIntervencao.java
│   │   │               │   ├── EquipeManutencao.java
│   │   │               │   ├── IntervencaoOperacional.java
│   │   │               │   ├── RelatorioPrioridade.java
│   │   │               │   └── TrechoRodovia.java
│   │   │               ├── repository/
│   │   │               │   ├── EquipeManutencaoRepository.java
│   │   │               │   ├── IntervencaoOperacionalRepository.java
│   │   │               │   ├── RelatorioPrioridadeRepository.java
│   │   │               │   └── TrechoRodoviaRepository.java
│   │   │               ├── service/
│   │   │               │   ├── EquipeManutencaoService.java
│   │   │               │   ├── IntervencaoOperacionalService.java
│   │   │               │   ├── RelatorioPrioridadeService.java
│   │   │               │   └── TrechoRodoviaService.java
│   │   │               ├── validation/
│   │   │               │   ├── OracleText.java
│   │   │               │   └── OracleTextValidator.java
│   │   │               └── GreenControlApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── br/
│       │       └── com/
│       │           └── greencontrol/
│       │               ├── EquipeManutencaoControllerTest.java
│       │               ├── HttpEvidenceTest.java
│       │               ├── IdentitySequenceCompatibilityTest.java
│       │               ├── RelatorioPrioridadeControllerTest.java
│       │               ├── Sprint4IntegrationTest.java
│       │               └── TrechoRodoviaControllerTest.java
│       └── resources/
│           ├── application.properties
│           └── identity-compatibility.sql
└── docs/
    ├── HOMOLOGACAO.md
    ├── ARQUIVOS-ALTERADOS.md
    ├── sql/migracao-sequences.sql
    └── evidencias/
        ├── reproduzir.ps1
        ├── http-h2.md
        ├── maven-java17.txt
        ├── inicializacao-h2.txt
        ├── seguranca.txt
        └── curl/                                # Corpos, headers e resultado.json
```

### 2.2 Entidades, Relacionamentos e Integridade

| Entidade | Tabela Oracle | Relacionamentos e Geração de ID |
| :--- | :--- | :--- |
| `EquipeManutencao` | `T_EQUIPE_MANUTENCAO` | ID por `SEQ_GC_EQUIPE`; pode ser referenciada por vários trechos. |
| `TrechoRodovia` | `T_TRECHO_RODOVIA` | `ManyToOne` com equipe opcional; ID por `SEQ_GC_TRECHO`. |
| `IntervencaoOperacional` | `T_INTERVENCAO_OPERACIONAL` | `ManyToOne` com trecho obrigatório; ID por `SEQ_GC_INTERVENCAO`. |
| `RelatorioPrioridade` | `T_RELATORIO_PRIORIDADE` | Snapshot independente com totais, resumo e data; ID por `SEQ_GC_RELATORIO`. |

As quatro entidades utilizam `@Entity`, `@Table`, `@Id`, `@GeneratedValue(strategy = GenerationType.SEQUENCE)` e `@SequenceGenerator(allocationSize = 1)`. O capítulo 5 explica a compatibilidade com o schema da Sprint 3 e a migração manual.

A API retorna **409** ao tentar excluir equipe associada a trechos ou trecho com intervenções. Essa proteção está nos Services, inclusive para o schema original, cujas FKs possuem `ON DELETE SET NULL` e `ON DELETE CASCADE`. Escritas diretas fora da API não recebem essa proteção de negócio.

### 2.3 Regras Preservadas: Intervenções e Simulação IoT

O enum **`EfeitoIntervencao`**, com método abstrato e implementações por constante, adapta o polimorfismo da Sprint 2. A entidade JPA representa o registro histórico; a estratégia determina o efeito físico aplicado na criação:

| Estratégia | Efeito no POST da Intervenção |
| :--- | :--- |
| **Roçada** | Fixa a vegetação em **5 cm**; a identificação do tipo normaliza acentos e maiúsculas/minúsculas. |
| **Pulverização** | Reduz **10 cm**, com piso de **0 cm**. |
| **Outros tipos** | Permanecem permitidos como registros operacionais sem alteração da vegetação. |

**PUT corrige o registro histórico e DELETE remove esse registro. Nenhuma dessas operações reaplica ou desfaz o serviço físico.** Uma reversão automática poderia restaurar valores incompatíveis com crescimentos ou intervenções posteriores; um novo serviço deve ser registrado com outro POST. A data omitida no POST assume o dia atual; no PUT, a data existente é preservada se o campo for omitido.

A interface histórica `MonitoravelViaIoT` simulava transmissão por mensagens no console, sem integração externa real. Na API, `/api/trechos/{id}/simular-crescimento` preserva o crescimento por clima: **Úmido adiciona 8 cm**; demais climas adicionam **2 cm**. Crescimento que exceda o armazenamento é rejeitado com 400 e não é persistido.

### 2.4 Motor de Prioridade e Histórico

| Vegetação (cm) | Prioridade | Valor no JSON |
| :--- | :--- | :--- |
| Superior a **60** | **URGENTE** | `URGENTE` |
| Superior a **30** e até **60** | **CRÍTICO** | `CRITICO` |
| De **15** até **30**, inclusive | **ATENÇÃO** | `ATENCAO` |
| Inferior a **15** | **NORMAL** | `NORMAL` |

A classificação está centralizada em `TrechoRodovia.obterNivelPrioridade()` e é reutilizada pelo `RelatorioPrioridadeService`. O relatório persiste um snapshot dos trechos atuais com contagens, resumo e data. Consultas leem esse histórico; excluir trechos não apaga nem recalcula relatórios anteriores.

### 2.5 DTOs, Validações e Respostas de Erro

Os DTOs validam campos obrigatórios, IDs positivos, valores não negativos e tamanhos dos textos. Os campos numéricos respeitam **`NUMBER(8,2)`**: até **999999,99**, com no máximo duas casas decimais. Os Services validam também valores finitos e quilômetro final estritamente maior que o inicial.

`OracleText` e `OracleTextValidator` aplicam limites conservadores de bytes UTF-8 aos `VARCHAR2` legados: **50** para clima e **100** para nome, especialidade e tipo de intervenção, além dos limites em caracteres.

`GlobalExceptionHandler` padroniza o JSON com `timestamp`, `status`, `erro`, `mensagem` e `detalhes`. JSON malformado, parâmetros incompatíveis e validações retornam **400**; recursos/referências inexistentes, **404**; conflitos de integridade, **409**; falhas inesperadas, **500**, sem stack traces, mensagens SQL ou outros detalhes internos na resposta.

---

## 3. Tabela Completa de Endpoints REST

Os endpoints abaixo correspondem aos Controllers atuais. Os POSTs de trechos, equipes e intervenções retornam **201** com `Location`; o POST de relatório retorna **201** com o snapshot no corpo.

### 3.1 Trechos de Rodovia (`/api/trechos`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/trechos` | Lista todos os trechos monitorados | `200 OK` | `500` |
| **GET** | `/api/trechos/{id}` | Busca trecho por ID | `200 OK` | `400 / 404` |
| **POST** | `/api/trechos` | Cadastra novo trecho com validação | `201 Created` | `400 / 404 / 409` |
| **PUT** | `/api/trechos/{id}` | Atualiza dados do trecho | `200 OK` | `400 / 404 / 409` |
| **DELETE** | `/api/trechos/{id}` | Remove trecho sem intervenções vinculadas | `204 No Content` | `400 / 404 / 409` |
| **POST** | `/api/trechos/{id}/simular-crescimento` | Simula crescimento dinâmico (IoT/microclima) | `200 OK` | `400 / 404` |
| **GET** | `/api/trechos/clima?tipo=...` | Derived Query: filtra trechos por clima | `200 OK` | `400` |
| **GET** | `/api/trechos/vegetacao-minima?minimo=...` | Derived Query: filtra vegetação maior ou igual ao mínimo | `200 OK` | `400` |

### 3.2 Equipes de Manutenção (`/api/equipes`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/equipes` | Lista todas as equipes | `200 OK` | `500` |
| **GET** | `/api/equipes/{id}` | Busca equipe por ID | `200 OK` | `400 / 404` |
| **POST** | `/api/equipes` | Cadastra nova equipe | `201 Created` | `400 / 409` |
| **PUT** | `/api/equipes/{id}` | Atualiza equipe | `200 OK` | `400 / 404 / 409` |
| **DELETE** | `/api/equipes/{id}` | Remove equipe sem trechos associados | `204 No Content` | `400 / 404 / 409` |
| **GET** | `/api/equipes/buscar?especialidade=...` | Derived Query: busca por especialidade | `200 OK` | `400` |

### 3.3 Intervenções Operacionais (`/api/intervencoes`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/intervencoes` | Lista histórico de intervenções | `200 OK` | `500` |
| **GET** | `/api/intervencoes/{id}` | Busca intervenção por ID | `200 OK` | `400 / 404` |
| **POST** | `/api/intervencoes` | Registra serviço (aplica efeito no trecho) | `201 Created` | `400 / 404 / 409` |
| **PUT** | `/api/intervencoes/{id}` | Corrige o histórico sem reaplicar o efeito físico | `200 OK` | `400 / 404 / 409` |
| **DELETE** | `/api/intervencoes/{id}` | Remove o histórico sem desfazer o efeito físico | `204 No Content` | `400 / 404` |
| **GET** | `/api/intervencoes/tipo?tipo=...` | Derived Query: filtra por tipo de serviço | `200 OK` | `400` |
| **GET** | `/api/intervencoes/trecho/{trechoId}` | Derived Query: lista intervenções por trecho | `200 OK` | `400` |

### 3.4 Motor de Prioridade e Relatórios (`/api/relatorios`)

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/relatorios` | Executa o motor sobre os trechos atuais e persiste no banco configurado | `201 Created` | `500` |
| **GET** | `/api/relatorios` | Lista o histórico completo de relatórios gerados | `200 OK` | `500` |
| **GET** | `/api/relatorios/periodo?inicio=...&fim=...` | Derived Query: consulta relatórios por intervalo | `200 OK` | `400` |

---

**Convenções:** IDs devem ser positivos. Recursos/referências inexistentes nas operações individuais retornam 404; coleções e filtros sem resultados retornam **`[]` com 200**. Consultar intervenções de um trecho sem registros não equivale a buscar individualmente o trecho. Falhas inesperadas podem retornar **500** em qualquer endpoint, além dos erros indicados nas tabelas.

O filtro de período aceita **`yyyy-MM-dd`** ou **`yyyy-MM-ddTHH:mm:ss[.fração]`**, sem timezone. A data final simples usa o início do dia seguinte como limite exclusivo; horários completos mantêm os limites solicitados, inclusivos. Se ausentes, início assume um mês antes do momento da consulta e fim assume esse momento. Valores vazios, datas inválidas e intervalos invertidos retornam **400**, sem substituição silenciosa por valores padrão.

No PUT de trecho, equipe omitida ou nula remove a associação; a referência informada precisa existir. No PUT de intervenção, tipo e trecho são atualizados, e a data existente é preservada se omitida.

---

## 4. Derived Queries do Spring Data JPA

As interfaces Repository declaram métodos cujos nomes expressam os critérios de busca. O **Spring Data** interpreta as assinaturas e constrói as consultas JPA; o **Hibernate** produz o SQL apropriado ao dialeto do banco configurado. Não há SQL manual nos Controllers ou Services.

As assinaturas abaixo existem no código atual:

```java
// TrechoRodoviaRepository extends JpaRepository<TrechoRodovia, Long>
List<TrechoRodovia> findByNivelVegetacaoGreaterThanEqual(Double minimo);
List<TrechoRodovia> findByTipoClimaIgnoreCase(String tipoClima);
List<TrechoRodovia> findByEquipeId(Long equipeId);
```

```java
// EquipeManutencaoRepository extends JpaRepository<EquipeManutencao, Long>
List<EquipeManutencao> findByNomeContainingIgnoreCase(String nome);
List<EquipeManutencao> findByEspecialidadeContainingIgnoreCase(String especialidade);
```

```java
// IntervencaoOperacionalRepository extends JpaRepository<IntervencaoOperacional, Long>
List<IntervencaoOperacional> findByTipoIntervencaoIgnoreCase(String tipoIntervencao);
List<IntervencaoOperacional> findByTrechoId(Long trechoId);
```

```java
// RelatorioPrioridadeRepository extends JpaRepository<RelatorioPrioridade, Long>
List<RelatorioPrioridade> findByDataGeracaoBetween(LocalDateTime inicio, LocalDateTime fim);
List<RelatorioPrioridade> findByDataGeracaoGreaterThanEqualAndDataGeracaoLessThan(LocalDateTime inicio, LocalDateTime fim);
List<RelatorioPrioridade> findAllByOrderByDataGeracaoDesc();
```

Além dessas consultas, as interfaces herdam `save`, `findAll`, `findById`, `existsById` e `deleteById`, entre outras operações de `JpaRepository`.

Para o período, `findByDataGeracaoBetween` atende intervalos com horário explícito. `findByDataGeracaoGreaterThanEqualAndDataGeracaoLessThan` atende a data final simples com limite superior exclusivo, incluindo relatórios gerados durante todo aquele dia. A interpretação e a validação das datas ficam no Service.

---

## 5. Como Executar o Projeto

### 5.1 Pré-requisitos e Obtenção do Projeto

- **JDK 17**, com `JAVA_HOME` apontando ao diretório do JDK.
- Git e acesso à rede para baixar Maven e dependências ainda não presentes no cache.
- Para executar contra Oracle: acesso ao Oracle FIAP, credenciais próprias e schema preparado conforme a subseção 5.3.
- Para os testes locais H2: **não são necessárias credenciais Oracle**.

```powershell
git clone https://github.com/MUnizZz526/GreenControl.git
cd GreenControl
java -version
.\mvnw.cmd -version
```

O projeto utiliza **Spring Boot 4.1.1** e **Maven Wrapper 3.10.0**. Não é necessário instalar Maven separadamente. Confirme que o Wrapper utiliza Java 17 antes de executar a aplicação ou os testes.

### 5.2 Segurança e Variáveis de Ambiente

A aplicação exige **`DB_URL`**, **`DB_USER`** e **`DB_PASSWORD`**, sem credenciais padrão. No PowerShell, configure as variáveis na mesma sessão em que iniciará a API; substitua o usuário fictício pelo seu usuário de homologação:

```powershell
$env:DB_URL = 'jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL'
$env:DB_USER = 'SEU_USUARIO_FIAP'
$gcSecret = Read-Host 'Senha Oracle' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $gcSecret).Password
```

A senha é solicitada sem ser publicada no comando. O arquivo [`.env.example`](.env.example) contém apenas placeholders; **Spring Boot não carrega `.env` automaticamente**. Arquivos de segredos locais, `.idea/`, `.iml`, JARs locais, logs e caches de homologação estão ignorados. O código legado também passou a obter a conexão por variáveis de ambiente.

**Credenciais anteriormente publicadas permanecem acessíveis no histórico Git e precisam ser rotacionadas no Oracle.** A remoção dos valores atuais não elimina essa exposição histórica. Os commits de correção já estão publicados no repositório; publicar código corrigido não substitui a rotação das credenciais.

A configuração ativa usa `spring.jpa.hibernate.ddl-auto=none`, `spring.jpa.show-sql=false` e suprime mensagens internas e stack traces das respostas de erro. Ao terminar a sessão de uso, remova a senha do ambiente:

```powershell
Remove-Item Env:DB_PASSWORD
```

### 5.3 Configuração e Migração Oracle

O schema da Sprint 3 utiliza colunas **`GENERATED BY DEFAULT AS IDENTITY`**. Para cumprir a exigência de `@SequenceGenerator`, a Sprint 4 utiliza **quatro sequences dedicadas**, sem alterar ou apagar os registros existentes:

| Entidade | Sequence JPA | Configuração |
| :--- | :--- | :--- |
| `EquipeManutencao` | `SEQ_GC_EQUIPE` | Passo 1; `allocationSize = 1`. |
| `TrechoRodovia` | `SEQ_GC_TRECHO` | Passo 1; `allocationSize = 1`. |
| `IntervencaoOperacional` | `SEQ_GC_INTERVENCAO` | Passo 1; `allocationSize = 1`. |
| `RelatorioPrioridade` | `SEQ_GC_RELATORIO` | Passo 1; `allocationSize = 1`. |

A API envia IDs explícitos obtidos dessas sequences, aceitos pelas colunas identity **BY DEFAULT** do schema legado. O procedimento está em [`docs/sql/migracao-sequences.sql`](docs/sql/migracao-sequences.sql).

1. **Faça backup validado e teste primeiro em schema Oracle de homologação.** A execução requer o proprietário do schema e permissão `CREATE SEQUENCE`.
2. Em **schema novo e vazio**, use [`greencontrol-criacao.sql`](greencontrol-criacao.sql) para criar as tabelas. A carga [`greencontrol-dados.sql`](greencontrol-dados.sql) é opcional e deve preceder a migração. Não execute esses scripts de criação/carga sobre o banco existente.
3. Em **schema Sprint 3 existente**, preserve as tabelas e os dados; não reinicialize o banco. Pare **todos** os escritores: API, console legado, jobs e imports.
4. Execute o script de migração **manualmente**, com SQL*Plus ou SQLcl. Ele cria cada sequence com **`START WITH MAX(id)+1`**, `INCREMENT BY 1`, `NOCYCLE` e `NOCACHE`, sem reutilizar IDs existentes na janela de manutenção.
5. O script recusa sequences já existentes, sem reiniciar contadores. **DDL Oracle faz commit implícito**: uma falha parcial exige inspeção dos objetos e permissões; não apague/recrie sequences nem repita o procedimento cegamente.
6. Depois da migração, **todos os escritores devem usar as novas sequences**. O gerador identity antigo não fica sincronizado. Não execute o console Sprint 3 ou imports sem ID contra o schema migrado, pois podem ocorrer colisões.
7. Antes de liberar o uso, confira registros preservados, IDs novos acima dos anteriores, permissões, CRUD, relacionamentos, histórico e precisão de datas no Oracle de homologação.

**A migração e a conexão Oracle FIAP não foram executadas nem validadas na homologação de 19/10/2026.** A aplicação não executa migrações automaticamente. O teste H2 comprova inserts JPA em tabelas identity com registros anteriores, mas não executa o PL/SQL nem comprova conectividade, permissões ou comportamento do Oracle real.

Referências técnicas: [Oracle CREATE SEQUENCE](https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/CREATE-SEQUENCE.html) e [Oracle ALTER TABLE / identidade](https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/ALTER-TABLE.html).

### 5.4 Executar a Aplicação

Após configurar o Oracle e as variáveis da sessão, execute no Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A API atende, por padrão, em **`http://localhost:8080`**. Também é possível empacotar e executar o JAR:

```powershell
.\mvnw.cmd -B clean verify
java -jar .\target\greencontrol-0.0.1-SNAPSHOT.jar
```

No Linux/macOS, após configurar `JAVA_HOME` e exportar as variáveis `DB_*`:

```bash
./mvnw -B clean verify
./mvnw spring-boot:run
```

O bit executável de `mvnw` está versionado, com LF preservado no checkout. O Wrapper Windows foi validado; a execução em Linux/macOS ainda não foi verificada neste ambiente.

### 5.5 Testes Locais com H2 Isolado

```powershell
.\mvnw.cmd -B clean verify
```

A suíte usa **JUnit 5**, **`@SpringBootTest`**, **MockMvc** e **H2 em memória em modo Oracle**. O H2 é dependência exclusiva de teste. Os testes anteriores usam rollback; os testes REST ampliados confirmam transações e limpam os dados antes e depois de cada caso. Os cenários de HTTP real e compatibilidade identity/sequence usam bancos próprios.

`IdentitySequenceCompatibilityTest` verifica as quatro entidades em tabelas identity com registros de **ID 100** e sequences iniciadas em **101**, preservando os dados anteriores. `HttpEvidenceTest` inicia Tomcat em porta aleatória e registra nove requisições reais, sem exigir Oracle.

Para regenerar somente essa coleta HTTP:

```powershell
.\mvnw.cmd -B '-Dtest=HttpEvidenceTest' test
```

Para iniciar uma API local exclusivamente com os recursos de teste H2, como na coleta cURL documentada:

```powershell
.\mvnw.cmd -B spring-boot:test-run '-Dspring-boot.run.arguments=--server.port=18080'
```

Esse último comando utiliza o classpath de teste, com banco efêmero; não representa a configuração Oracle de produção. Os relatórios completos de teste são gerados em `target/surefire-reports/`. O resultado registrado da homologação está em [`maven-java17.txt`](docs/evidencias/maven-java17.txt).

---

## 6. Exemplos de Requisição com cURL

Use **`curl.exe`** no PowerShell para evitar o alias `curl` de versões antigas. Os exemplos abaixo são instruções reproduzíveis, com IDs obtidos nas respostas; as evidências efetivamente coletadas estão no capítulo 7.

Execute apenas contra um ambiente de homologação: os comandos criam registros e o POST de relatório deixa um snapshot persistido. Para acompanhar os exemplos no H2 da subseção 5.5, use a porta **18080**; para uma API Oracle já preparada, ajuste `$baseUrl` para sua URL.

### 6.1 Criar um Trecho (`POST /api/trechos` — 201)

O contrato aceita equipe opcional. Este corpo não pressupõe uma equipe previamente cadastrada:

```json
{
  "quilometroInicial": 0,
  "quilometroFinal": 10,
  "nivelVegetacao": 65,
  "tipoClima": "Seco",
  "idEquipe": null
}
```

Para evitar diferenças de escape e de BOM entre versões do PowerShell, envie JSON em arquivo UTF-8 sem BOM:

```powershell
$baseUrl = 'http://localhost:18080'
$gcUtf8 = [System.Text.UTF8Encoding]::new($false)
$gcPayload = Join-Path ([System.IO.Path]::GetTempPath()) 'greencontrol-request.json'
$gcJson = '{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":65,"tipoClima":"Seco","idEquipe":null}'
[System.IO.File]::WriteAllText($gcPayload, $gcJson, $gcUtf8)
$gcTrecho = curl.exe --silent --show-error -X POST "$baseUrl/api/trechos" -H 'Content-Type: application/json' --data-binary "@$gcPayload" | ConvertFrom-Json
$trechoId = $gcTrecho.id
$gcTrecho | ConvertTo-Json
```

O POST retorna **201 Created**, corpo com o ID gerado e `Location` para o novo trecho. A variável `$trechoId` será reutilizada nos próximos exemplos.

### 6.2 Consultar Trecho e Coleção (`GET` — 200)

```powershell
curl.exe -i "$baseUrl/api/trechos/$trechoId"
curl.exe -i "$baseUrl/api/trechos"
curl.exe -i "$baseUrl/api/trechos/vegetacao-minima?minimo=30"
curl.exe -i --get "$baseUrl/api/trechos/clima" --data-urlencode 'tipo=Seco'
```

### 6.3 Atualizar o Trecho (`PUT` — 200)

O PUT recebe os campos do trecho; neste exemplo, a vegetação passa a 30 cm, correspondente a `ATENCAO`:

```powershell
$gcJson = '{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":30,"tipoClima":"Seco","idEquipe":null}'
[System.IO.File]::WriteAllText($gcPayload, $gcJson, $gcUtf8)
curl.exe -i -X PUT "$baseUrl/api/trechos/$trechoId" -H 'Content-Type: application/json' --data-binary "@$gcPayload"
```

### 6.4 Simular Crescimento (`POST` — 200)

```powershell
curl.exe -i -X POST "$baseUrl/api/trechos/$trechoId/simular-crescimento"
```

O clima Seco adiciona 2 cm; Úmido adicionaria 8 cm. Esse exemplo altera o estado do trecho e, portanto, as contagens dos relatórios gerados depois dele.

### 6.5 Criar Equipe e Registrar Intervenção (`POST` — 201)

Crie uma equipe e consulte sua especialidade:

```powershell
$gcJson = '{"nome":"Equipe de Homologacao","especialidade":"Rocada Mecanizada"}'
[System.IO.File]::WriteAllText($gcPayload, $gcJson, $gcUtf8)
curl.exe -i -X POST "$baseUrl/api/equipes" -H 'Content-Type: application/json' --data-binary "@$gcPayload"
curl.exe -i --get "$baseUrl/api/equipes/buscar" --data-urlencode 'especialidade=Rocada'
```

A intervenção exige `idTrecho` existente. A data é opcional e, quando informada, usa `yyyy-MM-dd`:

```json
{
  "tipoIntervencao": "RocadaMecanizada",
  "dataIntervencao": "2026-10-19",
  "idTrecho": 1
}
```

O ID 1 acima ilustra o formato; o comando utiliza o ID efetivamente criado, sem pressupor seu valor:

```powershell
$gcJson = @{ tipoIntervencao = 'RocadaMecanizada'; idTrecho = $trechoId } | ConvertTo-Json -Compress
[System.IO.File]::WriteAllText($gcPayload, $gcJson, $gcUtf8)
$gcIntervencao = curl.exe --silent --show-error -X POST "$baseUrl/api/intervencoes" -H 'Content-Type: application/json' --data-binary "@$gcPayload" | ConvertFrom-Json
$intervencaoId = $gcIntervencao.id
$gcIntervencao | ConvertTo-Json
curl.exe -i "$baseUrl/api/intervencoes/trecho/$trechoId"
curl.exe -i --get "$baseUrl/api/intervencoes/tipo" --data-urlencode 'tipo=RocadaMecanizada'
```

O POST aplica a roçada e fixa a vegetação em 5 cm. Para pulverização, o tipo pode ser `Pulverizacao`, reduzindo 10 cm com piso zero. Não use PUT para executar novamente um serviço físico.

### 6.6 Gerar Relatório e Consultar Histórico (201 / 200)

```powershell
curl.exe -i -X POST "$baseUrl/api/relatorios"
curl.exe -i "$baseUrl/api/relatorios"
```

O primeiro comando persiste um snapshot dos trechos atuais. O segundo consulta o histórico, ordenado por data de geração decrescente.

### 6.7 Consultar por Período e Validar Datas (200 / 400)

```powershell
# Data final simples: inclui todo o dia 19/10/2026.
curl.exe -i "$baseUrl/api/relatorios/periodo?inicio=2026-10-19&fim=2026-10-19"
# Horários completos: preserva os limites solicitados.
curl.exe -i "$baseUrl/api/relatorios/periodo?inicio=2026-10-19T10:00:00&fim=2026-10-19T12:00:00"
# Data inválida e intervalo invertido: HTTP 400.
curl.exe -i "$baseUrl/api/relatorios/periodo?inicio=2026-02-30&fim=2026-10-19"
curl.exe -i "$baseUrl/api/relatorios/periodo?inicio=2026-10-20&fim=2026-10-19"
```

Se não houver relatórios no período válido, a resposta será `[]` com HTTP 200.

### 6.8 Excluir Registros e Consultar ID Inexistente (204 / 404)

Se o exemplo de intervenção foi executado, o trecho está protegido por vínculo e sua exclusão retorna **409** até remover o registro da intervenção. A remoção do histórico não reverte a roçada:

```powershell
# Execute esta linha somente se criou a intervenção da subseção 6.5.
curl.exe -i -X DELETE "$baseUrl/api/intervencoes/$intervencaoId"
# Sem intervenção vinculada: 204, sem corpo de resposta.
curl.exe -i -X DELETE "$baseUrl/api/trechos/$trechoId"
# O mesmo ID, agora removido: 404.
curl.exe -i "$baseUrl/api/trechos/$trechoId"
```

### 6.9 Rejeitar Vegetação Negativa (`POST` — 400)

```powershell
$gcJson = '{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":-1,"tipoClima":"Seco"}'
[System.IO.File]::WriteAllText($gcPayload, $gcJson, $gcUtf8)
curl.exe -i -X POST "$baseUrl/api/trechos" -H 'Content-Type: application/json' --data-binary "@$gcPayload"
Remove-Item -LiteralPath $gcPayload
```

### 6.10 Roteiro Reproduzível das Nove Evidências

O script [`docs/evidencias/reproduzir.ps1`](docs/evidencias/reproduzir.ps1) executa nove requisições com `curl.exe`, confere os códigos esperados e grava requisições, cabeçalhos, respostas e `resultado.json`:

```powershell
.\docs\evidencias\reproduzir.ps1 -BaseUrl http://localhost:18080
```

Ele cria e exclui seu próprio trecho e deixa um relatório persistido. Não execute sobre dados reais de produção. Para preservar as evidências registradas e guardar nova coleta em outro diretório:

```powershell
.\docs\evidencias\reproduzir.ps1 -BaseUrl http://localhost:18080 -OutputDir (Join-Path $env:TEMP 'greencontrol-evidencias')
```

---

## 7. Evidências de Funcionamento

As evidências abaixo foram efetivamente coletadas em **19/10/2026**, com **Java 17 e H2 isolado**. Não representam uma conexão homologada com o Oracle FIAP. Os exemplos JSON são transcrições formatadas dos arquivos de resposta do cURL, preservando seus valores reais.

### Evidência 1: Build, Testes e Inicialização Local

Comando registrado na homologação:

```powershell
.\mvnw.cmd -B clean verify
```

Resultado real, em **Temurin 17.0.20.1**, com compilação `release 17`:

```text
Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

O log resumido está em [`maven-java17.txt`](docs/evidencias/maven-java17.txt). A API também iniciou em Tomcat na porta 18080 para a coleta cURL, conforme [`inicializacao-h2.txt`](docs/evidencias/inicializacao-h2.txt), e foi encerrada após a coleta.

| Classe de Teste | Casos Aprovados | Escopo |
| :--- | :---: | :--- |
| `TrechoRodoviaControllerTest` | 5 | Criação, validações, listagem e ID inexistente. |
| `EquipeManutencaoControllerTest` | 1 | Criação, listagem e busca por especialidade. |
| `RelatorioPrioridadeControllerTest` | 1 | Geração e consulta do histórico. |
| `Sprint4IntegrationTest` | 26 | CRUDs completos, vínculos, efeitos, rollback, crescimento, limites de prioridade, totais, períodos e erros. |
| `IdentitySequenceCompatibilityTest` | 1 | Inserts JPA com sequences, tabelas identity e preservação dos registros anteriores. |
| `HttpEvidenceTest` | 1 | Nove requisições reais contra Tomcat em porta aleatória e H2 próprio. |
| **Total** | **35** | **Zero falhas, erros ou casos ignorados.** |

### Evidência 2: Nove Requisições HTTP Verificadas com cURL

A coleta com `curl.exe` está consolidada em [`curl/resultado.json`](docs/evidencias/curl/resultado.json). Os cabeçalhos brutos também estão preservados no mesmo diretório.

| Nº | Requisição Real | Código HTTP | Resposta Registrada |
| :---: | :--- | :---: | :--- |
| 1 | `POST /api/trechos` | **201** | [Corpo da resposta](docs/evidencias/curl/01-post-trecho-response.txt) |
| 2 | `GET /api/trechos/1` | **200** | [Corpo da resposta](docs/evidencias/curl/02-get-trecho-response.txt) |
| 3 | `PUT /api/trechos/1` | **200** | [Corpo da resposta](docs/evidencias/curl/03-put-trecho-response.txt) |
| 4 | `POST /api/relatorios` | **201** | [Corpo da resposta](docs/evidencias/curl/04-post-relatorio-response.txt) |
| 5 | `GET /api/relatorios` | **200** | [Corpo da resposta](docs/evidencias/curl/05-get-historico-response.txt) |
| 6 | `GET /api/relatorios/periodo?inicio=2026-10-19&fim=2026-10-19` | **200** | [Corpo da resposta](docs/evidencias/curl/06-get-periodo-response.txt) |
| 7 | `DELETE /api/trechos/1` | **204** | [Corpo da resposta](docs/evidencias/curl/07-delete-trecho-response.txt) |
| 8 | `POST /api/trechos` | **400** | [Corpo da resposta](docs/evidencias/curl/08-invalido-response.txt) |
| 9 | `GET /api/trechos/1` | **404** | [Corpo da resposta](docs/evidencias/curl/09-inexistente-response.txt) |

Uma segunda coleta, via Java HttpClient e servidor HTTP real, está em [`http-h2.md`](docs/evidencias/http-h2.md). Ela pertence a outra execução; seus horários não devem ser misturados com os da coleta cURL abaixo.

### Evidência 3: Criação do Trecho — HTTP 201 Created

**`POST /api/trechos`**

```json
{
  "id": 1,
  "quilometroInicial": 0.0,
  "quilometroFinal": 10.0,
  "nivelVegetacao": 65.0,
  "tipoClima": "Seco",
  "nivelPrioridade": "URGENTE",
  "idEquipe": null,
  "nomeEquipe": null
}
```

### Evidência 4: Consulta do Trecho — HTTP 200 OK

**`GET /api/trechos/1`**

```json
{
  "id": 1,
  "quilometroInicial": 0.0,
  "quilometroFinal": 10.0,
  "nivelVegetacao": 65.0,
  "tipoClima": "Seco",
  "nivelPrioridade": "URGENTE",
  "idEquipe": null,
  "nomeEquipe": null
}
```

### Evidência 5: Atualização do Trecho — HTTP 200 OK

**`PUT /api/trechos/1`**

```json
{
  "id": 1,
  "quilometroInicial": 0.0,
  "quilometroFinal": 10.0,
  "nivelVegetacao": 30.0,
  "tipoClima": "Seco",
  "nivelPrioridade": "ATENCAO",
  "idEquipe": null,
  "nomeEquipe": null
}
```

### Evidência 6: Geração do Relatório Persistido — HTTP 201 Created

**`POST /api/relatorios`**

```json
{
  "id": 1,
  "qtUrgente": 0,
  "qtCritico": 0,
  "qtAtencao": 1,
  "qtNormal": 0,
  "resumo": "Análise de prioridade realizada para 1 trecho(s) monitorado(s).",
  "dataGeracao": "2026-10-19T11:01:25.6761626"
}
```

### Evidência 7: Consulta do Histórico — HTTP 200 OK

**`GET /api/relatorios`**

```json
[
  {
    "id": 1,
    "qtUrgente": 0,
    "qtCritico": 0,
    "qtAtencao": 1,
    "qtNormal": 0,
    "resumo": "Análise de prioridade realizada para 1 trecho(s) monitorado(s).",
    "dataGeracao": "2026-10-19T11:01:25.676163"
  }
]
```

### Evidência 8: Filtro Incluindo o Dia Final — HTTP 200 OK

**`GET /api/relatorios/periodo?inicio=2026-10-19&fim=2026-10-19`**

A consulta com `inicio=2026-10-19&fim=2026-10-19` retornou o snapshot gerado às 11:01 daquele dia, comprovando a inclusão de registros posteriores à meia-noite na data final.

```json
[
  {
    "id": 1,
    "qtUrgente": 0,
    "qtCritico": 0,
    "qtAtencao": 1,
    "qtNormal": 0,
    "resumo": "Análise de prioridade realizada para 1 trecho(s) monitorado(s).",
    "dataGeracao": "2026-10-19T11:01:25.676163"
  }
]
```

### Evidência 9: Exclusão do Trecho — HTTP 204 No Content

**`DELETE /api/trechos/1`**

```http
HTTP/1.1 204
```

Resposta sem corpo, conforme a semântica de exclusão bem-sucedida.

### Evidência 10: Validação de Vegetação Negativa — HTTP 400 Bad Request

**`POST /api/trechos`**

```json
{
  "timestamp": "2026-10-19T11:01:26.0134244",
  "status": 400,
  "erro": "Erro de Validação de Dados",
  "mensagem": "Um ou mais campos contêm valores inválidos.",
  "detalhes": [
    "nivelVegetacao: Nível de vegetação não pode ser negativo."
  ]
}
```

### Evidência 11: Busca Após Exclusão — HTTP 404 Not Found

**`GET /api/trechos/1`**

```json
{
  "timestamp": "2026-10-19T11:01:26.0474322",
  "status": 404,
  "erro": "Recurso Não Encontrado",
  "mensagem": "Trecho com ID 1 não encontrado.",
  "detalhes": []
}
```

### Evidência 12: Alcance da Homologação e Próximas Verificações

**Aprovado localmente:** build Java 17, 35 testes, inicialização Tomcat/H2, persistência JPA, geração de IDs e nove requisições HTTP verificadas. **Pendente no Oracle FIAP:** conexão, permissões, execução do script de sequences e validação das operações no banco real de homologação.

O [relatório completo de homologação](docs/HOMOLOGACAO.md) detalha o checklist e os limites das verificações; o [inventário de alterações](docs/ARQUIVOS-ALTERADOS.md) registra as correções técnicas daquela etapa. Esses documentos são registros históricos da auditoria: menções à ausência de push descrevem aquele momento; os commits de correção já foram publicados posteriormente no GitHub.

Não foram produzidas screenshots nesta coleta. Para a apresentação acadêmica, execute o roteiro ou importe os exemplos no Postman e capture URL, método, corpo e status. As evidências Oracle antigas não são utilizadas como comprovação da versão corrigida.

---

## 8. Conceitos em Foco — Perguntas de Reflexão

### 1. Por que o Repository é uma interface e não uma classe? Quem escreve a implementação e quando?

> **Resposta:**
>
> O Repository é uma interface porque declara o **contrato de persistência** da entidade. Nas interfaces que estendem `JpaRepository`, o Spring Data cria, na inicialização da aplicação, proxies que fornecem as operações herdadas e as consultas derivadas declaradas pelo projeto.
>
> Não precisamos implementar manualmente `save`, `findAll` ou `findById`: as operações comuns são atendidas pela infraestrutura do Spring Data, normalmente por `SimpleJpaRepository`, que utiliza o `EntityManager`. O Hibernate implementa a persistência JPA e traduz as operações para o banco. Essa separação permite que o Service dependa de um contrato, sem conhecer conexões ou comandos SQL.

---

### 2. O pattern DAO da Sprint 3 "morreu" na migração ou só mudou de forma? Explique com as suas palavras.

> **Resposta:**
>
> O pattern DAO **mudou de forma**: a responsabilidade de isolar o acesso aos dados permanece. Na Sprint 3, o grupo implementava essa responsabilidade com classes DAO, conexões, `PreparedStatement`, parâmetros, leitura de `ResultSet` e fechamento de recursos.
>
> Na Sprint 4, interfaces `JpaRepository` e a infraestrutura JPA assumem grande parte desse trabalho repetitivo. O Service continua sem SQL manual e o Controller continua sem conhecer o banco. A abstração de acesso aos dados foi preservada, enquanto a implementação foi delegada ao framework.

---

### 3. Por que a validação de `nivelVegetacao >= 0` (Sprint 1) deve ficar no Service e não no Controller?

> **Resposta:**
>
> Porque vegetação não negativa é uma **invariante de domínio**, válida independentemente do protocolo de entrada. O Controller deve receber a requisição, validar o contrato HTTP e delegar, sem concentrar as regras de cálculo e integridade.
>
> No GreenControl atual, `@PositiveOrZero` protege o DTO; `@Valid` e a validação dos Services complementam essa proteção. O `TrechoRodoviaService` verifica também vegetação negativa, valores finitos e a relação entre quilômetros. Assim, a regra não depende exclusivamente de uma chamada ao Controller e pode ser reutilizada por outros fluxos que utilizem o Service.
>
> Manter essas responsabilidades no Service evita duplicar regras em cada endpoint e permite associar a validação à mesma transação que persiste a alteração.

---

### 4. No JDBC puro vocês escreviam SQL. Onde está o SQL do `findByTipo()`? Quem o gerou?

> **Resposta:**
>
> `findByTipo()` é a forma conceitual da pergunta; a assinatura existente no projeto é **`findByTipoIntervencaoIgnoreCase(String tipoIntervencao)`**. Não há um método `findByTipo()` declarado nos Repositories atuais.
>
> O Spring Data interpreta o nome do método: `findBy` indica a busca, `TipoIntervencao` identifica o atributo da entidade e `IgnoreCase` define a comparação sem diferenciar maiúsculas/minúsculas. A partir disso, constrói a consulta JPA. O Hibernate produz o SQL adequado ao dialeto configurado, Oracle na aplicação e H2 nos testes.
>
> Portanto, o grupo declara a intenção da consulta na interface, o Spring Data resolve a consulta derivada e o Hibernate executa sua tradução SQL. Não é necessário escrever esse SQL nos Controllers ou Services.

---

## 9. Diferenciais e Bônus Implementados

Os quatro recursos previstos na rubrica estão implementados e são **elegíveis à pontuação adicional**. A atribuição da nota e dos bônus depende da avaliação do professor; os valores abaixo representam a pontuação prevista, não uma nota já concedida.

| Bônus | Implementação no GreenControl | Pontuação Prevista |
| :--- | :--- | :---: |
| **DTOs desacoplados das entidades** | Records `TrechoRequestDTO`, `TrechoResponseDTO`, `EquipeRequestDTO`, `EquipeResponseDTO`, `IntervencaoRequestDTO`, `IntervencaoResponseDTO` e `RelatorioResponseDTO` separam o JSON das entidades JPA. | **+0,3** |
| **Bean Validation** | `@Valid`, `@NotBlank`, `@NotNull`, `@Positive`, `@PositiveOrZero`, `@Size` e `@Digits`, complementados por `OracleText`, validação nos Services e regras de domínio. | **+0,3** |
| **ControllerAdvice** | `GlobalExceptionHandler` com `@RestControllerAdvice` e `ErroRespostaDTO` padroniza erros 400, 404, 409 e 500, sem expor detalhes internos nas falhas inesperadas. | **+0,2** |
| **SpringBootTest + MockMvc** | Testes de Controllers e `Sprint4IntegrationTest`, com H2 isolado; cenários adicionais validam compatibilidade identity/sequence e HTTP real com Tomcat. | **+0,2** |
| **Total Elegível** | **Implementações sujeitas à avaliação acadêmica.** | **+1,0** |

A homologação registrada apresentou **35 testes aprovados, zero falhas, zero erros e `BUILD SUCCESS` em Java 17**. As nove requisições HTTP reais complementam essa evidência. A integração e a migração **Oracle FIAP permanecem pendentes**, conforme o capítulo 7; esses resultados locais não equivalem à homologação do banco externo.

---

## 10. Integrantes do Grupo

* **Cauã** — RM566527
* **Matheus** — RM562765
* **Lucas** — RM561607
* **Rafael** — RM563285
* **Henrique** — RM564699
* **Victor** — RM564804
