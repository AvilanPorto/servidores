package com.example.servidores_api.dto;

import jakarta.validation.constraints.NotBlank;

public class SecretariaUpdateRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "A sigla é obrigatória.")
    private String sigla;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}