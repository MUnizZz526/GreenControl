# Homologação Sprint 4 — GreenControl

Data: 19/10/2026, America/Sao_Paulo. Base auditada: `dd7f9d2`, branch `master`,
árvore inicialmente limpa. Não houve push, reset, force push ou alteração do Oracle.

## Resumo e parecer

**Ainda não é possível declarar a entrega integralmente homologada no Oracle FIAP.**
O código e a homologação local estão aprovados: Java 17, build completo, 35 testes
sem falhas e nove requisições reais com cURL. Falta executar e validar a migração
de sequences e a persistência no Oracle de homologação com credenciais válidas.

Foram corrigidos segredos no código ativo/legado, geração de IDs, filtro de período,
validação e respostas de erro. A camada de serviço protege vínculos, impede valores
fora dos limites Oracle e mantém os efeitos físicos das intervenções somente no POST.
Foram preservados CRUDs, consultas derivadas, crescimento por clima, motor e histórico,
assim como fontes históricas e integrantes/perguntas reflexivas. Estratégias polimórficas
representam as intervenções; a adaptação da antiga interface console IoT está no README.

## Checklist da rubrica

| Requisito | Situação | Justificativa/evidência |
|---|---|---|
| Auditoria do estado atual, Git e legado | APROVADO | POM, fontes ativas/legadas, SQL, configuração, testes e README revisados; base limpa e commits inspecionados. |
| Spring Boot 4.1.x, Java 17, Maven | APROVADO | Boot 4.1.1, Maven 3.10.0, `clean verify` executado em Temurin 17.0.20.1. |
| JPA e Oracle JDBC via Maven | APROVADO | Quatro entidades e quatro JpaRepository; `ojdbc17` runtime; nenhum JAR local necessário. |
| Entity/Table/Id/GeneratedValue/SequenceGenerator | APROVADO | Quatro sequences distintas e allocationSize=1; teste H2 com dados anteriores de ID 100 preservados. |
| Migração/schema Oracle real | PARCIAL | Script manual cria acima de MAX(id), sem remover registros; PL/SQL não executado no Oracle. Exige parada de todos os escritores. |
| Segurança dos arquivos versionados atuais | APROVADO | DB_URL/DB_USER/DB_PASSWORD sem valores padrão; legado também via ambiente; ignorados arquivos de segredos. |
| Rotação de credenciais já publicadas | PENDENTE | Depende do proprietário/Oracle. Remoção atual não elimina histórico Git. |
| Controller → Service → Repository | APROVADO | Injeção por construtor, transações no Service, DTOs e validação. Sem SQL/JDBC/console no código ativo. |
| Conceitos anteriores de POO | APROVADO | Encapsulamento/crescimento/prioridades preservados; enum de estratégias com método abstrato e adaptação IoT documentada. |
| CRUD completo de trechos | APROVADO | POST/GET coleção e ID/PUT/DELETE, crescimento e derivados; 400/404 e bloqueio 409 verificados. |
| CRUD completo de equipes | APROVADO | Cinco operações e busca por especialidade; atualização de campos, 404 e vínculo 409 verificados. |
| CRUD completo de intervenções | APROVADO | Cinco operações, filtros tipo/trecho, piso zero, roçada, referências e rollback em atualização inválida. |
| Efeitos de intervenções | APROVADO | POST aplica uma vez; PUT/DELETE corrigem/removem o histórico sem replay ou desfazimento físico. Contrato explícito no README. |
| Relatórios persistidos | APROVADO | Snapshot gravado e consultado em outra transação; histórico persiste após remover trechos. |
| Classificação 15/30/60 e totais | APROVADO | Limites inferiores, superiores e totais exatos testados; classificação centralizada no modelo. |
| Datas simples e horários completos | APROVADO | Dia final incluído por limite exclusivo seguinte; horários inclusivos preservados; entradas inválidas/invertidas 400; vazio 200. |
| DTOs/Bean Validation/domínio | APROVADO | Campos obrigatórios, IDs positivos, tamanhos/caracteres multibyte, NUMBER(8,2), finitude e km final > inicial. |
| Tratamento global | APROVADO | 400 de JSON/parâmetro/validação, 404, 409 e 500 padronizados; 500 não contém mensagem interna. |
| Testes JUnit 5/SpringBootTest/MockMvc/H2 | APROVADO | 35 testes: 35 aprovados, zero falhas, erros e ignorados. Testes independentes com rollback ou limpeza antes/depois. |
| Inicialização e evidências reais | APROVADO | Tomcat/H2 em Java 17; HTTP real no teste e nove operações via curl.exe, com respostas e cabeçalhos salvos. |
| Oracle FIAP ponta a ponta | PENDENTE | Variáveis DB_* ausentes; nenhum acesso/tabela/registro Oracle alterado. |
| Limpeza de versionamento | APROVADO | .idea, .iml e driver local retirados do índice; mantidos no disco quando úteis e ignorados. |
| Wrapper Windows | APROVADO | Corrigida indexação de Target nulo; executado no PowerShell com Maven baixado via Wrapper. |
| Wrapper Linux/macOS | PARCIAL | Bit 100755 versionado; não executado em sistema Unix neste ambiente Windows. |
| README e perguntas reflexivas | APROVADO | Configuração, migração, arquitetura, endpoints completos, limites, exemplos, resultados e grupo documentados. |
| Screenshots | NÃO APLICÁVEL | Evidência textual HTTP/cURL coletada; roteiro de captura manual indicado, sem imagens simuladas. |
| Commits locais / ausência de push | APROVADO | Alterações separadas por segurança, JPA, API, relatório, testes e documentação. Hash final apresentado na resposta de entrega. |

