CREATE DATABASE atelie;

-- Obs: Conecte-se ao banco 'atelie' antes de executar os comandos abaixo.

CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    endereco VARCHAR(200),
    dias_atraso INT DEFAULT 0,
    possui_pendencia BOOLEAN DEFAULT FALSE
);

CREATE TABLE fabrica_parceira (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(18) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    especialidade VARCHAR(100)
);

CREATE TABLE tipo_servico (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    prazo_estimado_dias INT NOT NULL
);

CREATE TABLE ordem_servico (
    id SERIAL PRIMARY KEY,
    cliente_id INT NOT NULL,
    data_abertura DATE NOT NULL,
    data_prevista DATE NOT NULL,
    data_retorno DATE,
    status VARCHAR(50) NOT NULL, -- Ex: 'RECEBIDA', 'ENVIADA_FABRICA', 'RETORNO_FABRICA', 'FINALIZADA'
    valor_total NUMERIC(10,2) DEFAULT 0.00,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE encaminhamento (
    id SERIAL PRIMARY KEY,
    ordem_servico_id INT NOT NULL,
    fabrica_parceira_id INT NOT NULL,
    tipo_servico_id INT NOT NULL,
    data_encaminhamento DATE NOT NULL,
    data_retorno_prevista DATE,
    quantidade INT NOT NULL,
    valor_servico NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE,
    FOREIGN KEY (fabrica_parceira_id) REFERENCES fabrica_parceira(id),
    FOREIGN KEY (tipo_servico_id) REFERENCES tipo_servico(id)
);

-- =======================================================
-- Inserções Iniciais para Testes (Opcional)
-- =======================================================

INSERT INTO cliente (nome, cpf, telefone, endereco, dias_atraso, possui_pendencia) VALUES
('João Silva', '111.111.111-11', '(11) 98888-1111', 'Rua A, 123', 0, FALSE),
('Maria Souza', '222.222.222-22', '(11) 98888-2222', 'Rua B, 456', 45, TRUE);

INSERT INTO fabrica_parceira (nome, cnpj, telefone, especialidade) VALUES
('Costura Express', '12.345.678/0001-90', '(11) 3333-1111', 'Bordados e Acabamentos'),
('Confecção Alpha', '98.765.432/0001-10', '(11) 3333-2222', 'Tingimento e Lavagem');

INSERT INTO tipo_servico (descricao, valor, prazo_estimado_dias) VALUES
('Bordado Simples', 15.00, 2),
('Tingimento de Jaqueta', 50.00, 5),
('Ajuste de Barra', 20.00, 1);
