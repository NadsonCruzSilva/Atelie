# Fundação Compartilhada do Projeto Ateliê de Customização 🧵

> **Documento Técnico Detalhado** — Gerado com análise estática via Code Graph Context  
> **Disciplina:** POO2 | **Equipe:** Nadson, Eduardo, Marco Antônio  
> **Data:** Maio/2026

---

## 1. Introdução

### 1.1. Objetivo desta Fase

A **Fase 1 — Fundação Compartilhada** é o alicerce sobre o qual todo o sistema Ateliê de Customização é construído. Antes que qualquer membro da equipe possa desenvolver seus módulos individuais (CRUDs, processos de negócio, gráficos e relatórios), é necessário que exista uma base funcional e integrada que forneça:

1. **Ponto de entrada da aplicação** (`Main.java`) — responsável por inicializar o JavaFX e exibir a primeira tela.
2. **Tela principal com navegação** (`FXMLVBoxMain.fxml` + Controller) — o "container" central que hospeda dinamicamente todas as telas filhas do sistema.
3. **Infraestrutura de banco de dados** (pacote `database`) — a camada que abstrai a conexão com PostgreSQL usando o padrão Factory Method.
4. **Entidades de domínio** (pacote `domain`) — os POJOs que mapeiam as tabelas do banco de dados para objetos Java, servindo como "lingua franca" entre todas as camadas do sistema.

Somente com esses quatro pilares prontos e funcionais é que os três desenvolvedores puderam trabalhar de forma paralela e independente em seus respectivos módulos.

> 🏗️ **Analogia para entender a Fundação:**  
> Pense em construir um prédio. Antes de levantar os apartamentos (os módulos de Cadastro, Processos, Gráficos), é preciso que a **fundação** esteja pronta: o terreno nivelado (Main.java), a estrutura de concreto que sustenta os andares (VBoxMain), a rede elétrica e hidráulica que alimenta tudo (Database), e a planta que define como cada cômodo se conecta (as Entidades de Domínio). Se qualquer uma dessas peças falhar, nenhum "apartamento" funciona. Por isso toda a equipe trabalhou junta nessa fase antes de se dividir.

### 1.2. Padrão Arquitetural Adotado (MVC + DAO)

O projeto adota o padrão **MVC (Model-View-Controller)** para aplicações desktop, complementado pelo padrão **DAO (Data Access Object)** para isolar o acesso a dados.

> 🎭 **O que é MVC em linguagem simples?**  
> Imagine um **restaurante**:
> - A **View** é o cardápio e a mesa onde o cliente senta — é o que o usuário vê e toca (as telas FXML).
> - O **Controller** é o garçom — ele recebe o pedido do cliente, leva para a cozinha, e traz o prato pronto de volta. Ele **não cozinha** e **não é o cardápio**, apenas coordena.
> - O **Model** é a cozinha e a despensa — é onde os dados são preparados (DAO) e onde os ingredientes ficam armazenados (Domain/Entidades).
>
> O padrão **DAO** é como se, dentro da cozinha, houvesse um funcionário específico só para buscar ingredientes no estoque (banco de dados). O cozinheiro (Controller) nunca vai diretamente ao depósito — ele pede ao estoquista (DAO).

```
┌─────────────────────────────────────────────────────────┐
│                      VIEW (FXML)                        │
│   FXMLCadastrosCliente.fxml, FXMLVBoxMain.fxml, etc.    │
│   → O que o usuário vê: botões, tabelas, campos         │
└──────────────────────┬──────────────────────────────────┘
                       │  Eventos de UI (@FXML)
                       ▼
┌─────────────────────────────────────────────────────────┐
│                    CONTROLLER                           │
│   FXMLCadastrosClienteController.java, etc.             │
│   → O "garçom": recebe o clique, pede dados ao Model,  │
│     e atualiza a tela com o resultado                   │
└──────────┬──────────────────────────────┬───────────────┘
           │                              │
           ▼                              ▼
┌─────────────────────┐    ┌──────────────────────────────┐
│   MODEL / DOMAIN    │    │         MODEL / DAO           │
│   (POJOs)           │    │   ClienteDAO, OrdemServicoDAO │
│   Cliente.java      │◄───│   → O "estoquista": fala com  │
│   OrdemServico.java │    │     o banco e devolve objetos │
│   TipoServico.java  │    │     Java prontos para usar    │
└─────────────────────┘    └──────────────┬───────────────┘
                                          │
                                          ▼
                           ┌──────────────────────────────┐
                           │      MODEL / DATABASE         │
                           │   DatabaseFactory (Factory)   │
                           │   DatabasePostgreSQL (JDBC)   │
                           │   Database (Interface)        │
                           └──────────────────────────────┘
                                          │
                                          ▼
                                    ┌───────────┐
                                    │ PostgreSQL │
                                    │  (atelie)  │
                                    └───────────┘
```

**Princípio fundamental:** os Controllers nunca acessam o banco diretamente. Eles instanciam DAOs, injetam a `Connection` via `setConnection()`, e chamam métodos como `listar()`, `inserir()`, `alterar()` e `remover()`.

> 💡 **Por que essa separação importa?** Porque se amanhã o projeto precisar trocar o PostgreSQL por MySQL, só precisamos mexer no pacote `database`. Os Controllers e as Views nem ficam sabendo — eles continuam pedindo "me dá a lista de clientes" do mesmo jeito. Isso é o poder de uma arquitetura bem separada.

---

## 2. Estrutura de Pacotes

A organização completa do código-fonte segue uma hierarquia clara e padronizada:

