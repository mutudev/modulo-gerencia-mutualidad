package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelMunicipio;
import com.mutualidad.modulo_gerencia.Models.ModelTipoCredito;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class ModificarCreditoController implements Initializable {

    @FXML
    private Label lblMonto, lblBonif, lblIva, lblNombre, lblDescripcion;

    @FXML
    private ComboBox cmbCredito;

    @FXML
    private TextField txtMonto, txtNombre;

    @FXML
    private CheckBox chkBonif, chkIva;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private Button btnCargar, btnLimpiar, btnAplicar, btnCrear;

    @Autowired
    private Servicio servicio;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<ModelTipoCredito> tipoCreditos = servicio.traerTipoCredito();
        if (!tipoCreditos.isEmpty()) {
            cmbCredito.getItems().addAll(tipoCreditos);
            cmbCredito.getSelectionModel().selectFirst();
        }

        txtMonto.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Ganó foco: limpiar formato y activar formatter numérico
                String textoLimpio = txtMonto.getText().replaceAll("[^\\d.]", "");
                txtMonto.setTextFormatter(null);
                txtMonto.setText(textoLimpio);
                aplicarFormatterNumerico();
            } else {
                // Perdió foco sin Enter: formatear igual
                if (txtMonto.getTextFormatter() != null) {
                    settearMonto();
                }
            }
        });


        txtDescripcion.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.equals(newValue.toUpperCase())) {
                txtDescripcion.setText(newValue.toUpperCase());
            }
        });
    }

    @FXML
    public void cargarParametrosCredito() {

        ModelTipoCredito tipoCredito = (ModelTipoCredito) cmbCredito.getValue();

        chkBonif.setSelected(tipoCredito.getBonif());
        chkIva.setSelected(tipoCredito.getIva());

        txtMonto.setTextFormatter(null);
        txtMonto.setText(formatoMXN.format(tipoCredito.getMontoMaximo().doubleValue()));
        txtNombre.setText(tipoCredito.getNombre());
        txtDescripcion.setText(tipoCredito.getDescripcion());

        btnCrear.setVisible(false);
        btnLimpiar.setVisible(true);
        chkBonif.setVisible(true);
        chkIva.setVisible(true);
        txtMonto.setVisible(true);
        lblNombre.setVisible(true);
        lblBonif.setVisible(true);
        lblIva.setVisible(true);
        lblMonto.setVisible(true);
        btnAplicar.setVisible(true);
        txtNombre.setVisible(true);
        txtDescripcion.setVisible(true);
        lblDescripcion.setVisible(true);

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

    @FXML
    public void settearMonto() {
        String texto = txtMonto.getText().trim();

        if (texto.isEmpty()) {
            mostrarError("POR FAVOR, ESCRIBA EL MONTO");
            return;
        }

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

    @FXML
    public void limpiar(){
        cmbCredito.getSelectionModel().selectFirst();
        chkBonif.setVisible(false);
        chkIva.setVisible(false);
        txtMonto.setVisible(false);
        lblBonif.setVisible(false);
        lblIva.setVisible(false);
        lblNombre.setVisible(false);
        lblMonto.setVisible(false);
        btnAplicar.setVisible(false);
        txtNombre.setVisible(false);
        btnCrear.setVisible(true);
        btnLimpiar.setVisible(false);
        txtDescripcion.setVisible(false);
        lblDescripcion.setVisible(false);

    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("ERROR");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void  guardarCambios(){
        if(txtMonto.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER MODIFICAR EL CRÉDITO");
            alert.setContentText("INGRESE EL MONTO MÁXIMO DEL CRÉDITO");
            alert.showAndWait();
            return;
        }
        if(txtNombre.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER MODIFICAR EL CRÉDITO");
            alert.setContentText("INGRESE UN NOMBRE PARA EL CRÉDITO");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("MODIFICACIÓN DE CRÉDITO");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        double montoMaximo = 0;

        montoMaximo = parseMoneda(txtMonto.getText());
        if(montoMaximo == 0){
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER MODIFICAR EL CRÉDITO");
            alert.setContentText("INGRESE UN MONTO VÁLIDO");
            alert.showAndWait();
            return;
        }

        boolean iva = chkIva.isSelected();
        boolean bonif = chkBonif.isSelected();
        ModelTipoCredito tipoCredito = (ModelTipoCredito) cmbCredito.getValue();

        tipoCredito.setMontoMaximo(BigDecimal.valueOf(montoMaximo));
        tipoCredito.setBonif(bonif);
        tipoCredito.setIva(iva);
        tipoCredito.setDescripcion(txtDescripcion.getText());
        tipoCredito.setNombre(txtNombre.getText());

        tipoCredito = servicio.guardarModificaciones(tipoCredito);
        if(chkBonif.isSelected() == tipoCredito.getBonif()){
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ÉXITO");
            alert.setHeaderText("ÉXITO");
            alert.setContentText("MODIFICACIONES AL CRÉDITO GUARDADAS CON ÉXITO");
            alert.showAndWait();
            limpiar();
            return;
        }else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER MODIFICAR EL CRÉDITO");
            alert.setContentText("INGRESE UN MONTO VÁLIDO");
            alert.showAndWait();
            limpiar();
            return;
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

    @FXML
    public void crearCredito() throws IOException {
        Stage nuevaVentana = new Stage();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/crearCredito.fxml"));
        fxml.setControllerFactory(Main.context::getBean);
        Scene nuevaEscena = new Scene(fxml.load());
        CrearCreditoController controlador = fxml.getController();
        nuevaEscena
                .getStylesheets()
                .add(getClass().getResource("/assets/css/estilos.css").toExternalForm());
        nuevaVentana.setTitle("CREAR CRÉDITO");
        nuevaVentana.setScene(nuevaEscena);
        Image icon = new Image(getClass().getResourceAsStream("/assets/images/logo.png"));
        nuevaVentana.getIcons().add(icon);
        nuevaVentana.setResizable(false);
        nuevaVentana.centerOnScreen();
        nuevaVentana.initModality(Modality.APPLICATION_MODAL);
        nuevaVentana.show();
    }

}