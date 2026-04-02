package com.mutualidad.modulo_gerencia.Services;

import com.mutualidad.modulo_gerencia.Models.*;
import com.mutualidad.modulo_gerencia.Repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public HashMap validarLogin(String usuario, String password, String resultado, int rol, int cajero) {
        return repoUsuario.pa_validarLogin(usuario, password, resultado, rol, cajero);
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

    public List<Object[]> traerEstados() {
        return repoUsuario.traerEstados();
    }

    public List<Object[]> traerBeneficiarios(int numSocio, int estado) {
        return repoSocio.traerBeneficiarios(numSocio, estado);
    }

    public void actualizarAjusteCaja(int cajaId, int ajuste) {
        Optional<ModelCaja> cajaNueva = repoCaja.findById(cajaId);
        cajaNueva.get().setAjuste(ajuste);
        repoCaja.save(cajaNueva.get());
    }

    public List<Object[]> traerPuestos() {
        return repoUsuario.traerPuestos();
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

    public List<Object[]> traerTiposSocios() {
        return repoSocio.traerTiposSocios();
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

    public List<Object[]> traeMunicipios(int idEstado) {
        return repoUsuario.traerMunicipios(idEstado);
    }

    public List<Object[]> traerEmpleos() {
        return repoUsuario.traerEmpleos();
    }

    public List<Object[]> traerEstadosC() {
        return repoUsuario.traerEstadosC();
    }

    public List<Object[]> traerParentescos() {
        return repoSocio.traerParentescos();
    }

    public List<Object[]> traerIdsCredito(int numSocio) {
        return repoSocio.traerIdsCredito(numSocio);
    }


    public List<Object[]> traerRoles() {
        return repoUsuario.traerRoles();
    }

    public List<Object[]> traerUsuarios() {
        return repoUsuario.traerUsuarios();
    }


    public List<ModelCapitalSocial> traerCuentasCs(int numSocio) {
        return repoCapSoc.findByNumSocio(numSocio);
    }

    public ModelSocio traerSocioPorNumeroYEstado(int numSocio, boolean status) {
        return repoSocio.findByNumSocioAndStatus(numSocio, status);
    }

    public int contarCreditos(int numSocio) {
        return repoSocio.contarCreditos(numSocio);
    }

    public double traerSaldosCredito(int numCredito) {
        return repoSocio.traerSaldosCredito(numCredito);
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

    public List<ModelCredito> encontrarTodosLosCreditosPorSocio(int numSocio) {
        return repoCredito.findAllBySocio(numSocio);
    }

    public String traerCuotaParaSaldo(int creditoId) {
        return repoCredito.traerCuotasParaSaldo(creditoId);
    }

    public List<ModelConfModulo> traerModuloXUsuario(int usuarioId) {
        return repoConfModulo.findByUsuarioId(usuarioId);
    }


    public String traerNombrePuesto(int puestoCod) {
        return repoEmpleado.traerPuesto(puestoCod);
    }


    public String traerNombreRol(int rolCod) {
        return repoUsuario.traerRol(rolCod);
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

    public void realizarRetiroAhorros(ModelRetiro retiro) {
        ModelRetiro retiroGuardado = repoRetiro.save(retiro);
        //Creamos el nuevo retiro cajero, aprende como se hace ramitos
        ModelRetiroCajero retiroCajero = new ModelRetiroCajero();
        retiroCajero.setRetiro(retiroGuardado);
        retiroCajero.setEstado(true);
        retiroCajero.setFr(LocalDate.now());
        repoRetiroCajero.save(retiroCajero);
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




}