```
src/javafxmvc/
├── Main.java                       ← Ponto de entrada (Application.start)
│
├── model/
│   ├── domain/                     ← Entidades de Negócio (POJOs)
│   │   ├── Cliente.java            ← Cliente do ateliê
│   │   ├── FabricaParceira.java    ← Fábrica terceirizada
│   │   ├── TipoServico.java       ← Catálogo de serviços
│   │   ├── OrdemServico.java       ← Ordem de serviço (entidade central)
│   │   └── Encaminhamento.java     ← Envio de OS para fábrica
│   │
│   ├── dao/                        ← Data Access Objects
│   │   ├── ClienteDAO.java         ← CRUD de clientes
│   │   ├── FabricaParceiraDAO.java ← CRUD de fábricas
│   │   ├── TipoServicoDAO.java    ← CRUD de tipos de serviço
│   │   ├── OrdemServicoDAO.java    ← CRUD + regras de OS
│   │   └── EncaminhamentoDAO.java  ← CRUD + regras de encaminhamentos
│   │
│   └── database/                   ← Infraestrutura de conexão
│       ├── Database.java           ← Interface (contrato)
│       ├── DatabaseFactory.java    ← Factory Method
│       └── DatabasePostgreSQL.java ← Implementação concreta (JDBC)
│
├── controller/                     ← 17 Controllers JavaFX
│   ├── FXMLLoginController.java
│   ├── FXMLVBoxMainController.java          ← Controlador central de navegação
│   ├── FXMLCadastrosClienteController.java
│   ├── FXMLCadastrosClienteDialogController.java
│   ├── FXMLCadastrosFabricaParceiraController.java
│   ├── FXMLCadastrosFabricaParceiraDialogController.java
│   ├── FXMLCadastrosTipoServicoController.java
│   ├── FXMLCadastrosTipoServicoDialogController.java
│   ├── FXMLProcessosAtendimentoController.java
│   ├── FXMLProcessosAtendimentoDialogController.java
│   ├── FXMLProcessosTerceirizacaoController.java
│   ├── FXMLProcessosTerceirizacaoDialogController.java
│   ├── FXMLProcessosRetornoController.java
│   ├── FXMLProcessosRetornoDialogController.java
│   ├── FXMLGraficosStatusController.java
│   ├── FXMLGraficosVolumeController.java
│   └── FXMLGraficosReceitaController.java
│
├── view/                           ← 17 telas FXML + 1 CSS
│   ├── atelie.css                  ← Estilização global
│   ├── FXMLLogin.fxml
│   ├── FXMLVBoxMain.fxml           ← Container principal
│   ├── FXMLCadastrosCliente.fxml
│   ├── FXMLCadastrosClienteDialog.fxml
│   ├── FXMLCadastrosFabricaParceira.fxml
│   ├── FXMLCadastrosFabricaParceiraDialog.fxml
│   ├── FXMLCadastrosTipoServico.fxml
│   ├── FXMLCadastrosTipoServicoDialog.fxml
│   ├── FXMLProcessosAtendimento.fxml
│   ├── FXMLProcessosAtendimentoDialog.fxml
│   ├── FXMLProcessosTerceirizacao.fxml
│   ├── FXMLProcessosTerceirizacaoDialog.fxml
│   ├── FXMLProcessosRetorno.fxml
│   ├── FXMLProcessosRetornoDialog.fxml
│   ├── FXMLGraficosStatus.fxml
│   ├── FXMLGraficosVolume.fxml
│   └── FXMLGraficosReceita.fxml
│
└── relatorios/                     ← Relatórios JasperSoft (.jrxml / .jasper)
```

### Responsabilidade de Cada Pacote

| Pacote | Responsabilidade | Quantidade |
|:---|:---|:---:|
| `javafxmvc` (raiz) | Ponto de entrada da aplicação | 1 classe |
| `model.domain` | Entidades de negócio (POJOs serializáveis) | 5 classes |
| `model.dao` | Acesso a dados com JDBC (PreparedStatement) | 5 classes |
| `model.database` | Abstração de conexão (Factory Method) | 3 classes |
| `controller` | Lógica de interface e orquestração MVC | 17 classes |
| `view` | Interfaces declarativas FXML + CSS | 18 arquivos |
| `relatorios` | Templates JasperSoft para relatórios PDF | variável |

**Totais do projeto** (extraídos via Code Graph Context): **34 arquivos**, **31 classes**, **225 funções/métodos**.

---

## 3. Componentes da Fundação

### 3.1. Ponto de Entrada — `Main.java`

**Localização:** `src/javafxmvc/Main.java`  
**Responsável:** 🟨 Marco Antônio

A classe `Main` é o ponto de entrada da aplicação JavaFX — é literalmente **a primeira coisa que roda** quando você clica "Executar" no NetBeans. Ela estende `javafx.application.Application` e implementa o método `start()`, que é invocado automaticamente pelo runtime do JavaFX quando a aplicação inicia.

> 🚪 **Pense assim:** O `Main.java` é a **porta de entrada** do prédio. Quando alguém chega (o Java executa), a primeira coisa que acontece é abrir essa porta, que leva o visitante até a portaria (tela de Login). Sem essa porta, ninguém entra no sistema.

