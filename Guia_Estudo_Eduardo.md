# 📚 Guia de Estudo para a Avaliação (Foco: Eduardo)

Eduardo, a sua avaliação vai bater muito forte no **controle de janelas (Menu Principal)** e no **Processo de Terceirização**, que é a ponte que liga o Ateliê às fábricas de costura parceiras. O seu diferencial para a entrevista é o controle de transações em bloco e o carregamento dinâmico de telas.

## 1. Fase 1 (Fundação): O "Chassi" do Sistema

### 📄 `FXMLVBoxMain.fxml` e `FXMLVBoxMainController.java`
*   **O que faz:** A janela base onde aparecem os menus superiores (Cadastros, Processos, Gráficos) e aquele grande painel cinza vazio no meio onde as telas aparecem.
*   **O que o professor pode perguntar:** Como você abre uma tela filha sem abrir uma nova janela do Windows?
    *   *Resposta:* "Eu tenho um AnchorPane principal no centro. Quando clico no menu, eu chamo a classe `FXMLLoader.load()` passando o caminho do FXML que eu quero abrir (ex: Tela de Fábrica). Ele carrega a tela do disco e eu pego meu painel central e faço um `.getChildren().setAll(telaCarregada)`. A tela nova simplesmente entra dentro do contêiner central da tela antiga."

---

## 2. CRUD: Fábrica Parceira

### 📄 `FabricaParceiraDAO.java`
*   **O que faz:** Cadastra as costureiras e fábricas (Nome, CNPJ e Especialidade).
*   **Como explicar na entrevista:** "O fluxo é padrão MVC. Minha View (arquivo `.fxml`) escuta os cliques nos botões (`#handleButtonInserir`) e invoca o Controller. O Controller puxa os dados digitados e envia para o meu `FabricaParceiraDAO`, que é quem realmente faz o comando JDBC."

### 📄 `FXMLCadastrosFabricaParceiraController.java` e `DialogController.java`
*   **Pontos chave:** Como a janela de formulário (Dialog) sabe se ela deve inserir um novo cadastro ou alterar um que já existe?
    *   *Resposta:* "Quando eu chamo a tela modal pelo `showAndWait()`, eu passo um objeto. Se eu cliquei em 'Inserir', eu passo uma Fábrica em branco (`new FabricaParceira()`). Se cliquei em 'Alterar', eu passo a fábrica selecionada da tabela. Quando o Dialog fecha, o Controller confere se os campos foram preenchidos e chama `dao.inserir()` ou `dao.alterar()` dependendo se a fábrica já tinha um ID ou não."

---

## 3. Processo: Terceirização (Regra de Negócio Crítica)

Esse é o código que o professor mais vai olhar no seu escopo. É quando o Ateliê despacha a roupa para a costureira fora da loja.

### 📄 `FXMLProcessosTerceirizacaoController.java` e `DialogController.java`
*   **A Regra de Negócio (Limite Operacional):** Você não pode entulhar de roupa uma fábrica que já está cheia. 
    *   *Como explicar:* "Antes de salvar o envio, eu chamo `encDAO.isLimiteOperacionalExcedido(fabrica)`. O DAO vê no banco de dados se a fábrica tem 10 ou mais ordens que não foram devolvidas ainda. Se retornar `true`, eu disparo um Alerta na tela e dou um `return` para cancelar o fluxo."
*   **Transação Atômica (O pulo do gato!):** Se você enviar a roupa, **duas coisas** precisam acontecer no banco ao mesmo tempo: 1) Salvar na tabela `encaminhamento` e 2) Mudar a OS na tabela `ordem_servico` de "RECEBIDA" para "ENVIADA_FABRICA".
    *   *A Pergunta do Professor:* O que acontece se a internet cair ou der erro de Java entre essas duas coisas e só uma salvar?
    *   *A Resposta:* "Não vai corromper o banco. Eu uso Controle de Transação JDBC. Antes de rodar, eu falo `connection.setAutoCommit(false)`. Aí executo o INSERT e depois o UPDATE. Se ambas as classes DAO rodarem certinho, eu chamo o `connection.commit()`. Se alguma delas der exception, eu chamo `connection.rollback()`. É tudo ou nada."
*   **Verificação Defensiva do Retorno do DAO:** Além da transação, o código verifica o `boolean` retornado por cada operação do DAO. Os métodos `encDAO.inserir()` e `osDAO.alterar()` retornam `true` ou `false`. Se qualquer um retornar `false` (indicando falha silenciosa sem Exception), o sistema faz `rollback()` e exibe um alerta específico para o usuário.
    *   *Como explicar:* "Eu não confio cegamente que o DAO vai funcionar. Eu capturo o retorno `boolean insertOk = encDAO.inserir(enc)`. Se der `false`, eu chamo o `rollback()` e mostro um Alert dizendo exatamente qual operação falhou — se foi o INSERT do encaminhamento ou o UPDATE da OS. Isso é Programação Defensiva."

---

## 4. Gráfico e Relatório

### 📄 `FXMLGraficosVolumeController.java`
*   **O que faz:** Um BarChart (Gráfico de Barras) mostrando quantas peças cada fábrica já pegou no ateliê.
*   **Como funciona:** "O DAO executa um `SELECT fabrica_id, COUNT(*)` e retorna um Map. O JavaFX exige que os dados do BarChart entrem via uma série matemática. Então eu instancio `XYChart.Series`, amarro o nome da fábrica no eixo X (String) e a contagem no eixo Y (Integer)."

### 📄 `RelatorioRankingFabricas.jrxml`
*   **O que faz:** Um arquivo do JasperReports configurado com uma query pesada.
*   **O que faz no SQL:** Ele faz um ranking de quem movimenta mais dinheiro. O SQL faz um `GROUP BY` de todas as fábricas usando o `LEFT JOIN` para puxar OS ativas, OS finalizadas e a SOMA (`SUM`) total financeira que a fábrica rendeu ao ateliê, ordenando do maior pro menor.
