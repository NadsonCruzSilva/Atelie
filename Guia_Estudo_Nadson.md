# 📚 Guia de Estudo para a Avaliação (Foco: Nadson)

Nadson, a sua parte é fundamental porque trata do início do sistema: **O Cliente e a Entrada da Ordem de Serviço (Atendimento)**. O professor vai focar no seu entendimento sobre Orientação a Objetos Básica (POJOs e Mapeamento) e no fluxo de como uma OS é iniciada.

## 1. Arquivos da Fase de Fundação (Sua base)

### 📄 Pacote `javafxmvc.model.domain` (`Cliente`, `FabricaParceira`, `TipoServico`, `OrdemServico`, `Encaminhamento`)
*   **O que faz:** São os POJOs (Plain Old Java Objects). Eles são a representação das tabelas do banco de dados dentro da memória do Java.
*   **O que o professor pode perguntar:** "Como você fez os relacionamentos entre tabelas no Java? Você salvou os IDs?"
    *   *Resposta:* "Não! Em Orientação a Objetos a gente usa agregação/composição. Na classe `OrdemServico`, em vez de ter um `int idCliente`, eu criei um atributo `private Cliente cliente;`. Quando puxo do banco, instancio um objeto Cliente inteiro e guardo dentro da OS."

---

## 2. CRUD: Cliente

### 📄 `ClienteDAO.java`
*   **O que faz:** Interage com o banco (Tabela `cliente`).
*   **Como explicar na entrevista:** "O meu `ClienteDAO` tem os métodos `inserir()`, `alterar()`, `remover()` e `listar()`. Eu não coloco as variáveis direto na String do SQL porque seria falha de segurança. Eu uso `PreparedStatement` e coloco os dados com `stmt.setString(1, cliente.getNome())`".

### 📄 `FXMLCadastrosClienteController.java` e `DialogController.java`
*   **O que faz:** A tela que o usuário clica para gerenciar clientes.
*   **O que o professor pode perguntar:** Como a Tabela do JavaFX descobre o que mostrar na coluna de "Nome" do Cliente?
    *   *Resposta:* "Na inicialização da tabela, eu uso a classe `PropertyValueFactory<>("nome")`. O JavaFX lê isso e, via reflexão, procura automaticamente um método chamado `getNome()` dentro da classe `Cliente` e mostra o resultado na tela."

---

## 3. Processo: Atendimento (Regra de Negócio Crítica)

A tela de Processos -> Atendimento é o coração do seu trabalho. É aqui que você abre uma nova Ordem de Serviço (OS).

### 📄 `FXMLProcessosAtendimentoController.java` e `DialogController.java`
*   **Como funciona o fluxo:** O usuário clica em "Inserir", a tela modal (Dialog) abre. Ele escolhe um Cliente no ComboBox e um Tipo de Serviço no ComboBox. O sistema calcula a Data Prevista baseada no tempo do serviço e joga o Valor na tela.
*   **A Regra de Negócio (Bloqueio de Inadimplente):** O que acontece se o cliente dever o ateliê?
    *   *Como explicar:* "Antes de salvar a OS no banco, meu DialogController chama um método do DAO chamado `isClienteInadimplente(clienteSelecionado)`. Esse método roda uma query no banco procurando se o cliente tem OS em atraso ou devendo. Se o DAO retornar `true`, eu dou um `Alert` e uso um `return;` para abortar a função. A OS não é criada."
*   **Criação Automática do 1º Encaminhamento:** Quando o Dialog confirma a OS, ele não salva só a OS vazia — ele já cria automaticamente o primeiro `Encaminhamento` (item de serviço) vinculado ao `TipoServico` que o usuário escolheu. Isso evita que a OS nasça "órfã" sem nenhum item.
    *   *Como explicar:* "Dentro do `handleButtonConfirmar()`, após setar os dados da OS, eu instancio um `new Encaminhamento()`, preencho com o TipoServico selecionado, quantidade 1 e o valor calculado. Depois faço `ordemServico.getEncaminhamentos().add(enc)`. Assim, quando o Controller pai recebe a OS de volta do Dialog, ela já vem com o primeiro item pronto para ser persistido."

---

## 4. Gráfico e Relatório

### 📄 `FXMLGraficosStatusController.java`
*   **O que faz:** Mostra um gráfico de Pizza (PieChart) com o resumo de quantas OS estão "RECEBIDA", "FINALIZADA", etc.
*   **Como explicar:** "Meu DAO agrupa as ordens de serviço por `status` usando `GROUP BY` e retorna um `Map<String, Integer>`. No Controller, eu crio instâncias de `PieChart.Data` e adiciono no JavaFX."

### 📄 `RelatorioOrdensAtraso.jrxml`
*   **O que faz:** Relatório gerado no JasperSoft que mostra quais ordens estão presas nas fábricas.
*   **A Mágica do SQL:** A query busca ordens com status `'ENVIADA_FABRICA'` e faz um cálculo matemático de datas: `CURRENT_DATE - e.data_retorno_prevista`. O resultado mostra exatamente quantos dias a fábrica parceira atrasou a entrega da roupa.