```java
package javafxmvc;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/FXMLLogin.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("view/atelie.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Ateliê de Customização - Login");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

#### Análise Linha a Linha

| Trecho | Função |
|:---|:---|
| `extends Application` | Herança obrigatória do JavaFX para aplicações gráficas |
| `FXMLLoader.load(...)` | Carrega a árvore de nós da tela de Login a partir do arquivo FXML |
| `scene.getStylesheets().add(...)` | Aplica o CSS global (`atelie.css`) à cena |
| `stage.setResizable(false)` | A tela de login é fixa (não permite redimensionamento) |
| `stage.centerOnScreen()` | Centraliza a janela no monitor |
| `launch(args)` | Método estático que dispara o ciclo de vida do JavaFX |

> **Decisão de Design:** A primeira tela exibida é o **Login** (`FXMLLogin.fxml`), e **não** a tela principal. A tela principal (`FXMLVBoxMain.fxml`) só é carregada após a autenticação bem-sucedida no `FXMLLoginController`.

#### Tela de Login — `FXMLLoginController.java`

O controller de login implementa uma autenticação simplificada com credenciais fixas:

```java
private static final String USUARIO_PADRAO = "admin";
private static final String SENHA_PADRAO = "admin";
```

> 🔐 **Por que credenciais fixas?** Em um sistema de produção real, o login consultaria uma tabela `usuarios` no banco de dados. Aqui, como o foco do projeto é praticar MVC+DAO e não autenticação, usamos valores fixos no código. Para entrar no sistema, basta digitar `admin` nos dois campos.

O método `handleButtonEntrar()` faz uma coisa simples em 3 passos:
1. **Verifica se os campos estão preenchidos** — se não, mostra mensagem de erro.
2. **Compara com as credenciais fixas** — se bater, chama `abrirTelaPrincipal()`.
3. **Se a senha estiver errada** — exibe "Usuário ou senha incorretos!" e limpa o campo de senha.

O método `abrirTelaPrincipal()` é onde a "mágica" acontece — ele troca a tela de Login pela tela principal do sistema:

```java
private void abrirTelaPrincipal() {
    Parent root = FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLVBoxMain.fxml"));
    Stage stage = (Stage) buttonEntrar.getScene().getWindow();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/javafxmvc/view/atelie.css").toExternalForm());
    stage.setScene(scene);
    stage.setTitle("Ateliê de Customização - Sistema de Gestão");
    stage.setResizable(true);       // ← Agora permite redimensionar
    stage.setWidth(900);
    stage.setHeight(650);
    stage.centerOnScreen();
    stage.show();
}
```

> 🎬 **O que está acontecendo aqui, passo a passo:**
> 1. `FXMLLoader.load(...)` — Lê o arquivo XML da tela principal e cria todos os componentes visuais (menus, painéis, etc.) automaticamente.
> 2. `(Stage) buttonEntrar.getScene().getWindow()` — Pega a **janela que já está aberta** (a do Login). Em vez de abrir uma janela nova, reutilizamos a mesma.
> 3. `stage.setScene(scene)` — **Troca o cenário**. É como se a mesma janela do teatro trocasse o cenário por trás da cortina — a janela é a mesma, mas o conteúdo mudou completamente.
> 4. `stage.setResizable(true)` — Agora a janela pode ser redimensionada (na tela de Login era fixa).
> 5. Definimos o novo tamanho (900x650) e centralizamos na tela.

---

### 3.2. Tela Principal — `FXMLVBoxMain.fxml` + Controller

**Localização:** `src/javafxmvc/view/FXMLVBoxMain.fxml` e `src/javafxmvc/controller/FXMLVBoxMainController.java`  
**Responsável:** 🟩 Eduardo

Esta é a peça mais importante da fundação em termos de navegação. O `FXMLVBoxMain` funciona como um **shell (container)** que hospeda todas as outras telas do sistema.

> 📺 **Analogia da TV:** Pense na tela principal como uma **televisão**. A barra de menus no topo é o **controle remoto** — você escolhe qual "canal" quer assistir (Cadastros, Processos, Gráficos...). A área de conteúdo abaixo é a **tela da TV** — ela muda de conteúdo quando você troca de canal, mas a TV em si (a janela, os menus) permanece no lugar. Quando você clica em "Clientes" no menu, é como trocar para o canal de cadastro de clientes. A tela principal nunca fecha, ela apenas troca o que está sendo exibido no centro.

#### Estrutura do FXML

O layout é um `VBox` (caixa vertical, ou seja, uma caixa que empilha coisas de cima para baixo) com dois filhos:
1. **`MenuBar`** — Barra de menus no topo (o "controle remoto") com 5 menus: Cadastros, Processos, Gráficos, Relatórios e Sistema.
2. **`AnchorPane`** — Área de conteúdo dinâmico (a "tela da TV") que ocupa todo o espaço restante.

```xml
<VBox prefHeight="650.0" prefWidth="900.0"
      fx:controller="javafxmvc.controller.FXMLVBoxMainController">
   <children>
      <MenuBar>
        <menus>
          <Menu text="Cadastros">
            <items>
              <MenuItem fx:id="menuItemCadastrosClientes"
                        onAction="#handleMenuItemCadastrosClientes"
                        text="Clientes" />
              <MenuItem fx:id="menuItemCadastrosFabricaParceira"
                        onAction="#handleMenuItemCadastrosFabricaParceira"
                        text="Fábricas Parceiras" />
              <MenuItem fx:id="menuItemCadastrosTipoServico"
                        onAction="#handleMenuItemCadastrosTipoServico"
                        text="Tipos de Serviço" />
            </items>
          </Menu>
          <!-- Processos, Gráficos, Relatórios, Sistema... -->
        </menus>
      </MenuBar>
      <AnchorPane fx:id="anchorPane" VBox.vgrow="ALWAYS" />
   </children>
