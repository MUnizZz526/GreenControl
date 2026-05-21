# GreenControl — Monitoramento Inteligente de Rodovias

<div align="center">

### Segurança nas estradas através de tecnologia e automação

O **GreenControl** é um sistema desenvolvido para auxiliar no monitoramento e priorização da roçada de vegetação em rodovias, permitindo uma gestão mais eficiente da manutenção das vias e contribuindo para a segurança dos motoristas.

---

![Java](https://img.shields.io/badge/Java-11+-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![POO](https://img.shields.io/badge/POO-Orientação%20a%20Objetos-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-green?style=for-the-badge)
![Sprint](https://img.shields.io/badge/Sprint-1-purple?style=for-the-badge)

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

# Sprint 1 — Modelagem do Domínio

Nesta primeira sprint, o foco foi a construção da base do sistema utilizando conceitos de:

- Orientação a Objetos (POO)
- Clean Code
- Encapsulamento
- Associação entre classes
- Validação de dados
- Regras de negócio

---

# Funcionalidades Implementadas

## Classe `TrechoRodovia`

Responsável por representar os trechos monitorados da rodovia.

### Funcionalidades

- Armazenamento da quilometragem do trecho
- Controle do nível de vegetação
- Associação com equipe responsável
- Simulação do crescimento da vegetação

---

## Classe `EquipeManutencao`

Representa as equipes responsáveis pela manutenção das vias.

### Funcionalidades

- Identificação da equipe
- Associação aos trechos da rodovia
- Organização lógica das operações de manutenção

---

# Validações e Integridade

O sistema foi desenvolvido com foco em segurança e confiabilidade dos dados.

### Regras implementadas

- Não permite quilometragem negativa
- Não permite nível de vegetação inválido
- Atributos privados com encapsulamento
- Métodos de validação para evitar estados inconsistentes

---

# Conceitos Aplicados

| Conceito | Aplicação |
|---|---|
| Encapsulamento | Proteção dos atributos |
| Associação | Ligação entre trecho e equipe |
| Clean Code | Código organizado e legível |
| Regras de Negócio | Controle do crescimento da vegetação |
| POO | Estruturação orientada a objetos |

---

# Como Executar o Projeto

## Pré-requisitos

Antes de começar, você precisará ter instalado:

- JDK 11 ou superior
- Terminal ou IDE Java

---

## Executando o sistema

Clone o repositório:

```bash
git clone <URL_DO_REPOSITORIO>
```

Acesse a pasta do projeto:

```bash
cd GreenControl
```

Compile os arquivos Java:

```bash
javac -d bin src/br/com/greencontrol/*.java
```

Execute a aplicação:

```bash
java -cp bin br.com.greencontrol.Main
```

---
# Perguntas de Reflexão

## 1. Por que `TrechoRodovia` é uma classe e "BR-116 KM 10 ao 15" é um objeto?

`TrechoRodovia` é a classe porque funciona como o molde do sistema, definindo quais atributos e comportamentos um trecho deve possuir, como quilometragem, nível de vegetação e equipe responsável.

Já `"BR-116 KM 10 ao 15"` é um objeto porque representa uma instância real desse molde, contendo valores específicos armazenados na memória do sistema.

---

## 2. Como um método difere de uma função solta em programação estruturada?

Na programação estruturada, uma função existe de forma independente e recebe dados por parâmetro para executar uma tarefa.

Na Programação Orientada a Objetos, um método pertence obrigatoriamente a uma classe e representa o comportamento de um objeto. Além disso, ele pode acessar diretamente os atributos internos da instância através do encapsulamento.

---

## 3. Se o `nivelVegetacao` fosse público, que tipo de problema poderia ocorrer?

Se o atributo fosse público, qualquer parte do sistema poderia alterar seu valor sem validação.

Isso permitiria situações inválidas, como definir um valor negativo para a vegetação, comprometendo a consistência dos dados e podendo gerar erros em cálculos automáticos, relatórios ou algoritmos de priorização da manutenção.

# Estrutura do Projeto

```bash
GreenControl/
│
├── src/
│   └── br/
│       └── com/
│           └── greencontrol/
│               ├── Main.java
│               ├── TrechoRodovia.java
│               └── EquipeManutencao.java
│
├── bin/
└── README.md
```



# Desenvolvido por

- Cauã — RM566527  
- Matheus — RM562765  
- Lucas — RM561607  
- Rafael — RM563285  
- Henrique — RM564699

