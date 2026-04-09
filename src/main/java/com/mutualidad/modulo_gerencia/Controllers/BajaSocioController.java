package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.DTO.ResumenCreditosDTO;
import com.mutualidad.modulo_gerencia.Models.ModelAhorro;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class BajaSocioController implements Initializable {

    @FXML
    private TextField txtNumero, txtNombre, txtTipo, txtCuentaAhorro, txtCreditosVig, txtSaldoCre;

    @FXML
    private TextArea txtAviso;

    @FXML
    private Label lblNombre, lblTipo, lblSaldoAhorro, lblCredVig, lblSaldoCre, lblSaldoAhorro1;

    @FXML
    private Button btnBuscar, btnLimpiar, btnBloquear;

    @Autowired
    private Servicio servicio;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtNumero.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9]", ""));
                            return change;
                        }));
    }

    @FXML
    public void limpiar() {
        txtNombre.setVisible(false);
        txtNombre.clear();
        txtTipo.setVisible(false);
        txtTipo.clear();
        txtNumero.clear();
        txtNumero.setEditable(true);
        txtCuentaAhorro.setVisible(false);
        txtCuentaAhorro.clear();
        txtCreditosVig.setVisible(false);
        txtCreditosVig.clear();
        txtSaldoCre.setVisible(false);
        txtSaldoCre.clear();
        txtAviso.setVisible(false);
        lblSaldoAhorro1.setVisible(false);
        lblNombre.setVisible(false);
        lblTipo.setVisible(false);
        lblSaldoAhorro.setVisible(false);
        lblCredVig.setVisible(false);
        lblSaldoCre.setVisible(false);
        btnBloquear.setVisible(false);
    }

    //Falta terminar este método
    @FXML
    public void bloquearSocio() {
        //PRIMERO VALIDAR QUE YA SE HAYA RETIRADO EL DINERO
        double ahorro = parseMoneda(txtCuentaAhorro.getText().trim());
        if (ahorro != 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL DAR DE BAJA");
            alert.setContentText("EL SOCIO AÚN CUENTA CON SALDO DE AHORRO Y/O CAPITAL SOCIAL, RETIRE TODO ANTES.");
            alert.showAndWait();
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
    public void buscarSocio() {
        int numsocio = 0;
        if (!txtNumero.getText().isEmpty()) {
            numsocio = Integer.parseInt(txtNumero.getText());
            ModelSocio socio = servicio.traerSocioPorNumeroYEstado(numsocio, true);

            if (socio != null) {
                txtNombre.setVisible(true);
                txtTipo.setVisible(true);
                txtCuentaAhorro.setVisible(true);
                txtCreditosVig.setVisible(true);
                txtSaldoCre.setVisible(true);
                lblSaldoAhorro1.setVisible(true);
                lblNombre.setVisible(true);
                txtNumero.setEditable(false);
                lblTipo.setVisible(true);
                lblSaldoAhorro.setVisible(true);
                lblCredVig.setVisible(true);
                lblSaldoCre.setVisible(true);
                btnBloquear.setVisible(true);
                txtAviso.setVisible(true);

                if (socio.getCatTipoId() == 1) {
                    txtTipo.setText("MAYOR DE EDAD");
                } else {
                    txtTipo.setText("MENOR DE EDAD");
                }

                txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
                ModelAhorro cuentaAhorro = servicio.traerCuentaAhorroPorNumSocioYEstado(socio.getNumSocio(), 1);
                double montoCs = servicio.sumarCapitalSocial(socio.getNumSocio());
                txtCuentaAhorro.setText(formatoMXN.format(cuentaAhorro.getSaldo() + montoCs));
                ResumenCreditosDTO resumen = servicio.traerResumenCreditos(numsocio);
                txtCreditosVig.setText(String.valueOf(resumen.getNumCreditos()));
                txtSaldoCre.setText(formatoMXN.format(resumen.getSaldoTotal()));
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("SOCIO NO ENCONTRADO");
                alert.setHeaderText("SOCIO NO ENCONTRADO");
                alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("NÚMERO NO PROPORCIONADO");
            alert.setContentText("POR FAVOR, PROPORCIONE UN NÚMERO DE SOCIO.");
            alert.showAndWait();
        }
    }
}
