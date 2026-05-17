# 📚 Guia de Estudo para a Avaliação (Foco: Marco Antônio)

Marco, o professor vai cobrar de você a **Infraestrutura do Banco de Dados**, o CRUD de **Tipo de Serviço** e o Processo de **Retorno** (onde o pagamento é feito). O seu diferencial é o uso de **Padrões de Projeto (Factory)** e **Transações ACID**.

## 1. Arquivos da Fase de Fundação (Sua base)

### 📄 `script_atelie.sql`
*   **O que faz:** Cria a estrutura de tabelas relacionais do banco `atelie` no PostgreSQL.
*   **O que o professor pode perguntar:** Como as tabelas se relacionam?
    *   *Resposta:* Através de Chaves Estrangeiras (Foreign Keys). A tabela `ordem_servico` tem `cliente_id`, e a tabela `encaminhamento` liga a OS à `fabrica_parceira` e ao `tipo_servico`.

### 📄 Pacote `javafxmvc.model.database` (`Database`, `DatabaseFactory`, `DatabasePostgreSQL`)
*   **O que faz:** Gerencia a conexão com o banco sem espalhar o IP e a senha pelo código inteiro.
*   **O que o professor pode perguntar:** Qual padrão de projeto foi usado e por quê?
    *   *Resposta:* Usamos o **Factory Method** (Fábrica). O sistema pede `DatabaseFactory.getDatabase("postgresql")`. A fábrica instancia o `DatabasePostgreSQL` que implementa a interface `Database`. Isso permite trocar o banco de dados no futuro sem reescrever o sistema inteiro.

---

## 2. CRUD: Tipo de Serviço

### 📄 `TipoServicoDAO.java`
*   **O que faz:** Contém os comandos SQL (`INSERT`, `UPDATE`, `DELETE`, `SELECT`) para a tabela `tipo_servico`.
*   **Como explicar na entrevista:** "O meu DAO não abre a conexão sozinho. Ele recebe a conexão do Controller via injeção de dependência (`setConnection`). Uso `PreparedStatement` para proteger contra SQL Injection ao executar o `execute()` ou `executeQuery()`."

### 📄 `FXMLCadastrosTipoServicoController.java` e `DialogController.java`
*   **O que faz:** Controla a tela principal com a Tabela (TableView) e a tela auxiliar que abre por cima (Modal).
*   **Como explicar na entrevista:** "Na tela principal, converto a `List` que o DAO retorna em uma `ObservableList` para alimentar o `TableView`. Quando clico em Inserir/Alterar, eu abro o Dialog usando `dialogStage.showAndWait()`, que trava a tela de trás até o usuário preencher o form e clicar em Confirmar."

---

## 3. Processo: Retorno (Regra de Negócio e Transação)

Esse é o código mais crítico do seu escopo! É aqui que você finaliza a OS e dá o desconto de fidelidade.

### 📄 `FXMLProcessosRetornoController.java` e `DialogController.java`
*   **O que faz:** Pega uma OS que estava com status "ENVIADA_FABRICA" e finaliza, aplicando a regra de negócio.
*   **A Regra de Negócio (Desconto de Fidelidade):** Quando a tela de diálogo vai calcular o valor, ela chama a regra. Se o Cliente tiver 3 ou mais ordens com status "FINALIZADA", você dá 10% de desconto no valor total da OS atual.
*   **O que o professor vai perguntar sobre TRANSAÇÃO:** Como você garante que o banco não fique corrompido se der erro na hora de finalizar?
    *   *Resposta de ouro:* "Eu faço o controle manual da transação JDBC. Primeiro eu chamo `connection.setAutoCommit(false)`. Daí eu atualizo a OS para 'FINALIZADA'. Se tudo der certo, chamo `connection.commit()`. Se der qualquer erro de Java (Exception), o código cai no `catch` e executa `connection.rollback()`, desfazendo tudo o que foi tentado."

---

## 4. Gráfico e Relatórios

### 📄 `FXMLGraficosReceitaController.java` e `OrdemServicoDAO.java` (Método de gráfico)
*   **O que faz:** Monta um gráfico de linha mostrando quanto o ateliê ganhou mês a mês.
*   **A Mágica SQL:** O seu DAO usa a função `EXTRACT(MONTH FROM data_abertura)` no PostgreSQL e um `SUM(valor_total)` para agrupar as vendas por mês.

### 📄 `RelatorioReceitaServicos.jrxml`
*   **O que faz:** Gera um relatório de faturamento agrupado por tipo de serviço.
*   **A Mágica do Código:** No `FXMLVBoxMainController` (botão de Imprimir Relatório), você usou a classe `JasperCompileManager.compileReportToFile`.
    *   *Como explicar:* "Para não dependermos de gerar arquivos pre-compilados (`.jasper`) numa IDE externa, eu programo o sistema para ler o `.jrxml` cru e compilar ele em tempo de execução (*runtime*). Isso garante que o relatório esteja sempre atualizado."
