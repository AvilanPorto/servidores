package com.example.servidores_api.service;

import com.example.servidores_api.dto.SecretariaResponse;
import com.example.servidores_api.dto.ServidorCreateRequest;
import com.example.servidores_api.dto.ServidorResponse;
import com.example.servidores_api.entity.Secretaria;
import com.example.servidores_api.entity.Servidor;
import com.example.servidores_api.entity.ServidorHistorico;
import com.example.servidores_api.entity.enums.TipoEvento;
import com.example.servidores_api.exception.BusinessRuleException;
import com.example.servidores_api.exception.ResourceNotFoundException;
import com.example.servidores_api.repository.SecretariaRepository;
import com.example.servidores_api.repository.ServidorHistoricoRepository;
import com.example.servidores_api.repository.ServidorRepository;
import com.example.servidores_api.dto.ServidorHistoricoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.servidores_api.dto.ServidorUpdateRequest;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ServidorService {

    private final ServidorRepository servidorRepository;
    private final SecretariaRepository secretariaRepository;
    private final ServidorHistoricoRepository servidorHistoricoRepository;

    public ServidorService(
            ServidorRepository servidorRepository,
            SecretariaRepository secretariaRepository,
            ServidorHistoricoRepository servidorHistoricoRepository) {

        this.servidorRepository = servidorRepository;
        this.secretariaRepository = secretariaRepository;
        this.servidorHistoricoRepository = servidorHistoricoRepository;
    }

    @Transactional
    public ServidorResponse criar(ServidorCreateRequest request) {

        String email = normalizarEmail(request.getEmail());

        validarIdade(request.getDataNascimento());

        if (servidorRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException(
                    "Já existe um servidor cadastrado com o e-mail: " + email
            );
        }

        Secretaria secretaria = buscarSecretariaAtiva(
                request.getSecretariaId()
        );

        Servidor servidor = new Servidor();

        servidor.setNome(request.getNome());
        servidor.setEmail(email);
        servidor.setDataNascimento(request.getDataNascimento());
        servidor.setSecretaria(secretaria);
        servidor.setAtivo(true);

        Servidor servidorSalvo = servidorRepository.save(servidor);

        registrarAdmissao(servidorSalvo, secretaria);

        return toResponse(servidorSalvo);
    }

    public List<ServidorResponse> listarAtivos() {

        return servidorRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServidorResponse buscarPorId(Long id) {

        Servidor servidor = buscarEntidadePorId(id);

        return toResponse(servidor);
    }

    @Transactional
    public ServidorResponse atualizar(Long id, ServidorUpdateRequest request) {

        Servidor servidor = buscarEntidadePorId(id);

        String email = normalizarEmail(request.getEmail());

        validarIdade(request.getDataNascimento());

        if (!email.equalsIgnoreCase(servidor.getEmail())
                && servidorRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException(
                    "Já existe um servidor cadastrado com o e-mail: " + email
            );
        }

        Secretaria secretaria = buscarSecretariaAtiva(
                request.getSecretariaId()
        );

        servidor.setNome(request.getNome());
        servidor.setEmail(email);
        servidor.setDataNascimento(request.getDataNascimento());
        servidor.setSecretaria(secretaria);

        Servidor servidorSalvo = servidorRepository.save(servidor);

        return toResponse(servidorSalvo);
    }

    private Secretaria buscarSecretariaAtiva(Long secretariaId) {

        if (secretariaId == null) {
            throw new BusinessRuleException(
                    "A secretaria é obrigatória."
            );
        }

        Secretaria secretaria = secretariaRepository.findById(secretariaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Secretaria não encontrada: " + secretariaId
                        )
                );

        if (!secretaria.getAtivo()) {
            throw new BusinessRuleException(
                    "A secretaria informada está inativa."
            );
        }

        return secretaria;
    }

    private void validarIdade(LocalDate dataNascimento) {

        if (dataNascimento == null) {
            throw new BusinessRuleException(
                    "A data de nascimento é obrigatória."
            );
        }

        LocalDate hoje = LocalDate.now();

        if (dataNascimento.isAfter(hoje)) {
            throw new BusinessRuleException(
                    "A data de nascimento não pode ser futura."
            );
        }

        int idade = Period.between(dataNascimento, hoje).getYears();

        if (idade < 18 || idade > 75) {
            throw new BusinessRuleException(
                    "O servidor deve ter entre 18 e 75 anos."
            );
        }
    }

    private String normalizarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new BusinessRuleException(
                    "O e-mail é obrigatório."
            );
        }

        return email.trim().toLowerCase();
    }

    @Transactional
    public ServidorResponse transferir(
            Long id,
            Long secretariaDestinoId) {

        Servidor servidor = buscarEntidadePorId(id);

        if (!servidor.getAtivo()) {
            throw new BusinessRuleException(
                    "Não é possível transferir um servidor desligado."
            );
        }

        Secretaria secretariaOrigem = servidor.getSecretaria();

        Secretaria secretariaDestino =
                buscarSecretariaAtiva(secretariaDestinoId);

        if (secretariaOrigem.getId().equals(secretariaDestino.getId())) {
            throw new BusinessRuleException(
                    "O servidor já pertence a essa secretaria."
            );
        }

        servidor.setSecretaria(secretariaDestino);

        Servidor servidorAtualizado =
                servidorRepository.save(servidor);

        registrarTransferencia(
                servidorAtualizado,
                secretariaOrigem,
                secretariaDestino
        );

        return toResponse(servidorAtualizado);
    }

    @Transactional
    public ServidorResponse desligar(Long id) {

        Servidor servidor = buscarEntidadePorId(id);

        if (!servidor.getAtivo()) {
            throw new BusinessRuleException(
                    "O servidor já está desligado."
            );
        }

        Secretaria secretariaOrigem = servidor.getSecretaria();

        servidor.setAtivo(false);

        Servidor servidorAtualizado =
                servidorRepository.save(servidor);

        registrarDesligamento(
                servidorAtualizado,
                secretariaOrigem
        );

        return toResponse(servidorAtualizado);
    }

    @Transactional
    public ServidorResponse reativar(
            Long id,
            Long secretariaId) {

        Servidor servidor = buscarEntidadePorId(id);

        if (servidor.getAtivo()) {
            throw new BusinessRuleException(
                    "O servidor já está ativo."
            );
        }

        Secretaria secretaria =
                buscarSecretariaAtiva(secretariaId);

        servidor.setSecretaria(secretaria);
        servidor.setAtivo(true);

        Servidor servidorAtualizado =
                servidorRepository.save(servidor);

        registrarReativacao(
                servidorAtualizado,
                secretaria
        );

        return toResponse(servidorAtualizado);
    }

    private void registrarDesligamento(
            Servidor servidor,
            Secretaria secretariaOrigem) {

        Integer ultimoSequencial = servidorHistoricoRepository
                .buscarUltimoSequencial(servidor.getId())
                .orElse(0);

        ServidorHistorico historico = new ServidorHistorico();

        historico.setServidor(servidor);
        historico.setSequencial(ultimoSequencial + 1);
        historico.setTipoEvento(TipoEvento.DESLIGAMENTO);
        historico.setSecretariaOrigem(secretariaOrigem);
        historico.setSecretariaDestino(null);
        historico.setDataEvento(LocalDate.now());

        servidorHistoricoRepository.save(historico);
    }


    private void registrarAdmissao(
            Servidor servidor,
            Secretaria secretariaDestino) {

        ServidorHistorico historico = new ServidorHistorico();

        historico.setServidor(servidor);
        historico.setSequencial(1);
        historico.setTipoEvento(TipoEvento.ADMISSAO);
        historico.setSecretariaOrigem(null);
        historico.setSecretariaDestino(secretariaDestino);
        historico.setDataEvento(LocalDate.now());

        servidorHistoricoRepository.save(historico);
    }

    private void registrarTransferencia(
            Servidor servidor,
            Secretaria secretariaOrigem,
            Secretaria secretariaDestino) {

        Integer ultimoSequencial = servidorHistoricoRepository
                .buscarUltimoSequencial(servidor.getId())
                .orElse(0);

        ServidorHistorico historico = new ServidorHistorico();

        historico.setServidor(servidor);
        historico.setSequencial(ultimoSequencial + 1);
        historico.setTipoEvento(TipoEvento.TRANSFERENCIA);
        historico.setSecretariaOrigem(secretariaOrigem);
        historico.setSecretariaDestino(secretariaDestino);
        historico.setDataEvento(LocalDate.now());

        servidorHistoricoRepository.save(historico);
    }

    private void registrarReativacao(
            Servidor servidor,
            Secretaria secretariaDestino) {

        Integer ultimoSequencial = servidorHistoricoRepository
                .buscarUltimoSequencial(servidor.getId())
                .orElse(0);

        ServidorHistorico historico = new ServidorHistorico();

        historico.setServidor(servidor);
        historico.setSequencial(ultimoSequencial + 1);
        historico.setTipoEvento(TipoEvento.REATIVACAO);
        historico.setSecretariaOrigem(null);
        historico.setSecretariaDestino(secretariaDestino);
        historico.setDataEvento(LocalDate.now());

        servidorHistoricoRepository.save(historico);
    }

    public List<ServidorHistoricoResponse> listarHistorico(Long servidorId) {

        buscarPorId(servidorId);

        return servidorHistoricoRepository
                .findByServidorIdOrderBySequencialAsc(servidorId)
                .stream()
                .map(this::toHistoricoResponse)
                .toList();
    }

    private ServidorResponse toResponse(Servidor servidor) {

        Secretaria secretaria = servidor.getSecretaria();

        SecretariaResponse secretariaResponse =
                new SecretariaResponse(
                        secretaria.getId(),
                        secretaria.getNome(),
                        secretaria.getSigla(),
                        secretaria.getAtivo()
                );

        return new ServidorResponse(
                servidor.getId(),
                servidor.getNome(),
                servidor.getEmail(),
                servidor.getDataNascimento(),
                servidor.getAtivo(),
                secretariaResponse
        );
    }

    private ServidorHistoricoResponse toHistoricoResponse(
            ServidorHistorico historico) {

        SecretariaResponse secretariaOrigem = null;

        if (historico.getSecretariaOrigem() != null) {
            Secretaria secretaria = historico.getSecretariaOrigem();

            secretariaOrigem = new SecretariaResponse(
                    secretaria.getId(),
                    secretaria.getNome(),
                    secretaria.getSigla(),
                    secretaria.getAtivo()
            );
        }

        SecretariaResponse secretariaDestino = null;

        if (historico.getSecretariaDestino() != null) {
            Secretaria secretaria = historico.getSecretariaDestino();

            secretariaDestino = new SecretariaResponse(
                    secretaria.getId(),
                    secretaria.getNome(),
                    secretaria.getSigla(),
                    secretaria.getAtivo()
            );
        }

        return new ServidorHistoricoResponse(
                historico.getId(),
                historico.getSequencial(),
                historico.getTipoEvento(),
                historico.getDataEvento(),
                secretariaOrigem,
                secretariaDestino
        );
    }

    private Servidor buscarEntidadePorId(Long id) {

        return servidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Servidor não encontrado com o ID: " + id
                ));
    }


}