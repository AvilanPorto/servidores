package com.example.servidores_api.repository;

import com.example.servidores_api.entity.Secretaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {

    boolean existsBySiglaIgnoreCase(String sigla);

    List<Secretaria> findByAtivoTrue();
}