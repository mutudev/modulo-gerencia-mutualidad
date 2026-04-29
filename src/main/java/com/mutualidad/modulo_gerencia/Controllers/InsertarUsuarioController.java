package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelPuesto;
import com.mutualidad.modulo_gerencia.Models.ModelRol;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;


@Component
public class InsertarUsuarioController implements Initializable {


    @FXML
    private TextField txtNombre, txtApellidoP, txtApellidoM, txtTelefono, txtUsuario;

    @FXML
    private PasswordField txtContra, txtConfContra;


    @FXML
    private DatePicker dteNacimiento;

    @FXML
    private ComboBox cmbPuesto, cmbRol;

    @FXML
    private Button btnCrear, btnMostrar;

    @Autowired
    private Servicio servicio;

    Argon2 argon2 = Argon2Factory.create();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        List<ModelRol> roles = servicio.traerTodosLosRoles();
        cmbRol.getItems().clear();
        for (ModelRol fila : roles) {
            cmbRol.getItems().add(fila.getRol());
        }

        if (!roles.isEmpty()) {
            cmbRol.getSelectionModel().selectFirst();
        }


        List<ModelPuesto> puestos = servicio.traerTodosLosPuestos();
        cmbPuesto.getItems().clear();
        for (ModelPuesto fila : puestos) {
            cmbPuesto.getItems().add(fila.getDescripcion());
        }
        if (!puestos.isEmpty()) {
            cmbPuesto.getSelectionModel().selectFirst();
        }

        txtNombre.setTextFormatter(
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
        txtApellidoM.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtTelefono.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9]", ""));
                            return change;
                        }));
        txtUsuario.setTextFormatter(
                new TextFormatter<>(change -> {

                    String texto = change.getText().toUpperCase();

                    // Solo permite letras (A-Z)
                    if (!texto.matches("[A-Z]*")) {
                        change.setText("");
                    } else {
                        change.setText(texto);
                    }

                    return change;
                })
        );



    }

    private void limpiarFormulario() {

        // --- TextFields ---
        txtNombre.clear();
        txtApellidoP.clear();
        txtApellidoM.clear();
        txtTelefono.clear();
        txtUsuario.clear();

        // --- PasswordFields ---
        txtContra.clear();
        txtConfContra.clear();

        // --- DatePicker ---
        dteNacimiento.setValue(null); // mejor limpiar completamente

        // --- ComboBox ---
        cmbPuesto.getSelectionModel().clearSelection();
        cmbRol.getSelectionModel().clearSelection();

        // Opcional: volver a seleccionar el primero automáticamente
        if (!cmbPuesto.getItems().isEmpty()) {
            cmbPuesto.getSelectionModel().selectFirst();
        }

        if (!cmbRol.getItems().isEmpty()) {
            cmbRol.getSelectionModel().selectFirst();
        }

        // --- Opcional: enfoque inicial ---
        txtNombre.requestFocus();
    }

    @FXML
    public void crearUsuario(){

        if (!txtNombre.getText().trim().isEmpty() &&
                !txtApellidoP.getText().trim().isEmpty() &&
                !txtApellidoM.getText().trim().isEmpty() &&
                !txtTelefono.getText().trim().isEmpty() &&
                !txtUsuario.getText().trim().isEmpty() &&
                !txtContra.getText().trim().isEmpty() &&
                !txtConfContra.getText().trim().isEmpty()) {

            String nombreUsuario= txtUsuario.getText();
            String nombre = txtNombre.getText().trim();
            String apellidoP = txtApellidoP.getText().trim();
            String apellidoM = txtApellidoM.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String contra = txtContra.getText().trim();
            String contraConf = txtConfContra.getText().trim();
            LocalDate FNacimiento = dteNacimiento.getValue();

            if(nombreUsuario.length() < 4){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL INSERTAR AL USUARIO");
                alert.setContentText("EL NOMBRE DE USUARIO DEBERÁ CONTAR CON AL MENOS 4 CARACTERES.");
                alert.showAndWait();
                return;
            }

            if(telefono.length() > 10){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL INSERTAR AL USUARIO");
                alert.setContentText("EL NOMBRE DE USUARIO DEBERÁ CONTAR CON MÁXIMO 1O CARACTERES.");
                alert.showAndWait();
                return;
            }

            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{4,10}$";

            if (!contra.matches(regex)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR EN CONTRASEÑA");
                alert.setContentText("LA CONTRASEÑA DEBE TENER:\n" +
                        "- ENTRE 4 Y 10 CARACTERES\n" +
                        "- AL MENOS 1 MAYÚSCULA\n" +
                        "- 1 MINÚSCULA\n" +
                        "- 1 NÚMERO\n" +
                        "- 1 CARÁCTER ESPECIAL");
                alert.showAndWait();
                return;
            }


            List<Object[]> NomUsuarios = servicio.traerUsuarios();

            for(Object[] user : NomUsuarios ){
                String nombreU = user[0].toString();
                if(nombreU.equals(nombreUsuario)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("ERROR AL INSERTAR AL USUARIO");
                    alert.setContentText("EL NOMBRE DE USUARIO YA EXISTE");
                    alert.showAndWait();
                    return;
                }
            }

            if(telefono.length() != 10){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL INSERTAR AL USUARIO");
                alert.setContentText("INGRESE UN TELEFONO VALIDO");
                alert.showAndWait();
                return;

            }

            if (!contraConf.equals(contra)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL INSERTAR AL USUARIO");
                alert.setContentText("LAS CONTRASEÑAS NO COINCIDEN");
                alert.showAndWait();
                return;
            }

            String hash = "";
            char[] password = contra.toCharArray();
            try {
                // Hash password
                hash = argon2.hash(10, 65536, 1, password);
            } finally {
                // Wipe confidential data
                argon2.wipeArray(password);
            }


            String res = servicio.crearUsuario(nombre,apellidoP,apellidoM, telefono,nombreUsuario,
                    cmbRol.getSelectionModel().getSelectedIndex() + 1,cmbPuesto.getSelectionModel().getSelectedIndex() + 1,
                    hash, FNacimiento, LoginController.usuarioLoggeado);
            if (res.equalsIgnoreCase("CORRECTO")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("INSERCIÓN EXITOSA");
                alert.setHeaderText("INSERCIÓN EXITOSA");
                alert.setContentText("EL USUARIO SE HA CREADO CORRECTAMENTE.");
                alert.showAndWait();
                limpiarFormulario();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL CREAR AL USUARIO");
                alert.setContentText(res.toUpperCase());
                alert.showAndWait();
                return;
            }

            limpiarFormulario();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INSERTAR AL USUARIO");
            alert.setContentText("POR FAVOR LLENE TODOS LOS CAMPOS");
            alert.showAndWait();
            return;
        }




    }


}
