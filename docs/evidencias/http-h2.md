# Evidências HTTP reais — H2 isolado

Geradas por HttpEvidenceTest usando Java HttpClient contra Tomcat em porta aleatória.
Oracle não foi utilizado. Horários e IDs são os retornados na execução.

## POST /api/trechos

```http
POST /api/trechos
{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":65,"tipoClima":"Seco"}
HTTP 201
{"id":1,"quilometroInicial":0.0,"quilometroFinal":10.0,"nivelVegetacao":65.0,"tipoClima":"Seco","nivelPrioridade":"URGENTE","idEquipe":null,"nomeEquipe":null}
```

## GET /api/trechos/1

```http
GET /api/trechos/1
HTTP 200
{"id":1,"quilometroInicial":0.0,"quilometroFinal":10.0,"nivelVegetacao":65.0,"tipoClima":"Seco","nivelPrioridade":"URGENTE","idEquipe":null,"nomeEquipe":null}
```

## PUT /api/trechos/1

```http
PUT /api/trechos/1
{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":30,"tipoClima":"Seco"}
HTTP 200
{"id":1,"quilometroInicial":0.0,"quilometroFinal":10.0,"nivelVegetacao":30.0,"tipoClima":"Seco","nivelPrioridade":"ATENCAO","idEquipe":null,"nomeEquipe":null}
```

## POST /api/relatorios

```http
POST /api/relatorios
HTTP 201
{"id":1,"qtUrgente":0,"qtCritico":0,"qtAtencao":1,"qtNormal":0,"resumo":"Análise de prioridade realizada para 1 trecho(s) monitorado(s).","dataGeracao":"2026-10-19T11:02:07.2753603"}
```

## GET /api/relatorios

```http
GET /api/relatorios
HTTP 200
[{"id":1,"qtUrgente":0,"qtCritico":0,"qtAtencao":1,"qtNormal":0,"resumo":"Análise de prioridade realizada para 1 trecho(s) monitorado(s).","dataGeracao":"2026-10-19T11:02:07.27536"}]
```

## GET /api/relatorios/periodo?inicio=2026-10-19&fim=2026-10-19

```http
GET /api/relatorios/periodo?inicio=2026-10-19&fim=2026-10-19
HTTP 200
[{"id":1,"qtUrgente":0,"qtCritico":0,"qtAtencao":1,"qtNormal":0,"resumo":"Análise de prioridade realizada para 1 trecho(s) monitorado(s).","dataGeracao":"2026-10-19T11:02:07.27536"}]
```

## DELETE /api/trechos/1

```http
DELETE /api/trechos/1
HTTP 204

```

## POST /api/trechos

```http
POST /api/trechos
{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":-1,"tipoClima":"Seco"}
HTTP 400
{"timestamp":"2026-10-19T11:02:07.3453757","status":400,"erro":"Erro de Validação de Dados","mensagem":"Um ou mais campos contêm valores inválidos.","detalhes":["nivelVegetacao: Nível de vegetação não pode ser negativo."]}
```

## GET /api/trechos/1

```http
GET /api/trechos/1
HTTP 404
{"timestamp":"2026-10-19T11:02:07.3543786","status":404,"erro":"Recurso Não Encontrado","mensagem":"Trecho com ID 1 não encontrado.","detalhes":[]}
```

