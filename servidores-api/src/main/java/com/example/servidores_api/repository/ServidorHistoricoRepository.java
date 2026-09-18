package com.example.servidores_api.repository;

import com.example.servidores_api.entity.ServidorHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServidorHistoricoRepository
        extends JpaRepository<ServidorHistorico, Long> {

    List<ServidorHistorico> findByServidorIdOrderBySequencialAsc(Long servidorId);

    @Query("""
        SELECT MAX(historico.sequencial)
        FROM ServidorHistorico historico
        WHERE historico.servidor.id = :servidorId
        """)
    Optional<Integer> buscarUltimoSequencial(
            @Param("servidorId") Long servidorId
    );
}