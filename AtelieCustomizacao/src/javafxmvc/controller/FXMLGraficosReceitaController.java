package javafxmvc.controller;

import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafxmvc.model.dao.OrdemServicoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;

public class FXMLGraficosReceitaController implements Initializable {

    @FXML private LineChart<String, Double> lineChartReceita;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ordemServicoDAO.setConnection(connection);
        Map<Integer, Double> dados = ordemServicoDAO.getReceitaPorMes();
        
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        series.setName("Receita (R$)");
        
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        
        for (Map.Entry<Integer, Double> entry : dados.entrySet()) {
            int index = entry.getKey() - 1;
            String nomeMes = (index >= 0 && index < 12) ? meses[index] : "Mes " + entry.getKey();
            series.getData().add(new XYChart.Data<>(nomeMes, entry.getValue()));
        }
        lineChartReceita.getData().add(series);
    }
}
