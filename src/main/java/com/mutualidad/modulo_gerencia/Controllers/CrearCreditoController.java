package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelTipoCredito;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component

public class CrearCreditoController implements Initializable {

    @FXML
    private TextField txtMonto, txtNombre, txtCodigo;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private CheckBox chkBonif, chkIva;

    @FXML
    private Button btnLimpiar, btnCrear;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    @Autowired
    private Servicio servicio;


    @FXML
    public void crearCredito(){
        if(txtMonto.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
            alert.setContentText("INGRESE EL MONTO MÁXIMO DEL CRÉDITO");
            alert.showAndWait();
            return;
        }

        List<ModelTipoCredito> creds = servicio.traerTipoCredito();
        String nombre = txtNombre.getText().trim().toUpperCase();
        String codigo = txtCodigo.getText().trim().toUpperCase();

        if(txtNombre.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
            alert.setContentText("INGRESE UN NOMBRE PARA EL CRÉDITO");
            alert.showAndWait();
            return;
        }

        if(txtCodigo.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
            alert.setContentText("INGRESE UN CÓDIGO PARA EL CRÉDITO");
            alert.showAndWait();
            return;
        }
        for (ModelTipoCredito cred : creds) {

            if (nombre.equalsIgnoreCase(cred.getNombre())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
                alert.setContentText("YA EXISTE UN CRÉDITO CON ESE NOMBRE");
                alert.showAndWait();
                return;
            }

            if (codigo.equalsIgnoreCase(cred.getCodigoSistema())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
                alert.setContentText("YA EXISTE UN CRÉDITO CON ESE CÓDIGO");
                alert.showAndWait();
                return;
            }
        }

        if(txtDescripcion.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
            alert.setContentText("INGRESE UNA DESCRIPCION PARA EL CRÉDITO");
            alert.showAndWait();
            return;
        }
        double montoMaximo = 0;

        montoMaximo = parseMoneda(txtMonto.getText());
        if(montoMaximo == 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
            alert.setContentText("INGRESE UN MONTO VÁLIDO");
            alert.showAndWait();
            return;
        }

        ModelTipoCredito credito = new ModelTipoCredito();
        credito.setFc(servicio.traerFechaHoy());
        credito.setIva(chkIva.isSelected());
        credito.setBonif(chkBonif.isSelected());
        credito.setMontoMaximo(BigDecimal.valueOf(montoMaximo));
        credito.setNombre(nombre);
        credito.setCodigoSistema(codigo);
        credito.setDescripcion(txtDescripcion.getText());

        int creditosAntes = creds.size();
        credito= servicio.crearCredito(credito);
        int creditos = servicio.traerTipoCredito().size();
        if(creditos> creditosAntes){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ÉXITO");
            alert.setHeaderText("ÉXITO");
            alert.setContentText("CRÉDITO: " + txtCodigo.getText() + " CREADO CON ÉXITO");
            alert.showAndWait();

            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();

            return;
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER CREAR EL CRÉDITO");
            alert.setContentText("CRÉDITO NO CREADO");
            alert.showAndWait();
            return;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtNombre.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.equals(newValue.toUpperCase())) {
                txtNombre.setText(newValue.toUpperCase());
            }
        });

        txtCodigo.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.equals(newValue.toUpperCase())) {
                txtCodigo.setText(newValue.toUpperCase());
            }
        });

        txtDescripcion.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.equals(newValue.toUpperCase())) {
                txtDescripcion.setText(newValue.toUpperCase());
            }
        });

        txtMonto.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                String textoLimpio = txtMonto.getText().replaceAll("[^\\d.]", "");
                txtMonto.setTextFormatter(null);
                txtMonto.setText(textoLimpio);
                aplicarFormatterNumerico();
            } else {
                if (txtMonto.getTextFormatter() != null) {
                    settearMonto();
                }
            }
        });
    }

    @FXML
    public void limpiar(){
        txtMonto.clear();
        txtNombre.clear();
        txtCodigo.clear();
        txtDescripcion.clear();

        chkBonif.setSelected(false);
        chkIva.setSelected(false);
    }

    private void aplicarFormatterNumerico() {
        txtMonto.setTextFormatter(new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
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

    @FXML
    public void settearMonto() {
        String texto = txtMonto.getText().trim();


        double monto;
        try {
            monto = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return;
        }

        if (monto <= 0) {
            mostrarError("POR FAVOR, DIGITE UN MONTO MAYOR A CERO");
            return;
        }

        txtMonto.setTextFormatter(null);
        txtMonto.setText(formatoMXN.format(monto));
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("ERROR");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
