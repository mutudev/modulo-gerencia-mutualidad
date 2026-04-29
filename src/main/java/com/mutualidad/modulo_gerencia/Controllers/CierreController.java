package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Models.ModelCaja;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CierreController {

    @FXML
    private Button btnProcesar;

    @Autowired
    public Servicio servicio;

    @FXML
    public void procesarCierre() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CIERRE DE SISTEMA");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR EL CIERRE DEL SISTEMA?");
        alert.setContentText("EN CASO DE QUE SÍ, PRESIONE ACEPTAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
        }

        List<ModelCaja> cajas = servicio.traerCajasActivas(true);

        if (cajas.size() != 0) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL CERRAR EL SISTEMA");
            alert.setContentText("EXISTEN CUENTAS DE CAJERO ACTIVAS");
            alert.showAndWait();
            return;
        }


        btnProcesar.setDisable(true);


        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initStyle(StageStyle.UNDECORATED);
        loadingStage.setAlwaysOnTop(true);

        loadingStage.setOnCloseRequest(e -> e.consume());

        VBox loadingPane = new VBox(20);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setPadding(new Insets(30));
        loadingPane.setStyle("-fx-background-color: white; -fx-border-color: #185754; -fx-border-width: 2;");

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(60, 60);

        Label loadingLabel = new Label(
                "Procesando cierre...\n\nNO CIERRE ESTE MÓDULO HASTA QUE FINALICE"
        );
        loadingLabel.setWrapText(true);
        loadingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #39577c;");

        loadingPane.getChildren().addAll(progressIndicator, loadingLabel);

        Scene scene = new Scene(loadingPane, 320, 160);
        loadingStage.setScene(scene);
        loadingStage.centerOnScreen();


        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return servicio.procesarCierre("");
            }
        };


        task.setOnSucceeded(e -> {
            loadingStage.close();
            btnProcesar.setDisable(false); // 🔓 desbloquear

            String res = task.getValue();

            if (res.equalsIgnoreCase("CORRECTO")) {
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("CIERRE EXITOSO");
                ok.setHeaderText("CIERRE EXITOSO");
                ok.setContentText("EL CIERRE SE REALIZÓ CORRECTAMENTE");
                ok.showAndWait();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("ERROR");
                error.setHeaderText("ERROR");
                error.setContentText(res.toUpperCase());
                error.showAndWait();
            }
        });


        task.setOnFailed(e -> {
            loadingStage.close();
            btnProcesar.setDisable(false);

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("ERROR");
            error.setHeaderText("ERROR EN EL CIERRE");
            error.setContentText("OCURRIÓ UN ERROR: " + task.getException().getMessage());
            error.showAndWait();
        });

        loadingStage.show();
        new Thread(task).start();
    }

}
