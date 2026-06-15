package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


@Component
public class VerCuotasCreditoController implements Initializable {

    @Autowired
    public Servicio servicio;

    @FXML
    private Button btnBuscar, btnLimpiar, btnCargarCredito;

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
    }


    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setVerCuotasCreditoController(this);
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
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/mostrarCuotasCredito.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            MostrarCuotasCreditoController controlador = fxml.getController();
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






}
