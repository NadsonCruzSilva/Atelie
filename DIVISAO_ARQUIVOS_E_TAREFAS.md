# Divisão de Arquivos e Tarefas do Ateliê 🧵

Este documento detalha exatamente quais arquivos cada membro da equipe deve editar e quais as suas responsabilidades específicas ao longo do desenvolvimento do projeto Ateliê de Customização.

---

## 🛠️ Fase 1: Fundação Compartilhada
*A ser concluída por todos antes do foco nos módulos individuais.*

### 🟦 Nadson
* **O que fazer:** Implementar o mapeamento objeto-relacional criando os POJOs (Plain Old Java Objects). Todos devem ter atributos privados, métodos Getters/Setters e os construtores necessários.
* **Arquivos Responsáveis:**
  * `src/javafxmvc/model/domain/Cliente.java`
  * `src/javafxmvc/model/domain/FabricaParceira.java`
  * `src/javafxmvc/model/domain/TipoServico.java`
  * `src/javafxmvc/model/domain/OrdemServico.java`
  * `src/javafxmvc/model/domain/Encaminhamento.java`

### 🟩 Eduardo
* **O que fazer:** Montar o `VBox` principal que servirá de "container" para carregar as outras telas no meio dele. Criar os menus superiores (Cadastros, Processos, Gráficos, Relatórios) e a lógica base que carrega as telas filhas.
* **Arquivos Responsáveis:**
  * `src/javafxmvc/view/FXMLVBoxMain.fxml`
  * `src/javafxmvc/controller/FXMLVBoxMainController.java`

### 🟨 Marco Antônio
* **O que fazer:** Garantir que o banco de dados esteja criado (`script_atelie.sql`), configurar o padrão Factory para a conexão com o PostgreSQL, e o método `start()` para iniciar a aplicação abrindo a tela principal.
* **Arquivos Responsáveis:**
  * `src/javafxmvc/model/database/Database.java`
  * `src/javafxmvc/model/database/DatabaseFactory.java`
  * `src/javafxmvc/model/database/DatabasePostgreSQL.java`
  * `src/javafxmvc/Main.java`

---

## 🚀 Fase 2: Pilares Individuais

### 🟦 Pilar 1: O Cliente e o Atraso (Nadson)
**Responsabilidade Central:** Gerir os clientes que chegam ao ateliê e realizar o atendimento inicial abrindo Ordens de Serviço.

* **1. CRUD (Cliente)**
  * **O que fazer:** Fazer o DAO para banco de dados e as telas FXML e Controllers para Listar, Inserir, Alterar e Remover clientes.
  * **Arquivos:**
    * `src/javafxmvc/model/dao/ClienteDAO.java`
    * `src/javafxmvc/view/FXMLCadastrosCliente.fxml` e `...Dialog.fxml`
    * `src/javafxmvc/controller/FXMLCadastrosClienteController.java` e `...DialogController.java`
* **2. Processo de Negócio (Atendimento)**
  * **O que fazer:** Tela de registro da Ordem de Serviço inicial na loja.
  * **Arquivos:**
    * `src/javafxmvc/model/dao/OrdemServicoDAO.java`
    * `src/javafxmvc/view/FXMLProcessosAtendimento.fxml` e `...Dialog.fxml`
    * `src/javafxmvc/controller/FXMLProcessosAtendimentoController.java` e `...DialogController.java`
* **3. Regra de Negócio (Bloqueio Inadimplente)**
  * **O que fazer:** Na mesma tela de atendimento, antes de dar o `insert()` na OS, o Controller deve verificar se o Cliente selecionado está com `possui_pendencia = TRUE` ou `dias_atraso > 30`. Se sim, mostrar um alerta e impedir a abertura da OS.
* **4. Gráfico (Status - PieChart)**
  * **O que fazer:** Criar método no DAO com `GROUP BY status` na OS, alimentar a View e apresentar o gráfico de pizza.
  * **Arquivos:** `FXMLGraficosStatus.fxml` e `FXMLGraficosStatusController.java`
* **5. Relatório Jaspersoft (Ordens em Atraso)**
  * **O que fazer:** Relatório listando OS onde `data_prevista < data_atual`, agrupadas pela fábrica parceira com a qual a OS se encontra.
  * **Arquivos:** `src/javafxmvc/relatorios/RelatorioOrdensAtraso.jrxml`

---

### 🟩 Pilar 2: A Fábrica e o Volume (Eduardo)
**Responsabilidade Central:** Gerir o cadastro das fábricas que prestam serviços terceirizados para o Ateliê e realizar os envios das roupas/OS para elas.

