package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Repository.SocioRepository;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SaldosAhorroController implements Initializable {

    @FXML
    private Label lblNombre, lblEmpresa, lblAhorro, lblPsocMut, lblPsocNgu, lblAhorroCongelado;

    @FXML
    private TextField txtNombre, txtNumero, txtEmpresa, txtAhorro, txtPsocNgu, txtPsocMut, txtAhorroCongelado;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Button btnBuscar, btnLimpiar, btnImprimir;

    @Autowired
    private Servicio servicio;

    @Autowired
    private SocioRepository repoSocio;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Dotenv dotenv = Dotenv.load();

    @FXML
    public void buscarSocio() {

        if (txtNumero.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("NÚMERO NO PROPORCIONADO");
            alert.setContentText("POR FAVOR, PROPORCIONE UN NÚMERO DE SOCIO.");
            alert.showAndWait();
            return;
        }

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);
        if (socio != null) {

            ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(socio.getNumSocio(), 1);
            if(ahorro == null){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("CUENTA DE AHORRO INEXISTENTE");
                alert.setContentText("EL SOCIO QUE INTENTA BUSCAR NO TIENEN UNA CUENTA DE AHORRO ACTIVA");
                alert.showAndWait();

                txtNumero.clear();
                txtNumero.setEditable(true);
                return;
            }
            txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
            List<ModelCapitalSocial> cs = servicio.traerCuentasCs(socio.getNumSocio());

            if (cs != null) {
                for (ModelCapitalSocial cuenta : cs) {

                    if (cuenta.getEmpresaCod().equalsIgnoreCase("0001")) {
                        txtPsocMut.setText(formatoMXN.format(cuenta.getMonto_cubierto()));

                    } else {
                        txtPsocNgu.setText(formatoMXN.format(cuenta.getMonto_cubierto()));
                    }
                }
            }

            if (txtPsocNgu.getText().isEmpty()) {
                txtPsocNgu.setText(formatoMXN.format(0.00));
            }
            if (txtPsocMut.getText().isEmpty()) {
                txtPsocMut.setText(formatoMXN.format(0.00));
            }
            txtAhorro.setText(formatoMXN.format(ahorro.getSaldo()));
            txtAhorroCongelado.setText(formatoMXN.format(ahorro.getSaldo_congelado()));
            if (socio.getEmpresaCod().equalsIgnoreCase("0001")) {
                txtEmpresa.setText("MUTUALIDAD DOCE DE AGOSTO S.C. DE R.L. DE C.V.");
            } else {
                txtEmpresa.setText("NUEVA GENERACION DE UMAN, AC.");
            }


            txtNombre.setVisible(true);
            txtPsocMut.setVisible(true);
            txtPsocNgu.setVisible(true);
            txtEmpresa.setVisible(true);
            txtNumero.setEditable(false);
            txtAhorro.setVisible(true);
            txtAhorroCongelado.setVisible(true);
            imgBusqueda.setVisible(false);

            lblNombre.setVisible(true);
            lblPsocMut.setVisible(true);
            lblPsocNgu.setVisible(true);
            lblAhorroCongelado.setVisible(true);
            lblAhorro.setVisible(true);
            lblEmpresa.setVisible(true);
            btnImprimir.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SOCIO NO ENCONTRADO");
            alert.setHeaderText("SOCIO NO ENCONTRADO");
            alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
            alert.showAndWait();
            return;
        }

    }

    @FXML
    public void imprimirReporte() {

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

        Label loadingLabel = new Label("Generando reporte...");
        loadingLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        loadingLabel.setTextFill(Color.web("#39577c"));

        loadingPane.getChildren().addAll(progressIndicator, loadingLabel);

        Scene loadingScene = new Scene(loadingPane, 300, 150);
        loadingStage.setScene(loadingScene);
        loadingStage.centerOnScreen();

        //Validar si tiene algún registro, sino, cortar
        List<Integer> ids = List.of(1, 4, 5, 10, 11, 12, 13);
        List<ModelTransaccion> transacciones = servicio.traerTransaccionesPorTipoOperacion(Integer.parseInt(txtNumero.getText().trim()), true, ids);
        if (transacciones.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ADVERTENCIA DE REPORTE");
            alert.setContentText("EL SOCIO NO TIENE OPERACIONES ACTIVAS RELACIONADAS AL AHORRO.");
            alert.showAndWait();
            return;
        }

        try {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {

                        ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(Integer.parseInt(txtNumero.getText().trim()), 1);
                        Map pars = new HashMap<>();
                        pars.put("numSocio", txtNumero.getText().toString());
                        pars.put("nomSocio", txtNombre.getText().toString());

                        double ahorroTot = 0;
                        for (ModelCapitalSocial cuenta : servicio.traerCuentasCs(Integer.parseInt(txtNumero.getText().trim()))) {
                            ahorroTot += cuenta.getMonto_cubierto();
                        }

                        ahorroTot += ahorro.getSaldo();
                        pars.put("saldoAhorro", formatoMXN.format(ahorro.getSaldo()));
                        pars.put("saldoCongelado", formatoMXN.format(ahorro.getSaldo_congelado()));
                        pars.put("saldoTotal", formatoMXN.format(ahorroTot + ahorro.getSaldo_congelado()));
                        pars.put("fechaImp", formatoFecha.format(LocalDate.now()));

                        InputStream isRepo = getClass().getResourceAsStream("/Reports/movimientos_ahorro.jasper");
                        JasperReport jrRepo = (JasperReport) JRLoader.loadObject(isRepo);
                        Connection conn = DriverManager.getConnection(dotenv.get("DATABASE_URL"), dotenv.get("DATABASE_USERNAME"), dotenv.get("DATABASE_PASSWORD"));
                        JasperPrint jpRepo = JasperFillManager.fillReport(jrRepo, pars, conn);

                        Platform.runLater(() -> {
                            JasperViewer viewer = new JasperViewer(jpRepo, false);
                            viewer.setSize(800, 600);
                            viewer.setAlwaysOnTop(true);
                            viewer.setLocationRelativeTo(null);
                            viewer.setTitle("SALDOS DE AHORRO");
                            viewer.setVisible(true);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("ERROR AL GENERAR EL REPORTE");
                            alert.setContentText("OCURRIÓ UN ERROR: " + e.getMessage());
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





    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setSaldosAhorroController(this);
            nuevaEscena
                    .getStylesheets()
                    .add(getClass().getResource("/assets/css/estilos.css").toExternalForm());
            nuevaVentana.setTitle("BUSQUEDA DE SOCIO POR NOMBRE");
            nuevaVentana.setScene(nuevaEscena);
            nuevaVentana.setResizable(false);
            nuevaVentana.centerOnScreen();
            nuevaVentana.initModality(Modality.APPLICATION_MODAL);
            nuevaVentana.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void cargarSocioPorNombre(String numero) {
        txtNumero.setText(numero);
        buscarSocio();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtNumero.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9.]", ""));

                            // Verifica si ya hay más de un punto decimal
                            if (change.getText().matches(".*\\..*\\..*")) {
                                change.setText(change.getText().substring(0, change.getText().lastIndexOf('.')));
                            }

                            return change;
                        }));

    }

    public void limpiar(){

        txtNombre.setVisible(false);
        txtPsocMut.setVisible(false);
        txtPsocNgu.setVisible(false);
        txtEmpresa.setVisible(false);
        txtNumero.setEditable(true);
        txtAhorro.setVisible(false);
        txtAhorroCongelado.setVisible(false);
        imgBusqueda.setVisible(true);

        lblNombre.setVisible(false);
        lblPsocMut.setVisible(false);
        lblPsocNgu.setVisible(false);
        lblAhorroCongelado.setVisible(false);
        lblAhorro.setVisible(false);
        lblEmpresa.setVisible(false);
        btnImprimir.setVisible(false);
        txtNumero.setEditable(true);
        txtNumero.clear();

    }
}