</VBox>
```

#### Mapa Completo de Menus

| Menu | Item | Método Handler | Tela FXML Carregada |
|:---|:---|:---|:---|
| **Cadastros** | Clientes | `handleMenuItemCadastrosClientes()` | `FXMLCadastrosCliente.fxml` |
| | Fábricas Parceiras | `handleMenuItemCadastrosFabricaParceira()` | `FXMLCadastrosFabricaParceira.fxml` |
| | Tipos de Serviço | `handleMenuItemCadastrosTipoServico()` | `FXMLCadastrosTipoServico.fxml` |
| **Processos** | Atendimento | `handleMenuItemProcessosAtendimento()` | `FXMLProcessosAtendimento.fxml` |
| | Terceirização | `handleMenuItemProcessosTerceirizacao()` | `FXMLProcessosTerceirizacao.fxml` |
| | Retorno | `handleMenuItemProcessosRetorno()` | `FXMLProcessosRetorno.fxml` |
| **Gráficos** | Status | `handleMenuItemGraficosStatus()` | `FXMLGraficosStatus.fxml` |
| | Volume por Fábrica | `handleMenuItemGraficosVolume()` | `FXMLGraficosVolume.fxml` |
| | Receita Mensal | `handleMenuItemGraficosReceita()` | `FXMLGraficosReceita.fxml` |
| **Relatórios** | Ordens em Atraso | `handleMenuItemRelatoriosOrdensAtraso()` | *(JasperSoft)* |
| | Ranking de Fábricas | `handleMenuItemRelatoriosRankingFabricas()` | *(JasperSoft)* |
| | Receita de Serviços | `handleMenuItemRelatoriosReceitaServicos()` | *(JasperSoft)* |
| **Sistema** | Sair / Logout | `handleMenuItemSair()` | Volta para `FXMLLogin.fxml` |

#### O Mecanismo de Carregamento Dinâmico — `loadView()`

O método `loadView()` é o **coração da navegação** do sistema. Segundo a análise do Code Graph Context, ele possui **Fan-In de 9** — ou seja, é chamado por 9 métodos diferentes, sendo o segundo nó mais requisitado de toda a aplicação.

> 🔑 **O que é Fan-In?** Fan-In é uma métrica de análise de código que conta "quantas outras funções chamam esta função". Fan-In = 9 significa que 9 métodos diferentes pedem a ajuda de `loadView()` para funcionar. Quanto maior o Fan-In, mais importante a função é — e mais cuidado precisamos ter ao alterá-la, porque qualquer mudança impacta muitos pontos do sistema.

```java
private void loadView(String fxmlPath) {
    try {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource(fxmlPath));
        anchorPane.getChildren().setAll(a);       // Substitui o conteúdo atual
        AnchorPane.setTopAnchor(a, 0.0);          // Ancora em todos os lados
        AnchorPane.setBottomAnchor(a, 0.0);
        AnchorPane.setLeftAnchor(a, 0.0);
        AnchorPane.setRightAnchor(a, 0.0);
    } catch (IOException e) {
        System.err.println("Erro ao carregar: " + fxmlPath);
        e.printStackTrace();
    }
}
```

> 🧩 **Entendendo cada linha de `loadView()` como se fosse uma receita:**
>
> 1. **`FXMLLoader.load(getClass().getResource(fxmlPath))`** — *"Abra o arquivo FXML que eu estou pedindo e monte a tela descrita nele."* O JavaFX lê o XML, cria os botões, tabelas, labels, e já conecta tudo ao Controller da tela.
>
> 2. **`anchorPane.getChildren().setAll(a)`** — *"Jogue fora tudo o que estava na área central e coloque essa tela nova no lugar."* É o `setAll` que faz a troca — ele remove os filhos anteriores e coloca o novo.
>
> 3. **`setTopAnchor(a, 0.0)` ... `setRightAnchor(a, 0.0)`** — *"Estique essa tela nova para ocupar todo o espaço disponível."* Sem essas linhas, a tela filha ficaria com o tamanho mínimo no canto superior esquerdo, em vez de preencher a área inteira.
>
> **Se o carregamento falhar** (por exemplo, se o arquivo FXML não existir), o `catch` imprime o erro no console, mas o sistema não trava — ele simplesmente fica com a área central vazia.

Cada handler de menu simplesmente delega para `loadView()`:

```java
@FXML public void handleMenuItemCadastrosClientes() {
    loadView("/javafxmvc/view/FXMLCadastrosCliente.fxml");
}
```

#### Logout — `handleMenuItemSair()`

O botão "Sair" não encerra a aplicação, mas retorna à tela de login substituindo a Scene:

```java
@FXML
public void handleMenuItemSair() {
    Parent root = FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLLogin.fxml"));
    Stage stage = (Stage) anchorPane.getScene().getWindow();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/javafxmvc/view/atelie.css").toExternalForm());
    stage.setScene(scene);
    stage.setTitle("Ateliê de Customização - Login");
    stage.setResizable(false);
    stage.setWidth(800);
    stage.setHeight(500);
    stage.centerOnScreen();
}
```

---

### 3.3. Conexão com Banco de Dados — Pacote `model.database`

**Localização:** `src/javafxmvc/model/database/`  
**Responsável:** 🟨 Marco Antônio

O pacote `database` implementa o padrão **Factory Method** para abstrair a criação de conexões com o banco de dados. Ele é composto por três classes que, juntas, formam o componente mais crítico do sistema — o método `Database.conectar()` tem **Fan-In de 11** (o maior do projeto inteiro).

> 🔌 **Analogia da Tomada e do Adaptador:**  
> Imagine que você tem um aparelho eletrônico (o DAO) que precisa de energia (dados do banco). A `interface Database` é o **formato da tomada** — ela define que toda fonte de energia deve ter os métodos `conectar()` e `desconectar()`. A classe `DatabasePostgreSQL` é o **adaptador específico** para energia PostgreSQL. E a `DatabaseFactory` é a **loja de adaptadores** — você diz "quero PostgreSQL" e ela te dá o adaptador certo. Se amanhã o banco mudar para MySQL, basta criar um novo adaptador (`DatabaseMySQL`) e registrá-lo na fábrica — o resto do sistema nem percebe a mudança.

#### 3.3.1. Interface `Database.java`

```java
package javafxmvc.model.database;

import java.sql.Connection;

public interface Database {
    public Connection conectar();
    public void desconectar(Connection conn);
}
```

Define o **contrato** que qualquer implementação de banco de dados deve seguir:
- `conectar()` — Abre e retorna uma `java.sql.Connection`
- `desconectar(Connection)` — Fecha a conexão fornecida

> 📝 **O que é uma Interface em Java, de forma simples?** Uma interface é como um **contrato de trabalho**. Ela diz: "quem quiser trabalhar aqui precisa saber fazer essas coisas". A interface `Database` diz: "qualquer classe que se diga um banco de dados precisa saber `conectar()` e `desconectar()`. Como ela faz isso internamente, é problema dela." Isso é o princípio da **abstração** em Orientação a Objetos.

#### 3.3.2. Classe `DatabaseFactory.java`

```java
package javafxmvc.model.database;

