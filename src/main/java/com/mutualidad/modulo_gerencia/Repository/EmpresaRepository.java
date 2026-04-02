package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<ModelEmpresa, Integer> {

    List<ModelEmpresa> findAll();

    ModelEmpresa findByNombre(String nombre);

    ModelEmpresa findByCodigo(String codigo);
}
