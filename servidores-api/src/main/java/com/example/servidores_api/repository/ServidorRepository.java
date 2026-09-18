package com.example.servidores_api.repository;

import com.example.servidores_api.entity.Servidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServidorRepository extends JpaRepository<Servidor, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsBySecretariaIdAndAtivoTrue(Long secretariaId);

    List<Servidor> findByAtivoTrue();
}