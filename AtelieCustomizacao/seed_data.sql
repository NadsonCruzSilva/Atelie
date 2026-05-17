-- Apenas os dados fictícios (sem CREATE TABLE)
INSERT INTO cliente (nome, cpf, telefone, endereco, dias_atraso, possui_pendencia) VALUES
('Ana Beatriz Souza',    '100.200.300-01', '(71) 98001-1001', 'Rua das Flores, 10, Salvador - BA',   0,  FALSE),
('Carlos Eduardo Lima',  '100.200.300-02', '(71) 98001-1002', 'Av. Paralela, 250, Salvador - BA',    0,  FALSE),
('Fernanda Oliveira',    '100.200.300-03', '(71) 98001-1003', 'Rua do Tijolo, 33, Feira - BA',       0,  FALSE),
('Ricardo Mendes',       '100.200.300-04', '(71) 98001-1004', 'Travessa das Pedras, 5, Lauro - BA',  0,  FALSE),
('Juliana Costa',        '100.200.300-05', '(71) 98001-1005', 'Rua Nova, 88, Camacari - BA',         0,  FALSE),
('Marcos Vinicius Neto', '100.200.300-06', '(71) 98001-1006', 'Rua do Comercio, 120, Itabuna - BA',  0,  FALSE),
('Patricia Alves',       '100.200.300-07', '(71) 98001-1007', 'Rua Boa Vista, 14, Vitoria da Conquista - BA', 0, FALSE),
('Thiago Santos',        '100.200.300-08', '(71) 98001-1008', 'Alameda das Acacias, 55, Ilheus - BA', 0, FALSE),
('Camila Ferreira',      '100.200.300-09', '(71) 98001-1009', 'Rua das Palmeiras, 7, Porto Seguro - BA', 0, FALSE),
('Bruno Rocha',          '100.200.300-10', '(71) 98001-1010', 'Av. Central, 300, Santo Antonio - BA', 0, FALSE),
('Isabela Ramos',        '100.200.300-11', '(71) 98001-1011', 'Rua do Sol, 19, Salvador - BA',      35, TRUE),
('Diego Carvalho',       '100.200.300-12', '(71) 98001-1012', 'Rua da Lua, 27, Salvador - BA',      50, TRUE),
('Larissa Nunes',        '100.200.300-13', '(71) 98001-1013', 'Av. Oceanica, 180, Salvador - BA',    0, FALSE),
('Gabriel Martins',      '100.200.300-14', '(71) 98001-1014', 'Rua do Campo, 90, Alagoinhas - BA',   0, FALSE),
('Leticia Pires',        '100.200.300-15', '(71) 98001-1015', 'Rua das Artes, 66, Salvador - BA',    0, FALSE);

INSERT INTO fabrica_parceira (nome, cnpj, telefone, especialidade) VALUES
('Costura Express Ltda',       '11.111.111/0001-01', '(71) 3200-1001', 'Bordados e Acabamentos'),
('Confeccao Alpha S.A.',       '22.222.222/0001-02', '(71) 3200-1002', 'Tingimento e Lavagem'),
('Arte e Costura ME',          '33.333.333/0001-03', '(71) 3200-1003', 'Ajustes e Reformas'),
('Studio Textil Bahia Ltda',   '44.444.444/0001-04', '(71) 3200-1004', 'Estamparia e Silk'),
('Atelie Nordeste Confeccoes', '55.555.555/0001-05', '(71) 3200-1005', 'Costura Fina e Alta Costura');

INSERT INTO tipo_servico (descricao, valor, prazo_estimado_dias) VALUES
('Bordado Simples',      25.00, 2),
('Bordado Complexo',     60.00, 5),
('Tingimento Jaqueta',   55.00, 5),
('Tingimento Calca',     40.00, 4),
('Ajuste de Barra',      20.00, 1),
('Ajuste de Cos',        30.00, 2),
('Estamparia Digital',   45.00, 3),
('Customizacao Completa',120.00,7);