* **1. CRUD (Fábrica Parceira)**
  * **O que fazer:** DAO, FXMLs e Controllers para manter os dados de contato, CNPJ e especialidade das fábricas.
  * **Arquivos:**
    * `src/javafxmvc/model/dao/FabricaParceiraDAO.java`
    * `src/javafxmvc/view/FXMLCadastrosFabricaParceira.fxml` e `...Dialog.fxml`
    * `src/javafxmvc/controller/FXMLCadastrosFabricaParceiraController.java` e `...DialogController.java`
* **2. Processo de Negócio (Terceirização/Encaminhamento)**
  * **O que fazer:** Tela para encaminhar uma OS existente para uma Fábrica Parceira. Alterar o status da OS para `ENVIADA_FABRICA`. (Requer controle de transação no banco - commit/rollback).
  * **Arquivos:**
    * `src/javafxmvc/model/dao/EncaminhamentoDAO.java`
    * `src/javafxmvc/view/FXMLProcessosTerceirizacao.fxml` e `...Dialog.fxml`
    * `src/javafxmvc/controller/FXMLProcessosTerceirizacaoController.java` e `...DialogController.java`
* **3. Regra de Negócio (Limite Operacional)**
  * **O que fazer:** Ao tentar adicionar um Encaminhamento, o sistema deve contar quantas ordens a fábrica já tem pendentes (`data_retorno IS NULL`). Se for >= 10, bloquear a operação informando que a fábrica está no limite.
* **4. Gráfico (Volume - BarChart)**
  * **O que fazer:** Gráfico de barras horizontais ou verticais demonstrando o número de peças ou OS enviadas para cada fábrica parceira.
  * **Arquivos:** `FXMLGraficosVolume.fxml` e `FXMLGraficosVolumeController.java`
* **5. Relatório Jaspersoft (Ranking de Fábricas)**
  * **O que fazer:** Exibir dados de performance das fábricas: total de serviços já executados por elas, com seus contatos.
  * **Arquivos:** `src/javafxmvc/relatorios/RelatorioRankingFabricas.jrxml`

---

### 🟨 Pilar 3: O Serviço e o Valor (Marco Antônio)
**Responsabilidade Central:** Manter o catálogo de serviços, definir o fluxo de retorno das roupas ao ateliê e gerenciar pagamentos/receitas.

* **1. CRUD (Tipo de Serviço)**
  * **O que fazer:** DAO, FXMLs e Controllers mantendo a descrição dos serviços (bainha, bordado, tingimento), o valor (R$) e o tempo estimado para que seja feito.
  * **Arquivos:**
    * `src/javafxmvc/model/dao/TipoServicoDAO.java`
    * `src/javafxmvc/view/FXMLCadastrosTipoServico.fxml` e `...Dialog.fxml`
    * `src/javafxmvc/controller/FXMLCadastrosTipoServicoController.java` e `...DialogController.java`
* **2. Processo de Negócio (Retorno / Pagamento)**
  * **O que fazer:** Tela onde o usuário escolhe a OS enviada, registra a devolução alterando o status para `FINALIZADA` (ou `RETORNO_FABRICA`), baixando o Encaminhamento e finalizando o pagamento. (Também requer transação manual).
  * **Arquivos:**
    * `src/javafxmvc/view/FXMLProcessosRetorno.fxml` e `...Dialog.fxml`
    * `src/javafxmvc/controller/FXMLProcessosRetornoController.java` e `...DialogController.java`
* **3. Regra de Negócio (Prazo Automático)**
  * **O que fazer:** Sempre que o sistema for calcular a data prevista de uma OS/Encaminhamento, o Controller ou DAO deve somar a data de envio/abertura com o prazo estipulado pelo `TipoServico` selecionado, gerando a Data Prevista automaticamente.
* **4. Gráfico (Receita - LineChart)**
  * **O que fazer:** Fazer um somatório (`SUM`) no valor total de OS finalizadas agrupadas por data (mês). Exibir em um gráfico de linha indicando a evolução do faturamento do ateliê ao longo dos meses.
  * **Arquivos:** `FXMLGraficosReceita.fxml` e `FXMLGraficosReceitaController.java`
* **5. Relatório Jaspersoft (Receita de Serviços)**
  * **O que fazer:** Relatório exibindo a lista de Tipo de Serviço e quanto aquele serviço gerou de receita total e em quantidade para o ateliê.
  * **Arquivos:** `src/javafxmvc/relatorios/RelatorioReceitaServicos.jrxml`
