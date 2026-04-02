package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.ResourceBundle;


@Component
public class InscripcionController implements Initializable {

    @FXML
    private TextField txtNombre, txtApellidoP, txtApellidoM, txtCURP, txtRFC, txtDireccion, txtTelefono;
    ;

    @FXML
    private ComboBox cmbEstado, cmbMunicipio, cmbEmpleo, cmbEstadoCivil, cmbGenero;

    @FXML
    private Button btnRegistrar;

    @FXML
    private DatePicker dteNacimiento;

    @Autowired
    private Servicio servicio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtNombre.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtApellidoM.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtApellidoP.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtCURP.setTextFormatter(
                new TextFormatter<>(change -> {
                    String text = change.getText().toUpperCase();

                    if (!text.matches("[A-Z0-9]*")) {
                        return null;
                    }

                    change.setText(text);

                    if (change.getControlNewText().length() > 18) {
                        return null;
                    }

                    return change;
                })
        );

        txtRFC.setTextFormatter(
                new TextFormatter<>(change -> {
                    String text = change.getText().toUpperCase();

                    if (!text.matches("[A-Z0-9]*")) {
                        return null;
                    }

                    change.setText(text);

                    if (change.getControlNewText().length() > 13) {
                        return null;
                    }

                    return change;
                })
        );