public class DatabaseFactory {
    public static Database getDatabase(String nome){
        if(nome.equals("postgresql")){
            return new DatabasePostgreSQL();
        }
        return null;
    }
}
```

O **Factory Method** recebe uma string com o nome do banco e retorna a implementação correspondente. Atualmente só há uma implementação (`postgresql`), mas o design permite fácil extensão para MySQL, H2, SQLite, etc.

**Uso típico nos Controllers:**
```java
Database database = DatabaseFactory.getDatabase("postgresql");
Connection connection = database.conectar();
```

#### 3.3.3. Classe `DatabasePostgreSQL.java`

```java
package javafxmvc.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabasePostgreSQL implements Database {
    private Connection connection;

    @Override
    public Connection conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1/atelie", "postgres", "postgres"
            );
            return this.connection;
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DatabasePostgreSQL.class.getName())
                  .log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void desconectar(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabasePostgreSQL.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }
}
```

**Configuração de conexão:**

| Parâmetro | Valor |
|:---|:---|
| Driver JDBC | `org.postgresql.Driver` |
| URL | `jdbc:postgresql://127.0.0.1/atelie` |
| Usuário | `postgres` |
| Senha | `postgres` |
| Host | `127.0.0.1` (localhost) |
| Database | `atelie` |

**Observações técnicas:**
- `Class.forName("org.postgresql.Driver")` — Carregamento explícito do driver (necessário em versões antigas do JDBC; em JDBC 4.0+ é automático via SPI).
- Cada chamada a `conectar()` cria uma **nova conexão**. Não há pool de conexões (padrão aceitável para aplicações desktop monousuário).
- O tratamento de erros utiliza `java.util.logging` (logger nativo do Java), diferente dos DAOs que usam `printStackTrace()`.

---

### 3.4. Entidades de Domínio (POJOs) — Pacote `model.domain`

**Localização:** `src/javafxmvc/model/domain/`  
**Responsável:** 🟦 Nadson

O pacote `domain` contém as 5 classes POJO (Plain Old Java Objects) que representam as tabelas do banco de dados como objetos Java. Todas seguem o mesmo padrão:
- Implementam `java.io.Serializable`
- Possuem atributos privados com getters/setters públicos
- Possuem construtor vazio (obrigatório para o padrão JavaBean)
- Sobrescrevem `toString()` para exibição em componentes JavaFX (ComboBox, ListView, etc.)

> 📋 **O que são POJOs, de forma simples?**  
> Imagine que no ateliê físico existe um **ficheiro de metal** com fichas de cadastro. Cada ficha tem campos preenchidos (nome, CPF, telefone...). Um POJO é exatamente isso: uma **ficha de cadastro digital**. A classe `Cliente.java` é o modelo da ficha de cliente, a classe `OrdemServico.java` é o modelo da ficha de OS, e assim por diante.
>
> Essas fichas não "fazem" nada sozinhas — elas apenas **guardam informações**. Quem "faz coisas" com elas são os DAOs (salvam no banco) e os Controllers (exibem na tela).
>
> **Por que `toString()` importa?** Quando você coloca um objeto `Cliente` dentro de uma `ComboBox` do JavaFX, o JavaFX precisa saber "que texto eu mostro na lista suspensa?". Ele chama automaticamente o `toString()` do objeto. Como `Cliente.toString()` retorna o nome, a ComboBox mostra o nome do cliente. Sem o `toString()`, ela mostraria algo como `Cliente@1a2b3c4d` (endereço de memória), que não serve pra nada.

#### Diagrama de Relacionamento entre Entidades

```
┌──────────────┐       ┌────────────────────┐       ┌────────────────────┐
│   Cliente    │ 1───* │   OrdemServico     │ 1───* │  Encaminhamento    │
│──────────────│       │────────────────────│       │────────────────────│
│ id           │       │ id                 │       │ id                 │
│ nome         │       │ cliente ──────────►│       │ ordemServico ─────►│
│ cpf          │       │ dataAbertura       │       │ fabricaParceira ──►│
│ telefone     │       │ dataPrevista       │       │ tipoServico ──────►│
│ endereco     │       │ dataRetorno        │       │ dataEncaminhamento │
│ diasAtraso   │       │ status             │       │ dataRetornoPrevista│
│ possuiPend.  │       │ valorTotal         │       │ quantidade         │
└──────────────┘       │ encaminhamentos[]  │       │ valorServico       │
                       └────────────────────┘       └────────────────────┘
                                                            │        │
                                                            ▼        ▼
                                                   ┌──────────┐ ┌──────────────┐
                                                   │TipoServico│ │FabricaParceira│
                                                   │──────────│ │──────────────│
                                                   │id        │ │id            │
                                                   │descricao │ │nome          │
                                                   │valor     │ │cnpj          │
                                                   │prazoEst. │ │telefone      │
                                                   └──────────┘ │especialidade │
                                                                └──────────────┘
```

---

#### 3.4.1. `Cliente.java`

Representa um cliente do ateliê. Possui campos de controle de inadimplência (`diasAtraso`, `possuiPendencia`) que são utilizados pela regra de negócio de **Bloqueio de Inadimplente**.

| Atributo | Tipo | Coluna no BD | Descrição |
|:---|:---|:---|:---|
| `id` | `int` | `id SERIAL PK` | Identificador único (auto-gerado) |
| `nome` | `String` | `nome VARCHAR(100)` | Nome completo do cliente |
| `cpf` | `String` | `cpf VARCHAR(14) UNIQUE` | CPF com máscara |
| `telefone` | `String` | `telefone VARCHAR(20)` | Telefone de contato |
| `endereco` | `String` | `endereco VARCHAR(200)` | Endereço completo |
| `diasAtraso` | `int` | `dias_atraso INT DEFAULT 0` | Dias em atraso no pagamento |
| `possuiPendencia` | `boolean` | `possui_pendencia BOOLEAN DEFAULT FALSE` | Flag de pendência ativa |

**Fan-In no grafo:** `getNome()` = 5, `getEndereco()` = 4 — indicando que esses métodos são acessados frequentemente pelos Controllers de Atendimento e Cadastro.

**Construtores:**
```java
public Cliente() {}                                   // Obrigatório para JavaBeans
public Cliente(int id, String nome, String cpf) { ... } // Atalho para criação rápida
```

**`toString()`:** Retorna apenas o `nome`, o que permite que objetos `Cliente` sejam exibidos diretamente em `ComboBox<Cliente>` do JavaFX.

---

#### 3.4.2. `OrdemServico.java`

