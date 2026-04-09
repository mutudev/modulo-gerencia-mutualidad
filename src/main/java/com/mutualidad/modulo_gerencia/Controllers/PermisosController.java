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
import org.jfree.data.json.impl.JSONArray;
import org.jfree.data.json.impl.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PermisosController implements Initializable {

    @FXML
    private Label lblNombre, lblRol, lblPuesto, lblSelecTodos, lblDesSelecTodos;

    @FXML
    private TextField txtNombre, txtRol, txtPuesto, txtUsuario, txtBuscarTabla;

    @FXML
    private ComboBox cmbModulo;

    @FXML
    private TableView<ModelModulo> tblPermisos;

    @FXML
    private TableView<ModelCaja> tblCajeros;

    @FXML
    private TableColumn<ModelCaja, String> colIdCajero;

    @FXML
    private TableColumn<ModelCaja, String> colUsuarioCajero;

    @FXML
    private TableColumn<ModelCaja, String> colEmpresa;

    @FXML
    private TableColumn<ModelCaja, Boolean> colPermitirCierre;

    @FXML
    private TableColumn<ModelModulo, String> colId;

    @FXML
    private TableColumn<ModelModulo, String> colModulo;

    @FXML
    private TableColumn<ModelModulo, Boolean> colSeleccionar;

    @Autowired
    public Servicio servicio;

    private boolean seleccionado, todosSeleccionados = false;

    @FXML
    private Tab tabPermisos;

    @FXML
    private TabPane tabPadre;

    private final Map<ModelModulo, Boolean> estados = new HashMap<>();

    private final Map<ModelCaja, Boolean> estadosCajeros = new HashMap<>();

    private ObservableList<ModelModulo> listaOriginal = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colIdCajero.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId()))
        );

        colUsuarioCajero.setCellValueFactory(data -> {
            Integer usuarioId = data.getValue().getUsuarioId();

            ModelUsuario usuario = servicio.traerUsuarioXId(usuarioId);

            return new SimpleStringProperty(usuario.getUsuario());
        });

        colEmpresa.setCellValueFactory(data -> {

            ModelEmpresa emp = servicio.traerEmpresaXCodigo(data.getValue().getEmpresa());

            String nombre = emp != null ? emp.getNombre() : "";

            return new SimpleStringProperty(nombre);
        });


        colPermitirCierre.setCellValueFactory(data -> {
            ModelCaja caja = data.getValue();

            estadosCajeros.putIfAbsent(caja, caja.getAjuste() == 1);

            SimpleBooleanProperty prop =
                    new SimpleBooleanProperty(estadosCajeros.get(caja));

            prop.addListener((obs, oldVal, newVal) -> {

                estadosCajeros.put(caja, newVal);

                int nuevoAjuste = newVal ? 1 : 2;


                servicio.actualizarAjusteCaja(caja.getId(), nuevoAjuste);


                caja.setAjuste(nuevoAjuste);
            });

            return prop;
        });

        colPermitirCierre.setCellFactory(tc -> new CheckBoxTableCell<>());
        tblCajeros.setEditable(true);
        colPermitirCierre.setEditable(true);



        colId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        colModulo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDescripcion()
                ));

        colSeleccionar.setCellValueFactory(data -> {
            ModelModulo modulo = data.getValue();


            estados.putIfAbsent(modulo, false);

            SimpleBooleanProperty prop =
                    new SimpleBooleanProperty(estados.get(modulo));


            prop.addListener((obs, oldVal, newVal) -> estados.put(modulo, newVal));

            return prop;
        });

        txtUsuario.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtBuscarTabla.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        colSeleccionar.setCellFactory(tc -> new CheckBoxTableCell<>());

        tblPermisos.setEditable(true);
        colSeleccionar.setEditable(true);

        cmbModulo.getItems().addAll("CAJA", "CREDITO", "GERENCIA");
        cmbModulo.getSelectionModel().selectFirst();

        cargarDatosCajero();
    }


    @FXML
    public void cargarDatosTabla() {

        tblPermisos.getItems().clear();
        List<ModelModulo> modulos = servicio.traerModulos(cmbModulo.getSelectionModel().getSelectedItem().toString(), true);
        ObservableList<ModelModulo> data = FXCollections.observableArrayList(modulos);
        listaOriginal = data;
        tblPermisos.setItems(data);

        ModelUsuario usuario = servicio.traerUsuarioXUsuario(txtUsuario.getText());

        List<ModelConfModulo> confModulo = servicio.traerModuloXUsuario(usuario.getId());

        Set<Integer> modulosUsuario = confModulo.stream()
                .map(ModelConfModulo::getModuloId)
                .collect(Collectors.toSet());

        for (ModelModulo modulo : tblPermisos.getItems()) {
            if (modulosUsuario.contains(modulo.getId())) {
                estados.put(modulo, true);
            } else {
                estados.put(modulo, false);
            }
        }


        todosSeleccionados = tblPermisos.getItems().stream()
                .allMatch(modulo -> estados.getOrDefault(modulo, false));

        if (todosSeleccionados) {
            lblDesSelecTodos.setVisible(true);
            lblSelecTodos.setVisible(false);
        } else {
            lblDesSelecTodos.setVisible(false);
            lblSelecTodos.setVisible(true);
        }

        tblPermisos.refresh();

    }

    @FXML
    public void cargarDatosCajero() {
        tblCajeros.getItems().clear();
        List<ModelCaja> cajeros = servicio.traerCajerosActivos(true, 2, LocalDate.now());
        ObservableList<ModelCaja> data = FXCollections.observableArrayList(cajeros);
        tblCajeros.setItems(data);
    }

    @FXML
    public void limpiar() {
        txtNombre.setVisible(false);
        txtRol.setVisible(false);
        txtPuesto.setVisible(false);
        lblNombre.setVisible(false);
        lblRol.setVisible(false);
        lblPuesto.setVisible(false);
        txtUsuario.clear();
        txtUsuario.setEditable(true);
    }

    @FXML
    public void chequeoCambio() {
        if (!txtNombre.isVisible()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR AVANZAR");
            alert.setContentText(
                    "POR FAVOR, CARGUE UN USUARIO");
            alert.showAndWait();
            tabPadre.getSelectionModel().selectFirst();

        }

    }

    @FXML
    public void cargarUsuarios() {


        if (txtUsuario.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR BUSCAR AL USUARIO");
            alert.setContentText(
                    "POR FAVOR, RELLENE TODOS LOS CAMPOS");
            alert.showAndWait();
            return;
        }

        ModelUsuario usuario = servicio.traerUsuarioXUsuario(txtUsuario.getText());
        if (usuario != null) {

            if (!usuario.isStatus()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL INTENTAR BUSCAR AL USUARIO");
                alert.setContentText(
                        "EL USUARIO QUE INTENTA CARGAR NO ESTÁ ACTIVO");
                alert.showAndWait();
                return;
            }

            txtUsuario.setEditable(false);


            ModelEmpleado empleado = servicio.traerEmpleadoXId(usuario.getIdEmpleado());
            txtNombre.setText(empleado.getNombres() + ' ' + empleado.getApellidoP() + ' ' + empleado.getApellidoM());
            txtPuesto.setText(servicio.traerNombrePuesto(empleado.getPuesto()));
            txtRol.setText(servicio.traerNombreRol(usuario.getRol()));


            txtNombre.setVisible(true);
            txtUsuario.setVisible(true);
            txtRol.setVisible(true);
            txtPuesto.setVisible(true);
            lblNombre.setVisible(true);
            lblRol.setVisible(true);
            lblPuesto.setVisible(true);

            cargarDatosTabla();


        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR BUSCAR AL USUARIO");
            alert.setContentText(
                    "POR FAVOR, RELLENE TODOS LOS CAMPOS");
            alert.showAndWait();
            return;
        }
    }

    @FXML
    public void seleccionarTodos() {

        todosSeleccionados = !todosSeleccionados;

        for (ModelModulo modulo : tblPermisos.getItems()) {
            estados.put(modulo, todosSeleccionados);
        }

        tblPermisos.refresh();

        if (todosSeleccionados) {
            lblDesSelecTodos.setVisible(true);
            lblSelecTodos.setVisible(false);
        } else {
            lblDesSelecTodos.setVisible(false);
            lblSelecTodos.setVisible(true);
        }
    }

    @FXML
    public void buscarPermisos() {

        String texto = txtBuscarTabla.getText().toLowerCase().trim();

        if (texto.isEmpty()) {
            tblPermisos.setItems(listaOriginal);
            return;
        }

        ObservableList<ModelModulo> filtrados = FXCollections.observableArrayList();

        for (ModelModulo modulo : listaOriginal) {
            if (modulo.getDescripcion().toLowerCase().contains(texto)) {
                filtrados.add(modulo);
            }
        }

        tblPermisos.setItems(filtrados);
    }

    @FXML
    public void guardarPermisos() {
        JSONArray cuotasArray = new JSONArray();

        int rowCount = tblPermisos.getItems().size();

        for (int i = 0; i < rowCount; i++) {
            JSONObject obj = new JSONObject();
            ModelModulo modulo = tblPermisos.getItems().get(i);
            obj.put("modulo_id", modulo.getId());
            obj.put("modulo_nombre", modulo.getDescripcion());
            obj.put("seleccionado", estados.getOrDefault(modulo, false));
            cuotasArray.add(i, obj);
        }

        ModelUsuario usuario = servicio.traerUsuarioXUsuario(txtUsuario.getText());


        String res = servicio.actualizarPermisos(cuotasArray.toJSONString(), usuario.getId(), "");
        if (res.equalsIgnoreCase("CORRECTO")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CORRECTO");
            alert.setHeaderText("PERMISOS ACTUALIZADOS");
            alert.setContentText(
                    "PERMISOS DEL USUARIO: " + txtUsuario.getText() + " MODIFICADOS CORRECTAMENTE.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR MODIFICAR PERMISOS");
            alert.setContentText(
                    res.toUpperCase());
            alert.showAndWait();
        }

        cargarDatosTabla();

    }

    @FXML
    public void chequeoPermisosCierre() {
        
    }




}
