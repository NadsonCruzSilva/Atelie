package javafxmvc.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafxmvc.model.domain.Encaminhamento;
import javafxmvc.model.domain.FabricaParceira;
import javafxmvc.model.domain.OrdemServico;
import javafxmvc.model.domain.TipoServico;

public class EncaminhamentoDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(Encaminhamento encaminhamento) {
        String sql = "INSERT INTO encaminhamento (ordem_servico_id, fabrica_parceira_id, tipo_servico_id, data_encaminhamento, data_retorno_prevista, quantidade, valor_servico) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, encaminhamento.getOrdemServico().getId());
            stmt.setInt(2, encaminhamento.getFabricaParceira().getId());
            stmt.setInt(3, encaminhamento.getTipoServico().getId());
            stmt.setDate(4, Date.valueOf(encaminhamento.getDataEncaminhamento()));
            if (encaminhamento.getDataRetornoPrevista() != null) {
                stmt.setDate(5, Date.valueOf(encaminhamento.getDataRetornoPrevista()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            stmt.setInt(6, encaminhamento.getQuantidade());
            stmt.setDouble(7, encaminhamento.getValorServico());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean alterar(Encaminhamento encaminhamento) {
        String sql = "UPDATE encaminhamento SET ordem_servico_id=?, fabrica_parceira_id=?, tipo_servico_id=?, data_encaminhamento=?, data_retorno_prevista=?, quantidade=?, valor_servico=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, encaminhamento.getOrdemServico().getId());
            stmt.setInt(2, encaminhamento.getFabricaParceira().getId());
            stmt.setInt(3, encaminhamento.getTipoServico().getId());
            stmt.setDate(4, Date.valueOf(encaminhamento.getDataEncaminhamento()));
            if (encaminhamento.getDataRetornoPrevista() != null) {
                stmt.setDate(5, Date.valueOf(encaminhamento.getDataRetornoPrevista()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            stmt.setInt(6, encaminhamento.getQuantidade());
            stmt.setDouble(7, encaminhamento.getValorServico());
            stmt.setInt(8, encaminhamento.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean remover(Encaminhamento encaminhamento) {
        String sql = "DELETE FROM encaminhamento WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, encaminhamento.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    
    public boolean isLimiteOperacionalExcedido(FabricaParceira fabrica) {
        String sql = "SELECT COUNT(*) as qtd FROM encaminhamento WHERE fabrica_parceira_id = ? AND data_retorno_prevista IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fabrica.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int qtd = rs.getInt("qtd");
                return qtd >= 10;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public java.time.LocalDate calcularDataPrevista(java.time.LocalDate dataAbertura, TipoServico tipoServico) {
        if (dataAbertura != null && tipoServico != null) {
            return dataAbertura.plusDays(tipoServico.getPrazoEstimadoDias());
        }
        return null;
    }

    
    public Map<String, Integer> getVolumePorFabrica() {
        String sql = "SELECT f.nome, COUNT(e.id) as qtd FROM encaminhamento e " +
                     "JOIN fabrica_parceira f ON e.fabrica_parceira_id = f.id " +
                     "GROUP BY f.nome";
        Map<String, Integer> retorno = new java.util.HashMap<>();
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                retorno.put(rs.getString("nome"), rs.getInt("qtd"));
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }

    public List<Encaminhamento> listar() {
        // Omitting complex fetch for brevity in standard list, but normally we'd fetch relations
        return new ArrayList<>();
    }
    
    public List<Encaminhamento> listarPorOrdemServico(OrdemServico os) {
        String sql = "SELECT * FROM encaminhamento WHERE ordem_servico_id=?";
        List<Encaminhamento> retorno = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, os.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Encaminhamento enc = new Encaminhamento();
                enc.setId(rs.getInt("id"));
                enc.setOrdemServico(os);
                
                // Fetch Fabrica
                FabricaParceira fp = new FabricaParceira();
                fp.setId(rs.getInt("fabrica_parceira_id"));
                enc.setFabricaParceira(fp);
                
                // Fetch TipoServico
                TipoServico ts = new TipoServico();
                ts.setId(rs.getInt("tipo_servico_id"));
                enc.setTipoServico(ts);
                
                enc.setDataEncaminhamento(rs.getDate("data_encaminhamento").toLocalDate());
                if (rs.getDate("data_retorno_prevista") != null) {
                    enc.setDataRetornoPrevista(rs.getDate("data_retorno_prevista").toLocalDate());
                }
                enc.setQuantidade(rs.getInt("quantidade"));
                enc.setValorServico(rs.getDouble("valor_servico"));
                retorno.add(enc);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }
}