Entidade central do sistema — representa uma ordem de serviço aberta para um cliente. Possui uma relação de **composição** com `List<Encaminhamento>`.

| Atributo | Tipo | Coluna no BD | Descrição |
|:---|:---|:---|:---|
| `id` | `int` | `id SERIAL PK` | Identificador da OS |
| `cliente` | `Cliente` | `cliente_id INT FK` | Cliente vinculado à OS |
| `dataAbertura` | `LocalDate` | `data_abertura DATE` | Data de abertura |
| `dataPrevista` | `LocalDate` | `data_prevista DATE` | Prazo estimado de conclusão |
| `dataRetorno` | `LocalDate` | `data_retorno DATE` | Data real de finalização (nullable) |
| `status` | `String` | `status VARCHAR(50)` | Estado atual da OS |
| `valorTotal` | `double` | `valor_total NUMERIC(10,2)` | Valor total da OS |
| `encaminhamentos` | `List<Encaminhamento>` | *(1:N na tabela encaminhamento)* | Itens enviados a fábricas |

**Status possíveis:** `RECEBIDA` → `ENVIADA_FABRICA` → `RETORNO_FABRICA` → `FINALIZADA`

**Construtor:**
```java
public OrdemServico() {
    this.encaminhamentos = new ArrayList<>();  // Inicializa lista vazia
}
```

**`toString()`:** Retorna `"OS #1 - Ana Beatriz Souza"` — facilitando identificação em listas.

---

#### 3.4.3. `FabricaParceira.java`

Representa uma fábrica terceirizada que presta serviços de costura/customização para o ateliê.

| Atributo | Tipo | Coluna no BD | Descrição |
|:---|:---|:---|:---|
| `id` | `int` | `id SERIAL PK` | Identificador da fábrica |
| `nome` | `String` | `nome VARCHAR(100)` | Razão social |
| `cnpj` | `String` | `cnpj VARCHAR(18) UNIQUE` | CNPJ com máscara |
| `telefone` | `String` | `telefone VARCHAR(20)` | Contato da fábrica |
| `especialidade` | `String` | `especialidade VARCHAR(100)` | Área de atuação (ex: "Bordados") |

**Fan-In:** `getNome()` = 5 — utilizado nos Controllers de Terceirização e nos Gráficos de Volume.

**`toString()`:** Retorna o `nome` da fábrica.

---

#### 3.4.4. `TipoServico.java`

Representa um item do catálogo de serviços oferecidos pelo ateliê, com valor e prazo estimado.

| Atributo | Tipo | Coluna no BD | Descrição |
|:---|:---|:---|:---|
| `id` | `int` | `id SERIAL PK` | Identificador do serviço |
| `descricao` | `String` | `descricao VARCHAR(100)` | Nome do serviço (ex: "Bordado Simples") |
| `valor` | `double` | `valor NUMERIC(10,2)` | Preço unitário em R$ |
| `prazoEstimadoDias` | `int` | `prazo_estimado_dias INT` | Prazo para execução em dias |

**Fan-In elevado:** `getValor()` = 6 e `getPrazoEstimadoDias()` = 6 — esses são os métodos de domínio mais requisitados do sistema, usados nos Controllers de Atendimento, Terceirização e no cálculo de datas previstas.

**`toString()`:** Retorna a `descricao`, o que permite uso direto em `ComboBox<TipoServico>`.

---

#### 3.4.5. `Encaminhamento.java`

Representa o envio de uma OS (ou parte dela) para uma fábrica parceira executar um serviço específico. É a **entidade de junção** entre `OrdemServico`, `FabricaParceira` e `TipoServico`.

| Atributo | Tipo | Coluna no BD | Descrição |
|:---|:---|:---|:---|
| `id` | `int` | `id SERIAL PK` | Identificador do encaminhamento |
| `ordemServico` | `OrdemServico` | `ordem_servico_id INT FK` | OS de origem |
| `fabricaParceira` | `FabricaParceira` | `fabrica_parceira_id INT FK` | Fábrica destino |
| `tipoServico` | `TipoServico` | `tipo_servico_id INT FK` | Serviço a ser executado |
| `dataEncaminhamento` | `LocalDate` | `data_encaminhamento DATE` | Data de envio à fábrica |
| `dataRetornoPrevista` | `LocalDate` | `data_retorno_prevista DATE` | Previsão de retorno (nullable) |
| `quantidade` | `int` | `quantidade INT` | Quantidade de peças |
| `valorServico` | `double` | `valor_servico NUMERIC(10,2)` | Valor do serviço neste encaminhamento |

**Relacionamentos (3 FKs):**
- `OrdemServico` → com `ON DELETE CASCADE` (apagar a OS apaga seus encaminhamentos)
- `FabricaParceira` → referência direta
- `TipoServico` → referência direta

**`toString()`:** Retorna `"Encaminhamento #1"`.

---

## 4. Fluxo Geral da Aplicação

Agora que você conhece cada peça individual da fundação, vamos ver **como tudo se conecta quando o sistema roda**. O diagrama abaixo ilustra o trace completo desde a execução do `Main.java` até a exibição das telas filhas:

> 🚦 **Leia o diagrama de cima para baixo**, como se fosse uma linha do tempo. Cada caixa é um "momento" do sistema, e as setas mostram "o que acontece depois".

