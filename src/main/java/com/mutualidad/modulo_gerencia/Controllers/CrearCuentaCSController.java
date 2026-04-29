package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelAhorro;
import com.mutualidad.modulo_gerencia.Models.ModelCapitalSocial;
import com.mutualidad.modulo_gerencia.Models.ModelEmpresa;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class CrearCuentaCSController implements Initializable {

    @FXML
    private TextField txtNumero, txtNombre;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Label lblNombre, lblEmpresa;

    @FXML
    private Button btnCrear;

    @FXML
    private ComboBox cmbEmpresa;

    @Autowired
    private Servicio servicio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<ModelEmpresa> empresas = servicio.traerEmpresas();

        for(ModelEmpresa e : empresas) {
            cmbEmpresa.getItems().add(e.getNombre());
        }

        cmbEmpresa.getSelectionModel().selectFirst();

    }


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
                lblEmpresa.setVisible(true);
                cmbEmpresa.setVisible(true);

            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("SOCIO NO ENCONTRADO");
                alert.setHeaderText("SOCIO NO ENCONTRADO");
                alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
                alert.showAndWait();

            }
        }
    }


    @FXML
    public void limpiar() {
        txtNumero.setEditable(true);
        lblNombre.setVisible(false);
        txtNombre.setVisible(false);
        btnCrear.setVisible(false);
        cmbEmpresa.setVisible(false);
        imgBusqueda.setVisible(true);
        lblEmpresa.setVisible(false);
        txtNombre.clear();
        txtNumero.clear();
    }



    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setcrearCSController(this);
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


    @FXML
    public void crearCuenta(){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMACIÓN");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA CREAR LA CUENTA?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
        }

        String empresaCod = servicio.traerEmpresaPorNombre(cmbEmpresa.getSelectionModel().getSelectedItem().toString()).getCodigo();
        ModelCapitalSocial cs = servicio.traerCuentaCsXNumeroYEmpresa(Integer.parseInt(txtNumero.getText().trim()), empresaCod);
        if(cs != null){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CREAR LA CUENTA");
            alert.setContentText("EL SOCIO YA CUENTA CON UNA CUENTA DE CAPITAL SOCIAL PARA ESTA EMPRESA");
            alert.showAndWait();
            return;
        }


        if(Integer.parseInt(txtNumero.getText().trim()) >= 8543 && empresaCod.equalsIgnoreCase("0001") ){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CREAR LA CUENTA");
            alert.setContentText("LOS SOCIOS DE NGU NO PUEDEN CREAR UNA CUENTA DE CAPITAL SOCIAL PARA ESTA EMPRESA");
            alert.showAndWait();
            return;
        }


        ModelCapitalSocial cuentaNueva = new ModelCapitalSocial();
        cuentaNueva.setNumSocio(Integer.parseInt(txtNumero.getText().trim()));
        cuentaNueva.setEmpresaCod(empresaCod);
        cuentaNueva.setMonto_cubierto(0);
        cuentaNueva.setFc(servicio.traerFechaHoy());
        cuentaNueva.setFp(null);
        cuentaNueva.setUc(servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado).getId());

        ModelCapitalSocial cuentaCreada = servicio.crearCuentaCs(cuentaNueva);
        if(cuentaCreada.getId() != 0 && cuentaCreada != null){
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("EXITO");
            alert.setHeaderText("EXITO AL CREAR LA CUENTA");
            alert.setContentText("CUENTA PARA LA EMPRESA " + cmbEmpresa.getSelectionModel().getSelectedItem().toString() +
                    " DEL SOCIO " + txtNumero.getText().trim() + " CREADA CON ÉXITO");
            alert.showAndWait();

        }else{
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CREAR LA CUENTA");
            alert.setContentText("ERROR AL CREAR LA CUENTA");
            alert.showAndWait();

        }
        limpiar();
    }

    public void cargarSocioPorNombre(String numero) {
        txtNumero.setText(numero);
        buscarSocio();
    }



}
