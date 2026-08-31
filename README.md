# GreenControl — Monitoramento Inteligente de Rodovias

<div align="center">

### Segurança nas estradas através de tecnologia, automação e persistência de dados

O **GreenControl** é um sistema desenvolvido para auxiliar no monitoramento e priorização da roçada de vegetação em rodovias, permitindo uma gestão mais eficiente da manutenção das vias, integrando telemetria e armazenamento seguro em banco de dados relacional.

---

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle_Database-19c-red?style=for-the-badge&logo=oracle&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-DAO_Pattern-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Concluído-green?style=for-the-badge)
![Sprint](https://img.shields.io/badge/Sprint-3-purple?style=for-the-badge)

</div>

---

# Sobre o Projeto

O crescimento descontrolado da vegetação em rodovias pode causar diversos problemas, como:

- Redução da visibilidade das pistas e placas
- Aumento do risco de acidentes
- Dificuldade na manutenção preventiva
- Custos elevados por falta de priorização

O **GreenControl** surge como uma solução para identificar trechos críticos, otimizar a alocação das equipes de manutenção e manter um histórico completo de relatórios e intervenções gravados diretamente no banco de dados Oracle.

---

# Escopo das Sprints

## Sprint 1 — Modelagem do Domínio
Nesta primeira etapa, a base do sistema foi construída utilizando conceitos fundamentais de POO, encapsulamento, validações de integridade e associação entre trechos e equipes.

## Sprint 2 — O Motor de Regras (Inteligência)
Evolução da arquitetura para suportar tomadas de decisão inteligentes: regras de crescimento baseadas em microclima, suporte à telemetria via interface IoT e um motor polimórfico para geração de relatórios e sugestões de intervenções.

## Sprint 3 — Persistência Relacional com JDBC (Entrega Atual)
Implementação do padrão **DAO (Data Access Object)** e uso de **Java Records** para a comunicação e persistência completa das entidades no banco de dados **Oracle FIAP** via JDBC (`ojdbc17.jar`). Toda a geração de relatórios e manipulação de trechos e equipes agora possui gravação e histórico permanentes.

---

# Arquitetura e Estrutura do Projeto

O projeto foi organizado em pacotes modulares para garantir a separação de responsabilidades (Clean Code / MVC):

```text
GreenControl/
│
├── lib/
│   └── ojdbc17.jar                 # Driver JDBC do Oracle Database
│
├── src/
│   └── br/com/greencontrol/
│       ├── dao/                    # Camada de Acesso a Dados (CRUD + JDBC)
│       │   ├── EquipeManutencaoDAO.java
│       │   ├── IntervencaoOperacionalDAO.java
│       │   ├── RelatorioPrioridadeDAO.java
│       │   └── TrechoRodoviaDAO.java
│       │
│       ├── db/                     # Gerenciamento de Conexão com o Banco
│       │   └── ConexaoBD.java
│       │
│       ├── main/                   # Ponto de Entrada da Aplicação
│       │   └── Main.java
│       │
│       ├── model/                  # Modelos e DTOs Imutáveis (Java Records)
│       │   ├── EquipeManutencaoRecord.java
│       │   ├── IntervencaoOperacionalRecord.java
│       │   ├── RelatorioPrioridadeRecord.java
│       │   └── TrechoRodoviaRecord.java
│       │
│       ├── service/                # Regras de Negócio e Processamentos
│       │   └── GeradorRelatorio.java
│       │
│       ├── EquipeManutencao.java
│       ├── IntervencaoOperacional.java
│       ├── MonitoravelViaIoT.java
│       ├── Pulverizacao.java
│       ├── RocadaMecanizada.java
│       └── TrechoRodovia.java
│
├── greencontrol-criacao.sql        # Script DDL (Criação de Tabelas e Constraints)
├── greencontrol-dados.sql          # Script DML (Inserções e Cargas Iniciais)
└── README.md
```
###  Conceitos e Padrões Aplicados

| Conceito / Padrão | Aplicação no Projeto |
| :--- | :--- |
| **DAO Pattern** | Encapsulamento de toda a lógica SQL de manipulação (`INSERT`, `SELECT`, `UPDATE`, `DELETE`). |
| **Java Records** | Representação imutável dos dados trafegados entre as camadas da aplicação e o banco Oracle. |
| **Encapsulamento** | Proteção rigorosa dos atributos internos da camada de modelo do domínio. |
| **Herança & Polimorfismo** | Estruturação de intervenções operacionais e comportamento dinâmico do gerador de relatórios. |
| **Interfaces** | Contrato `MonitoravelViaIoT` desacoplado para integração de sensores no monitoramento das vias. |

---

###  Mapeamento do Banco de Dados Oracle (DDL)

O banco relacional é composto pelas seguintes tabelas principais:

* `T_EQUIPE_MANUTENCAO`: Cadastro de equipes e suas especialidades de campo.
* `T_TRECHO_RODOVIA`: Trechos monitorados, níveis de vegetação e chave estrangeira para a equipe.
* `T_INTERVENCAO_OPERACIONAL`: Histórico de intervenções realizadas vinculadas aos trechos.
* `T_RELATORIO_PRIORIDADE`: Gravação permanente dos relatórios consolidados de criticidade.

---

###  Como Executar o Projeto

#### **Pré-requisitos**
* **Java JDK 17** ou superior instalado.
* Banco de Dados **Oracle** com credenciais configuradas na classe `ConexaoBD.java`.
* IDE de sua preferência (IntelliJ IDEA, Eclipse, VS Code) ou Terminal.

#### **Configuração do Banco de Dados**
1. Abra seu cliente SQL (DBeaver, SQL Developer ou extensão do VS Code).
2. Conecte-se ao banco Oracle da FIAP.
3. Execute na ordem os arquivos fornecidos na raiz:
   * `greencontrol-criacao.sql`
   * `greencontrol-dados.sql`

#### **Execução via IDE (Recomendado - IntelliJ / Eclipse)**
1. Certifique-se de que o arquivo `lib/ojdbc17.jar` está adicionado como biblioteca do projeto (*Add as Library*).
2. Abra e execute a classe **`br.com.greencontrol.main.Main`**.

#### **Execução via Terminal (Linha de Comando)**

* **Para compilar incluindo o driver JDBC:**
  ```bash
  javac -cp "lib/ojdbc17.jar" -d bin src/br/com/greencontrol/*.java src/br/com/greencontrol/*/*.java

  Para rodar a aplicação:

  java -cp "bin:lib/ojdbc17.jar" br.com.greencontrol.main.Main
(No Windows, utilize ponto e vírgula no classpath: -cp "bin;lib/ojdbc17.jar")

Integrantes do Grupo
Cauã — RM566527

Matheus — RM562765

Lucas — RM561607

Rafael — RM563285

Henrique — RM564699

Victor — RM564804

Evidências de Funcionamento


<img width="824" height="231" alt="image" src="https://github.com/user-attachments/assets/30f7e0ba-1499-43cd-b3c2-207d939024e7" />

<img width="1336" height="770" alt="image" src="https://github.com/user-attachments/assets/b7f72a8c-2554-4002-9285-47d110ef42b4" />

