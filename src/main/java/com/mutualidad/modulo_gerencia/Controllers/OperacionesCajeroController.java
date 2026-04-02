package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class OperacionesCajeroController implements Initializable {

    @FXML
    private TextField txtBuscarTabla;

    @FXML
    private DatePicker dateInicio, dateFin;

    @FXML
    private ComboBox cmbEmpresa;

    @FXML
    private Label lblDesSelecTodos, lblSelecTodos;

    @FXML
    private TableView<ModelUsuario> tblCajeros;

    @FXML
    private TableColumn<ModelUsuario, String> colUsuario;

    @FXML
    private TableColumn<ModelUsuario, String> colNombre;

    @FXML
    private TableColumn<ModelUsuario, Boolean> colSeleccionar;

    @Autowired
    public Servicio servicio;

    private boolean todosSeleccionados = false;


    private final Map<ModelUsuario, Boolean> estados = new HashMap<>();

    private ObservableList<ModelUsuario> listaOriginal = FXCollections.observableArrayList();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    Dotenv dotenv = Dotenv.load();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dateInicio.setValue(LocalDate.now());
        dateFin.setValue(LocalDate.now());

        List<ModelEmpresa> empresas = servicio.traerEmpresas();
        for (ModelEmpresa empresa : empresas) {
            cmbEmpresa.getItems().add(empresa.getNombre());
        }
        cmbEmpresa.getItems().add("AMBAS");
        cmbEmpresa.getSelectionModel().selectFirst();

        txtBuscarTabla.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsuario()));

        colNombre.setCellValueFactory(data -> {
            ModelEmpleado empleado = servicio.traerEmpleadoXId(data.getValue().getIdEmpleado());

            SimpleStringProperty nombre = new SimpleStringProperty(empleado.getNombres() + " " + empleado.getApellidoP() + " " + empleado.getApellidoM());

            return nombre;

        });

        colSeleccionar.setCellValueFactory(data -> {
            ModelUsuario cajero = data.getValue();


            estados.putIfAbsent(cajero, false);

            SimpleBooleanProperty prop =
                    new SimpleBooleanProperty(estados.get(cajero));


            prop.addListener((obs, oldVal, newVal) -> estados.put(cajero, newVal));

            return prop;
        });

        colSeleccionar.setCellFactory(tc -> new CheckBoxTableCell<>());
        tblCajeros.setEditable(true);
        colSeleccionar.setEditable(true);

        lblSelecTodos.setVisible(true);

        cargarDatosTabla();

    }

    @FXML
    public void cargarDatosTabla() {
        tblCajeros.getItems().clear();
        List<ModelUsuario> cajeros = servicio.traerCajeros(1, true);
        ObservableList<ModelUsuario> data = FXCollections.observableArrayList(cajeros);
        listaOriginal = data;
        tblCajeros.setItems(data);
    }

    @FXML
    public void seleccionarTodos() {

        todosSeleccionados = !todosSeleccionados;

        for (ModelUsuario usuario : tblCajeros.getItems()) {
            estados.put(usuario, todosSeleccionados);
        }

        tblCajeros.refresh();

        if (todosSeleccionados) {
            lblDesSelecTodos.setVisible(true);
            lblSelecTodos.setVisible(false);
        } else {
            lblDesSelecTodos.setVisible(false);
            lblSelecTodos.setVisible(true);
        }
    }

    @FXML
    public void buscarCajeros() {

        String texto = txtBuscarTabla.getText().toLowerCase().trim();

        if (texto.isEmpty()) {
            tblCajeros.setItems(listaOriginal);
            return;
        }

        ObservableList<ModelUsuario> filtrados = FXCollections.observableArrayList();

        for (ModelUsuario cajero : listaOriginal) {
            if (cajero.getUsuario().toLowerCase().contains(texto)) {
                filtrados.add(cajero);
            }
        }

        tblCajeros.setItems(filtrados);
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

        Label loadingLabel = new Label("Generando reporte...");
        loadingLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        loadingLabel.setTextFill(Color.web("#39577c"));

        loadingPane.getChildren().addAll(progressIndicator, loadingLabel);

        Scene loadingScene = new Scene(loadingPane, 300, 150);
        loadingStage.setScene(loadingScene);
        loadingStage.centerOnScreen();

        List<String> usuariosSeleccionados = new ArrayList<>();

        List<Integer> listaIds = new ArrayList<>();

        for (ModelUsuario usuario : tblCajeros.getItems()) {
            if (estados.getOrDefault(usuario, false)) {
                usuariosSeleccionados.add(usuario.getUsuario());
                listaIds.add(usuario.getId());
            }
        }

        List<Object[]> datosTransacciones = servicio.traerTransaccionesComprobar(listaIds, dateInicio.getValue().toString(), dateFin.getValue().toString());


        if (usuariosSeleccionados.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL GENERAR EL REPORTE");
            alert.setContentText("POR FAVOR SELECCIONE AL MENOS UN USUARIO");
            alert.showAndWait();
            return;
        }

        if (datosTransacciones.size() < 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL GENERAR EL REPORTE");
            alert.setContentText("REGISTROS INEXISTENTES, SELECCIONE OTROS PARÁMETROS");
            alert.showAndWait();
            return;
        }

        try {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        InputStream logoMut = null;
                        InputStream logoNgu = null;

                        String empresa = "";
                        String codEmpresa="";
                        if (cmbEmpresa.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("AMBAS")) {
                            empresa = cmbEmpresa.getItems().get(0).toString() + " Y " + cmbEmpresa.getItems().get(1).toString();
                            codEmpresa = null;
                            logoNgu = getClass().getResourceAsStream("/assets/images/logo-ngu.jpg");
                            logoMut = getClass().getResourceAsStream("/assets/images/logo-mut.png");
                        } else {
                            empresa = cmbEmpresa.getSelectionModel().getSelectedItem().toString();
                            codEmpresa = servicio.traerEmpresaPorNombre(cmbEmpresa.getSelectionModel().getSelectedItem().toString()).getCodigo();
                            if (codEmpresa.equalsIgnoreCase("0001")) {
                                logoMut = getClass().getResourceAsStream("/assets/images/logo-mut.png");
                            } else {
                                logoNgu = getClass().getResourceAsStream("/assets/images/logo-ngu.jpg");
                            }
                        }

                        String fechaImp = LocalDate.now().format(formatter);



                        String cajero = String.join(", ", usuariosSeleccionados) + ".";


                        String fecha1 = dateInicio.getValue().format(formatter).toString();
                        String fecha2 = dateFin.getValue().format(formatter).toString();


                        Map params = new HashMap<>();
                        params.put("empresa", empresa);
                        params.put("usuario", usuariosSeleccionados);
                        params.put("empresaCod", codEmpresa);
                        params.put("fechaImp", "Rango de Fechas: " + fecha1 + " - " + fecha2);
                        params.put("cajero", "CAJERO(S): " + cajero);
                        params.put("rangoFecha1", fecha1);
                        params.put("rangoFecha2", fecha2);
                        if (cmbEmpresa.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("AMBAS")) {
                            params.put("imgMut", logoMut);
                            params.put("imgNgu", logoNgu);
                        } else {
                            if (codEmpresa.equalsIgnoreCase("0001")) {
                                params.put("imgMut", logoMut);
                            } else {
                                params.put("imgMut", logoNgu);
                            }
                        }
                        params.put("SUBREPORT_DIR",
                                getClass().getResource("/Reports/sub_operaciones.jasper").toString());


                        InputStream isRepo = getClass().getResourceAsStream("/Reports/operaciones_cajero.jasper");
                        JasperReport jrRepo = (JasperReport) JRLoader.loadObject(isRepo);
                        Connection conn = DriverManager.getConnection(dotenv.get("DATABASE_URL"), dotenv.get("DATABASE_USERNAME"), dotenv.get("DATABASE_PASSWORD"));
                        JasperPrint jpRepo = JasperFillManager.fillReport(jrRepo, params, conn);


                        Platform.runLater(() -> {
                            JasperViewer viewer = new JasperViewer(jpRepo, false);
                            viewer.setAlwaysOnTop(true);
                            viewer.setSize(800, 600);
                            viewer.setLocationRelativeTo(null);
                            viewer.setTitle("OPERACIONES POR CAJERO");
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
