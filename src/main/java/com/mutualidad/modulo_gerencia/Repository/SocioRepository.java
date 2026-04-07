package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.DTO.Interfaz.BeneficiarioProjection;
import com.mutualidad.modulo_gerencia.DTO.Interfaz.ResumenCreditosProjection;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SocioRepository extends JpaRepository<ModelSocio, Integer> {

    @Procedure(name = "Socio.pa_InsertarSocio")
    String paInsertarSocio(
            String NOMBRES,
            String APELLIDO_P,
            String APELLIDO_M,
            LocalDate FECHA_NACIMIENTO,
            String CURP,
            int CAT_EMP_ID,
            String DIRECCION,
            int CAT_ESTADO_ID,
            int CAT_MUNICIPIO_ID,
            int CAT_TIPO_ID,
            String RFC,
            int ESTADO_CIVIL_ID,
            String TELEFONO,
            String GENERO,
            String USUARIO
    );

    @Procedure(name = "Socio.pa_EditarSocio")
    String paEditarSocio(

            Integer NUM_SOCIO,

            String NOMBRES,
            String APELLIDO_P,
            String APELLIDO_M,
            LocalDate FECHA_NACIMIENTO,
            String CURP,

            Integer CAT_EMP_ID,

            String DIRECCION,

            Integer CAT_ESTADO_ID,
            Integer CAT_MUNICIPIO_ID,

            String RFC,

            Integer ESTADO_CIVIL_ID,

            String TELEFONO,

            String GENERO,

            String USUARIO,
            int TIPO_ID
    );

    @Procedure(name = "Socio.pa_InsertarBeneficiario")
    String paInsertarBeneficiario(

            String datos_beneficiarios,
            int actualizar,
            String Resultado
    );


    ModelSocio findByNumSocioAndStatus(int numSocio, Boolean status);

    List<ModelSocio> findByStatus(Boolean status);

    List<ModelSocio> findByStatusAndEmpresaCod(Boolean status, String empresaCod);

    List<ModelSocio> findByStatusAndCatTipoId(Boolean status, int catTipoId);

    List<ModelSocio> findByStatusAndCatTipoIdAndEmpresaCod(Boolean status, int catTipoId, String empresaCod);


    @Query(
            value =
                    "SELECT " +
                            "    (NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) AS NOMBRE, " +
                            "    NUM_SOCIO, " +
                            "    TIPO_SOCIO.DESCRIPCION, " +
                            "    EMPRESA.NOMBRE " +
                            "FROM SOCIO " +
                            "INNER JOIN TIPO_SOCIO ON TIPO_SOCIO.ID = SOCIO.CAT_TIPO_ID " +
                            "INNER JOIN EMPRESA ON EMPRESA.CODIGO = SOCIO.EMPRESA_COD " +
                            "WHERE STATUS = 1 " +
                            "AND (NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) LIKE '%' + :nombreCompleto + '%'",
            nativeQuery = true
    )
    List<Object[]> buscarPorNombreCompleto(@Param("nombreCompleto") String nombreCompleto);

    // OPCIÓN 2: Para búsqueda de 2 palabras específicas (más eficiente)
    @Query(
            value =
                    "SELECT " +
                            "    (NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) AS NOMBRE, " +
                            "    NUM_SOCIO, " +
                            "    TIPO_SOCIO.DESCRIPCION, " +
                            "    EMPRESA.NOMBRE " +
                            "FROM SOCIO " +
                            "INNER JOIN TIPO_SOCIO ON TIPO_SOCIO.ID = SOCIO.CAT_TIPO_ID " +
                            "INNER JOIN EMPRESA ON EMPRESA.CODIGO = SOCIO.EMPRESA_COD " +
                            "WHERE STATUS = 1 " +
                            "AND CHARINDEX(:palabra1, NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) > 0 " +
                            "AND CHARINDEX(:palabra2, NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) > 0",
            nativeQuery = true
    )
    List<Object[]> buscarPorDospalabras(@Param("palabra1") String palabra1, @Param("palabra2") String palabra2);

    // Query adicional para 3 palabras
    @Query(
            value =
                    "SELECT " +
                            "    (NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) AS NOMBRE, " +
                            "    NUM_SOCIO, " +
                            "    TIPO_SOCIO.DESCRIPCION, " +
                            "    EMPRESA.NOMBRE " +
                            "FROM SOCIO " +
                            "INNER JOIN TIPO_SOCIO ON TIPO_SOCIO.ID = SOCIO.CAT_TIPO_ID " +
                            "INNER JOIN EMPRESA ON EMPRESA.CODIGO = SOCIO.EMPRESA_COD " +
                            "WHERE STATUS = 1 " +
                            "AND CHARINDEX(:palabra1, NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) > 0 " +
                            "AND CHARINDEX(:palabra2, NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) > 0 " +
                            "AND CHARINDEX(:palabra3, NOMBRES + ' ' + APELLIDO_P + ' ' + APELLIDO_M) > 0",
            nativeQuery = true
    )
    List<Object[]> buscarPorTresPalabras(@Param("palabra1") String palabra1, @Param("palabra2") String palabra2, @Param("palabra3") String palabra3);


    @Query(value = "SELECT T.DESCRIPCION FROM SOCIO S " +
            "INNER JOIN TIPO_SOCIO T ON T.ID = S.CAT_TIPO_ID " +
            "WHERE S.NUM_SOCIO = :numSocio", nativeQuery = true)
    String buscarTipoSocio(@Param("numSocio") int numSocio);


    @Query(value = """
    SELECT 
        COUNT(*) AS num_creditos,
        COALESCE(SUM(saldo_individual), 0) AS saldo_total
    FROM (
        SELECT 
            COALESCE(
                (SELECT TOP 1 SALDO_CREDITO
                 FROM CUOTAS_EJEMPLO
                 WHERE CREDITO_ID = c.ID
                 AND FECHA_P_REALIZADA IS NOT NULL
                 ORDER BY NUM_CUOTA DESC),
                c.MONTO
            ) AS saldo_individual
        FROM CAT_CREDITOS c
        WHERE c.STATUS = 2 AND c.SOCIO = :numSocio
    ) AS creditos_calculados
    """, nativeQuery = true)
    ResumenCreditosProjection traerResumenCreditos(@Param("numSocio") int numSocio);

    @Query(value = "SELECT * FROM TIPO_SOCIO", nativeQuery = true)
    List<Object[]> traerTiposSocios();



    @Query(value = "SELECT B.ID, B.BENEFICIARIO, B.SOCIO, P.PARENTESCO, B.TITULAR, B.PORCENTAJE FROM BENEFICIARIOS B " +
            "INNER JOIN CAT_PARENTESCO P ON P.ID = B.PARENTESCO " +
            "WHERE SOCIO = :numSocio AND ESTADO = :estado", nativeQuery = true)
    List<BeneficiarioProjection> traerBeneficiarios(@Param("numSocio") int numSocio, @Param("estado") int estado);



}
