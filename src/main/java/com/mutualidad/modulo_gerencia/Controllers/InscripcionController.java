package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelEstado;
import com.mutualidad.modulo_gerencia.Models.ModelEstadoCivil;
import com.mutualidad.modulo_gerencia.Models.ModelMunicipio;
import com.mutualidad.modulo_gerencia.Models.ModelTrabajo;
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

    int yucatan = 0;
    String yuc = "";
    String uman = "";

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
        List<ModelEstado> estados = servicio.traerEstados();
        yucatan = 0;

        for (ModelEstado fila : estados) {
            cmbEstado.getItems().add(fila.getEstado());
            if (fila.getEstado().equalsIgnoreCase("Yucatán")) {
                yucatan = fila.getId();
                yuc =fila.getEstado();
            }
        }
        if (!estados.isEmpty()) {
            cmbEstado.getSelectionModel().select(yuc);
        }
        dteNacimiento.setValue(LocalDate.now());



        List<ModelMunicipio> municipios = servicio.traeMunicipios(yucatan);
        cmbMunicipio.getItems().clear();
        for (ModelMunicipio fila : municipios) {
            cmbMunicipio.getItems().add(fila.getMunicipio());
            if (fila.getMunicipio().equalsIgnoreCase("Umán")) {
                uman = fila.getMunicipio();
            }
        }
        if (!municipios.isEmpty()) {
            cmbMunicipio.getSelectionModel().select(uman);
        }


        List<ModelTrabajo> empleos = servicio.traerEmpleos();
        cmbEmpleo.getItems().clear();
        for (ModelTrabajo fila : empleos) {
            cmbEmpleo.getItems().add(fila.getTrabajo());
        }
        if (!empleos.isEmpty()) {
            cmbEmpleo.getSelectionModel().selectFirst();
        }

        List<ModelEstadoCivil> estadosC = servicio.traerEstadosC();
        cmbEstadoCivil.getItems().clear();
        for (ModelEstadoCivil fila : estadosC) {
            cmbEstadoCivil.getItems().add(fila.getEstadoCivil());
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

        String nomEstado = cmbEstado.getSelectionModel().getSelectedItem().toString();
        int idEstado =servicio.traerIdEstadoConEstado(nomEstado);
        List<ModelMunicipio> municipios = servicio.traeMunicipios(idEstado);
        cmbMunicipio.getItems().clear();
        for (ModelMunicipio fila : municipios) {
            cmbMunicipio.getItems().add(fila.getMunicipio());
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
        cmbEstado.getSelectionModel().select(yuc);
        cmbMunicipio.getSelectionModel().select(uman);
        cmbEstadoCivil.getSelectionModel().selectFirst();

    }

    @FXML
    public void registrarSocio(){

        String nombre = txtNombre.getText().trim();
        String ApellidoM = txtApellidoM.getText().trim();
        String ApellidoP = txtApellidoP.getText().trim();
        LocalDate fNacimiento = dteNacimiento.getValue();
        String curp = txtCURP.getText().trim();
        String rfc = txtRFC.getText().trim();
        int idEstado = servicio.traerIdEstadoConEstado(cmbEstado.getSelectionModel().getSelectedItem().toString());
        int idMunicipio = servicio.traerIdMunicipioConMunicipio(cmbMunicipio.getSelectionModel().getSelectedItem().toString());
        int idEmpleo = servicio.traerIdConEmpleos(cmbEmpleo.getSelectionModel().getSelectedItem().toString());
        int idEstadoCivil = servicio.traerIdConEstadosCivil(cmbEstadoCivil.getSelectionModel().getSelectedItem().toString());

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

        limpiar();

    }



}
