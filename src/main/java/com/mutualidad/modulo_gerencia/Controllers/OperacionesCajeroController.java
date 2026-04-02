package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
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

    private boolean todosSeleccionados = false;


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

        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsuario()));

        colNombre.setCellValueFactory(data -> {
            ModelEmpleado empleado = servicio.traerEmpleadoXId(data.getValue().getIdEmpleado());

            SimpleStringProperty nombre = new SimpleStringProperty(empleado.getNombres() + " " + empleado.getApellidoP() + " " + empleado.getApellidoM());

            return nombre;

        });

        colSeleccionar.setCellValueFactory(data -> {
            ModelUsuario cajero = data.getValue();


            estados.putIfAbsent(cajero, false);

            SimpleBooleanProperty prop =
                    new SimpleBooleanProperty(estados.get(cajero));


            prop.addListener((obs, oldVal, newVal) -> estados.put(cajero, newVal));

            return prop;
        });

        colSeleccionar.setCellFactory(tc -> new CheckBoxTableCell<>());
        tblCajeros.setEditable(true);
        colSeleccionar.setEditable(true);

        lblSelecTodos.setVisible(true);

        cargarDatosTabla();

    }

    @FXML
    public void cargarDatosTabla() {
        tblCajeros.getItems().clear();
        List<ModelUsuario> cajeros = servicio.traerCajeros(1, true);
        ObservableList<ModelUsuario> data = FXCollections.observableArrayList(cajeros);
        tblCajeros.setItems(data);
    }

    @FXML
    public void seleccionarTodos() {

        todosSeleccionados = !todosSeleccionados;

        for (ModelUsuario usuario : tblCajeros.getItems()) {
            estados.put(usuario, todosSeleccionados);
        }

        tblCajeros.refresh();

        if (todosSeleccionados) {
            lblDesSelecTodos.setVisible(true);
            lblSelecTodos.setVisible(false);
        } else {
            lblDesSelecTodos.setVisible(false);
            lblSelecTodos.setVisible(true);
        }
    }


}
