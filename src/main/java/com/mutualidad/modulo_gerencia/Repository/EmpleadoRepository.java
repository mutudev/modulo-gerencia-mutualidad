package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelCredito;
import com.mutualidad.modulo_gerencia.Models.ModelEmpleado;
import com.mutualidad.modulo_gerencia.Models.ModelUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface EmpleadoRepository extends JpaRepository<ModelEmpleado, Integer> {

    ModelEmpleado findById(int id);

    @Query(value = "SELECT DESCRIPCION FROM PUESTO WHERE ID =:puestoCod", nativeQuery = true)
    String traerPuesto(@Param("puestoCod") int puestoCod);
}
