package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelAhorro;
import com.mutualidad.modulo_gerencia.Models.ModelSocio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AhorroRepository extends JpaRepository<ModelAhorro, Integer> {

    ModelAhorro findBySocioAndStatus(int socio, int status);

    ModelAhorro findBySocio(int socio);

    @Procedure(name = "Ahorro.pa_InsertarCuentadeAhorro")
    String paIsertarCuentaAhorro(
         int NumeroSocio,
         String NumCuenta,
         String RESULTADO
    );

    @Procedure(name = "Ahorro.pa_InsertarCongelamiento")
    String paInsertarCongelamiento(
            int IdUsuario,
            int NumSocio,
            String empresa,
            double saldo_congelado,
            double Ahorro_alMomento,
            int opcion_con,
            String Resultado
    );
}
