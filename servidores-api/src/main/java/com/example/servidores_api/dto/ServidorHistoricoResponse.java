package com.example.servidores_api.dto;

import com.example.servidores_api.entity.enums.TipoEvento;

import java.time.LocalDate;

public class ServidorHistoricoResponse {

    private Long id;
    private Integer sequencial;
    private TipoEvento tipoEvento;
    private LocalDate dataEvento;
    private SecretariaResponse secretariaOrigem;
    private SecretariaResponse secretariaDestino;

    public ServidorHistoricoResponse(
            Long id,
            Integer sequencial,
            TipoEvento tipoEvento,
            LocalDate dataEvento,
            SecretariaResponse secretariaOrigem,
            SecretariaResponse secretariaDestino) {

        this.id = id;
        this.sequencial = sequencial;
        this.tipoEvento = tipoEvento;
        this.dataEvento = dataEvento;
        this.secretariaOrigem = secretariaOrigem;
        this.secretariaDestino = secretariaDestino;
    }

    public Long getId() {
        return id;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public SecretariaResponse getSecretariaOrigem() {
        return secretariaOrigem;
    }

    public SecretariaResponse getSecretariaDestino() {
        return secretariaDestino;
    }
}