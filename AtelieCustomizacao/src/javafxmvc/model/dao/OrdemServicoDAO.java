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
        String sql = "INSERT INTO ordem_servico (cliente_id, data_abertura, data_prevista, data_retorno, status, valor_total) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ordemServico.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
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
                for(Cliente c : clientes) {
                    if(c.getId() == rs.getInt("cliente_id")) {
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
}
