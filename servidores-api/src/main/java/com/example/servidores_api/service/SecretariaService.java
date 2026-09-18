package com.example.servidores_api.service;

import com.example.servidores_api.dto.SecretariaCreateRequest;
import com.example.servidores_api.dto.SecretariaResponse;
import com.example.servidores_api.dto.SecretariaUpdateRequest;
import com.example.servidores_api.entity.Secretaria;
import com.example.servidores_api.exception.BusinessRuleException;
import com.example.servidores_api.exception.ResourceNotFoundException;
import com.example.servidores_api.repository.SecretariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.servidores_api.repository.ServidorRepository;

import java.util.List;
import java.util.Locale;

@Service
public class SecretariaService {

    private final SecretariaRepository secretariaRepository;
    private final ServidorRepository servidorRepository;

    public SecretariaService(
            SecretariaRepository secretariaRepository,
            ServidorRepository servidorRepository
    ) {
        this.secretariaRepository = secretariaRepository;
        this.servidorRepository = servidorRepository;
    }

    @Transactional
    public SecretariaResponse criar(SecretariaCreateRequest request) {

        String sigla = normalizarSigla(request.getSigla());

        if (secretariaRepository.existsBySiglaIgnoreCase(sigla)) {
            throw new BusinessRuleException(
                    "Já existe uma secretaria cadastrada com a sigla: " + sigla
            );
        }

        Secretaria secretaria = new Secretaria();

        secretaria.setNome(request.getNome());
        secretaria.setSigla(sigla);
        secretaria.setAtivo(true);

        Secretaria salva = secretariaRepository.save(secretaria);

        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<SecretariaResponse> listarAtivas() {

        return secretariaRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SecretariaResponse buscarPorId(Long id) {

        Secretaria secretaria = buscarEntidadePorId(id);

        return toResponse(secretaria);
    }

    @Transactional
    public SecretariaResponse atualizar(
            Long id,
            SecretariaUpdateRequest request) {

        Secretaria secretaria = buscarEntidadePorId(id);

        if (!secretaria.getAtivo()) {
            throw new BusinessRuleException(
                    "Não é possível atualizar uma secretaria inativa."
            );
        }

        String sigla = normalizarSigla(request.getSigla());

        if (!secretaria.getSigla().equalsIgnoreCase(sigla)
                && secretariaRepository.existsBySiglaIgnoreCase(sigla)) {

            throw new BusinessRuleException(
                    "Já existe uma secretaria cadastrada com a sigla: " + sigla
            );
        }

        secretaria.setNome(request.getNome());
        secretaria.setSigla(sigla);

        Secretaria atualizada = secretariaRepository.save(secretaria);

        return toResponse(atualizada);
    }

    @Transactional
    public void inativar(Long id) {

        Secretaria secretaria = buscarEntidadePorId(id);

        if (!secretaria.getAtivo()) {
            throw new BusinessRuleException(
                    "A secretaria já está inativa."
            );
        }

        boolean possuiServidoresAtivos =
                servidorRepository.existsBySecretariaIdAndAtivoTrue(id);

        if (possuiServidoresAtivos) {
            throw new BusinessRuleException(
                    "Não é possível inativar a secretaria porque existem servidores ativos vinculados a ela."
            );
        }

        secretaria.setAtivo(false);

        secretariaRepository.save(secretaria);
    }

    private String normalizarSigla(String sigla) {
        return sigla.trim().toUpperCase(Locale.ROOT);
    }

    private SecretariaResponse toResponse(Secretaria secretaria) {

        return new SecretariaResponse(
                secretaria.getId(),
                secretaria.getNome(),
                secretaria.getSigla(),
                secretaria.getAtivo()
        );
    }

    private Secretaria buscarEntidadePorId(Long id) {

        return secretariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Secretaria não encontrada com o ID: " + id
                ));
    }


}