        txtDireccion.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            return change;
                        }));

        txtTelefono.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9]", ""));
                            return change;
                        }));

        List<Object[]> estados = servicio.traerEstados();


        dteNacimiento.setValue(LocalDate.now());

        cmbEstado.getItems().clear();
        for (Object[] fila : estados) {
            String nombreEstado = fila[1].toString();
            cmbEstado.getItems().add(nombreEstado);
        }
        if (!estados.isEmpty()) {
            cmbEstado.getSelectionModel().select(30);
        }

        List<Object[]> municipios = servicio.traeMunicipios(cmbEstado.getSelectionModel().getSelectedIndex() + 1);

        cmbMunicipio.getItems().clear();
        for (Object[] fila : municipios) {
            String nombreMunicipio = fila[1].toString();
            cmbMunicipio.getItems().add(nombreMunicipio);
        }
        if (!municipios.isEmpty()) {
            cmbMunicipio.getSelectionModel().select(100);
        }


        List<Object[]> empleos = servicio.traerEmpleos();

        cmbEmpleo.getItems().clear();
        for (Object[] fila : empleos) {
            String nombreEmpleos = fila[1].toString();
            cmbEmpleo.getItems().add(nombreEmpleos);
        }
        if (!empleos.isEmpty()) {
            cmbEmpleo.getSelectionModel().selectFirst();
        }

        List<Object[]> estadosC = servicio.traerEstadosC();

        cmbEstadoCivil.getItems().clear();
        for (Object[] fila : estadosC) {
            String nombreEstadosC = fila[1].toString();
            cmbEstadoCivil.getItems().add(nombreEstadosC);
        }
        if (!estadosC.isEmpty()) {
            cmbEstadoCivil.getSelectionModel().selectFirst();
        }

        cmbGenero.getItems().clear();
        cmbGenero.getItems().add("MASCULINO");
        cmbGenero.getItems().add("FEMENINO");
        cmbGenero.getItems().add("OTRO");

        cmbGenero.getSelectionModel().selectFirst();
    }


    @FXML
    public void cambiarMunicipios() {
        int idEstado = cmbEstado.getSelectionModel().getSelectedIndex() + 1;


        List<Object[]> municipios = servicio.traeMunicipios(idEstado);

        cmbMunicipio.getItems().clear();
        for (Object[] fila : municipios) {
            String nombreMunicipio = fila[1].toString();
            cmbMunicipio.getItems().add(nombreMunicipio);
        }
        if (!municipios.isEmpty()) {
            cmbMunicipio.getSelectionModel().selectFirst();
        }

    }

    public void limpiar() {

        // Limpiar TextField
        txtNombre.clear();
        txtApellidoP.clear();
        txtApellidoM.clear();
        txtCURP.clear();
        txtRFC.clear();
        txtDireccion.clear();
        txtTelefono.clear();

        // Limpiar DatePicker
        dteNacimiento.setValue(null);

        /* VOLVER A CARGAR COMBOS IGUAL QUE EN INITIALIZE */

        List<Object[]> estados = servicio.traerEstados();

        cmbEstado.getItems().clear();
        for (Object[] fila : estados) {
            cmbEstado.getItems().add(fila[1].toString());
        }
        if (!estados.isEmpty()) {
            cmbEstado.getSelectionModel().select(30);
        }

        List<Object[]> municipios = servicio.traeMunicipios(cmbEstado.getSelectionModel().getSelectedIndex() + 1);

        cmbMunicipio.getItems().clear();
        for (Object[] fila : municipios) {
            cmbMunicipio.getItems().add(fila[1].toString());
        }
        if (!municipios.isEmpty()) {
            cmbMunicipio.getSelectionModel().select(100);
        }

        List<Object[]> empleos = servicio.traerEmpleos();

        cmbEmpleo.getItems().clear();
        for (Object[] fila : empleos) {
            cmbEmpleo.getItems().add(fila[1].toString());
        }
        if (!empleos.isEmpty()) {
            cmbEmpleo.getSelectionModel().selectFirst();
        }

        List<Object[]> estadosC = servicio.traerEstadosC();

        cmbEstadoCivil.getItems().clear();
        for (Object[] fila : estadosC) {
            cmbEstadoCivil.getItems().add(fila[1].toString());
        }
        if (!estadosC.isEmpty()) {
            cmbEstadoCivil.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void registrarSocio(){

        String nombre = txtNombre.getText().trim();
        String ApellidoM = txtApellidoM.getText().trim();
        String ApellidoP = txtApellidoP.getText().trim();
        LocalDate fNacimiento = dteNacimiento.getValue();
        String curp = txtCURP.getText().trim();
        String rfc = txtRFC.getText().trim();
        int idEstado = cmbEstado.getSelectionModel().getSelectedIndex() + 1;
        int idMunicipio = cmbMunicipio.getSelectionModel().getSelectedIndex() + 1;
        int idEmpleo = cmbEmpleo.getSelectionModel().getSelectedIndex() + 1;
        int idEstadoCivil = cmbEstadoCivil.getSelectionModel().getSelectedIndex() + 1;
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();



        if (nombre.isEmpty() ||
                ApellidoP.isEmpty() ||
                ApellidoM.isEmpty() ||
                fNacimiento == null ||
                curp.isEmpty() ||
                direccion.isEmpty() ||
                telefono.isEmpty() ||
                cmbEstado.getSelectionModel().isEmpty() ||
                cmbMunicipio.getSelectionModel().isEmpty() ||
                cmbEmpleo.getSelectionModel().isEmpty() ||
                cmbEstadoCivil.getSelectionModel().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("DATOS INCOMPLETOS");
            alert.setHeaderText("FALTAN DATOS");
            alert.setContentText("TODOS LOS CAMPOS SON OBLIGATORIOS.");
            alert.showAndWait();
            return;
        }



        if (curp.length() != 18) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("CURP INCORRECTO");
            alert.setHeaderText("CURP INVÁLIDO");
            alert.setContentText("EL CURP DEBE TENER EXACTAMENTE 18 CARACTERES.");
            alert.showAndWait();
            return;
        }



        int edad = Period.between(fNacimiento, LocalDate.now()).getYears();
        int tipo = edad < 18 ? 2 : 1;


        if (edad >= 18) {
            if (rfc.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("RFC REQUERIDO");
                alert.setHeaderText("RFC OBLIGATORIO");
                alert.setContentText("LOS MAYORES DE EDAD DEBEN TENER RFC.");
                alert.showAndWait();
                return;
            }

            if (rfc.length() != 13) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("RFC INCORRECTO");
                alert.setHeaderText("RFC INVÁLIDO");
                alert.setContentText("EL RFC DEBE TENER EXACTAMENTE 13 CARACTERES.");
                alert.showAndWait();
                return;
            }
        }

        String genero = cmbGenero.getSelectionModel().getSelectedItem().toString();
        String res = servicio.insertarSocio(
                nombre, ApellidoP, ApellidoM, fNacimiento, curp,
                idEmpleo, direccion, idEstado, idMunicipio,
                tipo, rfc, idEstadoCivil, telefono, genero,
                LoginController.usuarioLoggeado
        );

        if (res.equalsIgnoreCase("CORRECTO")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("INSERCIÓN EXITOSA");
            alert.setHeaderText("INSERCIÓN EXITOSA");
            alert.setContentText("EL SOCIO SE HA INSERTADO CORRECTAMENTE.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INSERTAR AL SOCIO");
            alert.setContentText(res.toUpperCase());
            alert.showAndWait();
        }



    }



}
