package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class GerenteController implements Initializable {

    @FXML
    private Label lblHora, lblFecha, lblUsuario;

    @FXML
    private StackPane contentArea;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Servicio servicio;

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





}
