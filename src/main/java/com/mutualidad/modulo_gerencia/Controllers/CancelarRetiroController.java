package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelRetiro;
import com.mutualidad.modulo_gerencia.Models.ModelRetiroCajero;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


@Component
public class CancelarRetiroController implements Initializable {


    @Autowired
    public Servicio servicio;

    int numSocio = 0;

    String nombre = "";

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    @FXML
    private TextField txtNumSocio, txtNomSocio;

    @FXML
    private TableView<ModelRetiro> tblRetiros;

    @FXML
    private TableColumn<ModelRetiro, String> colFolio;

    @FXML
    private TableColumn<ModelRetiro, String> colMonto;

    @FXML
    private TableColumn<ModelRetiro, String> colSalAntes;

    @FXML
    private TableColumn<ModelRetiro, String> colSalDespues;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colFolio.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId()))
        );

        colMonto.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatoMXN.format(data.getValue().getMontoRetiro())
                )
        );

        colSalAntes.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatoMXN.format(data.getValue().getSaldoAnt())
                )
        );

        colSalDespues.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatoMXN.format(data.getValue().getSaldoNue())
                )
        );
    }

    public void setDatos(int numSocio, String nombre) {
        this.numSocio = numSocio;
        this.nombre = nombre;
        tblRetiros.getItems().clear();
        LocalDate fecha = servicio.traerFechaHoy();
        List<ModelRetiro> retiros = servicio.traerRetirosXSocioYFechaYActivo(numSocio, fecha, true);
        ObservableList<ModelRetiro> data = FXCollections.observableArrayList(retiros);
        txtNomSocio.setText(nombre);
        txtNumSocio.setText(String.valueOf(numSocio));
        tblRetiros.setItems(data);
    }

    @FXML
    public void cancelar() {
        ModelRetiro retiro = tblRetiros.getSelectionModel().getSelectedItem();


        if (retiro == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CANCELAR");
            alert.setContentText("SELECCIONE EL RETIRO QUE DESEA CANCELAR.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMACIÓN");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA CANCELAR EL RETIRO?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
        }

        ModelRetiroCajero retiroCajero = null;

        int forma = retiro.getForma();
        if (forma == 1) {
            retiroCajero = servicio.traerRetiroCajeroXIdRetiro(retiro);
            retiroCajero.setEstado(false);

        }
        retiro.setEstado(false);

        ModelRetiro retiroMod = servicio.cancelarRetiro(retiro, retiroCajero);

        if (!retiroMod.getEstado()) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ÉXITO");
            alert.setHeaderText("ÉXITO AL CANCELAR");
            alert.setContentText("EL RETIRO SE HA CANCELADO DE MANERA CORRECTA.");
            alert.showAndWait();

            Stage ventanaActual = (Stage) txtNumSocio.getScene().getWindow();
            ventanaActual.close();

        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CANCELAR");
            alert.setContentText("OCURRIO UN ERROR.");
            alert.showAndWait();

        }
    }


}
