package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.*;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


@Component
public class RetiroController implements Initializable {

    @FXML
    private Label lblNombre, lblEmpresa, lblAhorros, lblPSNgu, lblPSMut, lblDatos, lblMonto, lblRestante;

    @FXML
    private TextField txtNombre, txtEmpresa, txtAhorros, txtPSNgu, txtPSMut, txtMonto, txtRestante, txtNumero;

    @FXML
    private Button btnLimpiarMonto, btnProcesar;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Separator separador;

    @Autowired
    private Servicio servicio;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtNumero.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9]", ""));

                            // Verifica si ya hay más de un punto decimal
                            if (change.getText().matches(".*\\..*\\..*")) {
                                change.setText(change.getText().substring(0, change.getText().lastIndexOf('.')));
                            }

                            return change;
                        }));

        txtMonto.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9.]", ""));

                            // Verifica si ya hay más de un punto decimal
                            if (change.getText().matches(".*\\..*\\..*")) {
                                change.setText(change.getText().substring(0, change.getText().lastIndexOf('.')));
                            }

                            return change;
                        }));
    }

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

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText().trim()), true);

        if (socio == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SOCIO NO ENCONTRADO");
            alert.setHeaderText("SOCIO NO ENCONTRADO");
            alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
            alert.showAndWait();
            return;
        }

        ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(Integer.parseInt(txtNumero.getText().trim()), 1);

        if (ahorro == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("CUENTA DE AHORRO INEXISTENTE");
            alert.setContentText("EL SOCIO QUE INTENTA BUSCAR NO TIENEN UNA CUENTA DE AHORRO ACTIVA");
            alert.showAndWait();
            return;
        }

        txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
        List<ModelCapitalSocial> cs = servicio.traerCuentasCs(socio.getNumSocio());

        if (cs != null) {
            for (ModelCapitalSocial cuenta : cs) {
                if(cuenta.getEmpresa_cod().equalsIgnoreCase("0001")){
                    txtPSMut.setText(formatoMXN.format(cuenta.getMonto_cubierto()));
                }else{
                    txtPSNgu.setText(formatoMXN.format(cuenta.getMonto_cubierto()));
                }
            }
        }

        if(txtPSNgu.getText().isEmpty()){
            txtPSNgu.setText(formatoMXN.format(0.00));
        }

        if(txtPSMut.getText().isEmpty()){
            txtPSMut.setText(formatoMXN.format(0.00));
        }

        txtAhorros.setText(formatoMXN.format(ahorro.getSaldo()));

        if(socio.getEmpresaCod().equalsIgnoreCase("0001")){
            txtEmpresa.setText("MUTUALIDAD DOCE DE AGOSTO S.C. DE R.L. DE C.V.");
        }else {
            txtEmpresa.setText("NUEVA GENERACION DE UMAN, AC.");
        }

        txtNombre.setVisible(true);
        txtPSMut.setVisible(true);
        txtPSNgu.setVisible(true);
        txtEmpresa.setVisible(true);
        txtNumero.setEditable(false);
        txtAhorros.setVisible(true);
        txtMonto.setVisible(true);
        btnProcesar.setVisible(true);
        txtRestante.setVisible(true);
        imgBusqueda.setVisible(false);

        lblNombre.setVisible(true);
        lblPSMut.setVisible(true);
        lblPSNgu.setVisible(true);
        lblAhorros.setVisible(true);
        lblMonto.setVisible(true);
        lblEmpresa.setVisible(true);
        lblRestante.setVisible(true);
        btnLimpiarMonto.setVisible(true);
        separador.setVisible(true);
        lblDatos.setVisible(true);
    }

    @FXML
    public void limpiar() {
        txtNombre.setVisible(false);
        txtNombre.clear();

        txtPSMut.setVisible(false);
        txtPSMut.clear();

        txtPSNgu.setVisible(false);
        txtPSNgu.clear();

        txtEmpresa.setVisible(false);
        txtEmpresa.clear();

        txtNumero.setEditable(true);
        txtNumero.clear();

        txtAhorros.setVisible(false);
        txtAhorros.clear();

        txtMonto.setVisible(false);
        txtMonto.clear();

        btnProcesar.setVisible(false);

        txtRestante.setVisible(false);
        txtRestante.clear();

        imgBusqueda.setVisible(true);

        lblNombre.setVisible(false);
        lblPSMut.setVisible(false);
        lblPSNgu.setVisible(false);
        lblAhorros.setVisible(false);
        lblMonto.setVisible(false);
        txtMonto.setEditable(true);
        lblEmpresa.setVisible(false);
        lblRestante.setVisible(false);

        btnLimpiarMonto.setVisible(false);
        separador.setVisible(false);
        lblDatos.setVisible(false);
    }

    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setRetiroController(this);
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
    public void procesarRetiro() {
        if(txtRestante.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER RETIRAR SALDO");
            alert.setContentText("INGRESE EL MONTO A RETIRAR");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("RETIRO DE SALDO");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        //Chequear si no ya tiene retiros pendientes
        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);

        List<ModelRetiro> retirosPendientes = servicio.obtenerRetirosPendientes(socio.getNumSocio(), true);

        if (retirosPendientes.size() > 0) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER RETIRAR SALDO");
            alert.setContentText("EL SOCIO TIENE RETIROS PENDIENTES SIN PROCESAR.");
            alert.showAndWait();

            limpiar();

            return;
        }

        //Construir el retiro y en el servicio se construye el retiro del cajero
        ModelRetiro retiro = new ModelRetiro();
        retiro.setSocio(socio.getNumSocio());
        retiro.setSaldoAnt(BigDecimal.valueOf(parseMoneda(txtAhorros.getText().trim())));
        retiro.setSaldoNue(BigDecimal.valueOf(parseMoneda(txtRestante.getText().trim())));
        retiro.setMontoRetiro(BigDecimal.valueOf(parseMoneda(txtMonto.getText().trim())));
        retiro.setEstado(true);
        ModelUsuario usuario = servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado);
        retiro.setUsuarioId(usuario.getId());
        retiro.setEmpresa(socio.getEmpresaCod());
        retiro.setFr(LocalDate.now());

        //guardamos aprende ramitos gaysito mariconsito
        servicio.realizarRetiroAhorros(retiro);

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CORRECTO");
        alert.setHeaderText("RETIRO EXITOSO");
        alert.setContentText("RETIRO POR: " + txtMonto.getText() + " REALIZADO CORRECTAMENTE.");
        alert.showAndWait();

        limpiar();

    }

    @FXML
    public void calcularMontos() {

        if(txtMonto.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR ");
            alert.setContentText(
                    "POR FAVOR, ESCRIBA EL MONTO A RETIRAR");
            alert.showAndWait();
            return;
        }

        if(Double.parseDouble(txtMonto.getText()) <= 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR ");
            alert.setContentText(
                    "POR FAVOR, DIGITE UN MONTO MAYOR A CERO");
            alert.showAndWait();
            return;
        }

        double monto = Double.parseDouble(txtMonto.getText());

        ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(Integer.parseInt(txtNumero.getText()), 1);

        double montoRestante = 0;

        if(ahorro.getSaldo() <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR");
            alert.setContentText(
                    "EL SOCIO NO TIENE SALDO PARA RETIRAR");
            alert.showAndWait();
            return;
        }

        if(ahorro.getSaldo() < monto) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR");
            alert.setContentText(
                    "EL MONTO A RETIRAR ES MAYOR AL SALDO DEL AHORRO");
            alert.showAndWait();
            return;
        }

        montoRestante = ahorro.getSaldo() - monto;

        txtMonto.setTextFormatter(null);
        txtMonto.clear();
        txtMonto.setText(formatoMXN.format(monto));
        txtRestante.setText(formatoMXN.format(montoRestante));
        txtMonto.setEditable(false);
    }

    @FXML
    public void limpiarMontos(){
        txtRestante.clear();
        txtMonto.clear();
        txtMonto.setEditable(true);
        txtMonto.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9.]", ""));

                            // Verifica si ya hay más de un punto decimal
                            if (change.getText().matches(".*\\..*\\..*")) {
                                change.setText(change.getText().substring(0, change.getText().lastIndexOf('.')));
                            }

                            return change;
                        }));
    }

    private double parseMoneda(String moneda) {
        try {
            Number numero = formatoMXN.parse(moneda);
            return numero.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void cargarSocioPorNombre(String numero) {
        txtNumero.setText(numero);
        buscarSocio();
    }


}
