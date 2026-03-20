package com.kevin.medidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kevin.medidor.model.Casa;

@Repository
public interface CasaRepository extends JpaRepository<Casa, Integer> {
    // Aquí no escribimos código. 
    // Al heredar de JpaRepository, Spring ya sabe cómo hacer:
    // .save() -> Guardar
    // .findAll() -> Ver todos
    // .findById() -> Buscar una casa
}