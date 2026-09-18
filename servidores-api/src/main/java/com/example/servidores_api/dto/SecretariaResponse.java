package com.example.servidores_api.dto;

public class SecretariaResponse {

    private Long id;
    private String nome;
    private String sigla;
    private Boolean ativo;

    public SecretariaResponse(
            Long id,
            String nome,
            String sigla,
            Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}