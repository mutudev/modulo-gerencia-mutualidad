package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelCapitalSocial;
import com.mutualidad.modulo_gerencia.Models.ModelEmpresa;
import com.mutualidad.modulo_gerencia.Models.ModelPrevisionSocial;
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class CrearCuentaPSController implements Initializable {

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
            controlador.setCrearCuentaPSController(this);
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
        int numSocio = Integer.parseInt(txtNumero.getText());

        //Verificar si ya tiene una cuenta en la empresa en la que quiere crear
        ModelPrevisionSocial cuenta = servicio.traerCuentaPSPorEmpresaYSocio(numSocio, empresaCod);

        if (cuenta != null) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CREAR LA CUENTA");
            alert.setContentText("EL SOCIO YA CUENTA CON UNA CUENTA DE PREVISIÓN SOCIAL PARA ESTA EMPRESA");
            alert.showAndWait();
            return;
        }

        // SOLO PUEDE HABER UNA CUENTA DE PS POR SOCIO, EL DE MUT SOLO EN MUT Y EL DE NGU SOLO EN NGU
        if(numSocio >= 8543 && empresaCod.equalsIgnoreCase("0001") ){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CREAR LA CUENTA");
            alert.setContentText("LOS SOCIOS DE NGU NO PUEDEN CREAR UNA CUENTA DE PREVISIÓN SOCIAL PARA ESTA EMPRESA");
            alert.showAndWait();
            return;
        }

        if(numSocio < 8543 && empresaCod.equalsIgnoreCase("0002") ){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CREAR LA CUENTA");
            alert.setContentText("LOS SOCIOS DE MUTUALIDAD NO PUEDEN CREAR UNA CUENTA DE PREVISIÓN SOCIAL PARA ESTA EMPRESA");
            alert.showAndWait();
            return;
        }

        ModelPrevisionSocial cuentaNueva = new ModelPrevisionSocial();
        cuentaNueva.setNumSocio(numSocio);
        cuentaNueva.setEmpresaCod(empresaCod);
        cuentaNueva.setPrevision(BigDecimal.valueOf(0));
        cuentaNueva.setMontoAsignado(BigDecimal.valueOf(0));
        cuentaNueva.setFechaPago(null);
        cuentaNueva.setUr(servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado).getId());

        //Guardar la nueva cuenta
        ModelPrevisionSocial cuentaCreada = servicio.crearCuentaPrevisionSocial(cuentaNueva);
        if (cuentaCreada.getId() != 0 && cuentaCreada != null) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("EXITO");
            alert.setHeaderText("EXITO AL CREAR LA CUENTA");
            alert.setContentText("CUENTA DE PREVISIÓN SOCIAL PARA LA EMPRESA " + cmbEmpresa.getSelectionModel().getSelectedItem().toString() +
                    " DEL SOCIO " + numSocio + " CREADA CON ÉXITO");
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

}
