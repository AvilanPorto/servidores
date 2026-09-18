package com.example.servidores_api.entity;

import com.example.servidores_api.entity.enums.TipoEvento;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "servidor_historico",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_historico_servidor_sequencial",
                        columnNames = {"servidor_id", "sequencial"}
                )
        }
)
public class ServidorHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servidor_id", nullable = false)
    private Servidor servidor;

    @Column(nullable = false)
    private Integer sequencial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 20)
    private TipoEvento tipoEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretaria_origem_id")
    private Secretaria secretariaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretaria_destino_id")
    private Secretaria secretariaDestino;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ServidorHistorico() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Servidor getServidor() {
        return servidor;
    }

    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Secretaria getSecretariaOrigem() {
        return secretariaOrigem;
    }

    public void setSecretariaOrigem(Secretaria secretariaOrigem) {
        this.secretariaOrigem = secretariaOrigem;
    }

    public Secretaria getSecretariaDestino() {
        return secretariaDestino;
    }

    public void setSecretariaDestino(Secretaria secretariaDestino) {
        this.secretariaDestino = secretariaDestino;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}