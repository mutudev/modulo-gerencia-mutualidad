package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class GerenteController implements Initializable {
    @FXML
    private Label lblHora, lblFecha, lblUsuario,
            lblInscripciones, lblInfoSocio, lblBloqSocio, lblBajaSocio,
            lblBeneficiarios, lblAbrirCuenta, lblListado, lblInicio,
            lblAhorros, lblCongelamiento, lblBloquearAhorro, lblRetiro,
            lblCuentaCap, lblCuentaPrev,
            lblVerCuotas, lblModCreditos, lblCondonaciones,
            lblMesesMinimo, lblDiasSinInteres,
            lblAddUsuario, lblVerUsuario, lblActivarUsuario,
            lblContra, lblPermisos,
            lblOperaciones, lblSaldosCajero, lblTraslados,
            lblCierre, lblConfig, lblUtilidades, lblReportes;

    @FXML
    private StackPane contentArea;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Servicio servicio;

    private Map<String, Label> labelMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        iniciarReloj();
        try {
            inicio();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocalDateTime fecha = LocalDateTime.now();
        lblFecha.setText(fecha.getDayOfMonth() + "/" + fecha.getMonthValue() + "/" + fecha.getYear());
        Platform.runLater(() -> {
            Stage stage = (Stage) lblHora.getScene().getWindow();
            stage.setOnCloseRequest(event -> cierreDeVentana(event));
        });
    }

    public void cierreDeVentana(Event event) {
        event.consume();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CIERRE DE APLICATIVO");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA CERRAR EL APLICATIVO?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage ventanaActual = (Stage) lblHora.getScene().getWindow();
            ventanaActual.close();
        }
    }

    public void iniciarReloj() {
        Thread hiloReloj =
                new Thread(
                        () -> {
                            SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                            while (true) {
                                try {
                                    String horaActual = formatoHora.format(new Date());
                                    Platform.runLater(() -> lblHora.setText(horaActual));
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        hiloReloj.setDaemon(true);
        hiloReloj.start();
    }

    public void setUsario() {
        lblUsuario.setText(LoginController.usuarioLoggeado);
        generarModulos();
    }

    @FXML
    public void inicio() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/inicio.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void inscripcion() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/registroSocio.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
   }

    @FXML
    public void verInfo() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/verSocio.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void bloquearSocio() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/bloquearSocio.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void bajaSocio() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/bajaSocio.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }


    @FXML
    public void agregarBeneficiarios() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/beneficiarios.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }


    @FXML
    public void agregarCuenta() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/crearCuenta.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void listado_general() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/listadoGeneral.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }


    @FXML
    public void insertarUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/registroUsuario.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void verUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/verUsuarios.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }


    @FXML
    public void cambiarEstadoUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/bajaUsuario.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void cambiarContra() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/cambiarContra.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void permisosUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/permisosUsuario.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void verAhorros() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/saldosAhorro.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void congelarSaldo() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/congelamientoSaldo.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void bloquearCuenta() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/bloquearCuentaAhorro.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void retiro() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/retiros.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void configuraciones() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/configuraciones.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void operacionesCajero() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/operacionesCajero.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void saldosCajero() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/saldosCajero.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }



    @FXML
    public void cierreDiario() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/cierreGeneral.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void capSocial() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/crearCuentaCS.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void previsionSocial() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/crearCuentaPSOC.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void verCuotasCredito() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/verCuotasCredito.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void modificarCredito() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/modificarCreditos.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void condonaciones() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/condonaciones.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void diasSinInteres() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/diasSinIntereses.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }



    @FXML
    public void utilidades() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/utilidades.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    @FXML
    public void reportes() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/reportes.fxml")
        );

        loader.setControllerFactory(context::getBean);

        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
    }

    public void generarModulos() {

        ModelUsuario usuario =
                servicio.traerUsuario(LoginController.usuarioLoggeado);

        List<Object[]> result =
                servicio.traerModulos(usuario.getId());

        lblInicio.setVisible(false);
        lblInscripciones.setVisible(false);
        lblInfoSocio.setVisible(false);
        lblBloqSocio.setVisible(false);
        lblBajaSocio.setVisible(false);
        lblBeneficiarios.setVisible(false);
        lblAbrirCuenta.setVisible(false);
        lblListado.setVisible(false);

        lblAhorros.setVisible(false);
        lblCongelamiento.setVisible(false);
        lblBloquearAhorro.setVisible(false);
        lblRetiro.setVisible(false);
        lblCuentaCap.setVisible(false);
        lblCuentaPrev.setVisible(false);

        lblVerCuotas.setVisible(false);
        lblModCreditos.setVisible(false);
        lblCondonaciones.setVisible(false);
        lblMesesMinimo.setVisible(false);
        lblDiasSinInteres.setVisible(false);

        lblAddUsuario.setVisible(false);
        lblVerUsuario.setVisible(false);
        lblActivarUsuario.setVisible(false);
        lblContra.setVisible(false);
        lblPermisos.setVisible(false);

        lblOperaciones.setVisible(false);
        lblSaldosCajero.setVisible(false);
        lblTraslados.setVisible(false);

        lblCierre.setVisible(false);
        lblConfig.setVisible(false);
        lblUtilidades.setVisible(false);
        lblReportes.setVisible(false);

        labelMap.clear();

        labelMap.put("INICIO", lblInicio);
        labelMap.put("INSCRIPCIONES", lblInscripciones);
        labelMap.put("VER INFORMACION", lblInfoSocio);
        labelMap.put("BLOQUEAR SOCIO", lblBloqSocio);
        labelMap.put("DAR DE BAJA SOCIO", lblBajaSocio);
        labelMap.put("AGREGAR BENEFICIARIOS", lblBeneficiarios);
        labelMap.put("ABRIR CUENTA", lblAbrirCuenta);
        labelMap.put("LISTADO GENERAL", lblListado);

        labelMap.put("SALDO POR SOCIO", lblAhorros);
        labelMap.put("CONGELAMIENTO DE SALDO", lblCongelamiento);
        labelMap.put("BLOQUEAR CUENTAS DE AHORRO", lblBloquearAhorro);
        labelMap.put("RETIROS", lblRetiro);
        labelMap.put("CUENTA CAPITAL SOCIAL", lblCuentaCap);
        labelMap.put("CUENTA PREVISION SOCIAL", lblCuentaPrev);

        labelMap.put("VER CUOTAS DE CREDITO", lblVerCuotas);
        labelMap.put("MODIFICACION DE CREDITOS", lblModCreditos);
        labelMap.put("CONDONACIONES", lblCondonaciones);
        labelMap.put("MESES MINIMO DE CREDITO", lblMesesMinimo);
        labelMap.put("DIAS SIN INTERESES", lblDiasSinInteres);

        labelMap.put("CREAR USUARIOS", lblAddUsuario);
        labelMap.put("VER USUARIOS", lblVerUsuario);
        labelMap.put("ACTIVAR BAJA USUARIOS", lblActivarUsuario);
        labelMap.put("CAMBIAR CONTRA", lblContra);
        labelMap.put("PERMISOS", lblPermisos);

        labelMap.put("OPERACIONES POR CAJERO", lblOperaciones);
        labelMap.put("SALDOS POR CAJERO", lblSaldosCajero);

        labelMap.put("CIERRE", lblCierre);
        labelMap.put("CONFIGURACIONES", lblConfig);
        labelMap.put("UTILIDADES", lblUtilidades);
        labelMap.put("REPORTES", lblReportes);
        labelMap.put("TRASLADOS", lblTraslados);

        for (Object[] row : result) {

            String modulo = row[2].toString();

            Label label = labelMap.get(modulo);

            if (label != null) {
                label.setVisible(true);
                label.setDisable(false);
                label.setCursor(Cursor.HAND);
            }
        }
    }





}
