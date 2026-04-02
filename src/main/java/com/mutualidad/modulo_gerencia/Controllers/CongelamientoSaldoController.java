package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelAhorro;
import com.mutualidad.modulo_gerencia.Models.ModelCapitalSocial;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Repository.SocioRepository;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import io.github.cdimascio.dotenv.Dotenv;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class CongelamientoSaldoController implements Initializable {


    @FXML
    private Label lblNombre, lblEmpresa, lblAhorro, lblPsocMut, lblPsocNgu, lblCongelar, lblSaldoRestante, lblDesconge;

    @FXML
    private TextField txtNombre,txtNumero, txtEmpresa, txtAhorro, txtPsocNgu, txtPsocMut, txtSaldoRestante,
            txtCongelar;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Button btnBuscar, btnLimpiar, btnCongelar, btnLimpiar2;

    @Autowired
    private Servicio servicio;



    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Dotenv dotenv = Dotenv.load();

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
        if (socio != null) {

            txtNumero.setEditable(false);
            ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(socio.getNumSocio(), 1);

            if(ahorro == null){

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("CUENTA DE AHORRO INEXISTENTE");
                alert.setContentText("EL SOCIO QUE INTENTA BUSCAR NO TIENEN UNA CUENTA DE AHORRO ACTIVA");
                alert.showAndWait();

                txtNumero.clear();
                txtNumero.setEditable(true);
                return;
            }


            txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
            List<ModelCapitalSocial> cs = servicio.traerCuentasCs(socio.getNumSocio());

            if (cs != null) {
                for (ModelCapitalSocial cuenta : cs) {

                    if(cuenta.getEmpresa_cod().equalsIgnoreCase("0001")){
                        txtPsocMut.setText(formatoMXN.format(cuenta.getMonto_cubierto()));

                    }else{
                        txtPsocNgu.setText(formatoMXN.format(cuenta.getMonto_cubierto()));
                    }
                }
            }

            if(txtPsocNgu.getText().isEmpty()){
                txtPsocNgu.setText(formatoMXN.format(0.00));
            }
            if(txtPsocMut.getText().isEmpty()){
                txtPsocMut.setText(formatoMXN.format(0.00));
            }


            if (ahorro.getSaldo_congelado() != 0) {
                txtAhorro.setText(formatoMXN.format(ahorro.getSaldo()) + " - CONGELADO: " + formatoMXN.format(ahorro.getSaldo_congelado()));
                btnCongelar.setText("Descongelar Saldo");
                btnCongelar.setStyle("-fx-background-color: #39577c; -fx-text-fill: white;");
                lblDesconge.setVisible(true);
                lblCongelar.setVisible(false);
            } else {
                txtAhorro.setText(formatoMXN.format(ahorro.getSaldo()));
                btnCongelar.setText("Congelar Saldo");
                btnCongelar.setStyle("-fx-background-color: #185754; -fx-text-fill: white;");
                lblDesconge.setVisible(false);
                lblCongelar.setVisible(true);
            }




            if(socio.getEmpresaCod().equalsIgnoreCase("0001")){
                txtEmpresa.setText("MUTUALIDAD DOCE DE AGOSTO S.C. DE R.L. DE C.V.");
            }else {
                txtEmpresa.setText("NUEVA GENERACION DE UMAN, AC.");
            }


            txtNombre.setVisible(true);
            txtPsocMut.setVisible(true);
            txtPsocNgu.setVisible(true);
            txtEmpresa.setVisible(true);
            txtNumero.setEditable(false);
            txtAhorro.setVisible(true);
            txtCongelar.setVisible(true);
            txtSaldoRestante.setVisible(true);
            imgBusqueda.setVisible(false);

            lblNombre.setVisible(true);
            lblPsocMut.setVisible(true);
            lblPsocNgu.setVisible(true);
            lblAhorro.setVisible(true);
            lblEmpresa.setVisible(true);
            lblSaldoRestante.setVisible(true);

            btnCongelar.setVisible(true);
            btnLimpiar2.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SOCIO NO ENCONTRADO");
            alert.setHeaderText("SOCIO NO ENCONTRADO");
            alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
            alert.showAndWait();
            return;
        }

    }


    @FXML
    public void congelarSaldo(){
        if(txtSaldoRestante.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER CONGELAR SALDO");
            alert.setContentText("INGRESE EL MONTO A CONGELAR");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ACTUALIZACIÓN DE SALDO");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);

        double montoRestante = parseMoneda(txtSaldoRestante.getText());
        double montoCongelar = parseMoneda(txtCongelar.getText());
        int usuarioId = servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado).getId();
        ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(socio.getNumSocio(), 1);

        String res = "";

        if (ahorro.getSaldo_congelado() != 0) {
            //Descongelamos
            res = servicio.congelarSaldo(usuarioId,Integer.parseInt(txtNumero.getText()), socio.getEmpresaCod(),  montoCongelar, montoRestante,2, "" );
        } else {
            //Congelamos
            res = servicio.congelarSaldo(usuarioId,Integer.parseInt(txtNumero.getText()), socio.getEmpresaCod(),  montoCongelar, montoRestante,1,  "" );
        }

        if (res.equalsIgnoreCase("CORRECTO")) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CORRECTO");
            alert.setHeaderText((btnCongelar.getText().equalsIgnoreCase("Descongelar Saldo") ? "DESCONGELAMIENTO" : "CONGELAMIENTO") + " EXITOSO");
            alert.setContentText("SE LE HA " + (btnCongelar.getText().equalsIgnoreCase("Descongelar Saldo") ? "DESCONGELADO " : "CONGELADO ") +
                    txtCongelar.getText() + " AL SOCIO " + txtNumero.getText() + " CORRECTAMENTE");
            alert.showAndWait();
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL REALIZAR LA OPERACIÓN");
            alert.setContentText(res.toUpperCase());
            alert.showAndWait();
        }

        limpiar();

    }


    @FXML
    public void formatearMonto (){
        if(txtCongelar.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR ");
            alert.setContentText(
                    "POR FAVOR, ESCRIBA EL MONTO A CONGELAR");
            alert.showAndWait();
            return;
        }

        if(Double.parseDouble(txtCongelar.getText()) <= 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR ");
            alert.setContentText(
                    "POR FAVOR, DIGITE UN MONTO MAYOR A CERO");
            alert.showAndWait();
            return;
        }

        double monto = Double.parseDouble(txtCongelar.getText());


        ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(Integer.parseInt(txtNumero.getText()), 1);

        double montoRestante = 0;

        if (ahorro.getSaldo_congelado() != 0) {
            //Vamos a descongelar es decir, sumar  al monto de ahorro

            //Que el monto a descongelar no supere el monto que ya hay congelado
            if (monto > ahorro.getSaldo_congelado()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR");
                alert.setContentText(
                        "EL SALDO A DESCONGELAR NO DEBE SER MAYOR AL CONGELADO.");
                alert.showAndWait();
                return;
            }

            montoRestante = ahorro.getSaldo() + monto;


        } else {
            //Vamos a congelar
            if(ahorro.getSaldo() <=0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR");
                alert.setContentText(
                        "EL SOCIO NO TIENE SALDO PARA CONGELAR");
                alert.showAndWait();
                return;
            }

            if(ahorro.getSaldo() < monto){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR");
                alert.setContentText(
                        "EL MONTO A CONGELAR ES MAYOR AL SALDO DEL AHORRO");
                alert.showAndWait();
                return;
            }

            montoRestante = ahorro.getSaldo() - monto;


        }

        txtCongelar.setTextFormatter(null);
        txtCongelar.clear();
        txtCongelar.setText(formatoMXN.format(monto));
        txtSaldoRestante.setText(formatoMXN.format(montoRestante));
        txtCongelar.setEditable(false);








    }

    @FXML
    public void limpiar(){
        txtNombre.setVisible(false);
        txtPsocMut.setVisible(false);
        txtPsocNgu.setVisible(false);
        txtEmpresa.setVisible(false);
        txtNumero.setEditable(true);
        txtAhorro.setVisible(false);
        txtCongelar.setVisible(false);
        txtSaldoRestante.setVisible(false);
        txtSaldoRestante.clear();
        txtCongelar.clear();
        txtCongelar.setEditable(true);
        txtCongelar.setTextFormatter(
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
        imgBusqueda.setVisible(true);

        lblNombre.setVisible(false);
        lblPsocMut.setVisible(false);
        lblPsocNgu.setVisible(false);
        lblAhorro.setVisible(false);
        lblEmpresa.setVisible(false);
        lblCongelar.setVisible(false);
        lblDesconge.setVisible(false);
        lblSaldoRestante.setVisible(false);

        txtNumero.setEditable(true);
        txtNumero.clear();
        btnLimpiar2.setVisible(false);
        btnCongelar.setVisible(false);

    }

    @FXML
    public void limpiarMontos(){
        txtSaldoRestante.clear();
        txtCongelar.clear();
        txtCongelar.setEditable(true);
        txtCongelar.setTextFormatter(
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

        txtCongelar.setTextFormatter(
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
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setCongelamientoSaldoController(this);
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
