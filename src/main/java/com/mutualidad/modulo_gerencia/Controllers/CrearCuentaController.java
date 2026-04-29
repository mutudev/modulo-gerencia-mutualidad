package com.mutualidad.modulo_gerencia.Controllers;


import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelAhorro;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




@Component
public class CrearCuentaController {

    @FXML
    private TextField txtNumero, txtNombre;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Label lblNombre;


    @FXML
    private Button btnCrear, btnBuscar, btnLimpiar;



    @Autowired
    private Servicio servicio;


    @FXML
    public void buscarSocio() {

            int numsocio = 0;
            if (!txtNumero.getText().isEmpty()) {
                numsocio = Integer.parseInt(txtNumero.getText());

                ModelSocio socio = servicio.traerSocioPorNumeroYEstado(numsocio, true);

                if (socio != null) {
                    txtNombre.setText( socio.getNombres() + " " +
                            socio.getApellidoP() + " " +  socio.getApellidoM() + " " );
                 lblNombre.setVisible(true);
                 txtNombre.setVisible(true);
                 btnCrear.setVisible(true);
                 txtNumero.setEditable(false);
                 imgBusqueda.setVisible(false);

                }else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("SOCIO NO ENCONTRADO");
                    alert.setHeaderText("SOCIO NO ENCONTRADO");
                    alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
                    alert.showAndWait();
                    return;
                }
            }
    }

    @FXML
    public void limpiar() {
        txtNumero.setEditable(true);
        lblNombre.setVisible(false);
        txtNombre.setVisible(false);
        btnCrear.setVisible(false);
        imgBusqueda.setVisible(true);
        txtNumero.clear();
    }

    @FXML
    public void crearCuenta(){

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);
        int numsocio = Integer.parseInt(txtNumero.getText());
        ModelAhorro cuentaAhorroComprobar = servicio.traerCuentaAhorroPorNumSocio(socio.getNumSocio());
        if (cuentaAhorroComprobar == null) {
            String numCuenta = "";
            if(numsocio >=8543) {
                numCuenta = "0020018" + String.valueOf(numsocio);
            }else{
                numCuenta = "0010012" + String.valueOf(numsocio);
            }

            String res = servicio.insertarCuentaAhorro(numsocio, numCuenta, "");

            if (res.equalsIgnoreCase("CORRECTO")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("INSERCIÓN EXITOSA");
                alert.setHeaderText("INSERCIÓN EXITOSA");
                alert.setContentText("LA CUENTA DE AHORRO SE HA INSERTADO CORRECTAMENTE.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL INSERTAR LA CUENTA DE AHORRO");
                alert.setContentText(res.toUpperCase());
                alert.showAndWait();
            }

            limpiar();


        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INSERTAR CUENTA DE AHORRO");
            alert.setContentText("EL SOCIO YA CUENTA CON UNA CUENTA DE AHORRO");
            alert.showAndWait();

            limpiar();

            return;
        }

    }

    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setCrearCuentaController(this);
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
        buscarSocio();
    }



}
