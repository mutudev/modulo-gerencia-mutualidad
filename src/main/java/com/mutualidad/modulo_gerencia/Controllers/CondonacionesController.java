package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import com.tenpisoft.n2w.MoneyConverters;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class CondonacionesController implements Initializable {

    @Autowired
    public Servicio servicio;

    @FXML
    private Button btnBuscar, btnLimpiar, btnCargarCredito, btnImprimir;

    @FXML
    private TextField txtNumero, txtNombre;

    @FXML
    private Label lblEmpresa, lblSeleccionar, lblNombre;

    @FXML
    private ComboBox cmbEmpresa;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private TableView<ModelCredito> tblCreditos;

    @FXML
    private TableColumn<ModelCredito, String> colFolio;

    @FXML
    private TableColumn<ModelCredito, String> colMonto;

    @FXML
    private TableColumn<ModelCredito, String> colFechaDesem;

    @FXML
    private TableColumn<ModelCredito, String> colEstado;

    @FXML
    private TableColumn<ModelCredito, String> colTipo;


    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DecimalFormat formatter = new DecimalFormat("0.00'%'");
    Dotenv dotenv = Dotenv.load();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        List<ModelEmpresa> empresas = servicio.traerEmpresas();
        if (!empresas.isEmpty()) {
            cmbEmpresa.getItems().addAll(empresas);
            cmbEmpresa.getSelectionModel().selectFirst();
        }

        colFolio.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId()))
        );

        colMonto.setCellValueFactory(data ->
                new SimpleStringProperty(formatoMXN.format(data.getValue().getMonto()).toString())
        );

        colFechaDesem.setCellValueFactory(data ->
                new SimpleStringProperty(formatoFecha.format(data.getValue().getFd()).toString())
        );

        colEstado.setCellValueFactory(data -> {
            if (data.getValue().getStatus() == 2) {
                return new SimpleStringProperty("ACTIVO");
            } else {
                return new SimpleStringProperty("CANCELADO");
            }
        });

        colTipo.setCellValueFactory(data -> {

            String tipo = servicio.traerTipoCreditoConId(Long.valueOf(data.getValue().getTipo_credito())).get().getNombre();

            return new SimpleStringProperty(tipo);

        });

        txtNumero.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos
                            change.setText(change.getText().replaceAll("[^0-9]", ""));

                            return change;
                        }));


    }


    @FXML
    public void cargarDatosTabla() {
        tblCreditos.getItems().clear();
        String socio = txtNumero.getText().trim();
        ModelEmpresa empresa = (ModelEmpresa) cmbEmpresa.getValue();
        List<ModelCredito> creditos = servicio.traerCreditosPorSocioEmpresaEstado(socio, empresa.getCodigo(), 2);
        ObservableList<ModelCredito> data = FXCollections.observableArrayList(creditos);
        tblCreditos.setItems(data);
    }

    @FXML
    public void onChangeCmbEmpresa() {
        if (cmbEmpresa.isVisible()) {
            cargarDatosTabla();
        }
    }

    @FXML
    public void cargarSocio() {

        if (txtNumero.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("NÚMERO NO PROPORCIONADO");
            alert.setContentText("POR FAVOR, PROPORCIONE UN NÚMERO DE SOCIO.");
            alert.showAndWait();
            return;
        }

        String numero = txtNumero.getText().trim();

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(numero), true);

        if (socio != null) {

            txtNumero.setEditable(false);
            btnBuscar.setDisable(true);
            imgBusqueda.setVisible(false);
            txtNombre.setVisible(true);
            txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
            lblNombre.setVisible(true);
            lblEmpresa.setVisible(true);
            cmbEmpresa.setVisible(true);
            lblSeleccionar.setVisible(true);
            tblCreditos.setVisible(true);
            btnCargarCredito.setVisible(true);
            btnImprimir.setVisible(true);

            cargarDatosTabla();

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SOCIO NO ENCONTRADO");
            alert.setHeaderText("SOCIO NO ENCONTRADO");
            alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
            alert.showAndWait();

            limpiar();
        }

    }

    @FXML
    public void limpiar() {
        txtNumero.setEditable(true);
        txtNumero.clear();
        btnBuscar.setDisable(false);
        imgBusqueda.setVisible(true);
        txtNombre.clear();
        txtNombre.setVisible(false);
        lblNombre.setVisible(false);
        lblEmpresa.setVisible(false);
        cmbEmpresa.setVisible(false);
        lblSeleccionar.setVisible(false);
        tblCreditos.setVisible(false);
        btnCargarCredito.setVisible(false);
        btnImprimir.setVisible(false);
    }


    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setCondonacionesController(this);
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
        cargarSocio();
    }

    @FXML
    public void cargarCuotaConClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            mostrarCuotas();
        }
    }


    @FXML
    public void mostrarCuotas(){
        try {
            ModelCredito credito = tblCreditos.getSelectionModel().getSelectedItem();
            if(credito == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL CARGAR EL CRÉDITO");
                alert.setContentText("SELECCIONE UN CRÉDITO");
                alert.showAndWait();
                return;
            }


            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/verCuotasCondonacion.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            VerCuotasCondonacionController controlador = fxml.getController();
            controlador.setDatos(credito.getId());
            nuevaEscena
                    .getStylesheets()
                    .add(getClass().getResource("/assets/css/estilos.css").toExternalForm());
            nuevaVentana.setTitle("VER CUOTAS DE CRÉDITO");
            nuevaVentana.setScene(nuevaEscena);
            Image icon = new Image(getClass().getResourceAsStream("/assets/images/logo.png"));
            nuevaVentana.getIcons().add(icon);
            nuevaVentana.setResizable(false);
            nuevaVentana.centerOnScreen();
            nuevaVentana.initModality(Modality.APPLICATION_MODAL);
            nuevaVentana.show();
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL ABRIR LA PANTALLA");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    public void imprimirReporte(){

        ModelCredito credito = tblCreditos.getSelectionModel().getSelectedItem();
        if(credito == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL IMPRIMIR EL REPORTE");
            alert.setContentText("SELECCIONE UN CRÉDITO");
            alert.showAndWait();
            return;
        }

        if(servicio.traerCondonacionesXCredito(credito.getId(), true).size() <= 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL IMPRIMIR EL REPORTE");
            alert.setContentText("NO HAY CONDONACIONES PARA ESTE CRÉDITO");
            alert.showAndWait();
            return;
        }

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

        Label loadingLabel = new Label("Generando Retiro...");
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


                        InputStream isLogo;
                        String numSocio = String.valueOf(credito.getSocio());
                        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(numSocio), true);
                        String nomsocio = socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM();
                        ModelUsuario usuario = servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado);
                        String nomUsuario = usuario.getUsuario();
                        String monto = formatoMXN.format(credito.getMonto());
                        String saldo = formatoMXN.format(credito.getSaldo());
                        String fecha = formatoFecha.format(servicio.traerFechaHoy());
                        String numCredito = String.valueOf(credito.getId());
                        String fechaDesembolso = formatoFecha.format(credito.getFd());
                        String fechaVencimiento = formatoFecha.format(credito.getFv());
                        String tasa = formatter.format(credito.getTasa());

                        if (credito.getEmpresa().equals("0001")) {
                            isLogo = getClass().getResourceAsStream("/assets/images/logo-mut.png");
                        } else {
                            isLogo = getClass().getResourceAsStream("/assets/images/logo-ngu.jpg");
                        }

                        Map pars = new HashMap<>();

                        pars.put("LogoImg", isLogo);
                        pars.put("numSocio", numSocio);
                        pars.put("usuario","IMPRIME: " + nomUsuario);
                        pars.put("nomSocio", nomsocio);
                        pars.put("monto", monto);
                        pars.put("saldo", saldo);
                        pars.put("fechaHoy", fecha);
                        pars.put("folio", numCredito);
                        pars.put("fechaDes", fechaDesembolso);
                        pars.put("fechaVec", fechaVencimiento);
                        pars.put("tasa", tasa);
                        pars.put("SUBREPORT_DIR", getClass().getResource("/Reports/sub_condonacion.jasper").toString());

                        Connection conn = DriverManager.getConnection(dotenv.get("DATABASE_URL"), dotenv.get("DATABASE_USERNAME"), dotenv.get("DATABASE_PASSWORD"));

                        InputStream isRepo =
                                getClass().getResourceAsStream("/Reports/condonaciones.jasper");

                        JasperReport jrRepo = (JasperReport) JRLoader.loadObject(isRepo);
                        JasperPrint jpRepo =
                                JasperFillManager.fillReport(jrRepo, pars, conn);

                        Platform.runLater(() -> {
                            JasperViewer viewer = new JasperViewer(jpRepo, false);
                            viewer.setAlwaysOnTop(true);
                            viewer.setSize(800, 600);
                            viewer.setLocationRelativeTo(null);
                            viewer.setTitle("REPORTE DE CONDONACIONES");
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

}
