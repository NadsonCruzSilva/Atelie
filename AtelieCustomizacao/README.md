# Guia do Projeto: Ateliê de Customização 🧵

Este é o repositório do sistema **Ateliê de Customização**, desenvolvido para a disciplina de POO2. Este documento serve como guia para a equipe de desenvolvimento, detalhando a arquitetura, padrões adotados e a divisão de tarefas.

## 🛠 Padrões Arquiteturais e Stack Tecnológica

O projeto segue rigorosamente o padrão estabelecido no projeto de referência da disciplina.

*   **Padrão Arquitetural:** MVC (Model-View-Controller) Desktop
*   **Camada de Acesso a Dados:** DAO (Data Access Object)
*   **Conexão com Banco de Dados:** Factory Method (`DatabaseFactory` com implementação em `DatabasePostgreSQL`)
*   **Banco de Dados:** PostgreSQL (`jdbc:postgresql://127.0.0.1/atelie`, user: `postgres`, pass: `postgres`)
*   **Linguagem & UI:** Java, JavaFX (com arquivos `.fxml` e `FXMLLoader`)
*   **Relatórios:** Jaspersoft Studio (`.jrxml` compilados para `.jasper`)
*   **IDE Recomendada:** NetBeans

---

## 🏗 Estrutura de Pacotes

A estrutura de diretórios foi definida para manter a organização em conformidade com a arquitetura MVC:

```text
src/javafxmvc/
├── Main.java                            ← Ponto de entrada da aplicação
│
├── model/
│   ├── domain/                          ← Entidades de Negócio (POJOs)
│   ├── dao/                             ← Camada de Acesso a Dados
│   └── database/                        ← Conexão e Factory Method
│
├── controller/                          ← Controladores das telas do JavaFX
│
├── view/                                ← Telas (Interfaces FXML)
│
└── relatorios/                          ← Arquivos Jaspersoft (.jrxml e .jasper)
```

---

## 📜 Regras de Codificação (Importante!)

1. **Acesso a Dados (DAOs):** Nunca abra a conexão dentro do DAO. O DAO deve receber a `Connection` pronta através do método `setConnection(Connection)`.
2. **Transações Complexas:** Para operações que envolvam mais de uma tabela, utilize o controle manual de transação: `connection.setAutoCommit(false)` e finalize com `connection.commit()` (ou `connection.rollback()` em caso de falha).
3. **Gerenciamento de Telas (Views):** Existe um controlador central (`FXMLVBoxMainController`) que carrega as telas filhas (os CRUDs) dentro de um `AnchorPane` principal utilizando `FXMLLoader.load()`.
4. **Formulários de Edição (Dialogs):** Telas de inserção ou alteração devem ser abertas como janelas modais (`Stage` separado) utilizando o método `showAndWait()`.

---

## 👥 Divisão do Trabalho

A divisão foi pensada para garantir uma atuação individual balanceada para a avaliação. Existe uma **Fase 1 (Fundação)** compartilhada e três pilares independentes para cada membro.

### Fase 1: Fundação (Distribuição Inicial)

Para agilizar o início do projeto, a base compartilhada foi dividida entre os integrantes:

*   🟦 **Nadson:** Mapeamento das Entidades, criando todas as 5 classes POJO do pacote `domain` (Cliente, FabricaParceira, TipoServico, OrdemServico, Encaminhamento).
*   🟩 **Eduardo:** Criação do Container e Menu Principal (`FXMLVBoxMain.fxml` e `FXMLVBoxMainController.java`) e a lógica base de navegação entre telas.
*   🟨 **Marco Antônio:** Script SQL de criação do banco/tabelas e configuração da conexão (pacote `database` + `Main.java`).

### Pilares de Desenvolvimento Individual

Cada integrante é responsável de ponta a ponta (View, Controller, Model/DAO) pelas features do seu pilar:

| Componente | 🟦 Nadson | 🟩 Eduardo | 🟨 Marco Antônio |
| :--- | :--- | :--- | :--- |
| **Foco** | O Cliente e o Atraso | A Fábrica e o Volume | O Serviço e o Valor |
| **CRUD** | **Cliente**<br>(Manter dados de contato e status) | **Fábrica Parceira**<br>(Manter CNPJ, contato e especialidade) | **Tipo de Serviço**<br>(Manter descrição, valor e prazo) |
| **Processo** | **1. Atendimento**<br>(Receber peça, abrir OS) | **2. Terceirização**<br>(Adicionar itens/Encaminhar OS para a Fábrica) | **3. Retorno**<br>(Registrar retorno, baixar OS, realizar pagamento) |
| **Regra de Negócio** | **Bloqueio Inadimplente:** Cliente com pendência ou >30 dias de atraso não abre nova OS. | **Limite Operacional:** Uma fábrica não pode ter mais de 10 ordens ativas. | **Prazo Automático:** Data prevista = envio + prazo estipulado pelo Tipo de Serviço. |
| **Gráfico** | **Status (PieChart)**<br>Proporção das OS (Recebida, Enviada, etc.) | **Volume (BarChart)**<br>Quantidade de ordens por fábrica parceira. | **Receita (LineChart)**<br>Evolução de faturamento por mês. |
| **Relatório** | **Ordens em Atraso**<br>Quais ordens estão em atraso, agrupadas por fábrica. | **Ranking de Fábricas**<br>Desempenho (volume e tempo de entrega). | **Receita de Serviços**<br>Rentabilidade e quantidade vendida por serviço. |

---

## 🚀 Recomendações e Fluxo de Trabalho (GitHub)

1. **Sempre puxe (Pull) as alterações antes de começar a trabalhar.** Isso reduz as chances de conflitos de código.
2. Como a arquitetura divide as tarefas em **módulos verticais** quase independentes, a sobreposição de código será mínima. Mas tenha atenção redobrada ao alterar arquivos compartilhados (como o `Main`, os DAOs comuns e o banco de dados).
3. Concluam a *Fase 1 (Fundação)* antes de começarem os seus CRUDs e processos individuais.
4. Finalizem primeiro o básico exigido e as regras de negócio de cada módulo antes de se aventurarem com itens extras como logins complexos ou design CSS muito customizado.