```
 EXECUÇÃO
    │
    ▼
┌─────────────────────────────────────────────────┐
│  1. Main.main(args) → launch(args)              │
│     Inicia o ciclo de vida do JavaFX            │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│  2. Main.start(Stage stage)                     │
│     • FXMLLoader.load("view/FXMLLogin.fxml")    │
│     • Aplica atelie.css                         │
│     • stage.setResizable(false)                 │
│     • stage.show()                              │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│  3. TELA DE LOGIN (FXMLLoginController)         │
│     • Usuário digita "admin" / "admin"          │
│     • handleButtonEntrar() → valida campos      │
│     • Se OK → abrirTelaPrincipal()              │
│       ├─ FXMLLoader.load("FXMLVBoxMain.fxml")   │
│       ├─ stage.setResizable(true)               │
│       ├─ stage.setWidth(900), setHeight(650)    │
│       └─ stage.show()                           │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│  4. TELA PRINCIPAL (FXMLVBoxMainController)     │
│     • MenuBar com 5 menus (12 itens + Sair)     │
│     • AnchorPane central vazio                  │
│     • Aguarda interação do usuário...           │
└──────────────────────┬──────────────────────────┘
                       │
            Usuário clica em um MenuItem
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│  5. CARREGAMENTO DINÂMICO — loadView()          │
│     • FXMLLoader carrega o FXML da tela filha   │
│     • O Controller da tela filha é instanciado  │
│     • Seu initialize() é executado              │
│       ├─ DatabaseFactory.getDatabase("postgre") │
│       ├─ database.conectar() → Connection       │
│       ├─ dao.setConnection(connection)          │
│       ├─ dao.listar() → List<Entidade>          │
│       └─ carregarTableView() → exibe na tabela  │
│     • O AnchorPane central é substituído        │
└─────────────────────────────────────────────────┘
```

### Fluxo de Dados Típico (CRUD)

Para qualquer operação CRUD (ex: inserir um Cliente), o fluxo segue este padrão. Entenda o diagrama abaixo como uma **conversa entre 4 personagens** — o Controller pede ajuda ao DAO, que usa a Database para falar com o PostgreSQL:

```
[Controller]                [DAO]               [Database]          [PostgreSQL]
     │                        │                      │                    │
     │ 1. getDatabase("pg")   │                      │                    │
     │───────────────────────►│                      │                    │
     │                        │  2. conectar()       │                    │
     │                        │─────────────────────►│                    │
     │                        │                      │ 3. JDBC connect    │
     │                        │                      │───────────────────►│
     │                        │  ◄── Connection ─────│                    │
     │ 4. setConnection(conn) │                      │                    │
     │───────────────────────►│                      │                    │
     │ 5. inserir(cliente)    │                      │                    │
     │───────────────────────►│                      │                    │
     │                        │ 6. PreparedStatement │                    │
     │                        │───────────────────────────────────────────►
     │                        │ ◄───── resultado ────│                    │
     │ ◄── true/false ────────│                      │                    │
     │ 7. desconectar(conn)   │                      │                    │
     │───────────────────────►│  close()             │                    │
```

---

## 5. Regras Técnicas Importantes

Estas são as regras de codificação definidas para o projeto, extraídas do `README.md` oficial e verificadas no código-fonte:

### 5.1. Regra dos DAOs — Conexão Injetada

> **"Nunca abra a conexão dentro do DAO."**

> 🎯 **Por que essa regra é tão importante?** Imagine que você precisa salvar uma OS e seus 3 encaminhamentos. Se cada DAO abrisse sua própria conexão, seriam 4 conexões separadas e não haveria como fazer "tudo ou nada" (transação). Ao injetar a **mesma conexão** em todos os DAOs, o Controller garante que todas as operações compartilham o mesmo "canal" com o banco — e pode desfazer tudo junto se algo falhar.

Todos os 5 DAOs seguem o padrão de receber a `Connection` externamente:

```java
public class ClienteDAO {
    private Connection connection;

    public void setConnection(Connection connection) {   // Injeção manual
        this.connection = connection;
    }
    // ... métodos CRUD usam this.connection
}
```

Isso permite que o **Controller** controle o ciclo de vida da conexão e possa compartilhá-la entre múltiplos DAOs na mesma transação.

### 5.2. Transações Complexas — Commit/Rollback Manual

Para operações que envolvem mais de uma tabela (como o processo de Terceirização, que insere um `Encaminhamento` **e** atualiza o `status` da `OrdemServico`), o Controller deve:

```java
connection.setAutoCommit(false);    // 1. Desliga autocommit
try {
    ordemServicoDAO.alterar(os);    // 2. Altera a OS
    encaminhamentoDAO.inserir(enc); // 3. Insere o encaminhamento
    connection.commit();            // 4. Confirma tudo
} catch (Exception e) {
    connection.rollback();          // 5. Desfaz tudo em caso de erro
}
```

> 🎰 **Analogia do caixa de banco:** Imagine que você está transferindo dinheiro de uma conta para outra. São duas operações: (1) tirar de uma conta e (2) colocar na outra. Se a operação 1 funcionar mas a 2 falhar, você fica sem dinheiro nas duas contas! A transação (`setAutoCommit(false)` + `commit/rollback`) garante que **ou tudo funciona junto, ou nada acontece**. É o famoso "tudo ou nada".

### 5.3. Formulários como Janelas Modais (Dialog Pattern)

Todas as telas de inserção/alteração são abertas como **Dialogs modais** usando `Stage.showAndWait()`:

> 🛁 **O que é uma janela modal?** É uma janela "manhosa" — enquanto ela está aberta, você não consegue clicar em nada por trás dela. É igual ao pop-up de confirmação "Tem certeza que quer excluir?": você é obrigado a responder antes de continuar. No nosso sistema, os formulários de cadastro funcionam assim para evitar que o usuário mexa na tabela enquanto está editando um registro.

```java
Stage dialogStage = new Stage();
dialogStage.setTitle("Cadastro de Cliente");
dialogStage.initModality(Modality.WINDOW_MODAL);
dialogStage.initOwner(anchorPane.getScene().getWindow());
// ... carrega FXML do Dialog
dialogStage.showAndWait();  // Bloqueia até o dialog fechar
```

Isso garante que o usuário **não possa** interagir com a tela principal enquanto o formulário está aberto.

### 5.4. Padrão de Nomenclatura

O projeto segue um padrão rígido de nomenclatura para manter a consistência:

| Componente | Convenção | Exemplo |
|:---|:---|:---|
| **View (FXML)** | `FXML` + Módulo + Função + `.fxml` | `FXMLCadastrosCliente.fxml` |
| **View Dialog** | Mesmo + `Dialog` | `FXMLCadastrosClienteDialog.fxml` |
| **Controller** | Mesmo nome do FXML + `Controller.java` | `FXMLCadastrosClienteController.java` |
| **DAO** | Entidade + `DAO.java` | `ClienteDAO.java` |
| **Domain** | Nome da entidade + `.java` | `Cliente.java` |
| **Handler** | `handleButton/MenuItem` + Ação | `handleButtonInserir()` |

