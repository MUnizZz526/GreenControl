# Arquivos alterados na homologacao

Base: dd7f9d2. A = criado; M = modificado; D = retirado do versionamento.
Arquivos ignorados de cache, logs completos locais e JDK nao fazem parte da entrega.

| Estado | Arquivo | Motivo |
|---|---|---|
| A | `.env.example` | Placeholders de configuracao, sem segredos reais. |
| M | `.gitignore` | Ignora segredos locais e caches Maven/JDK de homologacao. |
| D | `.idea/.gitignore` | Retirado do versionamento; artefato local de IDE ignorado. |
| D | `.idea/libraries/ojdbc17.xml` | Retirado do versionamento; artefato local de IDE ignorado. |
| D | `.idea/misc.xml` | Retirado do versionamento; artefato local de IDE ignorado. |
| D | `.idea/modules.xml` | Retirado do versionamento; artefato local de IDE ignorado. |
| D | `.idea/vcs.xml` | Retirado do versionamento; artefato local de IDE ignorado. |
| D | `Green Control.iml` | Retirado do versionamento; artefato local de IDE ignorado. |
| M | `README.md` | Instrucoes completas, endpoints, migracao, contratos, resultados reais e perguntas preservadas. |
| A | `docs/ARQUIVOS-ALTERADOS.md` | Relatorio de homologacao e inventario integral de mudancas. |
| A | `docs/HOMOLOGACAO.md` | Relatorio de homologacao e inventario integral de mudancas. |
| A | `docs/evidencias/curl/01-post-trecho-headers.txt` | Captura real curl.exe: 01-post-trecho-headers.txt. |
| A | `docs/evidencias/curl/01-post-trecho-request.json` | Captura real curl.exe: 01-post-trecho-request.json. |
| A | `docs/evidencias/curl/01-post-trecho-response.txt` | Captura real curl.exe: 01-post-trecho-response.txt. |
| A | `docs/evidencias/curl/02-get-trecho-headers.txt` | Captura real curl.exe: 02-get-trecho-headers.txt. |
| A | `docs/evidencias/curl/02-get-trecho-response.txt` | Captura real curl.exe: 02-get-trecho-response.txt. |
| A | `docs/evidencias/curl/03-put-trecho-headers.txt` | Captura real curl.exe: 03-put-trecho-headers.txt. |
| A | `docs/evidencias/curl/03-put-trecho-request.json` | Captura real curl.exe: 03-put-trecho-request.json. |
| A | `docs/evidencias/curl/03-put-trecho-response.txt` | Captura real curl.exe: 03-put-trecho-response.txt. |
| A | `docs/evidencias/curl/04-post-relatorio-headers.txt` | Captura real curl.exe: 04-post-relatorio-headers.txt. |
| A | `docs/evidencias/curl/04-post-relatorio-response.txt` | Captura real curl.exe: 04-post-relatorio-response.txt. |
| A | `docs/evidencias/curl/05-get-historico-headers.txt` | Captura real curl.exe: 05-get-historico-headers.txt. |
| A | `docs/evidencias/curl/05-get-historico-response.txt` | Captura real curl.exe: 05-get-historico-response.txt. |
| A | `docs/evidencias/curl/06-get-periodo-headers.txt` | Captura real curl.exe: 06-get-periodo-headers.txt. |
| A | `docs/evidencias/curl/06-get-periodo-response.txt` | Captura real curl.exe: 06-get-periodo-response.txt. |
| A | `docs/evidencias/curl/07-delete-trecho-headers.txt` | Captura real curl.exe: 07-delete-trecho-headers.txt. |
| A | `docs/evidencias/curl/07-delete-trecho-response.txt` | Captura real curl.exe: 07-delete-trecho-response.txt. |
| A | `docs/evidencias/curl/08-invalido-headers.txt` | Captura real curl.exe: 08-invalido-headers.txt. |
| A | `docs/evidencias/curl/08-invalido-request.json` | Captura real curl.exe: 08-invalido-request.json. |
| A | `docs/evidencias/curl/08-invalido-response.txt` | Captura real curl.exe: 08-invalido-response.txt. |
| A | `docs/evidencias/curl/09-inexistente-headers.txt` | Captura real curl.exe: 09-inexistente-headers.txt. |
| A | `docs/evidencias/curl/09-inexistente-response.txt` | Captura real curl.exe: 09-inexistente-response.txt. |
| A | `docs/evidencias/curl/resultado.json` | Captura real curl.exe: resultado.json. |
| A | `docs/evidencias/http-h2.md` | Nove respostas HTTP reais contra Tomcat/H2 via HttpClient. |
| A | `docs/evidencias/inicializacao-h2.txt` | Trecho real do log de inicializacao e comando utilizado. |
| A | `docs/evidencias/maven-java17.txt` | Resultado real final Maven/Java 17 e contagem dos testes. |
| A | `docs/evidencias/reproduzir.ps1` | Roteiro cURL reproduzivel com validacao de nove codigos HTTP. |
| A | `docs/evidencias/seguranca.txt` | Resultado da verificacao de segredos e ausencia de JDBC ativo. |
| A | `docs/sql/migracao-sequences.sql` | Migracao Oracle manual com sequences acima de MAX(id), sem remover registros. |
| M | `greencontrol-criacao.sql` | Remove limpeza DROP TABLE; criacao somente em schema novo. |
| M | `legacy-sprint3/br/com/greencontrol/db/ConexaoBD.java` | Remove credenciais literais e detalhes sensiveis dos erros de conexao. |
| D | `lib/ojdbc17.jar` | Retirado do versionamento; driver resolvido pelo Maven. |
| M | `mvnw` | Bit executavel Unix versionado (100755). |
| M | `mvnw.cmd` | Corrige acesso a Target nulo no PowerShell. |
| M | `src/main/java/br/com/greencontrol/controller/EquipeManutencaoController.java` | Validacao de parametros positivos ou delegacao do periodo ao Service. |
| M | `src/main/java/br/com/greencontrol/controller/IntervencaoOperacionalController.java` | Validacao de parametros positivos ou delegacao do periodo ao Service. |
| M | `src/main/java/br/com/greencontrol/controller/RelatorioPrioridadeController.java` | Validacao de parametros positivos ou delegacao do periodo ao Service. |
| M | `src/main/java/br/com/greencontrol/controller/TrechoRodoviaController.java` | Validacao de parametros positivos ou delegacao do periodo ao Service. |
| M | `src/main/java/br/com/greencontrol/dto/EquipeRequestDTO.java` | Limites de tamanho/bytes, precisao numerica e IDs positivos. |
| M | `src/main/java/br/com/greencontrol/dto/IntervencaoRequestDTO.java` | Limites de tamanho/bytes, precisao numerica e IDs positivos. |
| M | `src/main/java/br/com/greencontrol/dto/TrechoRequestDTO.java` | Limites de tamanho/bytes, precisao numerica e IDs positivos. |
| M | `src/main/java/br/com/greencontrol/exception/GlobalExceptionHandler.java` | 400/409 padronizados e 500 sem detalhes internos. |
| A | `src/main/java/br/com/greencontrol/model/EfeitoIntervencao.java` | Estrategias polimorficas com normalizacao de acentos e piso zero. |
| M | `src/main/java/br/com/greencontrol/model/EquipeManutencao.java` | SequenceGenerator dedicado e igualdade/hash estaveis na persistencia. |
| M | `src/main/java/br/com/greencontrol/model/IntervencaoOperacional.java` | SequenceGenerator dedicado e igualdade/hash estaveis na persistencia. |
| M | `src/main/java/br/com/greencontrol/model/RelatorioPrioridade.java` | SequenceGenerator dedicado e igualdade/hash estaveis na persistencia. |
| M | `src/main/java/br/com/greencontrol/model/TrechoRodovia.java` | SequenceGenerator dedicado e igualdade/hash estaveis na persistencia. |
| M | `src/main/java/br/com/greencontrol/repository/RelatorioPrioridadeRepository.java` | Derived query com limite superior exclusivo para dia final. |
| M | `src/main/java/br/com/greencontrol/service/EquipeManutencaoService.java` | Validacoes transacionais, preservacao de vinculos ou regras/periodo centralizados. |
| M | `src/main/java/br/com/greencontrol/service/IntervencaoOperacionalService.java` | Validacoes transacionais, preservacao de vinculos ou regras/periodo centralizados. |
| M | `src/main/java/br/com/greencontrol/service/RelatorioPrioridadeService.java` | Validacoes transacionais, preservacao de vinculos ou regras/periodo centralizados. |
| M | `src/main/java/br/com/greencontrol/service/TrechoRodoviaService.java` | Validacoes transacionais, preservacao de vinculos ou regras/periodo centralizados. |
| A | `src/main/java/br/com/greencontrol/validation/OracleText.java` | Validacao de limite UTF-8 para VARCHAR2 BYTE legado. |
| A | `src/main/java/br/com/greencontrol/validation/OracleTextValidator.java` | Validacao de limite UTF-8 para VARCHAR2 BYTE legado. |
| M | `src/main/resources/application.properties` | Variaveis DB_* obrigatorias, sem SQL/stacktrace na resposta e sem alteracao automatica do Oracle. |
| M | `src/test/java/br/com/greencontrol/EquipeManutencaoControllerTest.java` | Rollback isola os testes anteriores entre execucoes. |
| A | `src/test/java/br/com/greencontrol/HttpEvidenceTest.java` | Servidor HTTP real em porta aleatoria, H2 proprio e registro de evidencias. |
| A | `src/test/java/br/com/greencontrol/IdentitySequenceCompatibilityTest.java` | Prova JPA/H2 em schema identity com IDs existentes preservados. |
| M | `src/test/java/br/com/greencontrol/RelatorioPrioridadeControllerTest.java` | Rollback isola os testes anteriores entre execucoes. |
| A | `src/test/java/br/com/greencontrol/Sprint4IntegrationTest.java` | 26 casos de CRUD, dominio, filtros, rollback, validacao e erros; limpeza antes/depois. |
| M | `src/test/java/br/com/greencontrol/TrechoRodoviaControllerTest.java` | Rollback isola os testes anteriores entre execucoes. |
| M | `src/test/resources/application.properties` | Desativa open-in-view tambem nos testes. |
| A | `src/test/resources/identity-compatibility.sql` | Fixture exclusiva H2 de teste: quatro tabelas identity, IDs 100 e sequences 101. |
