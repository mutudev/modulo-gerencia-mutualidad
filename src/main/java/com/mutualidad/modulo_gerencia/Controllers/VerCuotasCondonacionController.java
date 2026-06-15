package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.DTO.PagoCuotaDTO;
import com.mutualidad.modulo_gerencia.Models.ModelCredito;
import com.mutualidad.modulo_gerencia.Models.ModelCuotas;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Models.ModelTipoCredito;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class VerCuotasCondonacionController implements Initializable {

    @Autowired
    private Servicio servicio;

    // TABLA
    @FXML
    private TableView<PagoCuotaDTO> tablaCuotas;

    @FXML
    private TableColumn<PagoCuotaDTO, String> cuota;

    @FXML
    private TableColumn<PagoCuotaDTO, String> fecha;

    @FXML
    private TableColumn<PagoCuotaDTO, String> colcap;

    @FXML
    private TableColumn<PagoCuotaDTO, String> ord;

    @FXML
    private TableColumn<PagoCuotaDTO, String> colmora;

    @FXML
    private TableColumn<PagoCuotaDTO, String> coliva;

    @FXML
    private TableColumn<PagoCuotaDTO, String> bonif;

    @FXML
    private TableColumn<PagoCuotaDTO, String> tot;

    @FXML
    private TextField txtSaldoCredito, txtCuotaSelec, txtTotalCuota, txtInteresCon, txtMoraCon, txtCapCon;

    @FXML
    private Label lblPlazo, lblTasa, lblMora, lblTipo, lblNombre, lblNumSocio, lblCredito,
            lblTituloCondona, lblCuotaSelecc, lblTotCuota, lblInteresCon, lblMoraCon, lblCapCon;

    @FXML
    private CheckBox checkCastigo;

    public ModelCredito creditoEncontrado = null;

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FMT_MONEDA = NumberFormat.getCurrencyInstance(Locale.US);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        aplicarFormatterNumerico();
        configurarTabla();
    }

    private void configurarTabla() {

        cuota.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(data.getValue().getNumCuota())
                ));

        fecha.setCellValueFactory(data -> {
            LocalDate f = data.getValue().getFechaP();
            return new SimpleStringProperty(
                    f != null ? f.format(FMT_FECHA) : ""
            );
        });

        colcap.setCellValueFactory(data -> {
            BigDecimal v = data.getValue().getCapital();
            return new SimpleStringProperty(
                    v != null ? FMT_MONEDA.format(v) : ""
            );
        });

        ord.setCellValueFactory(data -> {
            BigDecimal v = data.getValue().getIntereses();
            return new SimpleStringProperty(
                    v != null ? FMT_MONEDA.format(v) : ""
            );
        });

        colmora.setCellValueFactory(data -> {
            BigDecimal v = data.getValue().getMora();

            LocalDate hoy = servicio.traerFechaHoy();
            LocalDate fechaFila = data.getValue().getFechaP();

            if (hoy.isAfter(fechaFila.plusDays(29))) {
                return new SimpleStringProperty(
                        v != null ? FMT_MONEDA.format(v) : ""
                );
            } else {
                return new SimpleStringProperty(
                        FMT_MONEDA.format(0)
                );
            }

        });

        coliva.setCellValueFactory(data -> {
            BigDecimal v = data.getValue().getIva();
            return new SimpleStringProperty(
                    v != null ? FMT_MONEDA.format(v) : ""
            );
        });

        bonif.setCellValueFactory(data -> {
            BigDecimal v = data.getValue().getBonif();
            return new SimpleStringProperty(
                    v != null ? FMT_MONEDA.format(v) : ""
            );
        });

        tot.setCellValueFactory(data -> {
            BigDecimal v = data.getValue().getTotal();

            return new SimpleStringProperty(
                    v != null ? FMT_MONEDA.format(v) : ""
            );

        });

        tablaCuotas.setRowFactory(tv -> new TableRow<PagoCuotaDTO>() {
            @Override
            protected void updateItem(PagoCuotaDTO item, boolean empty) {
                super.updateItem(item, empty);

                // limpiar estilos anteriores
                setStyle("");

                if (empty || item == null || item.getFechaP() == null) {
                    return;
                }

                if (item.getIsCondonado()) {
                    setDisable(true);
                    setMouseTransparent(true);
                    setStyle("-fx-opacity: 0.5;");
                    return;
                }

                LocalDate hoy = servicio.traerFechaHoy();
                LocalDate fechaFila = item.getFechaP();

                long dias = ChronoUnit.DAYS.between(fechaFila, hoy);

                // 1 día después -> verde (Atrasada)
                if (dias >= 1 && dias < 90) {
                    setStyle("-fx-background-color: #90EE90;");
                }

                // 29 días o más -> amarillo (Vencida)
                if (dias >= 90) {
                    setStyle("-fx-background-color: #FFF59D;");
                }
            }
        });
    }

    private void cargarCuotas() {

        tablaCuotas.getItems().clear();

        List<PagoCuotaDTO> lista =
                servicio.calcularPagoDeCuotas(
                        creditoEncontrado.getId(),
                        creditoEncontrado.getTasa(),
                        creditoEncontrado.getMora(),
                        creditoEncontrado.getIva(),
                        creditoEncontrado.getFd()
                );


        List<PagoCuotaDTO> primeras3 = lista.stream()
                .limit(lista.size())
                .collect(toList());

        ObservableList<PagoCuotaDTO> datos =
                FXCollections.observableArrayList(primeras3);

        tablaCuotas.setItems(datos);

        txtSaldoCredito.setText(FMT_MONEDA.format(creditoEncontrado.getSaldo()));

        ModelSocio socio = servicio.traerSocioPorNumeroYEstado(creditoEncontrado.getSocio(), true);

        lblNombre.setText(socio.getNombres() + " " + socio.getApellidoP() + " " + socio.getApellidoM());
        lblNumSocio.setText(String.valueOf(socio.getNumSocio()));
        lblCredito.setText(String.valueOf(creditoEncontrado.getId()));
        lblPlazo.setText("Plazo: "+ creditoEncontrado.getPlazo() + " meses");
        lblTasa.setText("Tasa Ordinaria: " + creditoEncontrado.getTasa() +"%");
        lblMora.setText("Tasa Moratoria: "+ creditoEncontrado.getMora() +"%");
        ModelTipoCredito tipoCredito = servicio.traerTipoCreditoConId(Long.valueOf(creditoEncontrado.getTipo_credito())).get();

        lblTipo.setText("Código: " + tipoCredito.getCodigoSistema());


    }

    public void setDatos(int creditoId) {
        this.creditoEncontrado = servicio.traerDatosCredito(creditoId).get();
        cargarCuotas();
    }

    @FXML
    public void cargarCuota(MouseEvent event) {
        if (event.getClickCount() == 2) {
            PagoCuotaDTO pagoCuotaDTO = tablaCuotas.getSelectionModel().getSelectedItem();
            if (pagoCuotaDTO != null) {
                txtCuotaSelec.setText(String.valueOf(pagoCuotaDTO.getNumCuota()));
                txtTotalCuota.setText(FMT_MONEDA.format(pagoCuotaDTO.getTotal()));
                txtMoraCon.setText(FMT_MONEDA.format(pagoCuotaDTO.getMora()));
                txtInteresCon.setText(FMT_MONEDA.format(pagoCuotaDTO.getIntereses()));
                tablaCuotas.setDisable(true);
                checkCastigo.setDisable(true);
            }
        }
    }

    @FXML
    public void permitirCastigo() {

        if (checkCastigo.isSelected()) {
            lblCapCon.setDisable(false);
            txtCapCon.setDisable(false);
            lblTituloCondona.setText("Indique el Capital a Condonar");
            lblCuotaSelecc.setDisable(true);
            txtCuotaSelec.setDisable(true);
            txtCuotaSelec.clear();
            lblTotCuota.setDisable(true);
            txtTotalCuota.setDisable(true);
            txtTotalCuota.clear();
            lblInteresCon.setDisable(true);
            txtInteresCon.setDisable(true);
            txtInteresCon.clear();
            lblMoraCon.setDisable(true);
            txtMoraCon.setDisable(true);
            txtMoraCon.clear();
            tablaCuotas.setDisable(true);
        } else {
            lblCapCon.setDisable(true);
            txtCapCon.setDisable(true);
            txtCapCon.clear();

            lblTituloCondona.setText("Seleccione la Cuota a Condonar");

            lblCuotaSelecc.setDisable(false);
            txtCuotaSelec.setDisable(false);

            lblTotCuota.setDisable(false);
            txtTotalCuota.setDisable(false);

            lblInteresCon.setDisable(false);
            txtInteresCon.setDisable(false);

            lblMoraCon.setDisable(false);
            txtMoraCon.setDisable(false);
            tablaCuotas.setDisable(false);
        }


    }

    @FXML
    public void limpiar() {
        lblCapCon.setDisable(true);
        txtCapCon.setDisable(true);
        txtCapCon.clear();

        lblTituloCondona.setText("Seleccione la Cuota a Condonar");

        lblCuotaSelecc.setDisable(false);
        txtCuotaSelec.setDisable(false);
        txtCuotaSelec.clear();

        lblTotCuota.setDisable(false);
        txtTotalCuota.setDisable(false);
        txtTotalCuota.clear();

        lblInteresCon.setDisable(false);
        txtInteresCon.setDisable(false);
        txtInteresCon.clear();

        lblMoraCon.setDisable(false);
        txtMoraCon.setDisable(false);
        txtMoraCon.clear();

        aplicarFormatterNumerico();
        txtCapCon.setEditable(true);
        tablaCuotas.setDisable(false);

        checkCastigo.setSelected(false);
        checkCastigo.setDisable(false);
    }

    @FXML
    public void aplicarCondonacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("APLICACIÓN DE CONDONACIÓN");
        alert.setHeaderText("¿ESTÁ SEGURO QUE DESEA REALIZAR LA OPERACIÓN?");
        alert.setContentText(
                "EN CASO DE QUE SÍ, PRESIONE ACEPTAR, EN CASO CONTRARIO PRESIONE CANCELAR");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK)  {
            return;
        }

        LocalDate hoy = servicio.traerFechaHoy();

        if (txtCapCon.isDisable()) {
            //Si está deshabilitado esto, es la condonación de los intereses de la cuota solo
            PagoCuotaDTO pagoCuotaDTO = tablaCuotas.getSelectionModel().getSelectedItem();

            if (txtCuotaSelec.getText().isEmpty() || txtInteresCon.getText().isEmpty() || txtMoraCon.getText().isEmpty()
            || txtTotalCuota.getText().isEmpty() || pagoCuotaDTO == null) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL PROCESAR LA OPERACIÓN");
                alert.setContentText("POR FAVOR, RELLENE TODOS LOS CAMPOS Y/O SELECCIONE UNA CUOTA");
                alert.showAndWait();
                return;
            }


            if(pagoCuotaDTO.getNumCuota() != servicio.traerProximaACondonar(pagoCuotaDTO.getCreditoId()).getNumCuota()){
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL PROCESAR LA OPERACIÓN");
                alert.setContentText("LA CUOTA QUE DESEA CONDONAR NO ES LA CONSECUTIVA");
                alert.showAndWait();
                return;
            }

            if(parseMoneda(txtInteresCon.getText()) <= 0){
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL PROCESAR LA OPERACIÓN");
                alert.setContentText("LA CUOTA QUE DESEA CONDONAR NO TIENE INTERESES AÚN");
                alert.showAndWait();
                return;
            }

            //lo que hay que hacer es lo siguiente, aquí a la cuota hay que settearle el isCondonado en 1,
            //además, hay que eliminarle los acumulados y hay que dejarle los intereses que pagó como quedó, básicamente
            //dejar todo igual solo settear acumulados y el isCondonado
            //Ya en el sp, ver hasta el final, si isCondonado = 1 pues no se calcula nada
            ModelCuotas cuota = servicio.obtenerCuotaPorNumeroYCredito(pagoCuotaDTO.getNumCuota(), creditoEncontrado.getId());
            BigDecimal totalCondonado = pagoCuotaDTO.getIntereses();
            BigDecimal moraCondonado = BigDecimal.ZERO;

            if (hoy.isAfter(pagoCuotaDTO.getFechaP().plusDays(29))) {
                totalCondonado = totalCondonado.add(pagoCuotaDTO.getMora());
                moraCondonado = pagoCuotaDTO.getMora();
            }

            BigDecimal interesCondonado = pagoCuotaDTO.getIntereses();
            BigDecimal interesAcumulado = pagoCuotaDTO.getInteresesAcumulados();
            BigDecimal moraAcumulado = pagoCuotaDTO.getMoraAcumulados();

            cuota.setInteresAcumulado(BigDecimal.ZERO);
            cuota.setMoraAcumulado(BigDecimal.ZERO);
            cuota.setIsCondonado(true);



            List<ModelCuotas> noTocadas = new ArrayList<>();
            //Acumular los intereses de las no afectadas
            for (int i = pagoCuotaDTO.getNumCuota(); i < tablaCuotas.getItems().size(); i++) {
                PagoCuotaDTO cuotaPendiente = tablaCuotas.getItems().get(i);

                ModelCuotas cuotaNoAfectada = servicio.obtenerCuotaPorNumeroYCredito(cuotaPendiente.getNumCuota(), creditoEncontrado.getId());

                if (cuotaPendiente.getIntereses().doubleValue() > 0) {
                    cuotaNoAfectada.setInteresAcumulado(
                            BigDecimal.valueOf(cuotaPendiente.getIntereses().doubleValue() + cuotaPendiente.getBonif().doubleValue())
                    );
                }

                if (cuotaPendiente.getMora().doubleValue() > 0) {
                    cuotaNoAfectada.setMoraAcumulado(
                            cuotaPendiente.getMora()
                    );
                }

                noTocadas.add(cuotaNoAfectada);
            }

            //hay que ver si ya hizo un pago o no, porque puede ser que se condones cuotas muy atrasadas q no tienen pagos
            if (cuota.getFechaPRealizada() == null) {
                cuota.setFechaAnterior(hoy);

            } else {
                //Aqui pues ya tenía hecho un pago así que no le movemos lo que pagó sino solo las fechas
                cuota.setFechaAnterior(cuota.getFechaPRealizada());
            }
            cuota.setFechaPRealizada(hoy);

            //Guardar
            cuota = servicio.guardarCuota(cuota, creditoEncontrado, totalCondonado, hoy, interesCondonado, moraCondonado,
                    interesAcumulado, moraAcumulado, noTocadas);

            if (cuota.getIsCondonado()) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("EXITO");
                alert.setHeaderText("EXITO AL CONDONAR");
                alert.setContentText("CUOTA: " + cuota.getNumCuota() + " CONDONADA CON EXITO.");
                alert.showAndWait();
                limpiar();
                cargarCuotas();
            }

        } else {
            //sino, es una condonación total de cap e intereses dependiendo del capital que se ingrese
            //por ello, lo que hay que realizar es obtener el capital e ir descontando, cabe aclarar
            //es básicamente ir restando como en F1, si en una cuota se gasta el capital dado, se descuenta
            //y se condona, si el capital dado sobre pasa el capital pues se condona y se salda
            List<ModelCuotas> cuotasList = new ArrayList<>();
            BigDecimal capitalDado = BigDecimal.valueOf(parseMoneda(txtCapCon.getText().trim()));
            BigDecimal totalCondonado = BigDecimal.valueOf(0);
            List<PagoCuotaDTO> copiaCuotas = new ArrayList<>();

            //al total contado primero sumarle todo el monto de capital dado
            totalCondonado = totalCondonado.add(capitalDado);

            //Ver si lo que puse no supera el capital vigente o si no es igual del crédito
            //ya que no se puede condonar TODO el crédito, sino que solo una parte de él
            if (capitalDado.doubleValue() >= creditoEncontrado.getSaldo()) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR AL CONDONAR");
                alert.setContentText("EL MONTO DEL CASTIGO NO PUEDE SER IGUAL O MAYOR AL SALDO DEL CRÉDITO");
                alert.showAndWait();

                txtCapCon.setEditable(true);
                txtCapCon.clear();
                aplicarFormatterNumerico();

                return;
            }

            int numCuotaUltimaAfectada = 0;
            for (PagoCuotaDTO pagoCuotaDTO : tablaCuotas.getItems()) {
                ModelCuotas cuota = servicio.obtenerCuotaPorNumeroYCredito(pagoCuotaDTO.getNumCuota(), creditoEncontrado.getId());

                //Ahora, es ir sumando solo los intereses porque el capital condonado ya lo sumé

                if (capitalDado.compareTo(BigDecimal.ZERO) > 0) {
                    numCuotaUltimaAfectada = cuota.getNumCuota();

                    //Sea cual sea el caso, los intereses siempre se saldan, así cubra yo 1 peso de capital, por ello
                    //desde el inicio sabiendo que capital dado > 0 (o sea que si afectaré) sumo interes y mora
                    totalCondonado = totalCondonado.add(pagoCuotaDTO.getIntereses());

                    if (hoy.isAfter(pagoCuotaDTO.getFechaP().plusDays(29))) {
                        totalCondonado = totalCondonado.add(pagoCuotaDTO.getMora());
                    }

                    //Anotar y guardar como estaba la cuota antes de settear sus acumulados
                    PagoCuotaDTO copia = new PagoCuotaDTO();
                    copia.setNumCuota(cuota.getNumCuota());
                    copia.setFechaP(cuota.getFechaP());
                    copia.setInteresesAcumulados(pagoCuotaDTO.getInteresesAcumulados());
                    copia.setMoraAcumulados(pagoCuotaDTO.getMoraAcumulados());

                    cuota.setInteresAcumulado(BigDecimal.ZERO);
                    cuota.setMoraAcumulado(BigDecimal.ZERO);
                    cuota.setIsCondonado(true);
                    if (capitalDado.compareTo(pagoCuotaDTO.getCapital()) >= 0) {

                        //Ahora, en la copia, como cubrí todo el capital o de más, guardo todo el interes y mora para el historial
                        //y el capital completo igual y ya, es todo en este caso porque solo eso pagué de esa cuota
                        copia.setCapital(pagoCuotaDTO.getCapital());
                        copia.setIntereses(pagoCuotaDTO.getIntereses());
                        copia.setMora(pagoCuotaDTO.getMora());

                        capitalDado = capitalDado.subtract(pagoCuotaDTO.getCapital());
                        cuota.setCapital(BigDecimal.ZERO);
                        cuota.setStatus(0);

                        //hay que ver si ya hizo un pago o no, porque puede ser que se condones cuotas muy atrasadas q no tienen pagos
                        if (cuota.getFechaPRealizada() == null) {
                            //Si su fecha p realizada es null la cuota condonada nunca ha tenido un pago
                            cuota.setIntereses(BigDecimal.ZERO);
                            cuota.setMora(BigDecimal.ZERO);
                            cuota.setBonif(BigDecimal.ZERO);
                            cuota.setIva(BigDecimal.ZERO);
                            cuota.setTotal(BigDecimal.ZERO);
                            cuota.setFechaAnterior(hoy);

                        } else {
                            //Aqui pues ya tenía hecho un pago así que no le movemos lo que pagó sino solo las fechas
                            cuota.setFechaAnterior(cuota.getFechaPRealizada());
                        }
                        cuota.setFechaPRealizada(hoy);
                        cuota.setFechaTerminoPago(hoy);

                        copia.setFechaAnterior(cuota.getFechaAnterior());
                        copia.setFechaTerminoPago(cuota.getFechaTerminoPago());

                    } else {

                        //Si cayó aquí en cambio la cuota pues de interes y mora guardo todo pero de capital no
                        //de capital va directamente capitalDado porque eso como fue menor al capital de la cuota
                        //pues solo lo que tiene capitalDado "condoné" o afecté por así decirlo
                        copia.setCapital(capitalDado);
                        copia.setIntereses(pagoCuotaDTO.getIntereses());
                        copia.setMora(pagoCuotaDTO.getMora());

                        cuota.setCapital(cuota.getCapital().subtract(capitalDado));
                        if (cuota.getFechaPRealizada() == null) {
                            cuota.setIntereses(BigDecimal.ZERO);
                            cuota.setMora(BigDecimal.ZERO);
                            cuota.setBonif(BigDecimal.ZERO);
                            cuota.setIva(BigDecimal.ZERO);
                            cuota.setFechaAnterior(hoy);
                        }else{
                            cuota.setFechaAnterior(cuota.getFechaPRealizada());
                        }
                        cuota.setFechaPRealizada(hoy);
                        capitalDado = BigDecimal.ZERO;

                        copia.setFechaAnterior(cuota.getFechaAnterior());
                        copia.setFechaTerminoPago(cuota.getFechaTerminoPago());

                    }

                    //se añade copia al array
                    copiaCuotas.add(copia);
                    //se añade la cuota ya modificada
                    cuotasList.add(cuota);

                } else {
                    break;
                }
            }


            List<ModelCuotas> noTocadas = new ArrayList<>();
            //Acumular los intereses de las no afectadas
            for (int i = numCuotaUltimaAfectada; i < tablaCuotas.getItems().size(); i++) {
                PagoCuotaDTO cuotaPendiente = tablaCuotas.getItems().get(i);

                ModelCuotas cuota = servicio.obtenerCuotaPorNumeroYCredito(cuotaPendiente.getNumCuota(), creditoEncontrado.getId());

                if (cuotaPendiente.getIntereses().doubleValue() > 0) {
                    cuota.setInteresAcumulado(
                            BigDecimal.valueOf(cuotaPendiente.getIntereses().doubleValue() + cuotaPendiente.getBonif().doubleValue())
                    );
                }

                if (cuotaPendiente.getMora().doubleValue() > 0) {
                    cuota.setMoraAcumulado(
                            cuotaPendiente.getMora()
                    );
                }

                noTocadas.add(cuota);

            }


            //Volvemos a asignar para pasar cuanto fue de capital
            capitalDado = BigDecimal.valueOf(parseMoneda(txtCapCon.getText().trim()));
            //guardar
            servicio.guardarVariasCuotas(cuotasList, creditoEncontrado, totalCondonado, hoy, copiaCuotas, capitalDado.doubleValue(), noTocadas);

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("EXITO");
            alert.setHeaderText("EXITO AL CONDONAR");
            alert.setContentText("CAPITAL DE: " + txtCapCon.getText().trim() + " CONDONADO CON EXITO");
            alert.showAndWait();

            limpiar();
            cargarCuotas();

        }

    }

    private void aplicarFormatterNumerico() {
        txtCapCon.setTextFormatter(new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    public void settearMonto() {
        String texto = txtCapCon.getText().trim();

        if (texto.isEmpty()) {
            mostrarError("POR FAVOR, ESCRIBA EL MONTO");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return;
        }

        if (monto <= 0) {
            mostrarError("POR FAVOR, DIGITE UN MONTO MAYOR A CERO");
            return;
        }

        txtCapCon.setTextFormatter(null);
        txtCapCon.setText(FMT_MONEDA.format(monto));
        txtCapCon.setEditable(false);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("ERROR EN EL MONTO DADO");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private double parseMoneda(String moneda) {
        try {
            Number numero = FMT_MONEDA.parse(moneda);
            return numero.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }



}
