package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelEmpleado;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class CambiarContraController implements Initializable {

    @FXML
    public Button btnBuscar, btnLimpiar, btnCambiarContra;

    @FXML
    public Label lblNombre, lblContra, lblConfContra;

    @FXML
    public TextField txtNombre, txtUsuario;

    @FXML
    public PasswordField txtContra, txtConfContra;

    @Autowired
    public Servicio servicio;

    Argon2 argon2 = Argon2Factory.create();

    @FXML
    public void cargarUsuarios() {
        if (txtUsuario.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR BUSCAR AL USUARIO");
            alert.setContentText(
                    "POR FAVOR, RELLENE TODOS LOS CAMPOS");
            alert.showAndWait();
            return;
        }

        ModelUsuario usuario = servicio.traerUsuarioXUsuario(txtUsuario.getText());
        if(usuario != null){
            txtUsuario.setEditable(false);

            ModelEmpleado empleado = servicio.traerEmpleadoXId(usuario.getIdEmpleado());
            txtNombre.setText(empleado.getNombres() + ' '+ empleado.getApellidoP() + ' '+ empleado.getApellidoM());
            btnCambiarContra.setVisible(true);
            txtNombre.setVisible(true);
            txtUsuario.setVisible(true);
            lblNombre.setVisible(true);
            txtContra.setVisible(true);
            txtConfContra.setVisible(true);
            lblContra.setVisible(true);
            lblConfContra.setVisible(true);

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR BUSCAR AL USUARIO");
            alert.setContentText(
                    "POR FAVOR, RELLENE TODOS LOS CAMPOS");
            alert.showAndWait();

        }
    }

    @FXML
    public void limpiar(){
        btnCambiarContra.setVisible(false);
        txtNombre.setVisible(false);
        txtUsuario.setVisible(false);
        lblNombre.setVisible(false);
        txtContra.setVisible(false);
        txtConfContra.setVisible(false);
        lblContra.setVisible(false);
        lblConfContra.setVisible(false);
        txtUsuario.clear();
        txtUsuario.setEditable(false);

    }

    @FXML
    public void cambiarContraseña() {

        String contra = txtContra.getText().trim();
        String contraConf = txtConfContra.getText().trim();

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

        if (!contraConf.equals(contra)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CAMBIAR LA CONTRASEÑA");
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

        ModelUsuario usuario = servicio.traerUsuarioXUsuario(txtUsuario.getText());
        usuario.setPass(hash);
        servicio.cambiarDatosUsuario(usuario);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("OPERACIÓN ÉXITOSA");
        alert.setHeaderText("CAMBIO ÉXITOSO");
        alert.setContentText("LA CONTRASEÑA DEL USUARIO: " + txtUsuario.getText() + " SE HA CAMBIADO CON ÉXITO.");
        alert.showAndWait();

        limpiar();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtUsuario.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));
    }



}