## Testes e execução

Comando exigido, executado com `JAVA_HOME` apontando ao JDK 17 local:

```powershell
.\mvnw.cmd -B clean verify
```

Resultado final: **BUILD SUCCESS — Tests run: 35, Failures: 0, Errors: 0, Skipped: 0**.
Relatório real resumido: [maven-java17.txt](evidencias/maven-java17.txt).
Distribuição: EquipeManutencaoControllerTest (1), TrechoRodoviaControllerTest (5),
RelatorioPrioridadeControllerTest (1), Sprint4IntegrationTest (26),
IdentitySequenceCompatibilityTest (1), HttpEvidenceTest (1).

Bloqueios encontrados e resolvidos: Wrapper acessava `Target[0]` inexistente; sandbox
não permitia cache em .m2 nem download; usado cache ignorado `.maven-local` e execução
autorizada com rede. Java padrão era 21; JDK 17 oficial Temurin foi baixado localmente,
sem instalação global, para executar a homologação exigida. O projeto compila em release 17.
Os defeitos de domínio/REST foram corrigidos e cobertos por regressões; não houve
expectativa alterada para esconder teste falhando. A suíte ampliada passou nas execuções.

O servidor foi iniciado também com `spring-boot:test-run`, recursos H2 de teste e porta
18080. `reproduzir.ps1` enviou requisições via **curl.exe**; todos os nove códigos esperados
foram confirmados antes de encerrar o servidor. [Inicialização real](evidencias/inicializacao-h2.txt).
As evidências [curl/resultado.json](evidencias/curl/resultado.json) referenciam corpos e
cabeçalhos individuais: 201, 200, 200, 201, 200, 200, 204, 400 e 404.
`HttpEvidenceTest` registra uma segunda coleta HTTP real em [http-h2.md](evidencias/http-h2.md).

## Limites da integração Oracle

O SQL original usa identity BY DEFAULT e FKs com ações de exclusão; não havia sequences
dedicadas. O JPA agora fornece IDs de sequences explícitas, compatíveis com essas colunas.
Em janela exclusiva, `migracao-sequences.sql` cria cada sequence acima de MAX(id), sem
reinicializar contadores existentes nem apagar registros. DDL Oracle faz commit implícito;
uma falha parcial requer inspeção de objetos, não reexecução cega.

**Não misturar a API migrada com inserts identity do console legado/imports.**
Esses geradores não são sincronizados; todos os escritores devem usar as novas sequences.
A operação é manual, documentada e nunca disparada pelo Spring. Testes H2 comprovam
inserts JPA, preservação de dados e IDs distintos, mas não a execução do PL/SQL Oracle.
As FKs originais permanecem; bloqueios contra exclusão destrutiva estão no Service da API.

## Pendências externas e entrega

1. Rotacionar credenciais Oracle anteriormente publicadas; elas continuam no histórico Git.
2. Fornecer variáveis válidas e acesso a schema Oracle de homologação (sem dados reais).
3. Validar backup, interromper escritores e aplicar manualmente a migração com permissão
   CREATE SEQUENCE. Conferir objetos e IDs, testar persistência, CRUD, relacionamentos e
   relatórios por período nesse schema; coletar evidências Oracle, sem testes destrutivos.
4. Executar o Wrapper em Linux/macOS somente se for necessário demonstrar essa plataforma.

**Parecer: código pronto e homologado localmente; entrega com integração Oracle plenamente
comprovada ainda NÃO.** Falta a validação externa acima. Nenhum resultado Oracle foi inventado.

## Arquivos e motivos

A lista integral de arquivos criados, alterados e retirados do versionamento está em
[ARQUIVOS-ALTERADOS.md](ARQUIVOS-ALTERADOS.md). Arquivos históricos de domínio e SQL foram
preservados; a limpeza prévia com DROP TABLE foi removida do script de criação.
Não foi reescrito o histórico nem feita qualquer publicação no GitHub.
