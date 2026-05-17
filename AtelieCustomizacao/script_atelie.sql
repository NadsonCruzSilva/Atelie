-- =================================================================
-- SCRIPT COMPLETO - ATELIÊ DE CUSTOMIZAÇÃO
-- Execute este script no PostgreSQL para criar e popular o banco.
-- =================================================================

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
    status VARCHAR(50) NOT NULL, -- 'RECEBIDA', 'ENVIADA_FABRICA', 'RETORNO_FABRICA', 'FINALIZADA'
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


-- =================================================================
-- DADOS FICTÍCIOS PARA DEMONSTRAÇÃO
-- =================================================================


-- ----------------------
-- CLIENTES (15 clientes)
-- ----------------------
INSERT INTO cliente (nome, cpf, telefone, endereco, dias_atraso, possui_pendencia) VALUES
('Ana Beatriz Souza',    '100.200.300-01', '(71) 98001-1001', 'Rua das Flores, 10, Salvador - BA',   0,  FALSE),
('Carlos Eduardo Lima',  '100.200.300-02', '(71) 98001-1002', 'Av. Paralela, 250, Salvador - BA',    0,  FALSE),
('Fernanda Oliveira',    '100.200.300-03', '(71) 98001-1003', 'Rua do Tijolo, 33, Feira - BA',       0,  FALSE),
('Ricardo Mendes',       '100.200.300-04', '(71) 98001-1004', 'Travessa das Pedras, 5, Lauro - BA',  0,  FALSE),
('Juliana Costa',        '100.200.300-05', '(71) 98001-1005', 'Rua Nova, 88, Camaçari - BA',         0,  FALSE),
('Marcos Vinicius Neto', '100.200.300-06', '(71) 98001-1006', 'Rua do Comércio, 120, Itabuna - BA', 0,  FALSE),
('Patricia Alves',       '100.200.300-07', '(71) 98001-1007', 'Rua Boa Vista, 14, Vitória da Conquista - BA', 0, FALSE),
('Thiago Santos',        '100.200.300-08', '(71) 98001-1008', 'Alameda das Acácias, 55, Ilhéus - BA', 0, FALSE),
('Camila Ferreira',      '100.200.300-09', '(71) 98001-1009', 'Rua das Palmeiras, 7, Porto Seguro - BA', 0, FALSE),
('Bruno Rocha',          '100.200.300-10', '(71) 98001-1010', 'Av. Central, 300, Santo Antônio de Jesus - BA', 0, FALSE),
-- Clientes com pendência (para demonstrar a regra de negócio)
('Isabela Ramos',        '100.200.300-11', '(71) 98001-1011', 'Rua do Sol, 19, Salvador - BA',      35, TRUE),
('Diego Carvalho',       '100.200.300-12', '(71) 98001-1012', 'Rua da Lua, 27, Salvador - BA',      50, TRUE),
('Larissa Nunes',        '100.200.300-13', '(71) 98001-1013', 'Av. Oceânica, 180, Salvador - BA',   0,  FALSE),
('Gabriel Martins',      '100.200.300-14', '(71) 98001-1014', 'Rua do Campo, 90, Alagoinhas - BA',  0,  FALSE),
('Letícia Pires',        '100.200.300-15', '(71) 98001-1015', 'Rua das Artes, 66, Salvador - BA',   0,  FALSE);


-- -----------------------------
-- FÁBRICAS PARCEIRAS (5 fábricas)
-- -----------------------------
INSERT INTO fabrica_parceira (nome, cnpj, telefone, especialidade) VALUES
('Costura Express Ltda',       '11.111.111/0001-01', '(71) 3200-1001', 'Bordados e Acabamentos'),
('Confecção Alpha S.A.',       '22.222.222/0001-02', '(71) 3200-1002', 'Tingimento e Lavagem'),
('Arte & Costura ME',          '33.333.333/0001-03', '(71) 3200-1003', 'Ajustes e Reformas'),
('Studio Têxtil Bahia Ltda',   '44.444.444/0001-04', '(71) 3200-1004', 'Estamparia e Silk'),
('Ateliê Nordeste Confecções', '55.555.555/0001-05', '(71) 3200-1005', 'Costura Fina e Alta Costura');


-- ----------------------------
-- TIPOS DE SERVIÇO (8 serviços)
-- ----------------------------
INSERT INTO tipo_servico (descricao, valor, prazo_estimado_dias) VALUES
('Bordado Simples',          25.00,  2),
('Bordado Complexo',         60.00,  5),
('Tingimento de Jaqueta',    55.00,  5),
('Tingimento de Calça',      40.00,  4),
('Ajuste de Barra',          20.00,  1),
('Ajuste de Cós',            30.00,  2),
('Estamparia Digital',       45.00,  3),
('Customização Completa',   120.00,  7);


-- --------------------------------------------------
-- ORDENS DE SERVIÇO (20 OS em diferentes status)
-- --------------------------------------------------

