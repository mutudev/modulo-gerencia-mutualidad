package com.mutualidad.modulo_gerencia.Services;

import com.mutualidad.modulo_gerencia.Controllers.LoginController;
import com.mutualidad.modulo_gerencia.DTO.BeneficiarioDTO;
import com.mutualidad.modulo_gerencia.DTO.DetalleUsuarioDTO;
import com.mutualidad.modulo_gerencia.DTO.Interfaz.DetalleUsuarioProjection;
import com.mutualidad.modulo_gerencia.DTO.Interfaz.ResumenCreditosProjection;
import com.mutualidad.modulo_gerencia.DTO.PagoCuotaDTO;
import com.mutualidad.modulo_gerencia.DTO.ResumenCreditosDTO;
import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class Servicio {

    @Autowired
    private UserRepository repoUsuario;

    @Autowired
    private SocioRepository repoSocio;

    @Autowired
    private AhorroRepository repoAhorro;

    @Autowired
    private CreditoRepository repoCredito;

    @Autowired
    private CapitalSocialRepository repoCapSoc;

    @Autowired
    private EmpresaRepository repoEmpresa;

    @Autowired
    private EmpleadoRepository repoEmpleado;

    @Autowired
    private ModuloRepository repoModulo;

    @Autowired
    private ConfModuloRepository repoConfModulo;

    @Autowired
    private CajaRepository repoCaja;

    @Autowired
    private RetiroRepository repoRetiro;

    @Autowired
    private RetiroCajeroRepository repoRetiroCajero;

    @Autowired
    private ConfiguracionRepository repoConfiguracion;

    @Autowired
    private FormaOperacionRepository repoForma;

    @Autowired
    private ParentescoRepository repoParentesco;

    @Autowired
    private TransaccionRepository repoTransaccion;

    @Autowired
    private EstadoRepository repoEstado;

    @Autowired
    private MunicipioRepository repoMunicipio;

    @Autowired
    private TrabajoRepository repoTrabajo;

    @Autowired
    private EstadoCivilRepository repoEstadoCivil;

    @Autowired
    private RolRepository repoRol;

    @Autowired
    private PuestoRepository repoPuesto;

    @Autowired
    private TipoSocioRepository repoTipoSocio;

    @Autowired
    private PrevisionSocialRepository repoPrevision;

    @Autowired
    private TipoCreditoRepository repoTipoCredito;

    @Autowired
    private CuotasRepository repoCuotas;

    @Autowired
    private FechasExcluyentesRepository repoFechasExcluyentes;

    @Autowired
    private HistorialCreditoCondonacionRepository repoHistorialCondonacion;

    @Autowired
    private  HistorialAcumCondonacionRepository repoHistorialAcumCondonacion;

    @Transactional
    public HashMap validarLogin(String usuario, String password, String resultado, int rol, int cajero) {
        return repoUsuario.pa_validarLogin(usuario, password, resultado, rol, cajero);
    }

    @Transactional
    public String procesarCierre(String resultado ){
        return repoCaja.paProcesarCierre(resultado);
    }


    @Transactional
    public String insertarSocio(
            String nombres,
            String apellidoP,
            String apellidoM,
            LocalDate fechaNacimiento,
            String curp,
            Integer catEmpId,
            String direccion,
            Integer catEstadoId,
            Integer catMunicipioId,
            Integer catTipoId,
            String rfc,
            Integer estadoCivilId,
            String telefono,
            String genero,
            String usuario
    ) {
        return repoSocio.paInsertarSocio(nombres, apellidoP, apellidoM, fechaNacimiento, curp, catEmpId, direccion,
                catEstadoId, catMunicipioId, catTipoId, rfc, estadoCivilId, telefono, genero, usuario);
    }

    @Transactional
    public String crearUsuario(
            String nombres,
            String apellidoP,
            String apellidoM,
            String telefono,
            String nomUsuario,
            Integer codRol,
            Integer codPuesto,
            String contra,
            LocalDate fNacimiento,
            String usuarioCreador
    ) {
        return repoUsuario.paCrearUsuario(
                nombres,
                apellidoP,
                apellidoM,
                telefono,
                nomUsuario,
                codRol,
                codPuesto,
                contra,
                fNacimiento,
                usuarioCreador
        );
    }

    @Transactional
    public String editarSocio(
            Integer numSocio,
            String nombres,
            String apellidoP,
            String apellidoM,
            LocalDate fechaNacimiento,
            String curp,
            Integer catEmpId,
            String direccion,
            Integer catEstadoId,
            Integer catMunicipioId,
            String rfc,
            Integer estadoCivilId,
            String telefono,
            String genero,
            String usuario,
            int tipo_id
    ) {

        return repoSocio.paEditarSocio(
                numSocio,
                nombres,
                apellidoP,
                apellidoM,
                fechaNacimiento,
                curp,
                catEmpId,
                direccion,
                catEstadoId,
                catMunicipioId,
                rfc,
                estadoCivilId,
                telefono,
                genero,
                usuario,
                tipo_id
        );
    }

    @Transactional
    public String insertarBeneficiario(String datos_beneficiarios, String Resultado, int actualizar) {
        return repoSocio.paInsertarBeneficiario(datos_beneficiarios, actualizar, Resultado);
    }

    @Transactional
    public String actualizarPermisos(String datos_permisos, int usuario_id, String Resultado) {
        return repoConfModulo.actualizarPermisos(datos_permisos, usuario_id, Resultado);
    }

    @Transactional
    public String insertarCuentaAhorro(int numScoio, String numCuenta, String Resultado) {
        return repoAhorro.paIsertarCuentaAhorro(numScoio, numCuenta, Resultado);
    }

    @Transactional
    public String congelarSaldo(int idUsuario,
                                int numSocio,
                                String empresa,
                                double saldoCongelado,
                                double ahorroAlMomento,
                                int opcion_con,
                                String Resultado) {
        return repoAhorro.paInsertarCongelamiento(idUsuario, numSocio, empresa, saldoCongelado, ahorroAlMomento, opcion_con, Resultado);
    }

    public ModelEmpresa traerEmpresaXCodigo(String codigo) {
        return repoEmpresa.findByCodigo(codigo);
    }

    public List<ModelCaja> traerCajerosActivos(boolean estado, int ajuste, LocalDate fr) {
        return repoCaja.findByEstadoAndAjusteAndFechaRegistro(estado, ajuste, fr);
    }

    public List<Object[]> traerDetalleUsuarios(String nombreUsuario) {
        return repoUsuario.traerDetalleUsuarios(nombreUsuario);
    }

    public void actualizarAjusteCaja(int cajaId, int ajuste) {
        Optional<ModelCaja> cajaNueva = repoCaja.findById(cajaId);
        cajaNueva.get().setAjuste(ajuste);
        repoCaja.save(cajaNueva.get());
    }

    public List<ModelEmpresa> traerEmpresas() {
        return repoEmpresa.findAll();
    }

    public ModelEmpresa traerEmpresaPorNombre(String nombre) {
        return repoEmpresa.findByNombre(nombre);
    }

    public ModelUsuario traerUsuarioXId(int id) {
        return repoUsuario.findById(id);
    }


    public ModelEmpleado traerEmpleadoXId(int id) {
        return repoEmpleado.findById(id);
    }

    public List<Object[]> traerUsuarios() {
        return repoUsuario.traerUsuarios();
    }


    public List<ModelCapitalSocial> traerCuentasCs(int numSocio) {
        return repoCapSoc.findByNumSocio(numSocio);
    }

    public ModelCapitalSocial traerCuentaCsXNumeroYEmpresa(int numSocio, String empresaCod) {return
            repoCapSoc.findByNumSocioAndEmpresaCod(numSocio, empresaCod);
    }

    public Double sumarCapitalSocial(int numSocio) {
        return repoCapSoc.sumarCapitalSocial(numSocio);
    }

    public ModelSocio traerSocioPorNumeroYEstado(int numSocio, boolean status) {
        return repoSocio.findByNumSocioAndStatus(numSocio, status);
    }
    public List<Object[]> traerModulos(int usuarioID) {
        return repoUsuario.traerModulos(usuarioID);
    }

    public List<Object[]> buscarSocioPorNombre(String nombreCompleto) {
        String[] palabras = nombreCompleto.trim().split("\\s+");

        if (palabras.length == 1) {
            return repoSocio.buscarPorNombreCompleto(nombreCompleto);
        } else if (palabras.length == 2) {
            return repoSocio.buscarPorDospalabras(palabras[0], palabras[1]);
        } else if (palabras.length >= 3) {
            return repoSocio.buscarPorTresPalabras(palabras[0], palabras[1], palabras[2]);
        }

        return new ArrayList<>();
    }

    public String traerTipoSocio(int numSocio) {
        return repoSocio.buscarTipoSocio(numSocio);
    }

    public ModelAhorro traerCuentaAhorroPorNumSocioYEstado(int numSocio, int status) {
        return repoAhorro.findBySocioAndStatus(numSocio, status);
    }

    public ModelAhorro traerCuentaAhorroPorNumSocio(int numSocio) {
        return repoAhorro.findBySocio(numSocio);
    }

    public ModelUsuario traerUsuarioXUsuario(String usuario) {
        return repoUsuario.findByUsuario(usuario);
    }

    public List<ModelConfModulo> traerModuloXUsuario(int usuarioId) {
        return repoConfModulo.findByUsuarioId(usuarioId);
    }

    public List<ModelCaja> traerCajasActivas(boolean estado){
        return repoCaja.findByEstado(estado);
    }

    public String traerNombrePuesto(int puestoCod) {
        return repoEmpleado.traerPuesto(puestoCod);
    }

    public String traerNombreRol(int rolCod) {
        return repoUsuario.traerRol(rolCod);
    }

    public ModelUsuario traerUsuario(String usuario) {
        return repoUsuario.findByUsuario(usuario);
    }

    @Transactional
    public String editarUsuario(

            String nombre,
            String apellidoP,
            String apellidoM,
            LocalDate fechaNacimiento,
            Integer codPuesto,
            Integer codRola,
            Integer idUsuario,
            Integer idEmpleado,
            String telefono,
            String usuarioModificador
    ) {

        return repoUsuario.pa_GuardarCambiosUsuario(
                nombre,
                apellidoP,
                apellidoM,
                fechaNacimiento,
                codPuesto,
                codRola,
                idUsuario,
                idEmpleado,
                telefono,
                usuarioModificador
        );
    }

    public void cambiarDatosUsuario(ModelUsuario usuario) {
        repoUsuario.save(usuario);
    }

    public List<ModelModulo> traerModulos(String modulo, boolean estado) {
        return repoModulo.findByModuloAndEstado(modulo, estado);
    }


    public void cambiarEstadoCuenta(ModelAhorro ahorro) {
        repoAhorro.save(ahorro);
    }

    public List<ModelRetiro> obtenerRetirosPendientes(int socio, boolean estado) {
        return repoRetiro.findBySocioAndEstado(socio, estado);
    }

    @Transactional
    public int realizarRetiroAhorros(ModelRetiro retiro, int opcion) {
        ModelRetiro retiroGuardado = repoRetiro.save(retiro);
        //Creamos el nuevo retiro cajero, aprende como se hace ramitos
        if (opcion == 1) {
            ModelRetiroCajero retiroCajero = new ModelRetiroCajero();
            retiroCajero.setRetiro(retiroGuardado);
            retiroCajero.setEstado(true);
            retiroCajero.setFr(LocalDate.now());
            repoRetiroCajero.save(retiroCajero);
            return 0;
        } else {
            //Se hace en cheque
            ModelTransaccion nuevaTransaccion = new ModelTransaccion();
            nuevaTransaccion.setUsuarioId(repoUsuario.findByUsuario(LoginController.usuarioLoggeado).getId());
            nuevaTransaccion.setOperacionId(13);
            nuevaTransaccion.setSocioId(retiro.getSocio());
            nuevaTransaccion.setSaldo(retiro.getMontoRetiro());
            nuevaTransaccion.setStatus(true);
            nuevaTransaccion.setFechaRegistro(LocalDate.now());
            nuevaTransaccion.setHora(LocalTime.now());
            nuevaTransaccion.setEmpresa(retiro.getEmpresa());
            nuevaTransaccion.setCajaId(0);
            nuevaTransaccion.setAhorroAlMomento(retiro.getSaldoNue());
            ModelTransaccion transaccionInsertada = repoTransaccion.save(nuevaTransaccion);

            ModelAhorro nuevaCuenta = repoAhorro.findBySocioAndStatus(retiro.getSocio(), 1);
            nuevaCuenta.setSaldo(retiro.getSaldoNue().doubleValue());
            repoAhorro.save(nuevaCuenta);
            return transaccionInsertada.getId();
        }

    }

    public ModelCapitalSocial crearCuentaCs(ModelCapitalSocial cs){
        ModelCapitalSocial cuentaCreada = repoCapSoc.save(cs);
        return cuentaCreada;
    }


    public Optional<ModelConfiguracion> traerConfiguraciones() {
        return repoConfiguracion.findById(1);
    }

    public void guardarConfiguraciones(Optional<ModelConfiguracion> configuracion) {
        repoConfiguracion.save(configuracion.get());
    }

    public List<ModelUsuario> traerCajeros(int cajero, boolean status) {
        return repoUsuario.findByCajeroAndStatus(cajero, status);
    }

    public List<Object[]> traerTransaccionesComprobar(List<Integer> listaIds, String rangoFecha1, String rangoFecha2) {
        return repoUsuario.traerTransacciones(listaIds, rangoFecha1, rangoFecha2);
    }


    public List<Object[]> traerCajasComprobar(List<String> listaIds, String rangoFecha1, String rangoFecha2, String turno) {
        return repoUsuario.traerCajas(listaIds, rangoFecha1, rangoFecha2, turno);
    }

    public List<ModelFormaOperacion> traerFormas() {
        return repoForma.findAll();
    }

    public ModelFormaOperacion buscarFormaPorNombre(String forma) {
        return repoForma.findByForma(forma);
    }

    public List<ModelTransaccion> traerTransaccionesPorTipoOperacion(int socioId, boolean status, List<Integer> operacionIds) {
        return repoTransaccion.findBySocioIdAndStatusAndOperacionIdIn(socioId, status, operacionIds);
    }

    // NUEVOS MÉTODOS USANDO DTO YA FUNCIONALES
    public ResumenCreditosDTO traerResumenCreditos(int numSocio) {
        ResumenCreditosProjection projection = repoSocio.traerResumenCreditos(numSocio);
        return new ResumenCreditosDTO(
                projection.getNum_creditos() != null ? projection.getNum_creditos() : 0,
                projection.getSaldo_total() != null ? projection.getSaldo_total() : 0.0
        );
    }

    public DetalleUsuarioDTO traerDetalleUsuario(String usuario) {
        DetalleUsuarioProjection p = repoUsuario.traerDetalleUsuario(usuario);
        if (p == null) return null;
        return new DetalleUsuarioDTO(
                p.getId(),
                p.getUsuario(),
                p.getEmpleado_id(),
                p.getEmpleado_nombre(),
                p.getEmpleadoSoloNombre(),
                p.getApellidoPaterno(),
                p.getApellidoMaterno(),
                p.getTelefono(),
                p.getRol_id(),
                p.getRol(),
                p.getPuesto(),
                p.getActivo(),
                p.getFechaNacimiento()
        );
    }

    public DetalleUsuarioDTO traerDetalleUsuarioPorId(int idUsuario) {
        DetalleUsuarioProjection p = repoUsuario.traerDetalleUsuarioConId(idUsuario);
        if (p == null) return null;
        return new DetalleUsuarioDTO(
                p.getId(),
                p.getUsuario(),
                p.getEmpleado_id(),
                p.getEmpleado_nombre(),
                p.getEmpleadoSoloNombre(),
                p.getApellidoPaterno(),
                p.getApellidoMaterno(),
                p.getTelefono(),
                p.getRol_id(),
                p.getRol(),
                p.getPuesto(),
                p.getActivo(),
                p.getFechaNacimiento()
        );
    }

    public List<BeneficiarioDTO> traerBeneficiarios(int numSocio, int estado) {
        return repoSocio.traerBeneficiarios(numSocio, estado)
                .stream()
                .map(p -> new BeneficiarioDTO(
                        p.getId(),
                        p.getBeneficiario(),
                        p.getSocio(),
                        p.getParentesco(),
                        p.getTitular(),
                        p.getPorcentaje()
                ))
                .toList();
    }

    public List<ModelSocio> traerListadoSocios(Boolean status) {
        return repoSocio.findByStatus(status);
    }

    public List<ModelSocio> traerListadoPorEmpresaYestado(Boolean status, String empresa) {
        return repoSocio.findByStatusAndEmpresaCod(status, empresa);
    }

    public List<ModelSocio> traerListadoPorTipo(Boolean status, int tipo) {
        return repoSocio.findByStatusAndCatTipoId(status, tipo);
    }

    public List<ModelSocio> traerListadoPorTipoYEmpresa(Boolean status, int tipo, String empresa) {
        return repoSocio.findByStatusAndCatTipoIdAndEmpresaCod(status, tipo, empresa);
    }

    public List<ModelEstado> traerEstados() {
        return repoEstado.findAll();
    }

    public int traerIdEstadoConEstado(String estado) {
        return repoEstado.findByEstado(estado).getId();
    }

    public String traerEstadoConId(int idEstado) {
        return repoEstado.findById(idEstado).get().getEstado();
    }

    public List<ModelMunicipio> traeMunicipios(int idEstado) {
        return repoMunicipio.findByIdEstado(idEstado);
    }

    public int traerIdMunicipioConMunicipio(String municipio) {
        return repoMunicipio.findByMunicipio(municipio).getId();
    }

    public String traerMunicipioConId(int municipioId) {
        return repoMunicipio.findById(municipioId).get().getMunicipio();
    }

    public List<ModelTrabajo> traerEmpleos() {
        return repoTrabajo.findAll();
    }

    public List<ModelTipoCredito> traerTipoCredito(){
        return  repoTipoCredito.findAll();
    }

    public ModelTipoCredito traerTipoCreditoXNombre(String nombre){
        return repoTipoCredito.findByNombre(nombre);
    }

    public List<ModelFechasExcluyentes> traerFechasExcluyentes(boolean estado) {
        return repoFechasExcluyentes.findByEstado(estado);
    }


    public int traerIdConEmpleos(String trabajo) {
        return repoTrabajo.findByTrabajo(trabajo).getId();
    }

    public List<ModelEstadoCivil> traerEstadosC() {
        return repoEstadoCivil.findAll();
    }

    public int traerIdConEstadosCivil(String estadoCivil) {
        return repoEstadoCivil.findByEstadoCivil(estadoCivil).getId();
    }

    public String traerEstadosCivilConId(int estadoCivilId) {
        return repoEstadoCivil.findById(estadoCivilId).get().getEstadoCivil();
    }

    public String traerEmpleoConId(int empleoId) {
        return repoTrabajo.findById(empleoId).get().getTrabajo();
    }

    public List<ModelParentesco> traerParentescos() {
        return repoParentesco.findAll();
    }

    public List<ModelRol> traerTodosLosRoles() {
        return repoRol.findAll();
    }

    public List<ModelPuesto> traerTodosLosPuestos() {
        return repoPuesto.findAll();
    }

    public List<ModelTipoSocio> traerTiposDeSocio() {
        return repoTipoSocio.findAll();
    }

    public LocalDate traerFechaHoy() {
        return repoConfiguracion.findById(1).get().getFechaSistema();
    }

    public ModelPrevisionSocial traerCuentaPSPorEmpresaYSocio(int numSocio, String empresaCod) {
        return repoPrevision.findByNumSocioAndEmpresaCod(numSocio, empresaCod);
    }


    public List<ModelRetiro> traerRetirosXSocioYFechaYActivo(int numSocio, LocalDate fecha, boolean activo){
        return  repoRetiro.findBySocioAndFrAndActivo(numSocio, fecha, activo);
    }

    public ModelPrevisionSocial crearCuentaPrevisionSocial(ModelPrevisionSocial cuentaNueva) {
        ModelPrevisionSocial cuentaCreada = repoPrevision.save(cuentaNueva);
        return cuentaCreada;
    }

    public  ModelRetiroCajero traerRetiroCajeroXIdRetiro(ModelRetiro retiro){
        return repoRetiroCajero.findByRetiro(retiro);
    }

    public  ModelTransaccion traerTransaccionActiva(int numSocio, boolean status, int operacionId){
        return repoTransaccion.findBySocioIdAndStatusAndOperacionId(numSocio,status,operacionId);
    }

    public ModelTransaccion traerTransaccionActivaPorFecha(int numSocio, boolean status, int operacionId, LocalDate hoy) {
        return repoTransaccion.findBySocioIdAndStatusAndOperacionIdAndFechaRegistro(numSocio, status, operacionId, hoy);
    }

    @Transactional
    public ModelRetiro cancelarRetiro(ModelRetiro retiro, ModelRetiroCajero retiroCajero){

        if(retiroCajero != null){
            repoRetiroCajero.save(retiroCajero);
        } else {
            ModelTransaccion transaccion = traerTransaccionActivaPorFecha(retiro.getSocio(), true, 13, traerFechaHoy());
            ModelAhorro ahorro = traerCuentaAhorroPorNumSocio(retiro.getSocio());

            ahorro.setSaldo(ahorro.getSaldo() +  retiro.getMontoRetiro().doubleValue());
            transaccion.setStatus(false);

            if (retiro.getUsuarioId() == null) {
                retiro.setUsuarioId(transaccion.getUsuarioId());
            }

            repoAhorro.save(ahorro);
            repoTransaccion.save(transaccion);
        }
        retiro.setActivo(false);

        return repoRetiro.save(retiro);

    }



    public List<ModelCredito> traerCreditosPorSocioEmpresaEstado(String socio, String empresa, int status) {
        return repoCredito.findAllBySocioAndEmpresaAndStatus(socio, empresa, status);
    }

    public Optional<ModelTipoCredito> traerTipoCreditoConId(Long id) {
        return repoTipoCredito.findById(id);
    }

    @Transactional
    public List<PagoCuotaDTO> calcularPagoDeCuotas(
            Integer creditoId,
            Double tasaInteres,
            Double tasaMora,
            Double tasaIva,
            LocalDate fechaDesembolso) {

        List<Object[]> resultados = repoCuotas.pa_CalcularPagoDeCuotas(
                creditoId,
                tasaInteres,
                tasaMora,
                tasaIva,
                fechaDesembolso
        );

        List<PagoCuotaDTO> lista = new ArrayList<>();

        for (Object[] row : resultados) {
            PagoCuotaDTO dto = new PagoCuotaDTO(
                    row[0] != null ? ((Number) row[0]).intValue()                        : null,
                    row[1] != null ? ((Number) row[1]).intValue()                        : null,
                    row[2] != null ? toLocalDate(row[2]) : null,
                    row[3] != null ? new BigDecimal(row[3].toString())                   : BigDecimal.ZERO,
                    row[4] != null ? new BigDecimal(row[4].toString())                   : BigDecimal.ZERO,
                    row[5] != null ? new BigDecimal(row[5].toString())                   : BigDecimal.ZERO,
                    row[6] != null ? new BigDecimal(row[6].toString())                   : BigDecimal.ZERO,
                    row[7] != null ? new BigDecimal(row[7].toString())                   : BigDecimal.ZERO,
                    row[8] != null ? new BigDecimal(row[8].toString())                   : BigDecimal.ZERO,
                    row[9] != null ? ((Number) row[9]).intValue()                        : null,
                    row[10] != null ? toLocalDate(row[10])                  : null,  // fecha
                    row[11] != null ? toLocalDate(row[11])                  : null,  // fecha
                    row[12] != null ? toLocalDate(row[12])                  : null,  // fecha
                    row[13] != null ? new BigDecimal(row[13].toString())                   : BigDecimal.ZERO,
                    row[14] != null ? new BigDecimal(row[14].toString())                   : BigDecimal.ZERO,
                    row[15] != null ? (Boolean) row[15] : null
            );
            lista.add(dto);
        }

        return lista;
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        } else if (value instanceof LocalDate) {
            return (LocalDate) value;
        } else if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }

    public ModelConfiguracion obtenerConfiguraciones() {
        return repoConfiguracion.findById(1).get();
    }


    public Optional<ModelCredito>  traerDatosCredito(int id) {
        return repoCredito.findById(id);
    }

    public ModelFechasExcluyentes encontrarSolapamiento(LocalDate inicio, LocalDate fin, boolean estado, Long idExcluir) {
        return repoFechasExcluyentes.encontrarSolapamiento(inicio, fin, estado, idExcluir);
    }

    @Transactional
    public ModelFechasExcluyentes guardarFechaExcluyente(ModelFechasExcluyentes fechasExcluyentes) {
        return repoFechasExcluyentes.save(fechasExcluyentes);
    }

    @Transactional
    public  ModelTipoCredito guardarModificaciones(ModelTipoCredito credito){

        repoTipoCredito.save(credito);
        return credito;
    }


    @Transactional
    public  ModelTipoCredito crearCredito(ModelTipoCredito credito){

        repoTipoCredito.save(credito);
        return credito;
    }

    public ModelCuotas obtenerCuotaPorNumeroYCredito(int numCuota, int creditoId) {
        return repoCuotas.findByNumCuotaAndCreditoId(numCuota, creditoId);
    }

    @Transactional
    public ModelCuotas guardarCuota(ModelCuotas cuota, ModelCredito credito, BigDecimal totalCondonado,
                                    LocalDate hoy, BigDecimal interesCondonado, BigDecimal moraCondonado,
                                    BigDecimal interesAcumulado, BigDecimal moraAcumulado,  List<ModelCuotas> noTocadas) {

        //Primero crear e insertar la transaccion
        ModelUsuario usuario = traerUsuarioXUsuario(LoginController.usuarioLoggeado);
        ModelTransaccion transaccion = new ModelTransaccion();
        transaccion.setUsuarioId(usuario.getId());
        transaccion.setOperacionId(14);
        transaccion.setSocioId(credito.getSocio());
        transaccion.setSaldo(totalCondonado);
        transaccion.setStatus(true);
        transaccion.setFechaRegistro(hoy);
        transaccion.setHora(LocalTime.now());
        transaccion.setEmpresa(credito.getEmpresa());
        transaccion.setCajaId(0);
        transaccion.setCapitalCreditoPagado(null);
        transaccion.setInteresesCreditoPagado(null);
        transaccion.setIvaCreditoPagado(null);
        transaccion.setMoraCreditoPagado(null);
        transaccion.setBonifCreditoPagado(null);
        transaccion.setTipoCredito(traerTipoCreditoConId(Long.valueOf(credito.getTipo_credito())).get().getCodigoSistema());
        transaccion.setCuotaAfectada(cuota.getNumCuota());
        transaccion.setCreditoAfectado(credito.getId());
        transaccion.setAhorroAlMomento(null);
        transaccion.setTotalCuotaPagada(null);

        //guardar la transacción
        transaccion = repoTransaccion.save(transaccion);

        //Construir el historial del crédito
        ModelHistorialCreditoCondonacion condonacion = new ModelHistorialCreditoCondonacion();
        condonacion.setCreditoId(credito.getId());
        condonacion.setFechaC(hoy);
        condonacion.setUsuarioId(usuario.getId());
        condonacion.setNumCuota(cuota.getNumCuota());
        condonacion.setFechaV(cuota.getFechaP());
        condonacion.setCapitalCondonado(BigDecimal.ZERO);
        condonacion.setInteresCondonado(interesCondonado);
        condonacion.setMoraCondonado(moraCondonado);
        condonacion.setTotalCondonado(totalCondonado);
        condonacion.setSaldoCredito(BigDecimal.valueOf(credito.getSaldo()));
        condonacion.setOperacionId(transaccion.getId());
        condonacion.setEstado(true);
        condonacion.setFechaPAnterior(cuota.getFechaAnterior());
        condonacion.setFechaPFinalizado(cuota.getFechaTerminoPago());
        condonacion.setFechaRegistro(LocalDateTime.now());

        repoHistorialCondonacion.save(condonacion);

        //Construir el historial de acumulados
        ModelHistorialAcumCondonacion acumulados = new ModelHistorialAcumCondonacion();
        acumulados.setCreditoId(credito.getId());
        acumulados.setNumCuota(cuota.getNumCuota());
        acumulados.setOperacionId(transaccion.getId());
        acumulados.setInteresAcumCon(interesAcumulado);
        acumulados.setMoraAcumCon(moraAcumulado);
        acumulados.setEstado(true);
        acumulados.setFr(LocalDateTime.now());

        //Guardar el acumulado
        repoHistorialAcumCondonacion.save(acumulados);

        repoCuotas.saveAll(noTocadas);

        return repoCuotas.save(cuota);
    }

    @Transactional
    public void guardarVariasCuotas(List<ModelCuotas> cuotasList, ModelCredito credito, BigDecimal totalCondonado,
                                    LocalDate hoy, List<PagoCuotaDTO> copiaCuotas, double capitalDado, List<ModelCuotas> noTocadas) {

        ModelUsuario usuario = traerUsuarioXUsuario(LoginController.usuarioLoggeado);
        ModelTransaccion transaccion = new ModelTransaccion();
        transaccion.setUsuarioId(usuario.getId());
        transaccion.setOperacionId(15);
        transaccion.setSocioId(credito.getSocio());
        transaccion.setSaldo(totalCondonado);
        transaccion.setStatus(true);
        transaccion.setFechaRegistro(hoy);
        transaccion.setHora(LocalTime.now());
        transaccion.setEmpresa(credito.getEmpresa());
        transaccion.setCajaId(0);
        transaccion.setCapitalCreditoPagado(null);
        transaccion.setInteresesCreditoPagado(null);
        transaccion.setIvaCreditoPagado(null);
        transaccion.setMoraCreditoPagado(null);
        transaccion.setBonifCreditoPagado(null);
        transaccion.setTipoCredito(traerTipoCreditoConId(Long.valueOf(credito.getTipo_credito())).get().getCodigoSistema());
        transaccion.setCuotaAfectada(null);
        transaccion.setCreditoAfectado(credito.getId());
        transaccion.setAhorroAlMomento(null);
        transaccion.setTotalCuotaPagada(null);

        //guardar la transacción
        transaccion = repoTransaccion.save(transaccion);

        double capitalDeCredito = credito.getSaldo();
        List<ModelHistorialCreditoCondonacion> listaCondonaciones = new ArrayList<>();

        for (PagoCuotaDTO copia : copiaCuotas) {
            ModelHistorialCreditoCondonacion condonacion = new ModelHistorialCreditoCondonacion();
            condonacion.setCreditoId(credito.getId());
            condonacion.setFechaC(hoy);
            condonacion.setUsuarioId(usuario.getId());
            condonacion.setFechaV(copia.getFechaP());
            condonacion.setCapitalCondonado(copia.getCapital());
            condonacion.setInteresCondonado(copia.getIntereses());
            condonacion.setMoraCondonado(BigDecimal.ZERO);

            if (hoy.isAfter(copia.getFechaP().plusDays(29))) {
                condonacion.setMoraCondonado(copia.getMora());
            }

            condonacion.setTotalCondonado(copia.getCapital().add(copia.getIntereses().add(copia.getMora())));
            condonacion.setSaldoCredito(BigDecimal.valueOf(capitalDeCredito - copia.getCapital().doubleValue()));
            condonacion.setOperacionId(transaccion.getId());
            condonacion.setEstado(true);
            condonacion.setFechaPAnterior(copia.getFechaAnterior());
            condonacion.setFechaPFinalizado(copia.getFechaTerminoPago());
            condonacion.setFechaRegistro(LocalDateTime.now());

            condonacion.setNumCuota(copia.getNumCuota());
            capitalDeCredito -= copia.getCapital().doubleValue();
            listaCondonaciones.add(condonacion);
        }

        //Al final del ciclo de todas las afectadas, guardamos
        repoHistorialCondonacion.saveAll(listaCondonaciones);

        //Ahora toca guardar los acumulados
        List<ModelHistorialAcumCondonacion> listaAcumulados = new ArrayList<>();
        for (PagoCuotaDTO copia : copiaCuotas) {
            ModelHistorialAcumCondonacion acumulados = new ModelHistorialAcumCondonacion();
            acumulados.setCreditoId(credito.getId());
            acumulados.setNumCuota(copia.getNumCuota());
            acumulados.setOperacionId(transaccion.getId());
            acumulados.setInteresAcumCon(copia.getInteresesAcumulados());
            acumulados.setMoraAcumCon(copia.getMoraAcumulados());
            acumulados.setEstado(true);
            acumulados.setFr(LocalDateTime.now());
            listaAcumulados.add(acumulados);
        }

        repoHistorialAcumCondonacion.saveAll(listaAcumulados);

        //Actualizar el saldo del crédito
        credito.setSaldo(credito.getSaldo() - capitalDado);
        repoCredito.save(credito);

        //Por último, guardar todas las cuotas ya condonadas
        repoCuotas.saveAll(cuotasList);

        repoCuotas.saveAll(noTocadas);
    }


    public ModelCuotas traerProximaACondonar(int numCredito){
        return repoCuotas.findFirstByCreditoIdAndIsCondonadoIsFalseOrderByNumCuotaAsc(numCredito);
    }

    public List<ModelHistorialCreditoCondonacion> traerCondonacionesXCredito(int creditoId, boolean estado){
        return repoHistorialCondonacion.findByCreditoIdAndEstado(creditoId, estado);
    }
}
