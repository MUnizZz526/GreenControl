# GreenControl — Monitoramento Inteligente de Rodovias

<div align="center">

### Segurança nas estradas através de tecnologia e automação

O **GreenControl** é um sistema desenvolvido para auxiliar no monitoramento e priorização da roçada de vegetação em rodovias, permitindo uma gestão mais eficiente da manutenção das vias e contribuindo para a segurança dos motoristas.

---

![Java](https://img.shields.io/badge/Java-11+-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![POO](https://img.shields.io/badge/POO-Orientação%20a%20Objetos-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-green?style=for-the-badge)
![Sprint](https://img.shields.io/badge/Sprint-2-purple?style=for-the-badge)

</div>

---

# Sobre o Projeto

O crescimento descontrolado da vegetação em rodovias pode causar diversos problemas, como:

- Redução da visibilidade das pistas e placas
- Aumento do risco de acidentes
- Dificuldade na manutenção preventiva
- Custos elevados por falta de priorização

O **GreenControl** surge como uma solução para identificar trechos críticos e otimizar a alocação das equipes de manutenção.

---

# Escopo das Sprints

## Sprint 1 — Modelagem do Domínio
Nesta primeira etapa, o foco foi a construção da base do sistema utilizando conceitos de Orientação a Objetos (POO), encapsulamento de atributos privados, validações de integridade e associação básica entre trechos e equipes.

## Sprint 2 — O Motor de Regras (Inteligência)
Nesta segunda etapa, evoluímos a arquitetura para suportar automação e decisões inteligentes. Criamos regras de crescimento baseadas no clima do trecho (seco vs. úmido) e um motor polimórfico que varre a rodovia gerando um relatório de prioridade automático com sugestões de intervenções específicas.

---

# Funcionalidades Implementadas

## Classe `TrechoRodovia`
Responsável por representar os trechos monitorados da rodovia.
- Armazenamento da quilometragem do trecho e do tipo de clima ("Seco" ou "Úmido").
- Controle do nível de vegetação.
- Associação com equipe responsável.
- **Motor de Crescimento:** Simulação de crescimento inteligente baseado no microclima do trecho.

## Interface `MonitoravelViaIoT`
Contrato que adiciona tecnologia de telemetria ao sistema.
- Permite que trechos com sensores enviem dados de crescimento e atualizem o sistema automaticamente, eliminando a dependência exclusiva de inspeção visual humana.

## Estrutura de Intervenções (`IntervencaoOperacional`)
Classe abstrata mãe que define o comportamento base para as ações de campo.
- **`RocadaMecanizada`**: Classe filha especializada em serviços pesados com tratores para trechos de prioridade alta.
- **`Pulverizacao`**: Classe filha especializada na aplicação de reguladores químicos para trechos de prioridade média.

---

# Validações e Integridade

O sistema foi desenvolvido com foco em segurança e confiabilidade dos dados.

### Regras implementadas
- Não permite quilometragem negativa.
- Não permite nível de vegetação inválido ou menor que zero.
- Atributos privados com encapsulamento rigoroso.
- Método de atualização de dados imutável contra taxas de crescimento negativas.

---

# Conceitos Aplicados

| Conceito | Aplicação |
|---|---|
| Encapsulamento | Proteção dos atributos e métodos de validação interna |
| Associação | Ligação lógica entre um trecho e sua equipe responsável |
| Classes Abstratas | Criação do modelo conceitual puro para os serviços de manutenção |
| Interfaces | Desacoplamento de recursos tecnológicos através do contrato IoT |
| Polimorfismo | Disparo dinâmico da intervenção correta baseado na criticidade do trecho |

---

# Como Executar o Projeto

## Pré-requisitos
Antes de começar, você precisará ter instalado:
- JDK 11 ou superior
- Terminal ou IDE Java

## Executando o sistema

Clone o repositório:
```bash
git clone <URL_DO_REPOSITORIO>
```
```
cd GreenControl
```

Compile os arquivos Java:

```javac -d bin src/br/com/greencontrol/*.java```
Execute a aplicação:

Bash
java -cp bin br.com.greencontrol.Main
Perguntas de Reflexão

**1. Por que TrechoRodovia é uma classe e "BR-116 KM 10 ao 15" é um objeto?**
TrechoRodovia é a classe porque funciona como o molde do sistema, definindo quais atributos e comportamentos um trecho deve possuir, como quilometragem, nível de vegetação e equipe responsável.
Já "BR-116 KM 10 ao 15" é um objeto porque representa uma instância real desse molde, contendo valores específicos armazenados na memória do sistema.

**2. Como um método difere de uma função solta em programação estruturada?**
Na programação estruturada, uma função existe de forma independente e recebe dados por parâmetro para executar uma tarefa. Na POO, um método pertence obrigatoriamente a uma classe e representa o comportamento de um objeto, podendo acessar e alterar diretamente os atributos internos daquela instância através do termo this.

**3. Se o nivelVegetacao fosse público, que tipo de problema poderia ocorrer?**
Se o atributo fosse público, qualquer parte do sistema poderia alterar seu valor sem passar por validações. Isso permitiria estados inconsistentes no domínio (como vegetação de -15.0cm), gerando quebras severas e cálculos completamente errados nos algoritmos automáticos de previsão de custos e relatórios de priorização de roçada.

**4. Por que não faz sentido para a Motiva que uma equipe execute apenas uma "Intervenção Operacional" genérica sem especificar qual é?**
Porque "Intervenção Operacional" é um conceito abstrato de alto nível. Uma equipe de campo não consegue carregar um caminhão ou planejar o dia sem saber a ação física exata que irá executar. Ela precisa saber de forma concreta se vai efetuar uma RocadaMecanizada (que exige tratores e roçadeiras) ou uma Pulverizacao (que exige insumos químicos e pulverizadores).

**5. Qual a diferença arquitetural entre fazer um Trecho herdar de uma classe abstrata vs. implementar uma Interface?**
A herança via classe abstrata dita o que um objeto É (relação de identidade estrita e acoplamento forte na árvore genealógica). Já a interface dita o que um objeto É CAPAZ DE FAZER (um contrato de comportamento independente). Usar a interface MonitoravelViaIoT permite que o sistema dê superpoderes de telemetria para o TrechoRodovia sem prendê-lo a uma árvore de herança rígida, permitindo que amanhã um Pedagio ou uma Equipe também implementem IoT de forma totalmente flexível.


```
Estrutura do Projeto
Bash
GreenControl/
│
├── src/
│   └── br/
│       └── com/
│           └── greencontrol/
│               ├── Main.java
│               ├── TrechoRodovia.java
│               ├── EquipeManutencao.java
│               ├── MonitoravelViaIoT.java
│               ├── IntervencaoOperacional.java
│               ├── RocadaMecanizada.java
│               └── Pulverizacao.java
│
├── bin/
└── README.md
```
Desenvolvido por:

Cauã — RM566527

Matheus — RM562765

Lucas — RM561607

Rafael — RM563285

Henrique — RM564699

Victor — RM564804
