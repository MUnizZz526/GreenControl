# GreenControl — Sprint 4 FIAP

Monitoramento de vegetação e gestão operacional de rodovias no desafio MOTIVA.
A API mantém trechos, equipes e intervenções e persiste snapshots de prioridade.

## Evolução

| Sprint | Evolução |
|---|---|
| 1 | Encapsulamento, equipes, trechos e domínio no console. |
| 2 | Interface IoT, classe abstrata de intervenção, polimorfismo, crescimento e prioridades. |
| 3 | Oracle via JDBC/DAOs; fonte histórica em `legacy-sprint3/`, fora do build Maven. |
| 4 | Spring Boot, JPA, REST, DTOs, validação, serviços transacionais e testes isolados. |

## Tecnologias e validação

Java 17, Spring Boot **4.1.1**, Maven Wrapper **3.10.0**, Spring Data JPA/Hibernate,
`spring-boot-starter-webmvc`, Bean Validation, Oracle `ojdbc17` via Maven e JUnit 5.
H2 é dependência exclusiva de teste. Não há driver JAR local necessário ao build.

Homologação local em 19/10/2026: **35 testes aprovados, zero falhas/erros**, `clean verify`
com Temurin **17.0.20.1**; compilação, empacotamento e inicialização Tomcat/H2 aprovados.
Oracle FIAP **pendente**, pois não foram fornecidas as variáveis de conexão.
O teste H2 não comprova dialeto, permissões, conectividade ou execução do PL/SQL Oracle.
Veja [relatório de homologação](docs/HOMOLOGACAO.md) e [evidências HTTP reais](docs/evidencias/http-h2.md).

## Arquitetura e domínio

`Controller → Service → JpaRepository → banco`

Controllers cuidam do protocolo HTTP. Services validam regras e delimitam transações;
Repositories são contratos de persistência implementados pelo Spring Data. DTOs em records
separam JSON das entidades. Não há JDBC nem SQL manual no código ativo.
Consultas derivadas cobrem clima, nível mínimo, especialidade, tipo, trecho e período.
`open-in-view=false`; os DTOs são montados dentro das transações dos serviços.

```text
src/main/java/br/com/greencontrol/
  controller/    contratos REST
  service/       regras e transações
  repository/    interfaces JpaRepository
  model/         quatro entidades e estratégias de intervenção
  dto/           entradas validadas e respostas
  exception/     erros JSON padronizados
  validation/    limite de bytes dos VARCHAR2 legados
src/main/resources/application.properties
src/test/        JUnit, MockMvc, HTTP/Tomcat real e H2
legacy-sprint3/  referência histórica (não executar contra schema migrado)
docs/sql/       migração manual não destrutiva de sequences
docs/evidencias/ registros reais e roteiro reproduzível
```

Entidades: `EquipeManutencao` (`T_EQUIPE_MANUTENCAO`), `TrechoRodovia` (`T_TRECHO_RODOVIA`),
`IntervencaoOperacional` (`T_INTERVENCAO_OPERACIONAL`) e `RelatorioPrioridade`
(`T_RELATORIO_PRIORIDADE`). Trecho tem equipe opcional (`ManyToOne`); intervenção tem
um trecho obrigatório (`ManyToOne`). Relatórios são snapshots independentes: excluir um
registro operacional não apaga o histórico de prioridades.

O CRUD rejeita com **409** excluir equipe associada ou trecho com intervenção. Essa regra
protege dados mesmo no schema original, cujas FKs usam `SET NULL` e `CASCADE`.
Escritas diretas fora da API não recebem essa proteção de Service.

A antiga hierarquia de intervenções foi adaptada para estratégias polimórficas em
`EfeitoIntervencao` (enum com método abstrato). A entidade JPA representa o registro,
e não uma classe de impressão no console. Na criação, roçada fixa 5 cm e pulverização
reduz 10 cm, com piso zero; outros tipos continuam permitidos como registros sem efeito.
**PUT corrige o registro histórico; DELETE remove o registro histórico. Nenhum deles
reaplica ou desfaz um serviço físico já executado.** Isso evita duplicação dos efeitos
ou restauração incorreta após crescimento/intervenções posteriores. Para executar outro
serviço, faça novo POST. A data omitida no POST assume hoje; no PUT preserva a existente.

A interface IoT histórica imprimia no console e não tinha integração externa real.
Na API, a simulação está exposta por `/simular-crescimento`: clima Úmido adiciona 8 cm;
outros climas adicionam 2 cm. O legado foi preservado para demonstrar a adaptação.

## Motor de prioridade

