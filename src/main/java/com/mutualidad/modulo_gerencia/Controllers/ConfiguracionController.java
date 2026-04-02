package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelConfiguracion;
import com.mutualidad.modulo_gerencia.Models.ModelEmpresa;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class ConfiguracionController implements Initializable {

    @FXML
    private TextField txtAhorroMen, txtNCuotas, txtIDE, txtTesorero, txtApoderado;

    @FXML
    private ComboBox cmbEmpresa;

    @Autowired
    public Servicio servicio;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    DecimalFormat formatoPorcentaje = new DecimalFormat("#0.00'%'");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Optional<ModelConfiguracion> configuracion = servicio.traerConfiguraciones();

        txtAhorroMen.setText(formatoMXN.format(configuracion.get().getAhorroMensual().doubleValue()));
        txtNCuotas.setText(configuracion.get().getCuotasMaximasCajero().toString());
        txtIDE.setText(formatoPorcentaje.format(configuracion.get().getTasaIde().doubleValue()));
        txtApoderado.setText(configuracion.get().getApoderadoLegal());

        List<ModelEmpresa> empresas = servicio.traerEmpresas();

        for (ModelEmpresa empresa : empresas) {
            cmbEmpresa.getItems().add(empresa.getAbreviacion());
        }

        cmbEmpresa.getSelectionModel().selectFirst();

        if (cmbEmpresa.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("MUT")) {
            txtTesorero.setText(configuracion.get().getTesoreroMut());
        } else {
            txtTesorero.setText(configuracion.get().getTesoreroNgu());
        }

        txtApoderado.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));


        txtTesorero.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtAhorroMen.setTextFormatter(
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

        txtIDE.setTextFormatter(
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

        txtNCuotas.setTextFormatter(
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

    }

    @FXML
    public void cambiarTesorero() {
        Optional<ModelConfiguracion> configuracion = servicio.traerConfiguraciones();

        if (cmbEmpresa.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("MUT")) {
            txtTesorero.setText(configuracion.get().getTesoreroMut());
        } else {
            txtTesorero.setText(configuracion.get().getTesoreroNgu());
        }
    }

    @FXML
    public void settearMontoMensual() {
        txtAhorroMen.setTextFormatter(null);
        txtAhorroMen.setText(formatoMXN.format(Double.parseDouble(txtAhorroMen.getText())));
        txtAhorroMen.setTextFormatter(
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
    public void settearPorcentajeIDe() {
        txtIDE.setTextFormatter(null);
        txtIDE.setText(formatoPorcentaje.format(Double.parseDouble(txtIDE.getText())));
        txtIDE.setTextFormatter(
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
    public void guardarConfiguraciones() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CAMBIO DE CONFIGURACIONES");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        Optional<ModelConfiguracion> configuracion = servicio.traerConfiguraciones();

        configuracion.get().setFm(LocalDate.now());
        configuracion.get().setAhorroMensual(BigDecimal.valueOf(parseMoneda(txtAhorroMen.getText().trim())));
        configuracion.get().setTasaIde(BigDecimal.valueOf(parsePorcentaje(txtIDE.getText().trim())));
        configuracion.get().setCuotasMaximasCajero(Integer.parseInt(txtNCuotas.getText().trim()));
        configuracion.get().setApoderadoLegal(txtApoderado.getText().trim());

        if (cmbEmpresa.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("MUT")) {
            configuracion.get().setTesoreroMut(txtTesorero.getText());
        } else {
            configuracion.get().setTesoreroNgu(txtTesorero.getText());
        }

        ModelUsuario usuario = servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado);

        configuracion.get().setUm(usuario.getId());

        //Enviamos a guardar
        servicio.guardarConfiguraciones(configuracion);

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CORRECTO");
        alert.setHeaderText("GUARDADO EXITOSO");
        alert.setContentText("CONFIGURACIONES GUARDADAS CORRECTAMENTE");
        alert.showAndWait();

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

    private double parsePorcentaje(String porcentaje) {
        try {
            Number numero = formatoPorcentaje.parse(porcentaje);
            return numero.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


}
