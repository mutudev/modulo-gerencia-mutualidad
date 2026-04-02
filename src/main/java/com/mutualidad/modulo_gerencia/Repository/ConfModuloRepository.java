package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelConfModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfModuloRepository extends JpaRepository<ModelConfModulo, Integer> {

    List<ModelConfModulo> findByUsuarioId(int usuarioId);

    @Procedure(name = "ConfModulo.pa_ModificarPermisos")
    String actualizarPermisos(String datos_permisos, int usuario_id, String Resultado);
}
