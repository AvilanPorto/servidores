ALTER TABLE servidor_historico
DROP CONSTRAINT ck_historico_tipo_evento;

ALTER TABLE servidor_historico
    ADD CONSTRAINT ck_historico_tipo_evento
        CHECK (
            tipo_evento IN (
                            'ADMISSAO',
                            'TRANSFERENCIA',
                            'DESLIGAMENTO',
                            'REATIVACAO'
                )
            );