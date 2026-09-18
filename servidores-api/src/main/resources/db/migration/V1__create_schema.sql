CREATE TABLE secretaria (
                            id BIGSERIAL PRIMARY KEY,
                            nome VARCHAR(150) NOT NULL,
                            sigla VARCHAR(20) NOT NULL,
                            ativo BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT uk_secretaria_sigla UNIQUE (sigla)
);

CREATE TABLE servidor (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          email VARCHAR(254) NOT NULL,
                          data_nascimento DATE NOT NULL,
                          secretaria_id BIGINT NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT uk_servidor_email UNIQUE (email),

                          CONSTRAINT fk_servidor_secretaria
                              FOREIGN KEY (secretaria_id)
                                  REFERENCES secretaria(id)
);

CREATE TABLE servidor_historico (
                                    id BIGSERIAL PRIMARY KEY,
                                    servidor_id BIGINT NOT NULL,
                                    sequencial INTEGER NOT NULL,
                                    tipo_evento VARCHAR(20) NOT NULL,
                                    secretaria_origem_id BIGINT,
                                    secretaria_destino_id BIGINT,
                                    data_evento DATE NOT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT uk_historico_servidor_sequencial
                                        UNIQUE (servidor_id, sequencial),

                                    CONSTRAINT ck_historico_sequencial_positivo
                                        CHECK (sequencial > 0),

                                    CONSTRAINT ck_historico_tipo_evento
                                        CHECK (
                                            tipo_evento IN (
                                                            'ADMISSAO',
                                                            'TRANSFERENCIA',
                                                            'DESLIGAMENTO'
                                                )
                                            ),

                                    CONSTRAINT fk_historico_servidor
                                        FOREIGN KEY (servidor_id)
                                            REFERENCES servidor(id),

                                    CONSTRAINT fk_historico_secretaria_origem
                                        FOREIGN KEY (secretaria_origem_id)
                                            REFERENCES secretaria(id),

                                    CONSTRAINT fk_historico_secretaria_destino
                                        FOREIGN KEY (secretaria_destino_id)
                                            REFERENCES secretaria(id)
);

CREATE INDEX idx_servidor_secretaria
    ON servidor (secretaria_id);

CREATE INDEX idx_servidor_email
    ON servidor (email);

CREATE INDEX idx_historico_servidor
    ON servidor_historico (servidor_id);

CREATE INDEX idx_historico_data_evento
    ON servidor_historico (data_evento);