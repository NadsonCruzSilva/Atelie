package javafxmvc.controller;

import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafxmvc.model.dao.EncaminhamentoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;

public class FXMLGraficosVolumeController implements Initializable {

    @FXML private BarChart<String, Integer> barChartVolume;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final EncaminhamentoDAO encaminhamentoDAO = new EncaminhamentoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        encaminhamentoDAO.setConnection(connection);
        Map<String, Integer> dados = encaminhamentoDAO.getVolumePorFabrica();
        
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Volume Enviado");
        
        for (Map.Entry<String, Integer> entry : dados.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChartVolume.getData().add(series);
    }
}
