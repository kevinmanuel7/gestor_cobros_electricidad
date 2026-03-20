package com.kevin.medidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kevin.medidor.model.LecturaMensual;
import java.util.List;

@Repository
public interface LecturaRepository extends JpaRepository<LecturaMensual, Integer> {
    List<LecturaMensual> findByCasaId(int casaId);
    List<LecturaMensual> findByMesAnio(String mesAnio);
}