INSERT INTO ordem_servico (cliente_id, data_abertura, data_prevista, data_retorno, status, valor_total) VALUES
(1,  '2025-01-05', '2025-01-07',  '2025-01-08',  'FINALIZADA',      25.00),
(2,  '2025-01-10', '2025-01-15',  '2025-01-16',  'FINALIZADA',      55.00),
(3,  '2025-01-20', '2025-01-22',  '2025-01-23',  'FINALIZADA',      20.00),
(4,  '2025-02-03', '2025-02-08',  '2025-02-09',  'FINALIZADA',      60.00),
(5,  '2025-02-14', '2025-02-16',  '2025-02-18',  'FINALIZADA',      30.00),
(6,  '2025-03-01', '2025-03-06',  '2025-03-07',  'FINALIZADA',     120.00),
(7,  '2025-03-15', '2025-03-18',  '2025-03-20',  'FINALIZADA',      45.00),
(8,  '2025-03-22', '2025-03-24',  '2025-03-25',  'FINALIZADA',      40.00),
(9,  '2025-04-02', '2025-04-07',  '2025-04-08',  'FINALIZADA',      55.00),
(10, '2025-04-18', '2025-04-21',  '2025-04-22',  'FINALIZADA',      25.00),
(1,  '2025-04-25', '2025-04-27',  '2025-04-28',  'FINALIZADA',      60.00),
(2,  '2025-05-05', '2025-05-10',  '2025-05-11',  'FINALIZADA',     120.00),
(3,  '2025-05-12', '2025-05-14',  '2025-05-15',  'FINALIZADA',      30.00),
(13, '2025-05-20', '2025-05-22',  NULL,           'RECEBIDA',        45.00),
(14, '2025-05-21', '2025-05-26',  NULL,           'RECEBIDA',        60.00),
(15, '2025-05-10', '2025-05-17',  NULL,           'ENVIADA_FABRICA', 55.00),
(10, '2025-05-08', '2025-05-13',  NULL,           'ENVIADA_FABRICA', 40.00),
(9,  '2025-05-15', '2025-05-22',  NULL,           'ENVIADA_FABRICA', 25.00),
(8,  '2025-05-18', '2025-05-25',  NULL,           'RETORNO_FABRICA',120.00),
(7,  '2025-05-22', '2025-05-24',  NULL,           'RECEBIDA',        30.00);

INSERT INTO encaminhamento (ordem_servico_id, fabrica_parceira_id, tipo_servico_id, data_encaminhamento, data_retorno_prevista, quantidade, valor_servico) VALUES
(1,  1, 1, '2025-01-05', '2025-01-07',  1,  25.00),
(2,  2, 3, '2025-01-10', '2025-01-15',  1,  55.00),
(3,  3, 5, '2025-01-20', '2025-01-21',  1,  20.00),
(4,  1, 2, '2025-02-03', '2025-02-08',  1,  60.00),
(5,  3, 6, '2025-02-14', '2025-02-16',  1,  30.00),
(6,  4, 8, '2025-03-01', '2025-03-08',  1, 120.00),
(7,  4, 7, '2025-03-15', '2025-03-18',  1,  45.00),
(8,  2, 4, '2025-03-22', '2025-03-26',  1,  40.00),
(9,  2, 3, '2025-04-02', '2025-04-07',  1,  55.00),
(10, 1, 1, '2025-04-18', '2025-04-20',  1,  25.00),
(11, 3, 2, '2025-04-25', '2025-04-30',  1,  60.00),
(12, 5, 8, '2025-05-05', '2025-05-12',  1, 120.00),
(13, 3, 6, '2025-05-12', '2025-05-14',  1,  30.00),
(16, 1, 3, '2025-05-10', '2025-05-15',  1,  55.00),
(17, 4, 4, '2025-05-08', '2025-05-12',  1,  40.00),
(18, 2, 1, '2025-05-15', '2025-05-17',  1,  25.00);
