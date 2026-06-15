package com.mutualidad.modulo_gerencia.Controllers;

import com.mutualidad.modulo_gerencia.DTO.PagoCuotaDTO;
import com.mutualidad.modulo_gerencia.Models.ModelConfiguracion;
import com.mutualidad.modulo_gerencia.Models.ModelCredito;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Models.ModelTipoCredito;
import com.mutualidad.modulo_gerencia.Services.Servicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;


@Component
public class MostrarCuotasCreditoController implements Initializable {

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
    private TextField txtInmediatas, txtSaldoCredito, txtMonto;

    @FXML
    private Label lblPrimerasN, lblPlazo, lblTasa, lblMora, lblTipo, lblNombre, lblNumSocio, lblCredito;

    public int creditoId = 0;

    public ModelCredito creditoEncontrado = null;

    List<PagoCuotaDTO> copiaCuotas = null;

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FMT_MONEDA = NumberFormat.getCurrencyInstance(Locale.US);
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        List<PagoCuotaDTO> lista =
                servicio.calcularPagoDeCuotas(
                        creditoEncontrado.getId(),
                        creditoEncontrado.getTasa(),
                        creditoEncontrado.getMora(),
                        creditoEncontrado.getIva(),
                        creditoEncontrado.getFd()
                );

        copiaCuotas = lista;

        List<PagoCuotaDTO> primeras3 = lista.stream()
                .limit(copiaCuotas.size())
                .collect(toList());

        ObservableList<PagoCuotaDTO> datos =
                FXCollections.observableArrayList(primeras3);

        tablaCuotas.setItems(datos);

        double totalCuotasinmediatas = 0;

        for(PagoCuotaDTO c : datos){
            totalCuotasinmediatas += c.getTotal().doubleValue();
        }

        txtInmediatas.setText(FMT_MONEDA.format(totalCuotasinmediatas));

        txtSaldoCredito.setText(FMT_MONEDA.format(creditoEncontrado.getSaldo()));

        lblPrimerasN.setText("Total De Cuotas Pendientes:");


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



}


