package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class DatosController implements Initializable {

    @FXML
    private TextField txtNombre, txtApellidoP, txtApellidoM, txtCURP, txtRFC, txtDireccion, txtTelefono, txtNumero;

    @FXML
    private ComboBox cmbEstado, cmbMunicipio, cmbEmpleo, cmbEstadoCivil, cmbGenero;

    @FXML
    private Button btnBuscar, btnLimpiar, btnRegistrar;

    @FXML
    private DatePicker dteNacimiento;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Label lblNombres,
            lblApellidoP,
            lblApellidoM,
            lblCurp,
            lblRfc,
            lblDireccion,
            lblTelefono,
            lblEstado,
            lblMunicipio,
            lblEmpleo,
            lblEstadoC,
            lblGenero,
            lblFecha;

    @Autowired
    private Servicio servicio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtNombre.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtApellidoM.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtApellidoP.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            if (change.getText().matches("[0-9]")) {
                                change.setText("");
                            }
                            return change;
                        }));

        txtCURP.setTextFormatter(
                new TextFormatter<>(change -> {
                    String text = change.getText().toUpperCase();

                    if (!text.matches("[A-Z0-9]*")) {
                        return null;
                    }

                    change.setText(text);

                    if (change.getControlNewText().length() > 18) {
                        return null;
                    }

                    return change;
                })
        );

        txtRFC.setTextFormatter(
                new TextFormatter<>(change -> {
                    String text = change.getText().toUpperCase();

                    if (!text.matches("[A-Z0-9]*")) {
                        return null;
                    }

                    change.setText(text);

                    if (change.getControlNewText().length() > 13) {
                        return null;
                    }

                    return change;
                })
        );

        txtDireccion.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            change.setText(change.getText().toUpperCase());
                            return change;
                        }));

        txtTelefono.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9]", ""));
                            return change;
                        }));



    }

    @FXML
    public void limpiar() {

        /* TEXTFIELDS */
        txtNombre.clear();
        txtApellidoP.clear();
        txtApellidoM.clear();
        txtCURP.clear();
        txtNumero.clear();
        txtRFC.clear();
        txtDireccion.clear();
        txtTelefono.clear();

        /* DATEPICKER */
        dteNacimiento.setValue(null);

        /* COMBOBOX */
        cmbEstado.getSelectionModel().clearSelection();
        cmbMunicipio.getItems().clear();
        cmbEmpleo.getSelectionModel().clearSelection();
        cmbEstadoCivil.getSelectionModel().clearSelection();
        cmbGenero.getSelectionModel().clearSelection();

        /* OCULTAR CAMPOS */
        cmbEstadoCivil.setVisible(false);
        cmbGenero.setVisible(false);
        txtNombre.setVisible(false);
        txtApellidoP.setVisible(false);
        txtApellidoM.setVisible(false);
        dteNacimiento.setVisible(false);
        txtCURP.setVisible(false);
        txtRFC.setVisible(false);
        cmbEstado.setVisible(false);
        cmbMunicipio.setVisible(false);
        txtDireccion.setVisible(false);
        cmbEmpleo.setVisible(false);
        txtTelefono.setVisible(false);
        lblNombres.setVisible(false);
        lblApellidoP.setVisible(false);
        lblApellidoM.setVisible(false);
        lblCurp.setVisible(false);
        lblRfc.setVisible(false);
        lblDireccion.setVisible(false);
        lblTelefono.setVisible(false);
        lblFecha.setVisible(false);
        lblEstado.setVisible(false);
        lblMunicipio.setVisible(false);
        lblEmpleo.setVisible(false);
        lblEstadoC.setVisible(false);
        lblGenero.setVisible(false);
        btnRegistrar.setVisible(false);
        txtNumero.setEditable(true);
        imgBusqueda.setVisible(true);

    }


    @FXML
    public void buscarSocio() {

        List<Object[]> estados = servicio.traerEstados();

        cmbEstado.getItems().clear();
        for (Object[] fila : estados) {
            String nombreEstado = fila[1].toString();
            cmbEstado.getItems().add(nombreEstado);
        }
        if (!estados.isEmpty()) {
            cmbEstado.getSelectionModel().select(30);
        }

        List<Object[]> municipios = servicio.traeMunicipios(cmbEstado.getSelectionModel().getSelectedIndex() + 1);

        cmbMunicipio.getItems().clear();
        for (Object[] fila : municipios) {
            String nombreMunicipio = fila[1].toString();
            cmbMunicipio.getItems().add(nombreMunicipio);
        }
        if (!municipios.isEmpty()) {
            cmbMunicipio.getSelectionModel().select(100);
        }


        List<Object[]> empleos = servicio.traerEmpleos();

        cmbEmpleo.getItems().clear();
        for (Object[] fila : empleos) {
            String nombreEmpleos = fila[1].toString();
            cmbEmpleo.getItems().add(nombreEmpleos);
        }
        if (!empleos.isEmpty()) {
            cmbEmpleo.getSelectionModel().selectFirst();
        }

        List<Object[]> estadosC = servicio.traerEstadosC();

        cmbEstadoCivil.getItems().clear();
        for (Object[] fila : estadosC) {
            String nombreEstadosC = fila[1].toString();
            cmbEstadoCivil.getItems().add(nombreEstadosC);
        }
        if (!estadosC.isEmpty()) {
            cmbEstadoCivil.getSelectionModel().selectFirst();
        }

        cmbGenero.getItems().clear();
        cmbGenero.getItems().add("MASCULINO");
        cmbGenero.getItems().add("FEMENINO");
        cmbGenero.getItems().add("OTRO");

        cmbGenero.getSelectionModel().selectFirst();

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);
        if (socio == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SOCIO NO ENCONTRADO");
            alert.setHeaderText("SOCIO NO ENCONTRADO");
            alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
            alert.showAndWait();
            return;
        }

        String genero = socio.getGenero();

        switch (genero) {
            case "MASCULINO":
                cmbGenero.getSelectionModel().select(0);
                break;

            case "FEMENINO":
                cmbGenero.getSelectionModel().select(1);
                break;

            case "OTRO":
                cmbGenero.getSelectionModel().select(2);
                break;

            default:
                cmbGenero.getSelectionModel().clearSelection();
        }


        txtNombre.setText(socio.getNombres());
        txtApellidoP.setText(socio.getApellidoP());
        txtApellidoM.setText(socio.getApellidoM());
        dteNacimiento.setValue(socio.getFechaNacimiento());
        txtCURP.setText(socio.getCurp());
        txtRFC.setText(socio.getRfc() == null ? "" : socio.getRfc());
        cmbEstado.getSelectionModel().select(socio.getCatEstadoId() - 1);
        cmbMunicipio.getSelectionModel().select(socio.getCatMunicipioId() - 1);
        cmbEstadoCivil.getSelectionModel().select(socio.getEstadoCivilId() - 1);

        txtDireccion.setText(socio.getDireccion());
        cmbEmpleo.getSelectionModel().select(socio.getCatEmpId() - 1);
        txtTelefono.setText(socio.getTelefono());

        cmbEstadoCivil.setVisible(true);
        cmbGenero.setVisible(true);
        txtNombre.setVisible(true);
        txtApellidoP.setVisible(true);
        txtApellidoM.setVisible(true);
        dteNacimiento.setVisible(true);
        txtCURP.setVisible(true);
        txtRFC.setVisible(true);
        cmbEstado.setVisible(true);
        cmbMunicipio.setVisible(true);
        txtDireccion.setVisible(true);
        cmbEmpleo.setVisible(true);
        txtTelefono.setVisible(true);
        lblNombres.setVisible(true);
        lblApellidoP.setVisible(true);
        lblApellidoM.setVisible(true);
        lblCurp.setVisible(true);
        lblRfc.setVisible(true);
        lblDireccion.setVisible(true);
        lblTelefono.setVisible(true);
        lblFecha.setVisible(true);

        lblEstado.setVisible(true);
        lblMunicipio.setVisible(true);
        lblEmpleo.setVisible(true);
        lblEstadoC.setVisible(true);
        lblGenero.setVisible(true);
        btnBuscar.setVisible(true);
        btnLimpiar.setVisible(true);
        btnRegistrar.setVisible(true);

        txtNumero.setEditable(false);
        imgBusqueda.setVisible(false);

    }

    @FXML
    public void editarSocio() {
        String nombre = txtNombre.getText().trim();
        String ApellidoM = txtApellidoM.getText().trim();
        String ApellidoP = txtApellidoP.getText().trim();
        LocalDate fNacimiento = dteNacimiento.getValue();
        String curp = txtCURP.getText().trim();
        String rfc = txtRFC.getText().trim();
        int idEstado = cmbEstado.getSelectionModel().getSelectedIndex() + 1;
        int idMunicipio = cmbMunicipio.getSelectionModel().getSelectedIndex() + 1;
        int idEmpleo = cmbEmpleo.getSelectionModel().getSelectedIndex() + 1;
        int idEstadoCivil = cmbEstadoCivil.getSelectionModel().getSelectedIndex() + 1;
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();



        if (nombre.isEmpty() ||
                ApellidoP.isEmpty() ||
                ApellidoM.isEmpty() ||
                fNacimiento == null ||
                curp.isEmpty() ||
                direccion.isEmpty() ||
                telefono.isEmpty() ||
                cmbEstado.getSelectionModel().isEmpty() ||
                cmbMunicipio.getSelectionModel().isEmpty() ||
                cmbEmpleo.getSelectionModel().isEmpty() ||
                cmbEstadoCivil.getSelectionModel().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("DATOS INCOMPLETOS");
            alert.setHeaderText("FALTAN DATOS");
            alert.setContentText("TODOS LOS CAMPOS SON OBLIGATORIOS.");
            alert.showAndWait();
            return;
        }



        if (curp.length() != 18) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("CURP INCORRECTO");
            alert.setHeaderText("CURP INVÁLIDO");
            alert.setContentText("EL CURP DEBE TENER EXACTAMENTE 18 CARACTERES.");
            alert.showAndWait();
            return;
        }



        int edad = Period.between(fNacimiento, LocalDate.now()).getYears();
        int tipo = edad < 18 ? 2 : 1;


        if (edad >= 18) {
            if (rfc.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("RFC REQUERIDO");
                alert.setHeaderText("RFC OBLIGATORIO");
                alert.setContentText("LOS MAYORES DE EDAD DEBEN TENER RFC.");
                alert.showAndWait();
                return;
            }

            if (rfc.length() != 13) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("RFC INCORRECTO");
                alert.setHeaderText("RFC INVÁLIDO");
                alert.setContentText("EL RFC DEBE TENER EXACTAMENTE 13 CARACTERES.");
                alert.showAndWait();
                return;
            }
        }

        String genero = cmbGenero.getSelectionModel().getSelectedItem().toString();

        int numSocio = Integer.parseInt(txtNumero.getText());

        String res = servicio.editarSocio(
                numSocio,
                nombre,
                ApellidoP,
                ApellidoM,
                fNacimiento,
                curp,
                idEmpleo,
                direccion,
                idEstado,
                idMunicipio,
                rfc,
                idEstadoCivil,
                telefono,
                genero,
                LoginController.usuarioLoggeado, tipo
        );

        if (res.equalsIgnoreCase("CORRECTO")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ACTUALIZACIÓN EXITOSA");
            alert.setHeaderText("ACTUALIZACIÓN EXITOSA");
            alert.setContentText("EL SOCIO SE HA INSERTADO CORRECTAMENTE.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL ACTUALIZAR AL SOCIO");
            alert.setContentText(res.toUpperCase());
            alert.showAndWait();
        }

        limpiar();
    }

    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setDatosController(this);
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




}
