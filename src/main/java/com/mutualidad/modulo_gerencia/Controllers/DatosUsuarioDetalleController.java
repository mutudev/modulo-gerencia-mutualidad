package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.DTO.DetalleUsuarioDTO;
import com.mutualidad.modulo_gerencia.Models.ModelEmpleado;
import com.mutualidad.modulo_gerencia.Models.ModelPuesto;
import com.mutualidad.modulo_gerencia.Models.ModelRol;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;



@Component
public class DatosUsuarioDetalleController implements Initializable {

    @Autowired
    public Servicio servicio;


    @FXML
    private TextField txtNombre, txtApellidoP, txtApellidoM, txtTelefono, txtUsuario;


    @FXML
    private DatePicker dteNacimiento;

    @FXML
    private ComboBox cmbPuesto, cmbRol;

    @FXML
    private Button btnGuardarCambios, btnCerrar;
    private int idUsuarioActual;

    public void settearDatos(int idUsuario){
        this.idUsuarioActual = idUsuario;

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

        DetalleUsuarioDTO usuario = servicio.traerDetalleUsuarioPorId(idUsuario);

        txtUsuario.setText(usuario.getUsuario());

        cmbRol.getSelectionModel().select(usuario.getRol());
        System.out.println(usuario.getEmpleadoSoloNombre());
        txtNombre.setText(usuario.getEmpleadoSoloNombre());
        dteNacimiento.setValue(usuario.getFechaNacimiento());
        txtTelefono.setText(usuario.getTelefono());
        txtApellidoP.setText(usuario.getApellidoPaterno());
        txtApellidoM.setText(usuario.getApellidoMaterno());
        cmbPuesto.getSelectionModel().select(usuario.getPuesto());

    }


    @FXML
    public void cerrarModal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CIERRE");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA CERRAR LA VENTANA?");
        alert.setContentText(
                "LAS MODIFICACIONES NO GUARDADAS SE PERDERÁN");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            txtNombre.clear();
            txtTelefono.clear();
            txtApellidoP.clear();
            txtApellidoM.clear();
            Stage ventanaActual = (Stage) txtUsuario.getScene().getWindow();
            ventanaActual.close();
        }
    }


    @FXML
    public void guardar() {
            if (txtNombre.getText().isEmpty() ||
                    txtApellidoP.getText().isEmpty() ||
                    txtTelefono.getText().isEmpty() ||
                    dteNacimiento.getValue() == null) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("VALIDACIÓN");
                alert.setHeaderText("CAMPOS OBLIGATORIOS");
                alert.setContentText("Por favor complete los campos requeridos.".toUpperCase());
                alert.showAndWait();
                return;
            }

             ModelUsuario usuario = servicio.traerUsuarioXId(idUsuarioActual);


            ModelEmpleado empleado = servicio.traerEmpleadoXId(usuario.getIdEmpleado());

            // Obtener valores del formulario
            String nombre = txtNombre.getText();
            String apellidoP = txtApellidoP.getText();
            String apellidoM = txtApellidoM.getText();
            String telefono = txtTelefono.getText();
            String usuarioModificador = LoginController.usuarioLoggeado;
            var fechaNacimiento = dteNacimiento.getValue();
            int codRol = cmbRol.getSelectionModel().getSelectedIndex() + 1;
            int codPuesto = cmbPuesto.getSelectionModel().getSelectedIndex() + 1;


            String resultado = servicio.editarUsuario(
                    nombre,
                    apellidoP,
                    apellidoM,
                    fechaNacimiento,
                    codPuesto,
                    codRol,
                       idUsuarioActual ,
                        empleado.getId(),
                    telefono,
                    usuarioModificador
            );

            if ("Correcto".equalsIgnoreCase(resultado)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("CORRECTO");
                alert.setHeaderText("DATOS ACTUALIZADOS");
                alert.setContentText("El usuario se actualizó correctamente.".toUpperCase());
                alert.showAndWait();

                // cerrar ventana
                Stage ventanaActual = (Stage) txtUsuario.getScene().getWindow();
                ventanaActual.close();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL MODIFICAR AL USUARIO");
                alert.setContentText(resultado.toUpperCase());
                alert.showAndWait();
                return;
            }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(
                () -> {
                    Stage stage = (Stage) txtUsuario.getScene().getWindow();
                    stage.setOnCloseRequest(event -> cierreDeVentana(event));
                });
    }

    public void cierreDeVentana(Event event) {
        event.consume();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CIERRE");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA CERRAR LA VENTANA?");
        alert.setContentText(
                "LAS MODIFICACIONES NO GUARDADAS SE PERDERÁN");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage ventanaActual = (Stage) txtUsuario.getScene().getWindow();
            ventanaActual.close();
        }
    }
}
