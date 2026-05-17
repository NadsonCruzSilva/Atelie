package javafxmvc.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafxmvc.model.domain.Cliente;
import javafxmvc.model.domain.OrdemServico;

public class OrdemServicoDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(OrdemServico ordemServico) {
        String sqlVerifica = "SELECT possui_pendencia, dias_atraso FROM cliente WHERE id = ? FOR UPDATE";
        String sqlOrdem = "INSERT INTO ordem_servico (cliente_id, data_abertura, data_prevista, data_retorno, status, valor_total) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEncaminhamento = "INSERT INTO encaminhamento (ordem_servico_id, fabrica_parceira_id, tipo_servico_id, data_encaminhamento, data_retorno_prevista, quantidade, valor_servico) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlUpdateOrdem = "UPDATE ordem_servico SET valor_total = ? WHERE id = ?";
        String sqlUpdateCliente = "UPDATE cliente SET dias_atraso = 0 WHERE id = ?";

        try {
            connection.setAutoCommit(false); // Inicia transação ACID

            // TRANSAÇÃO 1: Consultar cliente com bloqueio (FOR UPDATE) para regra de negócio
            try (PreparedStatement stmtVerifica = connection.prepareStatement(sqlVerifica)) {
                stmtVerifica.setInt(1, ordemServico.getCliente().getId());
                try (ResultSet rs = stmtVerifica.executeQuery()) {
                    if (rs.next()) {
                        boolean pendencia = rs.getBoolean("possui_pendencia");
                        int diasAtraso = rs.getInt("dias_atraso");
                        if (pendencia || diasAtraso > 30) {
                            connection.rollback();
                            throw new SQLException("Cliente inadimplente. Operação cancelada.");
                        }
                    }
                }
            }

            // TRANSAÇÃO 2: Inserir a Ordem de Serviço
            try (PreparedStatement stmtOrdem = connection.prepareStatement(sqlOrdem, Statement.RETURN_GENERATED_KEYS)) {
                stmtOrdem.setInt(1, ordemServico.getCliente().getId());
                stmtOrdem.setDate(2, Date.valueOf(ordemServico.getDataAbertura()));
                stmtOrdem.setDate(3, Date.valueOf(ordemServico.getDataPrevista()));
                if (ordemServico.getDataRetorno() != null) {
                    stmtOrdem.setDate(4, Date.valueOf(ordemServico.getDataRetorno()));
                } else {
                    stmtOrdem.setNull(4, java.sql.Types.DATE);
                }
                stmtOrdem.setString(5, ordemServico.getStatus());
                stmtOrdem.setDouble(6, ordemServico.getValorTotal());
                stmtOrdem.execute();

                ResultSet rs = stmtOrdem.getGeneratedKeys();
                if (rs.next()) {
                    ordemServico.setId(rs.getInt(1));
                }
            }

            // TRANSAÇÃO 3: Inserir itens de Encaminhamento
            double somaTotal = 0;
            if (ordemServico.getEncaminhamentos() != null) {
                try (PreparedStatement stmtEnc = connection.prepareStatement(sqlEncaminhamento)) {
                    for (javafxmvc.model.domain.Encaminhamento enc : ordemServico.getEncaminhamentos()) {
                        stmtEnc.setInt(1, ordemServico.getId());
                        stmtEnc.setInt(2, enc.getFabricaParceira().getId());
                        stmtEnc.setInt(3, enc.getTipoServico().getId());
                        stmtEnc.setDate(4, Date.valueOf(enc.getDataEncaminhamento()));
                        if (enc.getDataRetornoPrevista() != null) {
                            stmtEnc.setDate(5, Date.valueOf(enc.getDataRetornoPrevista()));
                        } else {
                            stmtEnc.setNull(5, java.sql.Types.DATE);
                        }
                        stmtEnc.setInt(6, enc.getQuantidade());
                        stmtEnc.setDouble(7, enc.getValorServico());
                        stmtEnc.execute();
                        somaTotal += enc.getValorServico() * enc.getQuantidade();
                    }
                }
            }

            // TRANSAÇÃO 4: Atualizar Ordem de Serviço com o valor total recalculado
            if (somaTotal > 0 && somaTotal != ordemServico.getValorTotal()) {
                try (PreparedStatement stmtUpdateOrdem = connection.prepareStatement(sqlUpdateOrdem)) {
                    stmtUpdateOrdem.setDouble(1, somaTotal);
                    stmtUpdateOrdem.setInt(2, ordemServico.getId());
                    stmtUpdateOrdem.execute();
                }
            }

            // TRANSAÇÃO 5: Atualizar status do cliente
            try (PreparedStatement stmtUpdateCliente = connection.prepareStatement(sqlUpdateCliente)) {
                stmtUpdateCliente.setInt(1, ordemServico.getCliente().getId());
                stmtUpdateCliente.execute();
            }

            connection.commit(); // Confirma as 5 transações
            return true;

        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean alterar(OrdemServico ordemServico) {
        String sql = "UPDATE ordem_servico SET cliente_id=?, data_abertura=?, data_prevista=?, data_retorno=?, status=?, valor_total=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ordemServico.getCliente().getId());
            stmt.setDate(2, Date.valueOf(ordemServico.getDataAbertura()));
            stmt.setDate(3, Date.valueOf(ordemServico.getDataPrevista()));
            if (ordemServico.getDataRetorno() != null) {
                stmt.setDate(4, Date.valueOf(ordemServico.getDataRetorno()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setString(5, ordemServico.getStatus());
            stmt.setDouble(6, ordemServico.getValorTotal());
            stmt.setInt(7, ordemServico.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean remover(OrdemServico ordemServico) {
        String sql = "DELETE FROM ordem_servico WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ordemServico.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean isClienteInadimplente(Cliente cliente) {
        if (cliente.isPossuiPendencia() || cliente.getDiasAtraso() > 30) {
            return true;
        }
        return false;
    }

    public List<OrdemServico> listar() {
        String sql = "SELECT * FROM ordem_servico";
        List<OrdemServico> retorno = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                OrdemServico ordemServico = new OrdemServico();
                ordemServico.setId(rs.getInt("id"));

                ClienteDAO clienteDAO = new ClienteDAO();
                clienteDAO.setConnection(connection);
                // In a real scenario we might have a findById in DAO
                List<Cliente> clientes = clienteDAO.listar();
                for (Cliente c : clientes) {
                    if (c.getId() == rs.getInt("cliente_id")) {
                        ordemServico.setCliente(c);
                        break;
                    }
                }

                ordemServico.setDataAbertura(rs.getDate("data_abertura").toLocalDate());
                ordemServico.setDataPrevista(rs.getDate("data_prevista").toLocalDate());
                if (rs.getDate("data_retorno") != null) {
                    ordemServico.setDataRetorno(rs.getDate("data_retorno").toLocalDate());
                }
                ordemServico.setStatus(rs.getString("status"));
                ordemServico.setValorTotal(rs.getDouble("valor_total"));
                retorno.add(ordemServico);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }

    public Map<Integer, Double> getReceitaPorMes() {
        String sql = "SELECT EXTRACT(MONTH FROM data_retorno) as mes, SUM(valor_total) as total " +
                "FROM ordem_servico WHERE status = 'FINALIZADA' AND data_retorno IS NOT NULL " +
                "GROUP BY EXTRACT(MONTH FROM data_retorno) ORDER BY mes";
        Map<Integer, Double> retorno = new java.util.LinkedHashMap<>();
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
                java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                retorno.put(rs.getInt("mes"), rs.getDouble("total"));
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }

    public Map<String, Integer> getQuantidadeOSPorStatus() {
        String sql = "SELECT status, COUNT(*) as qtd FROM ordem_servico GROUP BY status";
        Map<String, Integer> retorno = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                retorno.put(rs.getString("status"), rs.getInt("qtd"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }

    public int getQuantidadeOSFinalizadas(Cliente cliente) {
        String sql = "SELECT COUNT(*) as total_compras FROM ordem_servico WHERE cliente_id = ? AND status = 'FINALIZADA'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_compras");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
