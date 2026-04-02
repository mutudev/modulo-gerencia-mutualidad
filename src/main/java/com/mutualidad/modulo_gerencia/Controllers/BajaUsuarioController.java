package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelEmpleado;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

@Component
public class BajaUsuarioController implements Initializable {

    @FXML
    public Button btnBuscar, btnLimpiar, btnEstado;

    @FXML
    public TextField txtNombre, txtUsuario, txtRol, txtPuesto;

    @FXML
    public Label lblNombre, lblRol, lblPuesto;

    @Autowired
    Servicio servicio;

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
            txtPuesto.setText(servicio.traerNombrePuesto(empleado.getPuesto()));
            txtRol.setText(servicio.traerNombreRol(usuario.getRol()));


            if(!usuario.isStatus()){
                btnEstado.setText("Reactivar");
                btnEstado.setStyle("-fx-background-color: #39577c; -fx-text-fill: white;");
            }
            btnEstado.setVisible(true);
            txtNombre.setVisible(true);
            txtUsuario.setVisible(true);
            txtRol.setVisible(true);
            txtPuesto.setVisible(true);
            lblNombre.setVisible(true);
            lblRol.setVisible(true);
            lblPuesto.setVisible(true);
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR BUSCAR AL USUARIO");
            alert.setContentText(
                    "POR FAVOR, RELLENE TODOS LOS CAMPOS");
            alert.showAndWait();
            return;
        }
    }


    @FXML
    public void limpiar(){
        btnEstado.setVisible(false);

        txtNombre.setVisible(false);
        txtRol.setVisible(false);
        txtPuesto.setVisible(false);

        lblNombre.setVisible(false);
        lblRol.setVisible(false);
        lblPuesto.setVisible(false);
        txtUsuario.clear();
        txtUsuario.setEditable(true);
    }

    @FXML
    public void cambiarEstado(){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMACIÓN");
        alert.setHeaderText("¿ESTA SEGURO QUE DESEA CAMBIAR EL ESTADO DEL USUARIO?");
        alert.setContentText("PRESIONE ACEPTAR PARA CONTINUAR, DE LO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> resultado = alert.showAndWait();

        ModelUsuario usuario = servicio.traerUsuarioXUsuario(txtUsuario.getText());

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            if (usuario.isStatus()) {
                usuario.setStatus(false);
            } else {
                usuario.setStatus(true);
            }

            servicio.cambiarDatosUsuario(usuario);

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OPERACIÓN EXITOSA");
            alert.setHeaderText(usuario.isStatus() ? "REACTIVACIÓN DE USUARIO" : "DESACTIVACIÓN DE USUARIO");
            alert.setContentText(
                    usuario.isStatus() ? "USUARIO REACTIVADO CON ÉXITO" : "USUARIO DESACTIVADO CON ÉXITO");
            alert.showAndWait();

            limpiar();

        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OPERACIÓN CANCELADA");
            alert.setHeaderText("OPERACIÓN CANCELADA");
            alert.setContentText(
                    "EL CAMBIO DE ESTADO DEL USUARIO HA SIDO CANCELADO");
            alert.showAndWait();
        }
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