| Vegetação (cm) | Classificação JSON |
|---|---|
| > 60 | URGENTE |
| > 30 e ≤ 60 | CRITICO |
| ≥ 15 e ≤ 30 | ATENCAO |
| < 15 | NORMAL |

A classificação tem uma única implementação no domínio (`obterNivelPrioridade`),
reutilizada pelo relatório. O POST persiste contagens e data; consultas leem esse
histórico, não recalculam snapshots antigos. Testes verificam 0, 14,99, 15, 30, 30,01,
60 e 60,01 cm e os totais do relatório.

## Oracle e segurança (Windows PowerShell)

Instale JDK 17 e configure `JAVA_HOME` para seu diretório. `java -version` e
`.\mvnw.cmd -version` devem confirmar o Java 17. O Wrapper baixa o Maven na primeira
execução e requer rede para dependências ainda não armazenadas no cache.

Configure na sessão que executará a aplicação (substitua os placeholders):

```powershell
$env:DB_URL = 'jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL'
$env:DB_USER = 'SEU_USUARIO_FIAP'
$gcSecret = Read-Host 'Senha Oracle' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $gcSecret).Password
.\mvnw.cmd spring-boot:run
# Alternativa, após build:
java -jar .\target\greencontrol-0.0.1-SNAPSHOT.jar
# Ao terminar:
Remove-Item Env:DB_PASSWORD
```

Não há valores padrão para essas três variáveis. `.env.example` contém somente
placeholders; Spring Boot **não carrega `.env` automaticamente**. Segredos locais,
`.idea/`, `.iml`, logs, caches e JARs estão ignorados. Nunca comite segredos.
As credenciais anteriormente publicadas **permanecem no histórico Git**; devem ser
rotacionadas no Oracle. O histórico não foi reescrito e nenhum push foi feito.

A aplicação usa `ddl-auto=none`: não cria, altera, limpa nem migra seu Oracle.
Não registra SQL por padrão nem devolve detalhes internos em erros 500.

## Banco novo e migração de banco existente

1. Faça backup e valide primeiro em schema Oracle de homologação.
2. **Schema novo e vazio:** execute `greencontrol-criacao.sql`. Ele cria somente tabelas;
   não tem limpeza prévia. `greencontrol-dados.sql` é uma carga opcional anterior à migração.
3. **Schema existente Sprint 3:** não execute o script de criação nem a carga de exemplo.
4. Pare TODOS os escritores (API, console legado, jobs e imports). Execute manualmente
   `docs/sql/migracao-sequences.sql` com o dono do schema e permissão `CREATE SEQUENCE`.
5. O script cria quatro sequences com `START WITH MAX(id)+1`, passo 1, `NOCYCLE`.
   JPA usa `@SequenceGenerator(allocationSize=1)` e envia IDs explícitos, aceitos pelas
   colunas `GENERATED BY DEFAULT AS IDENTITY` do schema original. Nenhum registro é removido.
6. O script recusa sequences já existentes, sem reiniciar contadores. DDL Oracle faz
   commit implícito: se houver falha parcial, inspecione objetos/permissões com o DBA;
   não tente apagar/recriar sequences. Mantenha os escritores parados até concluir.
7. Após migrar, **somente a API Sprint 4 ou escritores usando essas mesmas sequences
   podem inserir**. O gerador IDENTITY original não fica sincronizado. Não rode o console
   Sprint 3 nem imports sem ID no schema migrado: podem colidir com IDs da API.
8. Homologue no Oracle: conferir registros preservados, IDs novos acima do máximo,
   permissões das sequences, CRUD, relatórios e precisão das datas em schema descartável.
   Essa etapa não foi executada nesta auditoria.

Referências: [Oracle CREATE SEQUENCE](https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/CREATE-SEQUENCE.html)
e [Oracle ALTER TABLE / identidade](https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/ALTER-TABLE.html).
O teste `IdentitySequenceCompatibilityTest` valida no H2 os quatro inserts JPA em
tabelas identity com registros de ID 100 e sequences iniciando em 101. Não executa PL/SQL.

## Execução e testes

```powershell
.\mvnw.cmd -B clean verify
# Regerar somente evidências HTTP contra servidor isolado H2:
.\mvnw.cmd -B -Dtest=HttpEvidenceTest test
```

A aplicação Oracle atende por padrão em `http://localhost:8080`. Testes usam bancos
H2 isolados e não precisam das variáveis Oracle. Os testes REST novos confirmam suas
transações; limpam os dados antes e depois de cada teste. Os anteriores usam rollback.
O teste HTTP inicia Tomcat real em porta aleatória. O log resumido desta homologação
está em `docs/evidencias/maven-java17.txt`; relatórios completos são gerados em
`target/surefire-reports/` a cada execução.

