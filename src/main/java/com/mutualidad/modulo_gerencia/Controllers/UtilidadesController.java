package com.mutualidad.modulo_gerencia.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Year;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class UtilidadesController implements Initializable {

    @FXML
    private Button btnGenerar;

    @FXML
    private TextField txtTasa, txtMonto;

    @FXML
    private ComboBox cmbYear;


    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));


    private final DecimalFormat formatoPorcentaje = new DecimalFormat("0.00");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int anioActual = Year.now().getValue();
        for (int anio = anioActual; anio >= 1950; anio--) {
            cmbYear.getItems().add(anio);
        }
        cmbYear.setValue(anioActual);


        txtTasa.setTextFormatter(
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

    @FXML
    public void settearTasa() {
        String texto = txtTasa.getText().trim();

        double tasa;
        try {
            tasa = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return;
        }

        if (tasa < 0) {
            mostrarError("POR FAVOR, DIGITE UNA TASA VÁLIDA");
            return;
        }

        txtTasa.setTextFormatter(null);
        txtTasa.setText(formatoPorcentaje.format(tasa) + " %");
    }


    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("ERROR");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
