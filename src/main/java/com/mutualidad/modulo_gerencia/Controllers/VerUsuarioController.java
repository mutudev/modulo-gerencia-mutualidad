package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class VerUsuarioController implements Initializable {

    @FXML
    private TableView<Map<String, String>> tblUsuarios;

    @FXML
    private TableColumn<Map<String, String>, String> colID;

    @FXML
    private TableColumn<Map<String, String>, String> colUsuario;

    @FXML
    private TableColumn<Map<String, String>, String> colNombre;



    @FXML
    private TextField txtUsuario;

    @Autowired
    private Servicio servicio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colID.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("idUsuario"))
        );

        colUsuario.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("usuario_nom"))
        );

        colNombre.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().get("nombre"))
        );

    }

    @FXML
    public void mostrarDatosUsuario(MouseEvent event) throws Exception {
        int idUsuario = 0;
        if(event.getClickCount() == 2 && tblUsuarios.getSelectionModel().getSelectedItem() != null){
            idUsuario = Integer.parseInt(colID.getCellData(tblUsuarios.getSelectionModel().getSelectedIndex()));
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/datosUsuario.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            DatosUsuarioDetalleController controlador = fxml.getController();
            controlador.settearDatos(idUsuario);
            nuevaEscena
                    .getStylesheets()
                    .add(getClass().getResource("/assets/css/estilos.css").toExternalForm());
            nuevaVentana.setTitle("INFORMACIÓN USUARIO");
            Image icon = new Image(getClass().getResourceAsStream("/assets/images/logo.png"));
            nuevaVentana.getIcons().add(icon);
            nuevaVentana.setAlwaysOnTop(false);
            nuevaVentana.setScene(nuevaEscena);
            nuevaVentana.setResizable(false);
            nuevaVentana.centerOnScreen();
            nuevaVentana.show();
            limpiar();

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

        tblUsuarios.getItems().clear();

        String busqueda= txtUsuario.getText();


        List<Object[]> similitudes = servicio.traerDetalleUsuarios(busqueda);

        for(Object[] user : similitudes){
            Map<String, String> usuariosLista = new HashMap<>();
            String nombre = user[2].toString();
            String nomUsuario = user[1].toString();
            String id = user[0].toString();

            usuariosLista.put("idUsuario", id );
            usuariosLista.put("usuario_nom", nomUsuario);
            usuariosLista.put("nombre", nombre);
            tblUsuarios.getItems().add(usuariosLista);
        }
    }



    @FXML
    public void limpiar(){
        tblUsuarios.getItems().clear();
        txtUsuario.clear();
    }

}