No Linux/macOS: `./mvnw -B clean verify` e `./mvnw spring-boot:run`, após exportar
as variáveis. O bit executável de `mvnw` está versionado; a execução Unix não foi
validada neste ambiente Windows.

## Endpoints

| Método | Caminho | Sucesso |
|---|---|---|
| GET | `/api/trechos` | 200 |
| GET | `/api/trechos/{id}` | 200 |
| POST | `/api/trechos` | 201 + Location |
| PUT | `/api/trechos/{id}` | 200 |
| DELETE | `/api/trechos/{id}` | 204 |
| POST | `/api/trechos/{id}/simular-crescimento` | 200 |
| GET | `/api/trechos/clima?tipo=Úmido` | 200 |
| GET | `/api/trechos/vegetacao-minima?minimo=30` | 200 |
| GET | `/api/equipes` | 200 |
| GET | `/api/equipes/{id}` | 200 |
| POST | `/api/equipes` | 201 + Location |
| PUT | `/api/equipes/{id}` | 200 |
| DELETE | `/api/equipes/{id}` | 204 |
| GET | `/api/equipes/buscar?especialidade=Roçada` | 200 |
| GET | `/api/intervencoes` | 200 |
| GET | `/api/intervencoes/{id}` | 200 |
| POST | `/api/intervencoes` | 201 + Location |
| PUT | `/api/intervencoes/{id}` | 200 |
| DELETE | `/api/intervencoes/{id}` | 204 |
| GET | `/api/intervencoes/tipo?tipo=Pulverizacao` | 200 |
| GET | `/api/intervencoes/trecho/{trechoId}` | 200 |
| POST | `/api/relatorios` | 201 |
| GET | `/api/relatorios` | 200 |
| GET | `/api/relatorios/periodo?inicio=2026-10-01&fim=2026-10-31` | 200 |

Erros: **400** para JSON, tipos, datas e valores inválidos; **404** para recurso/referência
inexistente; **409** para vínculos/restrições de integridade; **500** para falha inesperada.
IDs devem ser positivos; filtros sem resultados retornam `[]` com 200.
Erros possuem `timestamp`, `status`, `erro`, `mensagem` e `detalhes`.
Textos respeitam tamanhos e limites conservadores de bytes UTF-8 do Oracle; campos
numéricos respeitam `NUMBER(8,2)` (até 999999,99, no máximo duas casas decimais).

O filtro de período aceita ISO `yyyy-MM-dd` ou `yyyy-MM-ddTHH:mm:ss[.fração]`, sem timezone.
Data final simples inclui todo o dia pelo intervalo exclusivo até o próximo dia.
Horários completos são preservados e inclusivos. Se ausentes, início usa um mês atrás
e fim usa agora; valores vazios/inválidos e intervalos invertidos retornam 400.

## Exemplos cURL e evidências

Use `curl.exe` no PowerShell para evitar o alias de versões antigas. O roteiro
[docs/evidencias/reproduzir.ps1](docs/evidencias/reproduzir.ps1) captura nove respostas
reais e códigos HTTP contra uma API já iniciada. Executar apenas em homologação:
cria e exclui seu próprio trecho e grava um relatório permanente.

```powershell
.\docs\evidencias\reproduzir.ps1 -BaseUrl http://localhost:8080
```

O roteiro usa JSON em arquivos de requisição e `--data-binary`, evitando diferenças de
escape entre PowerShell e cURL. Exemplo avulso (guardar o ID retornado):

```powershell
'{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":65,"tipoClima":"Seco"}' | Set-Content -Encoding utf8 trecho.json
curl.exe -i -X POST http://localhost:8080/api/trechos -H 'Content-Type: application/json' --data-binary '@trecho.json'
curl.exe -i http://localhost:8080/api/trechos/ID_RETORNADO
curl.exe -i -X POST http://localhost:8080/api/relatorios
curl.exe -i http://localhost:8080/api/relatorios
```

As evidências atuais em `http-h2.md` foram coletadas automaticamente via Java HttpClient
em HTTP real, sem simulação de resposta. Não houve captura de tela: para a apresentação,
importe os exemplos no Postman ou execute o roteiro e capture URL, corpo e status.
As antigas alegações de homologação Oracle do README não foram usadas como prova da
versão corrigida.

## Perguntas reflexivas

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

## Integrantes

- Cauã — RM566527
- Matheus — RM562765
- Lucas — RM561607
- Rafael — RM563285
- Henrique — RM564699
- Victor — RM564804