-- OS Finalizadas (para alimentar o gráfico de receita)
INSERT INTO ordem_servico (cliente_id, data_abertura, data_prevista, data_retorno, status, valor_total) VALUES
-- Janeiro
(1,  '2025-01-05', '2025-01-07',  '2025-01-08',  'FINALIZADA',     25.00),
(2,  '2025-01-10', '2025-01-15',  '2025-01-16',  'FINALIZADA',     55.00),
(3,  '2025-01-20', '2025-01-22',  '2025-01-23',  'FINALIZADA',     20.00),
-- Fevereiro
(4,  '2025-02-03', '2025-02-08',  '2025-02-09',  'FINALIZADA',     60.00),
(5,  '2025-02-14', '2025-02-16',  '2025-02-18',  'FINALIZADA',     30.00),
-- Março
(6,  '2025-03-01', '2025-03-06',  '2025-03-07',  'FINALIZADA',    120.00),
(7,  '2025-03-15', '2025-03-18',  '2025-03-20',  'FINALIZADA',     45.00),
(8,  '2025-03-22', '2025-03-24',  '2025-03-25',  'FINALIZADA',     40.00),
-- Abril
(9,  '2025-04-02', '2025-04-07',  '2025-04-08',  'FINALIZADA',     55.00),
(10, '2025-04-18', '2025-04-21',  '2025-04-22',  'FINALIZADA',     25.00),
(1,  '2025-04-25', '2025-04-27',  '2025-04-28',  'FINALIZADA',     60.00),
-- Maio
(2,  '2025-05-05', '2025-05-10',  '2025-05-11',  'FINALIZADA',    120.00),
(3,  '2025-05-12', '2025-05-14',  '2025-05-15',  'FINALIZADA',     30.00),
-- OS em andamento (para PieChart de status)
(13, '2025-05-20', '2025-05-22',  NULL,           'RECEBIDA',       45.00),
(14, '2025-05-21', '2025-05-26',  NULL,           'RECEBIDA',       60.00),
(15, '2025-05-10', '2025-05-17',  NULL,           'ENVIADA_FABRICA', 55.00),
(10, '2025-05-08', '2025-05-13',  NULL,           'ENVIADA_FABRICA', 40.00),
(9,  '2025-05-15', '2025-05-22',  NULL,           'ENVIADA_FABRICA', 25.00),
(8,  '2025-05-18', '2025-05-25',  NULL,           'RETORNO_FABRICA', 120.00),
(7,  '2025-05-22', '2025-05-24',  NULL,           'RECEBIDA',       30.00);


-- -------------------------------------------------------
-- ENCAMINHAMENTOS (para alimentar o gráfico de volume)
-- -------------------------------------------------------
INSERT INTO encaminhamento (ordem_servico_id, fabrica_parceira_id, tipo_servico_id, data_encaminhamento, data_retorno_prevista, quantidade, valor_servico) VALUES
-- OS 1 → Costura Express (Bordado Simples)
(1,  1, 1, '2025-01-05', '2025-01-07',  1, 25.00),
-- OS 2 → Confecção Alpha (Tingimento)
(2,  2, 3, '2025-01-10', '2025-01-15',  1, 55.00),
-- OS 3 → Arte & Costura (Ajuste)
(3,  3, 5, '2025-01-20', '2025-01-21',  1, 20.00),
-- OS 4 → Costura Express (Bordado Complexo)
(4,  1, 2, '2025-02-03', '2025-02-08',  1, 60.00),
-- OS 5 → Arte & Costura (Ajuste Cós)
(5,  3, 6, '2025-02-14', '2025-02-16',  1, 30.00),
-- OS 6 → Studio Têxtil (Customização Completa)
(6,  4, 8, '2025-03-01', '2025-03-08',  1, 120.00),
-- OS 7 → Studio Têxtil (Estamparia)
(7,  4, 7, '2025-03-15', '2025-03-18',  1, 45.00),
-- OS 8 → Confecção Alpha (Tingimento Calça)
(8,  2, 4, '2025-03-22', '2025-03-26',  1, 40.00),
-- OS 9 → Confecção Alpha (Tingimento Jaqueta)
(9,  2, 3, '2025-04-02', '2025-04-07',  1, 55.00),
-- OS 10 → Costura Express (Bordado Simples)
(10, 1, 1, '2025-04-18', '2025-04-20',  1, 25.00),
-- OS 11 → Arte & Costura (Bordado Complexo)
(11, 3, 2, '2025-04-25', '2025-04-30',  1, 60.00),
-- OS 12 → Ateliê Nordeste (Customização Completa)
(12, 5, 8, '2025-05-05', '2025-05-12',  1, 120.00),
-- OS 13 → Arte & Costura (Ajuste Cós)
(13, 3, 6, '2025-05-12', '2025-05-14',  1, 30.00),
-- OS em andamento (com encaminhamentos ativos - sem data_retorno)
(16, 1, 3, '2025-05-10', '2025-05-15',  1, 55.00),
(17, 4, 4, '2025-05-08', '2025-05-12',  1, 40.00),
(18, 2, 1, '2025-05-15', '2025-05-17',  1, 25.00);
