package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.Main;
import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import com.tenpisoft.n2w.MoneyConverters;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class RetiroController implements Initializable {

    @FXML
    private Label lblNombre, lblEmpresa, lblAhorros, lblPSNgu, lblPSMut, lblDatos, lblMonto, lblRestante, lblForma, lblCancelar;

    @FXML
    private TextField txtNombre, txtEmpresa, txtAhorros, txtPSNgu, txtPSMut, txtMonto, txtRestante, txtNumero;

    @FXML
    private Button btnLimpiarMonto, btnProcesar;

    @FXML
    private ImageView imgBusqueda;

    @FXML
    private Separator separador;

    @FXML
    private ComboBox cmbForma;

    @Autowired
    private Servicio servicio;

    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<ModelFormaOperacion> formas = servicio.traerFormas();
        cmbForma.getItems().clear();

        for (ModelFormaOperacion forma : formas) {
            cmbForma.getItems().add(forma.getForma());
        }

        cmbForma.getSelectionModel().selectFirst();

        txtNumero.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9]", ""));

                            // Verifica si ya hay más de un punto decimal
                            if (change.getText().matches(".*\\..*\\..*")) {
                                change.setText(change.getText().substring(0, change.getText().lastIndexOf('.')));
                            }

                            return change;
                        }));

        txtMonto.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9.]", ""));

                            // Verifica si ya hay más de un punto decimal
                            if (change.getText().matches(".*\\..*\\..*")) {
                                change.setText(change.getText().substring(0, change.getText().lastIndexOf('.')));
                            }

                            return change;
                        }));
    }

    @FXML
    public void buscarSocio() {

        if (txtNumero.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("NÚMERO NO PROPORCIONADO");
            alert.setContentText("POR FAVOR, PROPORCIONE UN NÚMERO DE SOCIO.");
            alert.showAndWait();
            return;
        }

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText().trim()), true);

        if (socio == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SOCIO NO ENCONTRADO");
            alert.setHeaderText("SOCIO NO ENCONTRADO");
            alert.setContentText("NO EXISTE SOCIO ACTIVO CON ESE NÚMERO.");
            alert.showAndWait();
            return;
        }

        ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(Integer.parseInt(txtNumero.getText().trim()), 1);

        if (ahorro == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("CUENTA DE AHORRO INEXISTENTE");
            alert.setContentText("EL SOCIO QUE INTENTA BUSCAR NO TIENEN UNA CUENTA DE AHORRO ACTIVA");
            alert.showAndWait();
            return;
        }

        txtNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
        List<ModelCapitalSocial> cs = servicio.traerCuentasCs(socio.getNumSocio());

        if (cs != null) {
            for (ModelCapitalSocial cuenta : cs) {
                if(cuenta.getEmpresaCod().equalsIgnoreCase("0001")){
                    txtPSMut.setText(formatoMXN.format(cuenta.getMonto_cubierto()));
                }else{
                    txtPSNgu.setText(formatoMXN.format(cuenta.getMonto_cubierto()));
                }
            }
        }

        if(txtPSNgu.getText().isEmpty()){
            txtPSNgu.setText(formatoMXN.format(0.00));
        }

        if(txtPSMut.getText().isEmpty()){
            txtPSMut.setText(formatoMXN.format(0.00));
        }

        txtAhorros.setText(formatoMXN.format(ahorro.getSaldo()));

        if(socio.getEmpresaCod().equalsIgnoreCase("0001")){
            txtEmpresa.setText("MUTUALIDAD DOCE DE AGOSTO S.C. DE R.L. DE C.V.");
        }else {
            txtEmpresa.setText("NUEVA GENERACION DE UMAN, AC.");
        }

        txtNombre.setVisible(true);
        txtPSMut.setVisible(true);
        txtPSNgu.setVisible(true);
        txtEmpresa.setVisible(true);
        txtNumero.setEditable(false);
        txtAhorros.setVisible(true);
        txtMonto.setVisible(true);
        btnProcesar.setVisible(true);
        txtRestante.setVisible(true);
        imgBusqueda.setVisible(false);
        lblCancelar.setVisible(true);
        cmbForma.setVisible(true);
        lblForma.setVisible(true);
        lblNombre.setVisible(true);
        lblPSMut.setVisible(true);
        lblPSNgu.setVisible(true);
        lblAhorros.setVisible(true);
        lblMonto.setVisible(true);
        lblEmpresa.setVisible(true);
        lblRestante.setVisible(true);
        btnLimpiarMonto.setVisible(true);
        separador.setVisible(true);
        lblDatos.setVisible(true);
    }

    @FXML
    public void limpiar() {
        txtNombre.setVisible(false);
        txtNombre.clear();

        txtPSMut.setVisible(false);
        txtPSMut.clear();

        txtPSNgu.setVisible(false);
        txtPSNgu.clear();

        txtEmpresa.setVisible(false);
        txtEmpresa.clear();

        txtNumero.setEditable(true);
        txtNumero.clear();

        txtAhorros.setVisible(false);
        txtAhorros.clear();

        txtMonto.setVisible(false);
        txtMonto.clear();

        btnProcesar.setVisible(false);

        txtRestante.setVisible(false);
        txtRestante.clear();
        cmbForma.setVisible(false);

        imgBusqueda.setVisible(true);

        lblNombre.setVisible(false);
        lblPSMut.setVisible(false);
        lblPSNgu.setVisible(false);
        lblAhorros.setVisible(false);
        lblMonto.setVisible(false);
        txtMonto.setEditable(true);
        lblEmpresa.setVisible(false);
        lblRestante.setVisible(false);
        lblForma.setVisible(false);
        btnLimpiarMonto.setVisible(false);
        separador.setVisible(false);
        lblDatos.setVisible(false);
        lblCancelar.setVisible(false);
    }

    @FXML
    public void buscarSocioPorNombre() {
        try {
            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/busquedaSocio.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            BusquedaController controlador = fxml.getController();
            controlador.setRetiroController(this);
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

    @FXML
    public void procesarRetiro() {
        if(txtRestante.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER RETIRAR SALDO");
            alert.setContentText("INGRESE EL MONTO A RETIRAR");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("RETIRO DE SALDO");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        //Chequear si no ya tiene retiros pendientes
        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(Integer.parseInt(txtNumero.getText()), true);

        List<ModelRetiro> retirosPendientes = servicio.obtenerRetirosPendientes(socio.getNumSocio(), true);


        //CHECAR QUE SOLO HAYA UN R
        if (retirosPendientes.size() > 0) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR AL QUERER RETIRAR SALDO");
            alert.setContentText("EL SOCIO TIENE RETIROS PENDIENTES SIN PROCESAR.");
            alert.showAndWait();

            limpiar();

            return;
        }

        //Cuando es para cajas no hace falta generar ningún reporte
        if (cmbForma.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("EFECTIVO")) {
            //Construir el retiro y en el servicio se construye el retiro del cajero
            ModelRetiro retiro = new ModelRetiro();
            retiro.setSocio(socio.getNumSocio());
            retiro.setSaldoAnt(BigDecimal.valueOf(parseMoneda(txtAhorros.getText().trim())));
            retiro.setSaldoNue(BigDecimal.valueOf(parseMoneda(txtRestante.getText().trim())));
            retiro.setMontoRetiro(BigDecimal.valueOf(parseMoneda(txtMonto.getText().trim())));
            retiro.setForma(servicio.buscarFormaPorNombre(cmbForma.getSelectionModel().getSelectedItem().toString()).getId());
            retiro.setActivo(true);
            retiro.setEstado(true);
            ModelUsuario usuario = servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado);
            retiro.setUsuarioId(usuario.getId());
            retiro.setEmpresa(socio.getEmpresaCod());
            retiro.setFr(LocalDate.now());

            //guardamos aprende ramitos gaysito mariconsito
            servicio.realizarRetiroAhorros(retiro, 1);

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CORRECTO");
            alert.setHeaderText("RETIRO EXITOSO");
            alert.setContentText("RETIRO POR: " + txtMonto.getText() + " REALIZADO CORRECTAMENTE.");
            alert.showAndWait();
        } else {
            //Cuando sale en cheque directamente sacamos el reporte y ya solo se registra en RETIRO Y NO EN RETIRO_CAJERO
            Stage loadingStage = new Stage();
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            loadingStage.initStyle(StageStyle.UNDECORATED);
            loadingStage.setAlwaysOnTop(true);

            VBox loadingPane = new VBox(20);
            loadingPane.setAlignment(Pos.CENTER);
            loadingPane.setPadding(new Insets(30));
            loadingPane.setStyle("-fx-background-color: white; -fx-border-color: #185754; -fx-border-width: 2;");

            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setPrefSize(60, 60);

            Label loadingLabel = new Label("Generando Retiro...");
            loadingLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            loadingLabel.setTextFill(Color.web("#39577c"));

            loadingPane.getChildren().addAll(progressIndicator, loadingLabel);

            Scene loadingScene = new Scene(loadingPane, 300, 150);
            loadingStage.setScene(loadingScene);
            loadingStage.centerOnScreen();

            String txtAhorrosVal = txtAhorros.getText().trim();
            String txtRestanteVal = txtRestante.getText().trim();
            String txtMontoVal = txtMonto.getText().trim();
            LocalDateTime fecha = LocalDateTime.now();
            String fechaTicket = fecha.format(formatter);
            String montoanterior = txtAhorros.getText().trim();
            String montoactual = txtRestante.getText().trim();
            String montoretirado = txtMonto.getText().trim();


            try {

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        try {

                            InputStream isLogo;

                            LocalTime hora = fecha.toLocalTime();

                            ModelAhorro ahororoactual = servicio.traerCuentaAhorroPorNumSocio(socio.getNumSocio());

                            ModelEmpresa empresa = servicio.traerEmpresaXCodigo(socio.getEmpresaCod());

                            String horaFormateada = hora.format(formatterHora);
                            String nombreEmpresa = empresa.getNombre();
                            String rfcEmpresa = empresa.getRfc();
                            String direcEmpresa =
                                    empresa.getCalle()
                                            + " "
                                            + empresa.getCruzamiento()
                                            + " COL. CENTRO";

                            String numcuenta = ahororoactual.getNum_cuenta();
                            String nomsocio = socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM();

                            //Construimos el retiro
                            ModelRetiro retiro = new ModelRetiro();
                            retiro.setSocio(socio.getNumSocio());
                            retiro.setSaldoAnt(BigDecimal.valueOf(parseMoneda(txtAhorrosVal)));
                            retiro.setSaldoNue(BigDecimal.valueOf(parseMoneda(txtRestanteVal)));
                            retiro.setMontoRetiro(BigDecimal.valueOf(parseMoneda(txtMontoVal)));
                            retiro.setForma(servicio.buscarFormaPorNombre(cmbForma.getSelectionModel().getSelectedItem().toString()).getId());
                            retiro.setEstado(false);
                            retiro.setActivo(true);
                            ModelUsuario usuario = servicio.traerUsuarioXUsuario(LoginController.usuarioLoggeado);
                            retiro.setUsuarioId(usuario.getId());
                            retiro.setEmpresa(socio.getEmpresaCod());
                            retiro.setFr(LocalDate.now());


                            int idTran = servicio.realizarRetiroAhorros(retiro, 2);

                            String folio = String.valueOf(idTran);

                            MoneyConverters converter = MoneyConverters.SPANISH_BANKING_MONEY_VALUE;
                            String moneyAsWords =
                                    converter.asWords(BigDecimal.valueOf(parseMoneda(txtMontoVal))).toUpperCase() + " MXN";

                            if (empresa.equals("0001")) {
                                isLogo = getClass().getResourceAsStream("/assets/images/logo-mut.png");
                            } else {
                                isLogo = getClass().getResourceAsStream("/assets/images/logo-ngu.jpg");
                            }

                            Map pars = new HashMap<>();
                            pars.put("Empresa", nombreEmpresa);
                            pars.put("Logo", isLogo);
                            pars.put("Rfc", rfcEmpresa);
                            pars.put("Direccion", direcEmpresa);
                            pars.put("Titulo", "REPORTE DE RETIRO DE AHORROS");
                            pars.put("Montoanterior", montoanterior);
                            pars.put("Fecha", fechaTicket);
                            pars.put("Id", folio);
                            pars.put("Numsocio", String.valueOf(socio.getNumSocio()));
                            pars.put("Nombresocio", nomsocio);
                            pars.put("Numcuenta", numcuenta);
                            pars.put("forma", "Cheque");
                            pars.put("Montoretirado", montoretirado);
                            pars.put("Montorestante", montoactual);
                            pars.put("Montoletras", moneyAsWords);
                            pars.put("Cajero", LoginController.usuarioLoggeado);
                            pars.put("Hora", horaFormateada);

                            if (empresa.equals("0001")) {
                                pars.put(
                                        "Descripcion",
                                        "Recibí de la " + nombreEmpresa
                                                + " la cantidad de " + montoretirado
                                                + " (" + moneyAsWords + ") por concepto de RETIRO DE CUENTA DE AHORRO."
                                );
                            } else {
                                pars.put(
                                        "Descripcion",
                                        "Recibí de " + nombreEmpresa
                                                + " la cantidad de " + montoretirado
                                                + " (" + moneyAsWords + ") por concepto de RETIRO DE CUENTA DE AHORRO."
                                );
                            }

                            InputStream isRepo =
                                    getClass().getResourceAsStream("/Reports/retiro.jasper");

                            JasperReport jrRepo = (JasperReport) JRLoader.loadObject(isRepo);
                            JasperPrint jpRepo =
                                    JasperFillManager.fillReport(jrRepo, pars, new JREmptyDataSource());

                            Platform.runLater(() -> {
                                JasperViewer viewer = new JasperViewer(jpRepo, false);
                                viewer.setAlwaysOnTop(true);
                                viewer.setSize(800, 600);
                                viewer.setLocationRelativeTo(null);
                                viewer.setTitle("REPORTE DE RETIRO");
                                viewer.setVisible(true);
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("ERROR");
                                alert.setHeaderText("ERROR AL GENERAR EL REPORTE");
                                alert.setContentText("OCURRIÓ UN ERROR: " + e.getMessage());
                                alert.showAndWait();
                            });
                        }
                        return null;
                    }
                };

                task.setOnSucceeded(e -> loadingStage.close());
                task.setOnFailed(e -> loadingStage.close());

                loadingStage.show();
                new Thread(task).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }





        limpiar();

    }

    @FXML
    public void calcularMontos() {

        if (txtMonto.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR ");
            alert.setContentText(
                    "POR FAVOR, ESCRIBA EL MONTO A RETIRAR");
            alert.showAndWait();
            return;
        }

        if (Double.parseDouble(txtMonto.getText().trim()) <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR ");
            alert.setContentText(
                    "POR FAVOR, DIGITE UN MONTO MAYOR A CERO");
            alert.showAndWait();
            return;
        }

        double monto = Double.parseDouble(txtMonto.getText());

        ModelAhorro ahorro = servicio.traerCuentaAhorroPorNumSocioYEstado(Integer.parseInt(txtNumero.getText()), 1);

        double montoRestante = 0;

        if (ahorro.getSaldo() <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR");
            alert.setContentText(
                    "EL SOCIO NO TIENE SALDO PARA RETIRAR");
            alert.showAndWait();
            return;
        }

        if (ahorro.getSaldo() < monto) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR");
            alert.setContentText(
                    "EL MONTO A RETIRAR ES MAYOR AL SALDO DEL AHORRO");
            alert.showAndWait();
            return;
        }

        montoRestante = ahorro.getSaldo() - monto;

        if(montoRestante < 6000){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR");
            alert.setContentText(
                    "EL MONTO RESTANTE NO PUEDE SER MENOR A $6,000.00");
            alert.showAndWait();
            return;
        }
        txtMonto.setTextFormatter(null);
        txtMonto.clear();
        txtMonto.setText(formatoMXN.format(monto));
        txtRestante.setText(formatoMXN.format(montoRestante));
        txtMonto.setEditable(false);
    }


    @FXML
    public void verRetirosCancelar(){
        try {

            Stage nuevaVentana = new Stage();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/verRetirosCancelacion.fxml"));
            fxml.setControllerFactory(Main.context::getBean);
            Scene nuevaEscena = new Scene(fxml.load());
            CancelarRetiroController controlador = fxml.getController();
            controlador.setDatos(Integer.parseInt(txtNumero.getText()), txtNombre.getText());
            nuevaEscena
                    .getStylesheets()
                    .add(getClass().getResource("/assets/css/estilos.css").toExternalForm());
            nuevaVentana.setTitle("CANCELAR RETIRO");
            Image icon = new Image(getClass().getResourceAsStream("/assets/images/logo.png"));
            nuevaVentana.getIcons().add(icon);
            nuevaVentana.setAlwaysOnTop(false);
            nuevaVentana.setScene(nuevaEscena);
            nuevaVentana.setResizable(false);
            nuevaVentana.initModality(Modality.APPLICATION_MODAL);
            nuevaVentana.centerOnScreen();
            nuevaVentana.show();
        }catch (Exception e){

            e.printStackTrace();
        }

    }


    @FXML
    public void limpiarMontos(){
        txtRestante.clear();
        txtMonto.clear();
        txtMonto.setEditable(true);
        txtMonto.setTextFormatter(
                new TextFormatter<>(
                        change -> {
                            // Permite solo dígitos y el punto decimal
                            change.setText(change.getText().replaceAll("[^0-9.]", ""));

                            // Verifica si ya hay más de un punto decimal
                            if (change.getText().matches(".*\\..*\\..*")) {
                                change.setText(change.getText().substring(0, change.getText().lastIndexOf('.')));
                            }

                            return change;
                        }));
    }

    private double parseMoneda(String moneda) {
        try {
            Number numero = formatoMXN.parse(moneda);
            return numero.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void cargarSocioPorNombre(String numero) {
        txtNumero.setText(numero);
        buscarSocio();
    }


}
