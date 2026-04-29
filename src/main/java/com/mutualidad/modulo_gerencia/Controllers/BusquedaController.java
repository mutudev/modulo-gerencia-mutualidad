package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class BusquedaController implements Initializable {

  @FXML private TextField txtNombreSocio;

  @FXML private Label lblError;

  @FXML private TableView tableSocios;

  @FXML private TableColumn<Object[], String> colSocio;

  @FXML private TableColumn<Object[], String> colNumero;

  @FXML private TableColumn<Object[], String> colTipo;

  @FXML private TableColumn<Object[], String> colEmpresa;

  public Validator validator = new Validator();

  @Autowired
  private Servicio busquedaServicio;

  public DatosController datosController = null;

  public BloquearSocioController bloquearSocioController = null;

  public BeneficiarioController beneficiarioController = null;

  public CrearCuentaController crearCuentaController = null;

  public SaldosAhorroController saldosAhorroController = null;

  public CongelamientoSaldoController congelamientoSaldoController = null;

  public BloquearAhorroController bloquearAhorroController = null;

  public RetiroController retiroController = null;

  public CrearCuentaCSController crearCSController = null;

  public CrearCuentaPSController crearCuentaPSController = null;

  public int ventana;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // Validación del usuario
    validator
        .createCheck()
        .dependsOn("input", txtNombreSocio.textProperty())
        .withMethod(
            c -> {
              String texto = c.get("input");
              if (texto == null || texto.isEmpty()) {
                c.error("El campo no puede estar vacío");
                lblError.setText("El campo no puede estar vacío");
              } else if (texto.matches(".*\\d.*")) {
                c.error("No se permiten números en este campo");
                lblError.setText("No se permiten números en este campo");
              } else if (texto.length() < 3) {
                c.error("El texto debe tener al menos 3 caracteres");
                lblError.setText("El texto debe tener al menos 3 caracteres");
              } else {
                lblError.setText("");
              }
            })
        .decorates(txtNombreSocio)
        .immediate();

    txtNombreSocio.setTextFormatter(
        new TextFormatter<>(
            change -> {
              change.setText(change.getText().toUpperCase());
              if (change.getText().matches("[0-9]")) {
                change.setText("");
              }
              return change;
            }));

    colSocio.setCellValueFactory(
        cellData -> new SimpleStringProperty((String) cellData.getValue()[0]));
    colNumero.setCellValueFactory(
        cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue()[1])));
    colTipo.setCellValueFactory(
        cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue()[2])));
    colEmpresa.setCellValueFactory(
        cellData -> new SimpleStringProperty((String) cellData.getValue()[3]));
  }

  public void setDatosController(DatosController controller) {
    this.datosController = controller;
  }

  public void setBloquearSocioController(BloquearSocioController controller) {
    this.bloquearSocioController = controller;
  }

  public void setBeneficiarioController(BeneficiarioController controller) {
    this.beneficiarioController = controller;
  }

  public void setCrearCuentaController(CrearCuentaController controller) {
    this.crearCuentaController = controller;
  }

  public void setSaldosAhorroController(SaldosAhorroController controller) {
    this.saldosAhorroController = controller;
  }

  public void setCongelamientoSaldoController(CongelamientoSaldoController controller) {
    this.congelamientoSaldoController = controller;
  }

  public void setBloquearAhorroController(BloquearAhorroController controller) {
    this.bloquearAhorroController = controller;
  }

  public void setRetiroController(RetiroController controller) {
    this.retiroController = controller;
  }

  public void setcrearCSController(CrearCuentaCSController controller) {
    this.crearCSController = controller;
  }

  public void setCrearCuentaPSController(CrearCuentaPSController controller) {
    this.crearCuentaPSController = controller;
  }


  @FXML
  public void cerrarModal(KeyEvent event) {
    if (event.getCode().equals(KeyCode.ESCAPE)) {
      validator = new Validator();
      Stage ventanaActual = (Stage) txtNombreSocio.getScene().getWindow();
      ventanaActual.close();
    } else if (event.getCode().equals((KeyCode.ENTER)) && !txtNombreSocio.getText().isEmpty()) {
      traerCoincidencias();
    }
  }

  @FXML
  public void traerCoincidencias() {
    String socioBuscar = txtNombreSocio.getText().trim();
    List<Object[]> resultados = busquedaServicio.buscarSocioPorNombre(socioBuscar);

    ObservableList<Object[]> socios = FXCollections.observableArrayList();

    for (Object[] resultado : resultados) {
      socios.add(resultado);
    }

    tableSocios.setItems(socios);
  }

  public void cargarSocio(MouseEvent event) {
    if (event.getClickCount() == 2) {
      Object[] selectedRow = (Object[]) tableSocios.getSelectionModel().getSelectedItem();

      if (selectedRow != null) {
        String nombre = (String) selectedRow[0];
        String numSocio = String.valueOf(selectedRow[1]);

        if (datosController != null) {
            datosController.cargarSocioPorNombre(numSocio);
        }

        if (bloquearSocioController != null) {
          bloquearSocioController.cargarSocioPorNombre(numSocio);
        }

        if (beneficiarioController != null) {
          beneficiarioController.cargarSocioPorNombre(numSocio);
        }

        if (crearCuentaController != null) {
          crearCuentaController.cargarSocioPorNombre(numSocio);
        }

        if (saldosAhorroController != null) {
          saldosAhorroController.cargarSocioPorNombre(numSocio);
        }

        if (congelamientoSaldoController != null) {
          congelamientoSaldoController.cargarSocioPorNombre(numSocio);
        }

        if (bloquearAhorroController != null) {
          bloquearAhorroController.cargarSocioPorNombre(numSocio);
        }

        if (retiroController != null) {
          retiroController.cargarSocioPorNombre(numSocio);
        }


        if (crearCSController  != null) {
          crearCSController.cargarSocioPorNombre(numSocio);
        }

        if (crearCuentaPSController != null) {
          crearCuentaPSController.cargarSocioPorNombre(numSocio);
        }


        // Cierra la ventana actual
        validator = new Validator();
        Stage ventanaActual = (Stage) txtNombreSocio.getScene().getWindow();
        ventanaActual.close();
      }
    }
  }
}
