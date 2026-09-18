package com.example.servidores_api.dto;

import java.time.LocalDate;

public class ServidorResponse {

    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private Boolean ativo;
    private SecretariaResponse secretaria;

    public ServidorResponse(
            Long id,
            String nome,
            String email,
            LocalDate dataNascimento,
            Boolean ativo,
            SecretariaResponse secretaria) {

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.ativo = ativo;
        this.secretaria = secretaria;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public SecretariaResponse getSecretaria() {
        return secretaria;
    }
}