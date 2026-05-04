package javafxmvc.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafxmvc.model.domain.FabricaParceira;

public class FabricaParceiraDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(FabricaParceira fabrica) {
        String sql = "INSERT INTO fabrica_parceira (nome, cnpj, telefone, especialidade) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fabrica.getNome());
            stmt.setString(2, fabrica.getCnpj());
            stmt.setString(3, fabrica.getTelefone());
            stmt.setString(4, fabrica.getEspecialidade());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean alterar(FabricaParceira fabrica) {
        String sql = "UPDATE fabrica_parceira SET nome=?, cnpj=?, telefone=?, especialidade=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fabrica.getNome());
            stmt.setString(2, fabrica.getCnpj());
            stmt.setString(3, fabrica.getTelefone());
            stmt.setString(4, fabrica.getEspecialidade());
            stmt.setInt(5, fabrica.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean remover(FabricaParceira fabrica) {
        String sql = "DELETE FROM fabrica_parceira WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fabrica.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<FabricaParceira> listar() {
        String sql = "SELECT * FROM fabrica_parceira";
        List<FabricaParceira> retorno = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                FabricaParceira fabrica = new FabricaParceira();
                fabrica.setId(rs.getInt("id"));
                fabrica.setNome(rs.getString("nome"));
                fabrica.setCnpj(rs.getString("cnpj"));
                fabrica.setTelefone(rs.getString("telefone"));
                fabrica.setEspecialidade(rs.getString("especialidade"));
                retorno.add(fabrica);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }
}