### 5.5. Uso de PreparedStatement

Todos os DAOs utilizam `PreparedStatement` com parâmetros posicionais (`?`) em vez de concatenação de strings, prevenindo **SQL Injection**:

> 🛡️ **O que é SQL Injection, e por que devemos nos proteger?** SQL Injection é quando um usuário malicioso digita código SQL dentro de um campo de texto (por exemplo, no campo de nome, digitar `'; DROP TABLE cliente; --`). Se o sistema montar a query concatenando strings, esse código seria executado e poderia **apagar todo o banco de dados**. O `PreparedStatement` impede isso porque trata os parâmetros como **texto puro**, nunca como código SQL.

```java
// ✅ CORRETO — usado no projeto (seguro contra SQL Injection)
String sql = "INSERT INTO cliente (nome, cpf) VALUES (?, ?)";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, cliente.getNome());   // O Java "escapa" automaticamente
stmt.setString(2, cliente.getCpf());

// ❌ ERRADO — nunca usado (vulnerável a SQL Injection)
String sql = "INSERT INTO cliente VALUES ('" + nome + "', '" + cpf + "')";
```

### 5.6. Try-With-Resources

Os DAOs utilizam `try-with-resources` para garantir que `PreparedStatement` e `ResultSet` sejam fechados automaticamente:

> 🚣 **O que é try-with-resources?** Em Java, toda vez que você abre um recurso (conexão, arquivo, cursor de banco), você precisa **fechar depois** para não causar vazamento de memória. O `try-with-resources` faz isso automaticamente: ele garante que tudo que foi aberto dentro dos parênteses será fechado quando o bloco terminar, mesmo que ocorra um erro. É como um "seguro": você nunca esquece de fechar.

```java
try (PreparedStatement stmt = connection.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
    // ... processamento
}   // ← stmt e rs são fechados automaticamente aqui, mesmo se der erro
```

---

## 6. Responsabilidades na Fundação

A tabela abaixo resume **quem fez o quê** na Fase 1 — Fundação Compartilhada:

| Membro | Cor | Responsabilidade | Arquivos | Status |
|:---|:---:|:---|:---|:---:|
| **Nadson** | 🟦 | Mapeamento Objeto-Relacional: criar os 5 POJOs do pacote `domain` com atributos privados, getters/setters e construtores | `Cliente.java`, `FabricaParceira.java`, `TipoServico.java`, `OrdemServico.java`, `Encaminhamento.java` | ✅ |
| **Eduardo** | 🟩 | Container e Navegação: montar o VBox principal com MenuBar, AnchorPane e a lógica de `loadView()` para carregamento dinâmico | `FXMLVBoxMain.fxml`, `FXMLVBoxMainController.java` | ✅ |
| **Marco Antônio** | 🟨 | Infraestrutura: script SQL do banco, Factory Method de conexão, classe `Main.java` com `start()` | `Database.java`, `DatabaseFactory.java`, `DatabasePostgreSQL.java`, `Main.java`, `script_atelie.sql` | ✅ |

### Interdependências da Fundação

```
Marco Antônio (Database + Main)
        │
        │ Fornece: Connection, start()
        │
        ├──────────────►  Eduardo (VBoxMain + Navegação)
        │                    │
        │                    │ Fornece: Container para telas filhas
        │                    │
        └──────────────►  Nadson (POJOs)
                             │
                             │ Fornece: Entidades para os DAOs e Controllers
                             ▼
                    ┌─────────────────────┐
                    │   FASE 2 (Pilares)  │
                    │   CRUDs, Processos, │
                    │   Gráficos, Reports │
                    └─────────────────────┘
```

---

## 7. Conclusão

A **Fundação Compartilhada** do projeto Ateliê de Customização estabeleceu os quatro pilares essenciais para o desenvolvimento paralelo e independente dos módulos funcionais:

1. **`Main.java`** (a porta de entrada 🚪) garantiu que a aplicação inicializa corretamente com o ciclo de vida do JavaFX, exibindo primeiro a tela de login e então a tela principal.

2. **`FXMLVBoxMain` + Controller** (a TV com controle remoto 📺) criou o mecanismo de navegação dinâmica via `loadView()`, que permite que qualquer membro da equipe crie uma nova tela FXML e ela seja carregada no container central com uma única linha de código.

3. **O pacote `database`** (a tomada e o adaptador 🔌) com o padrão Factory Method abstraiu completamente a conexão com PostgreSQL, permitindo que os DAOs trabalhem apenas com `java.sql.Connection` sem se preocupar com drivers ou strings de conexão.

4. **As 5 entidades do pacote `domain`** (as fichas de cadastro 📋) mapearam fielmente as tabelas do banco de dados para objetos Java, servindo como a linguagem comum entre Controllers, DAOs e Views.

> 🏁 **Recapitulando com a analogia do restaurante:**  
> Agora o restaurante está pronto para abrir. A porta de entrada foi instalada (Main.java), os menus e mesas estão montados (VBoxMain), a rede elétrica que conecta à cozinha está funcionando (Database), e todas as fichas de ingredientes estão organizadas (POJOs). Agora cada garçom (Controller) pode começar a atender seus clientes, usando os estoquistas (DAOs) para buscar e guardar dados, sem precisar saber nada sobre como a cozinha funciona por dentro.

Com essa fundação concluída, os três desenvolvedores puderam trabalhar simultaneamente em seus pilares — **O Cliente e o Atraso** (Nadson), **A Fábrica e o Volume** (Eduardo), e **O Serviço e o Valor** (Marco Antônio) — com sobreposição mínima de código e máxima independência operacional.

---

> **Documento gerado com auxílio do Code Graph Context (análise estática de código)**  
> Estatísticas do grafo: 34 arquivos | 31 classes | 225 funções | 1 repositório indexado
