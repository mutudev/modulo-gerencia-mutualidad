package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelFechasExcluyentes;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class DiasSinInteresController implements Initializable {


    @Autowired
    private Servicio servicio;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private DatePicker dateInicial, dateFinal;

    @FXML
    private Button btnGuardar, btnLimpiar, btnEliminar;

    @FXML
    private Label lblCreacion;

    @FXML
    private TableView<ModelFechasExcluyentes> tblPeriodos;

    @FXML
    private TableColumn<ModelFechasExcluyentes, String> colFechaInicial;

    @FXML
    private TableColumn<ModelFechasExcluyentes, String> colFechaFinal;

    @FXML
    private TableColumn<ModelFechasExcluyentes, String> colDescripcion;

    DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtDescripcion.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            return change;
                        }));

        colFechaInicial.setCellValueFactory(data ->
                new SimpleStringProperty(formatterDate.format(data.getValue().getFechaInicial()))
        );

        colFechaFinal.setCellValueFactory(data ->
                new SimpleStringProperty(formatterDate.format(data.getValue().getFechaFinal()))
        );

        colDescripcion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescripcion().toString())
        );

        cargarDatosTabla();
    }

    @FXML
    public void cargarDatosTabla() {
        tblPeriodos.getItems().clear();
        List<ModelFechasExcluyentes> fechasExcluyentes = servicio.traerFechasExcluyentes(true);
        ObservableList<ModelFechasExcluyentes> data = FXCollections.observableArrayList(fechasExcluyentes);
        tblPeriodos.setItems(data);
    }

    @FXML
    public void settearDatos(MouseEvent event) {
        if (event.getClickCount() == 2) {
            ModelFechasExcluyentes fechasExcluyentes = tblPeriodos.getSelectionModel().getSelectedItem();
            dateInicial.setValue(fechasExcluyentes.getFechaInicial());
            dateFinal.setValue(fechasExcluyentes.getFechaFinal());
            txtDescripcion.setText(fechasExcluyentes.getDescripcion());
            tblPeriodos.setDisable(true);
            ModelUsuario usuario = servicio.traerUsuarioXId(fechasExcluyentes.getUr());
            lblCreacion.setText("PERIODO CREADO Y/0 ACTUALIZADO POR: " + usuario.getUsuario() + " EL: " + formatterDateTime.format(fechasExcluyentes.getFr()));
            btnGuardar.setText("Actualizar");
            btnEliminar.setVisible(true);
        }
    }

    @FXML
    public void limpiar() {
        dateInicial.setValue(null);
        dateFinal.setValue(null);
        txtDescripcion.clear();
        tblPeriodos.setDisable(false);
        lblCreacion.setText("");
        btnGuardar.setText("Guardar");
        btnEliminar.setVisible(false);
    }

    @FXML
    public void guardar() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("PERIODO SIN COBRO INTERESES");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        LocalDate inicio = dateInicial.getValue();
        LocalDate fin = dateFinal.getValue();
        String descripcion = txtDescripcion.getText().trim();
        ModelUsuario usuario = servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado);

        if (inicio == null || fin == null || descripcion.isEmpty()) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL REGISTRAR EL PERIODO");
            alert.setContentText("COMPLETE TODOS LOS CAMPOS POR FAVOR");
            alert.showAndWait();
            return;
        }

        if (txtDescripcion.getLength() > 255) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL REGISTRAR EL PERIODO");
            alert.setContentText("LA DESCRIPCIÓN NO DEBE SUPERAR LOS 255 CARACTERES");
            alert.showAndWait();
            return;
        }

        if (inicio.isAfter(fin)) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL REGISTRAR EL PERIODO");
            alert.setContentText("INGRESE UN RANGO DE FECHAS VALIDO");
            alert.showAndWait();
            return;
        }


        if (btnGuardar.getText().equalsIgnoreCase("Guardar")) {
            //Ver si dicho periodo no está ya registrado
            if (servicio.encontrarSolapamiento(inicio, fin, true, 0L) != null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL REGISTRAR EL PERIODO");
                alert.setContentText("EL RANGO INGRESADO SE SOLAPA CON UN PERIODO YA EXISTENTE");
                alert.showAndWait();
                return;
            }


            ModelFechasExcluyentes fechasExcluyentes = ModelFechasExcluyentes.builder()
                    .fechaInicial(inicio)
                    .fechaFinal(fin)
                    .descripcion(descripcion)
                    .ur(usuario.getId())
                    .fr(LocalDateTime.now())
                    .estado(true)
                    .build();

            ModelFechasExcluyentes nuevo = servicio.guardarFechaExcluyente(fechasExcluyentes);
            if (nuevo.getId() != null) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("EXITO");
                alert.setHeaderText("EXITO AL REGISTRAR EL PERIODO");
                alert.setContentText("PERIODO REGISTRADO CORRECTAMENTE");
                alert.showAndWait();
                limpiar();
                cargarDatosTabla();
            }
        } else {

            ModelFechasExcluyentes fechasExcluyentes = tblPeriodos.getSelectionModel().getSelectedItem();

            //Al actualizar, solo hay que checar si no existe ya ese periodo que se está mandando
            if (servicio.encontrarSolapamiento(inicio, fin, true, fechasExcluyentes.getId()) != null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL ACTUALIZAR EL PERIODO");
                alert.setContentText("EL RANGO INGRESADO SE SOLAPA CON UN PERIODO YA EXISTENTE");
                alert.showAndWait();
                return;
            }

            fechasExcluyentes.setFechaInicial(dateInicial.getValue());
            fechasExcluyentes.setFechaFinal(dateFinal.getValue());
            fechasExcluyentes.setDescripcion(txtDescripcion.getText());
            fechasExcluyentes.setUr(usuario.getId());
            fechasExcluyentes.setFr(LocalDateTime.now());

            servicio.guardarFechaExcluyente(fechasExcluyentes);

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("EXITO");
            alert.setHeaderText("EXITO AL REGISTRAR EL PERIODO");
            alert.setContentText("PERIODO ACTUALIZADO CORRECTAMENTE");
            alert.showAndWait();

            limpiar();
            cargarDatosTabla();
        }
    }

    @FXML
    public void eliminar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("PERIODO SIN COBRO INTERESES");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        ModelFechasExcluyentes fechasExcluyentes = tblPeriodos.getSelectionModel().getSelectedItem();
        fechasExcluyentes.setEstado(false);
        servicio.guardarFechaExcluyente(fechasExcluyentes);

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EXITO");
        alert.setHeaderText("EXITO AL ELIMINAR EL PERIODO");
        alert.setContentText("PERIODO DADO DE BAJA CORRECTAMENTE");
        alert.showAndWait();

        limpiar();
        cargarDatosTabla();

    }



}
