package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelEmpresa;
import com.mutualidad.modulo_gerencia.Models.ModelModulo;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class OperacionesCajeroController implements Initializable {

    @FXML
    private TextField txtBuscarTabla;

    @FXML
    private DatePicker dateInicio, dateFin;

    @FXML
    private ComboBox cmbEmpresa;

    @FXML
    private Label lblDesSelecTodos, lblSelecTodos;

    @FXML
    private TableView<ModelUsuario> tblCajeros;

    @FXML
    private TableColumn<ModelUsuario, String> colUsuario;

    @FXML
    private TableColumn<ModelUsuario, String> colNombre;

    @FXML
    private TableColumn<ModelUsuario, Boolean> colSeleccionar;

    @Autowired
    public Servicio servicio;

    private final Map<ModelUsuario, Boolean> estados = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dateInicio.setValue(LocalDate.now());
        dateFin.setValue(LocalDate.now());

        List<ModelEmpresa> empresas = servicio.traerEmpresas();
        for (ModelEmpresa empresa : empresas) {
            cmbEmpresa.getItems().add(empresa.getRazonSocial());
        }
        cmbEmpresa.getSelectionModel().selectFirst();



    }
}
