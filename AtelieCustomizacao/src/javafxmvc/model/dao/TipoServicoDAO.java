package javafxmvc.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafxmvc.model.domain.TipoServico;

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
}
