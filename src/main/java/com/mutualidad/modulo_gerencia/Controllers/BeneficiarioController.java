package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.DTO.BeneficiarioDTO;
import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelParentesco;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jfree.data.json.impl.JSONArray;
import org.jfree.data.json.impl.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class BeneficiarioController implements Initializable {



    @FXML
    private TextField txtNumero, txtNombre, txtNomBeneficiario;

    @FXML
    private Button btnBuscar, btnLimpiar,btnAgregar, btnEliminar, btnGuardar;

    @FXML
    private ComboBox cmbParentesco, cmbPorcentaje;
    @FXML
    private Label lblNombre, lblBeneficiario, lblParentesco, lblPorcentaje;


    @FXML
    private ImageView imgBusqueda;

    @FXML
    private TableView<Map<String, String>> tblBeneficiarios;

    @FXML
    private TableColumn<Map<String, String>, String> colParentesco;

    @FXML
    private TableColumn<Map<String, String>, String> colNombre;
    @FXML
    private TableColumn<Map<String, String>, String> colTitular;
    @FXML
    private TableColumn<Map<String, String>, String> colPorcentaje;
    @FXML
    private TableColumn<Map<String, String>, String> colID;

    int update = 0;


    @Autowired
    private Servicio servicio;
    DecimalFormat formatoPorcentaje = new DecimalFormat("#0'%'");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // 🔹 FORMATOS DE TEXTFIELDS
        txtNomBeneficiario.setTextFormatter(
                new TextFormatter<>(change -> {
                    change.setText(change.getText().toUpperCase());
                    return change;
                }));

        txtNumero.setTextFormatter(
                new TextFormatter<>(change -> {
                    change.setText(change.getText().replaceAll("[^0-9]", ""));
                    return change;
                }));

        tblBeneficiarios.setEditable(true);

        colNombre.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("nombre"))
        );

        colNombre.setCellFactory(tc -> {
            TextFieldTableCell<Map<String, String>, String> cell = new TextFieldTableCell<>();

            cell.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(String object) {
                    return object;
                }

                @Override
                public String fromString(String string) {
                    return string.toUpperCase();
                }
            });

            return cell;
        });

        colNombre.setOnEditCommit(event -> {
            event.getRowValue().put("nombre", event.getNewValue().toUpperCase());
        });

        colParentesco.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("parentesco"))
        );


        colPorcentaje.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("porcentaje"))
        );

        colPorcentaje.setCellFactory(TextFieldTableCell.forTableColumn());

        colPorcentaje.setOnEditCommit(event -> {

            String valor = event.getNewValue().replace("%", "").trim();

            // VALIDACIÓN: solo números
            if (!valor.matches("\\d+")) {
                event.getRowValue().put("porcentaje", event.getOldValue());
                tblBeneficiarios.refresh();
                return;
            }

            int nuevoValor = Integer.parseInt(valor);

            // VALIDACIÓN: solo valores permitidos
            if (!(nuevoValor == 100 || nuevoValor == 50 || nuevoValor == 35 || nuevoValor == 30)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("VALOR NO PERMITIDO");
                alert.setHeaderText("PORCENTAJE INVÁLIDO");
                alert.setContentText("Solo se permiten los valores: 100%, 50%, 35% y 30%".toUpperCase());
                alert.showAndWait();

                event.getRowValue().put("porcentaje", event.getOldValue());
                tblBeneficiarios.refresh();
                return;
            }

            double suma = 0;

            for (Map<String, String> item : tblBeneficiarios.getItems()) {
                if (item == event.getRowValue()) continue;

                String v = item.get("porcentaje").replace("%", "").trim();
                suma += Double.parseDouble(v);
            }

            if (suma + nuevoValor > 100) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("PORCENTAJE EXCEDIDO");
                alert.setHeaderText("LA SUMA NO PUEDE SUPERAR 100%");
                alert.setContentText("La suma actual es: " + suma +
                        "% y estás intentando poner: " + nuevoValor + "%");
                alert.showAndWait();

                event.getRowValue().put("porcentaje", event.getOldValue());
                tblBeneficiarios.refresh();
                return;
            }

            event.getRowValue().put("porcentaje", nuevoValor + "%");
        });
        colTitular.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("titular"))
        );

        colID.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("id_benef"))
        );


        List<ModelParentesco> parentescos = servicio.traerParentescos();
        cmbParentesco.getItems().clear();
        for (ModelParentesco p : parentescos) {
            cmbParentesco.getItems().add(p.getParentesco());
        }
        cmbParentesco.getSelectionModel().selectFirst();

        cmbPorcentaje.getItems().clear();
        cmbPorcentaje.getItems().add("100%");
        cmbPorcentaje.getItems().add("50%");
        cmbPorcentaje.getItems().add("35%");
        cmbPorcentaje.getItems().add("30%");
        cmbPorcentaje.getSelectionModel().selectFirst();
    }

    public void cargarDatosSocio(int numSocio) {

        List<BeneficiarioDTO> beneficiarios = servicio.traerBeneficiarios(numSocio, 1);
        tblBeneficiarios.getItems().clear();

        if (beneficiarios != null && !beneficiarios.isEmpty()) {

            for (BeneficiarioDTO b : beneficiarios) {
                Map<String, String> fila = new HashMap<>();
                fila.put("id_benef", String.valueOf(b.getId()));
                fila.put("nombre", b.getBeneficiario());
                fila.put("parentesco", b.getParentesco());
                fila.put("titular", Boolean.TRUE.equals(b.getTitular()) ? "SÍ" : "NO");
                fila.put("porcentaje", b.getPorcentaje() + "%");
                tblBeneficiarios.getItems().add(fila);
            }

            update = 1;
        }
    }

    @FXML
    public void buscarSocio() {
        int numsocio = 0;
        if (!txtNumero.getText().isEmpty()) {
            numsocio = Integer.parseInt(txtNumero.getText());

            ModelSocio socio = servicio.traerSocioPorNumeroYEstado(numsocio, true);

            if (socio != null) {
                txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
                txtNombre.setEditable(false);
                txtNombre.setVisible(true);
                txtNomBeneficiario.setVisible(true);
                imgBusqueda.setVisible(false);
                btnLimpiar.setVisible(true);
                btnAgregar.setVisible(true);
                btnGuardar.setVisible(true);
                cmbPorcentaje.setVisible(true);
                btnEliminar.setVisible(true);
                cmbParentesco.setVisible(true);
                tblBeneficiarios.setVisible(true);
                lblNombre.setVisible(true);
                txtNumero.setEditable(false);
                lblPorcentaje.setVisible(true);
                lblBeneficiario.setVisible(true);
                lblParentesco.setVisible(true);

                cargarDatosSocio(numsocio);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("SOCIO NO ENCONTRADO");
                alert.setHeaderText("SOCIO NO ENCONTRADO");
                alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
                alert.showAndWait();
                return;
            }
        }
    }

    @FXML
    private void limpiar() {
        txtNombre.setEditable(true);
        txtNombre.setVisible(false);
        txtNomBeneficiario.setVisible(false);
        btnAgregar.setVisible(false);
        txtNombre.setText("");
        btnGuardar.setVisible(false);
        txtNomBeneficiario.setText("");
        btnEliminar.setVisible(false);
        cmbParentesco.setVisible(false);
        lblPorcentaje.setVisible(false);
        tblBeneficiarios.setVisible(false);
        lblNombre.setVisible(false);
        lblBeneficiario.setVisible(false);
        lblParentesco.setVisible(false);
        txtNumero.setEditable(true);
        txtNumero.clear();
        cmbPorcentaje.setVisible(false);
        tblBeneficiarios.getItems().clear();
        imgBusqueda.setVisible(true);
    }

    @FXML
    private void agregarbeneficiario() {
        String nombreBen = "";

        if (txtNomBeneficiario.getText().isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR AL INTENTAR AGREGAR AL BENEFICIARIO");
            alert.setHeaderText("ERROR AL INTENTAR AGREGAR AL BENEFICIARIO");
            alert.setContentText("POR FAVOR, LLENE TODOS LOS CAMPOS");
            alert.showAndWait();
            return;
        }

        double sumaActual = 0;
        for (Map<String, String> item : tblBeneficiarios.getItems()) {
            String valor = item.get("porcentaje").replace("%", "").trim();
            sumaActual += Double.parseDouble(valor);
        }

        int nuevoPorcentaje = Integer.parseInt(cmbPorcentaje.getSelectionModel().getSelectedItem().toString().replace("%", "").trim());

        if (sumaActual + nuevoPorcentaje > 100) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PORCENTAJE EXCEDIDO");
            alert.setHeaderText("LA SUMA DE PORCENTAJES NO PUEDE SUPERAR 100%");
            alert.setContentText("La suma actual es: ".toUpperCase() + sumaActual +
                    "% y estás intentando agregar: ".toUpperCase() + nuevoPorcentaje + "%".toUpperCase());
            alert.showAndWait();
            return;
        }

        nombreBen = txtNomBeneficiario.getText().toUpperCase();
        String parentesco = cmbParentesco.getSelectionModel().getSelectedItem().toString().toUpperCase();

        Map<String, String> fila = new HashMap<>();

        if (tblBeneficiarios.getItems().size() == 0) {
            fila.put("titular", "SÍ");
        } else {
            fila.put("titular", "NO");
        }

        fila.put("porcentaje", nuevoPorcentaje + "%");

        fila.put("nombre", nombreBen);
        fila.put("parentesco", parentesco);
        fila.put("id_benef", String.valueOf(0));

        tblBeneficiarios.getItems().add(fila);
        txtNomBeneficiario.clear();
    }

    @FXML
    private void quitarBeneficiario() {
        int index = tblBeneficiarios.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            tblBeneficiarios.getItems().remove(index);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR AL INTENTAR ELIMINAR AL BENEFICIARIO");
            alert.setHeaderText("ERROR AL INTENTAR ELIMINAR AL BENEFICIARIO");
            alert.setContentText(
                    "POR FAVOR, SELECCIONE UN PROPIETARIO BENEFICIARIO.");
            alert.showAndWait();
        }
    }

    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setBeneficiarioController(this);
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

    public void cargarSocioPorNombre(String numero) {
        txtNumero.setText(numero);
        buscarSocio();
    }

    @FXML
    public void guardarBeneficiario() {
        JSONArray benefArray = new JSONArray();
        int rowCount = tblBeneficiarios.getItems().size();

        if (rowCount <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL INTENTAR GUARDAR BENEFICIARIOS");
            alert.setContentText("POR FAVOR, AGREGUE BENEFICIARIOS");
            alert.showAndWait();
            return;
        }

        double sumaActual = 0;
        for (Map<String, String> item : tblBeneficiarios.getItems()) {
            String valor = item.get("porcentaje").replace("%", "").trim();
            sumaActual += Double.parseDouble(valor);
        }


        if (sumaActual < 100) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL GUARDAR LOS BENEFICIARIOS");
            alert.setContentText("LA SUMA DE PORCENTAJES NO PUEDE SER MENOR A 100%");
            alert.showAndWait();
            return;
        }

        for (int i = 0; i < rowCount; i++) {
            JSONObject obj = new JSONObject();
            obj.put("beneficiario", colNombre.getCellData(i));
            obj.put("socio", txtNumero.getText());
            obj.put("titular", colTitular.getCellData(i));
            obj.put("parentesco", colParentesco.getCellData(i));
            obj.put("porcentaje", colPorcentaje.getCellData(i));
            obj.put("id_benef", colID.getCellData(i));
            benefArray.add(i, obj);
        }

        String res = servicio.insertarBeneficiario(benefArray.toString(),"", update);

        if (res.equalsIgnoreCase("CORRECTO")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("EXITOSO");
            alert.setHeaderText("GUARDADO EXITOSO");
            alert.setContentText("BENEFICIARIOS AGREGADOS CORRECTAMENTE.");
            alert.showAndWait();
            limpiar();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL GUARDAR");
            alert.setContentText(res.toUpperCase());
            alert.showAndWait();
        }

    }


}
