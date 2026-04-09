package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelMunicipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MunicipioRepository extends JpaRepository<ModelMunicipio, Integer> {
    List<ModelMunicipio> findByIdEstado(int idEstado);

    ModelMunicipio findByMunicipio(String municipio);
}
