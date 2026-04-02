package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ModelUsuario, Integer> {

  @Procedure(name = "Usuario.pa_ValidarLogin")
  HashMap pa_validarLogin(String Usuario, String Pass, String Resultado, int Rol, int Cajero);

  @Procedure(name = "Socio.pa_CrearUsuario")
  String paCrearUsuario(
          @Param("Nombres") String nombres,
          @Param("ApellidoP") String apellidoP,
          @Param("ApellidoM") String apellidoM,
          @Param("Telefono") String telefono,
          @Param("NomUsuario") String nomUsuario,
          @Param("CodRol") Integer codRol,
          @Param("CodPuesto") Integer codPuesto,
          @Param("Contra") String contra,
          @Param("FNacimiento") LocalDate fNacimiento,
          @Param("UsuarioCreador") String usuarioCreador
  );




  @Query(value = "SELECT * FROM PUESTO", nativeQuery = true)
  List<Object[]> traerPuestos();

  @Query(
      value =
          "SELECT USUARIO_ID, MODULO_ID, MODULO.DESCRIPCION "
              + "FROM CONF_MODULO "
              + "INNER JOIN MODULO ON MODULO.ID = CONF_MODULO.MODULO_ID "
              + "WHERE USUARIO_ID = :usuarioID",
      nativeQuery = true)
  List<Object[]> traerModulos(@Param("usuarioID") int usuarioID);


  ModelUsuario findByUsuario(String usuario);


  @Query(value = "SELECT ROL FROM ROL WHERE ID= :rolCod", nativeQuery = true)
  String traerRol(@Param("rolCod") int rolCod);

  List<ModelUsuario> findByRol(int rol);

  @Query(value = "SELECT NOMBRE FROM VW_DATOS_USUARIO WHERE USUARIO = :usuario", nativeQuery = true)
  String traerAsesor(
          @Param("usuario") String usuario
  );

  @Query(
          value =
                  "SELECT * FROM CAT_ESTADOS",
          nativeQuery = true)
  List<Object[]> traerEstados();


  @Query(
          value =
                  "SELECT * FROM CAT_MUNICIPIOS WHERE ID_ESTADO = :idestado",
          nativeQuery = true)
  List<Object[]> traerMunicipios(@Param("idestado") int idEstado);


  @Query(
          value =
                  "SELECT * FROM CAT_TRABAJO",
          nativeQuery = true)
  List<Object[]> traerEmpleos();

  @Query(
          value =
                  "SELECT * FROM CAT_ESTADO_CIVIL",
          nativeQuery = true)
  List<Object[]> traerEstadosC();

  @Query(
          value =
                  "SELECT * FROM ROL",
          nativeQuery = true)
  List<Object[]> traerRoles();

  @Query(
          value =
                  "SELECT USUARIO FROM USUARIO",
          nativeQuery = true)
  List<Object[]> traerUsuarios();


  @Query(value = "SELECT U.ID AS idUsuario, " +
          "U.USUARIO AS usuario_nom, " +
          "(E.NOMBRES + ' ' + E.APELLIDO_P + ' ' + E.APELLIDO_M) AS nombre_completo " +
          "FROM USUARIO U " +
          "INNER JOIN EMPLEADO E ON E.ID = U.EMP_ID " +
          "WHERE U.USUARIO LIKE %:nombreUsuario%",
          nativeQuery = true)
  List<Object[]> traerDetalleUsuarios(@Param("nombreUsuario") String nombreUsuario);

  ModelUsuario findById (int id);

  List<ModelUsuario> findByCajeroAndStatus(int cajero, boolean status);

  @Procedure(name = "Usuario.pa_GuardarCambiosUsuario")
  String pa_GuardarCambiosUsuario(
          @Param("Nombre") String nombre,
          @Param("ApellidoP") String apellidoP,
          @Param("ApellidoM") String apellidoM,
          @Param("FechaNacimiento") LocalDate fechaNacimiento,
          @Param("codPuesto") Integer codPuesto,
          @Param("codRola") Integer codRola,
          @Param("idUsuario") Integer idUsuario,
          @Param("idEmpleado") Integer idEmpleado,
          @Param("telefono") String telefono,
          @Param("usuarioModificador") String usuarioModificador
  );


}
