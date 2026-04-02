package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelAhorro;
import com.mutualidad.modulo_gerencia.Models.ModelCapitalSocial;
import com.mutualidad.modulo_gerencia.Models.ModelCredito;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Repository.SocioRepository;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class BloquearSocioController {

    @FXML
    private Label lblNombre, lblTipo, lblSaldoAhorro, lblCredVig, lblSaldoCre, lblNota;

    @FXML
    private TextField txtNombre, txtTipo, txtCuentaAhorro, txtCreditosVig, txtSaldoCre, txtNumero;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Button btnBuscar, btnLimpiar, btnBloquear;

    @FXML
    private TextArea txtAviso;

    @Autowired
    private Servicio servicio;

    @Autowired
    private SocioRepository repoSocio;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    @FXML
    public void buscarSocio() {

        if (txtNumero.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("NÚMERO NO PROPORCIONADO");
            alert.setContentText("POR FAVOR, PROPORCIONE UN NÚMERO DE SOCIO.");
            alert.showAndWait();
            return;
        }

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);


        if (socio == null) {
            socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), false);
            ModelAhorro cuentaAhorroComprobar = servicio.traerCuentaAhorroPorNumSocio(socio.getNumSocio());
            if (cuentaAhorroComprobar.getStatus() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL CARGAR AL SOCIO");
                alert.setContentText("EL SOCIO QUE INTENTA BUSCAR ESTÁ DADO DE BAJA");
                alert.showAndWait();
                return;
            } else {
                btnBloquear.setText("Desbloquear");
                btnBloquear.setStyle("-fx-background-color: #F2E018;");
            }
        }

        ModelAhorro cuentaAhorro = servicio.traerCuentaAhorroPorNumSocio(socio.getNumSocio());
        List<ModelCredito> creditos = servicio.encontrarTodosLosCreditosPorSocio(socio.getNumSocio());

        txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
        txtTipo.setText(servicio.traerTipoSocio(socio.getNumSocio()));

        List<ModelCapitalSocial> cs = servicio.traerCuentasCs(socio.getNumSocio());
        double montoCs = 0;

        if (cs != null) {
            for (ModelCapitalSocial cuenta : cs) {
                montoCs += cuenta.getMonto_cubierto();
            }
        }

        txtCuentaAhorro.setText(formatoMXN.format(cuentaAhorro.getSaldo() + montoCs));


        txtCreditosVig.setText(String.valueOf(creditos.size()));

        double acumulador = 0;

        for (ModelCredito credito : creditos) {

            String saldo = servicio.traerCuotaParaSaldo(credito.getId());

            if (saldo != null && !saldo.isEmpty()) {
                acumulador += Double.parseDouble(saldo);
            } else {
                acumulador += credito.getMonto();
            }
        }

        txtSaldoCre.setText(formatoMXN.format(acumulador));

        btnBloquear.setVisible(true);
        txtSaldoCre.setVisible(true);
        txtNombre.setVisible(true);
        txtCuentaAhorro.setVisible(true);
        txtTipo.setVisible(true);
        txtCreditosVig.setVisible(true);
        txtAviso.setVisible(true);
        txtNumero.setEditable(false);
        imgBusqueda.setVisible(false);

        lblNombre.setVisible(true);
        lblCredVig.setVisible(true);
        lblTipo.setVisible(true);
        lblSaldoAhorro.setVisible(true);
        lblSaldoCre.setVisible(true);
        btnBuscar.setDisable(true);

        if (creditos.size() != 0) {
            lblNota.setVisible(true);
        }


    }

    @FXML
    public void limpiar() {

        txtNombre.clear();
        txtSaldoCre.clear();
        txtNumero.clear();
        txtCreditosVig.clear();
        txtCuentaAhorro.clear();
        txtTipo.clear();

        btnBloquear.setVisible(false);
        txtSaldoCre.setVisible(false);
        txtNombre.setVisible(false);
        txtCuentaAhorro.setVisible(false);
        txtTipo.setVisible(false);
        txtCreditosVig.setVisible(false);
        txtAviso.setVisible(false);
        txtNumero.setEditable(true);
        imgBusqueda.setVisible(true);

        lblNombre.setVisible(false);
        lblCredVig.setVisible(false);
        lblTipo.setVisible(false);
        lblSaldoAhorro.setVisible(false);
        lblSaldoCre.setVisible(false);

        btnBuscar.setDisable(false);
        lblNota.setVisible(false);
    }

    @FXML
    public void bloquearSocio() {

        ModelSocio socio = null;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        boolean bloquear = btnBloquear.getText().equalsIgnoreCase("Bloquear");

        if (bloquear) {
            alert.setTitle("BLOQUEO DE SOCIO");
            alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA BLOQUEAR AL SOCIO?");
            socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);
        } else {
            alert.setTitle("DESBLOQUEO DE SOCIO");
            alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA DESBLOQUEAR AL SOCIO?");
            socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), false);
        }

        alert.setContentText("EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            socio.setStatus(!bloquear);
            socio.setUm(LoginController.usuarioLoggeado);
            socio.setFm(LocalDate.now());

            repoSocio.save(socio);

            if (bloquear) {
                btnBloquear.setText("Desbloquear");
                btnBloquear.setStyle("-fx-background-color: #F2E018;");
            } else {
                btnBloquear.setText("Bloquear");
                btnBloquear.setStyle("-fx-background-color: #bc1414;");
            }

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("OPERACIÓN EXITOSA");

            if (bloquear) {
                exito.setHeaderText("SOCIO BLOQUEADO CORRECTAMENTE");
            } else {
                exito.setHeaderText("SOCIO DESBLOQUEADO CORRECTAMENTE");
            }

            exito.setContentText("LA OPERACIÓN SE REALIZÓ CON ÉXITO.");
            exito.showAndWait();

            limpiar();

        } else {
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
            controlador.setBloquearSocioController(this);
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
