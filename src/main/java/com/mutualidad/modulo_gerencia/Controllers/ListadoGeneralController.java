package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelEmpresa;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ListadoGeneralController implements Initializable {

    @FXML
    private ComboBox cmbEmpresas, cmbTipo;

    @Autowired
    private Servicio servicio;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Dotenv dotenv = Dotenv.load();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<ModelEmpresa> empresas = servicio.traerEmpresas();
        cmbEmpresas.getItems().clear();
        for (ModelEmpresa empresa : empresas) {
            cmbEmpresas.getItems().add(empresa.getNombre());
        }
        cmbEmpresas.getItems().add("AMBAS");
        cmbEmpresas.getSelectionModel().selectFirst();

        List<Object[]> tipos = servicio.traerTiposSocios();
        cmbTipo.getItems().clear();
        for (Object[] tipo : tipos) {
            String tipoNom = tipo[1].toString();
            cmbTipo.getItems().add(tipoNom);
        }
        cmbTipo.getItems().add("AMBOS");
        cmbTipo.getSelectionModel().selectFirst();
    }


    @FXML
    public void generarReporte() {


        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initStyle(StageStyle.UNDECORATED);
        loadingStage.setAlwaysOnTop(true);

        VBox loadingPane = new VBox(20);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setPadding(new Insets(30));
        loadingPane.setStyle("-fx-background-color: white; -fx-border-color: #185754; -fx-border-width: 2;");

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(60, 60);

        Label loadingLabel = new Label("Generando Listado...");
        loadingLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        loadingLabel.setTextFill(Color.web("#39577c"));

        loadingPane.getChildren().addAll(progressIndicator, loadingLabel);

        Scene loadingScene = new Scene(loadingPane, 300, 150);
        loadingStage.setScene(loadingScene);
        loadingStage.centerOnScreen();


        try {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {

                        //Obtener empresa de primeras
                        String empresa = "";
                        String tipo = "";
                        String empresaTitulo = "";
                        InputStream logoMut = null;
                        InputStream logoNgu = null;
                        String fechaImp = "FECHA DE IMPRESIÓN: " + LocalDate.now().format(formatter);
                        String conteos = "";

                        //Sin deferenciar menores y mayores
                        int totalSociosActivos = servicio.traerListadoSocios(true).size();
                        int totalActivosMut = servicio.traerListadoPorEmpresaYestado(true, "0001").size();
                        int totalActivosNgu = servicio.traerListadoPorEmpresaYestado(true, "0002").size();

                        //Diferenciando menores y mayores de ambas empresas
                        int totalMenoresActivos = servicio.traerListadoPorTipo(true, 2).size();
                        int totalMayoresActivos = servicio.traerListadoPorTipo(true, 1).size();

                        //total diferenciando por empresas
                        int totalMenoresMut = servicio.traerListadoPorTipoYEmpresa(true, 2, "0001").size();
                        int totalMenoresNgu = servicio.traerListadoPorTipoYEmpresa(true, 2, "0002").size();
                        int totalMayoresMut = servicio.traerListadoPorTipoYEmpresa(true, 1, "0001").size();
                        int totalMayoresNgu = servicio.traerListadoPorTipoYEmpresa(true, 1, "0002").size();

                        //Obtener el tipo que se desea
                        if (cmbTipo.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("AMBOS")) {
                            tipo = null;
                        } else {
                            tipo = cmbTipo.getSelectionModel().getSelectedItem().toString();
                        }

                        if (cmbEmpresas.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("AMBAS")) {
                            empresa = null;
                            empresaTitulo = "MUTUALIDAD DOCE DE AGOSTO Y NUEVA GENERACIÓN DE UMÁN";
                            logoNgu = getClass().getResourceAsStream("/assets/images/logo-ngu.jpg");
                            logoMut = getClass().getResourceAsStream("/assets/images/logo-mut.png");
                        } else {
                            empresa = servicio.traerEmpresaPorNombre(cmbEmpresas.getSelectionModel().getSelectedItem().toString()).getAbreviacion();
                            empresaTitulo = cmbEmpresas.getSelectionModel().getSelectedItem().toString();
                            if (empresa.equalsIgnoreCase("MUT")) {
                                logoMut = getClass().getResourceAsStream("/assets/images/logo-mut.png");
                            } else {
                                logoNgu = getClass().getResourceAsStream("/assets/images/logo-ngu.jpg");
                            }
                        }


                        conteos =
                                String.format("%-35s %-35s %-35s\n",
                                        "TOTAL SOCIOS: " + totalSociosActivos,
                                        "   MUT: " + totalActivosMut,
                                        "              NGU: " + totalActivosNgu) +

                                        String.format("%-35s %-35s %-35s\n",
                                                "MENORES TOTAL: " + totalMenoresActivos,
                                                "MENORES MUT: " + totalMenoresMut,
                                                "MENORES NGU: " + totalMenoresNgu) +

                                        String.format("%-35s %-35s %-35s",
                                                "MAYORES TOTAL: " + totalMayoresActivos,
                                                "MAYORES MUT: " + totalMayoresMut,
                                                "MAYORES NGU: " + totalMayoresNgu);


                        Map pars = new HashMap<>();
                        pars.put("empresa", empresaTitulo);
                        pars.put("fechaImp", fechaImp);
                        if (cmbEmpresas.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("AMBAS")) {
                            pars.put("imgMut", logoMut);
                            pars.put("imgNgu", logoNgu);
                        } else {
                            if (empresa.equalsIgnoreCase("MUT")) {
                                pars.put("imgMut", logoMut);
                            } else {
                                pars.put("imgMut", logoNgu);
                            }
                        }
                        pars.put("empresaCod", empresa);
                        pars.put("tipoSocio", tipo);
                        pars.put("conteos", conteos);

                        InputStream isRepo =
                                getClass().getResourceAsStream("/Reports/listado_general.jasper");
                        Connection conn = DriverManager.getConnection(dotenv.get("DATABASE_URL"), dotenv.get("DATABASE_USERNAME"), dotenv.get("DATABASE_PASSWORD"));
                        JasperReport jrRepo = (JasperReport) JRLoader.loadObject(isRepo);
                        JasperPrint jpRepo =
                                JasperFillManager.fillReport(jrRepo, pars, conn);

                        Platform.runLater(() -> {
                            JasperViewer viewer = new JasperViewer(jpRepo, false);
                            viewer.setAlwaysOnTop(true);
                            viewer.setSize(800, 600);
                            viewer.setLocationRelativeTo(null);
                            viewer.setTitle("LISTADO GENERAL DE SOCIOS");
                            viewer.setVisible(true);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("ERROR AL GENERAR EL REPORTE");
                            alert.setContentText("OCURRIÓ UN ERROR: "+ e.getMessage());
                            alert.showAndWait();
                        });
                    }
                    return null;
                }
            };

            task.setOnSucceeded(e -> loadingStage.close());
            task.setOnFailed(e -> loadingStage.close());

            loadingStage.show();
            new Thread(task).start();

        } catch (Exception e) {
            e.printStackTrace();
        }










    }

}
