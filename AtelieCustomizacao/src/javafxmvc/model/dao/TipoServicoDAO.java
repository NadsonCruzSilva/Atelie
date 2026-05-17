package javafxmvc.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafxmvc.model.domain.TipoServico;
import javafxmvc.model.domain.ReceitaServico;

public class TipoServicoDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(TipoServico tipoServico) {
        String sql = "INSERT INTO tipo_servico (descricao, valor, prazo_estimado_dias) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipoServico.getDescricao());
            stmt.setDouble(2, tipoServico.getValor());
            stmt.setInt(3, tipoServico.getPrazoEstimadoDias());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean alterar(TipoServico tipoServico) {
        String sql = "UPDATE tipo_servico SET descricao=?, valor=?, prazo_estimado_dias=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipoServico.getDescricao());
            stmt.setDouble(2, tipoServico.getValor());
            stmt.setInt(3, tipoServico.getPrazoEstimadoDias());
            stmt.setInt(4, tipoServico.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean remover(TipoServico tipoServico) {
        String sql = "DELETE FROM tipo_servico WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tipoServico.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<TipoServico> listar() {
        String sql = "SELECT * FROM tipo_servico";
        List<TipoServico> retorno = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TipoServico tipoServico = new TipoServico();
                tipoServico.setId(rs.getInt("id"));
                tipoServico.setDescricao(rs.getString("descricao"));
                tipoServico.setValor(rs.getDouble("valor"));
                tipoServico.setPrazoEstimadoDias(rs.getInt("prazo_estimado_dias"));
                retorno.add(tipoServico);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }

    public List<ReceitaServico> getReceitasServicos(String filtro) {
        String sql = "SELECT ts.descricao, ts.valor AS valor_unitario, " +
                     "COALESCE(COUNT(e.id), 0) AS quantidade, " +
                     "COALESCE(SUM(e.valor_servico), 0.00) AS receita_total " +
                     "FROM tipo_servico ts " +
                     "LEFT JOIN encaminhamento e ON ts.id = e.tipo_servico_id " +
                     "LEFT JOIN ordem_servico os ON e.ordem_servico_id = os.id AND os.status = 'FINALIZADA' " +
                     "WHERE ts.descricao ILIKE ? " +
                     "GROUP BY ts.id, ts.descricao, ts.valor " +
                     "ORDER BY receita_total DESC";
        List<ReceitaServico> retorno = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, filtro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReceitaServico rsrv = new ReceitaServico();
                    rsrv.setDescricao(rs.getString("descricao"));
                    rsrv.setValorUnitario(rs.getDouble("valor_unitario"));
                    rsrv.setQuantidade(rs.getLong("quantidade"));
                    rsrv.setReceitaTotal(rs.getDouble("receita_total"));
                    retorno.add(rsrv);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }
}
