package com.example.servidores_api.service;

import com.example.servidores_api.exception.BusinessRuleException;
import com.example.servidores_api.exception.ResourceNotFoundException;
import com.example.servidores_api.repository.SecretariaRepository;
import com.example.servidores_api.repository.ServidorHistoricoRepository;
import com.example.servidores_api.repository.ServidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.servidores_api.dto.ServidorCreateRequest;
import com.example.servidores_api.dto.ServidorResponse;
import com.example.servidores_api.entity.Secretaria;
import com.example.servidores_api.entity.Servidor;
import com.example.servidores_api.dto.ServidorUpdateRequest;
import java.util.Optional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServidorServiceTest {

    @Mock
    private ServidorRepository servidorRepository;

    @Mock
    private SecretariaRepository secretariaRepository;

    @Mock
    private ServidorHistoricoRepository servidorHistoricoRepository;

    @InjectMocks
    private ServidorService servidorService;

    @Test
    void deveCriarServidorComDadosValidos() {

        ServidorCreateRequest request = new ServidorCreateRequest();
        request.setNome("Carlos Oliveira");
        request.setEmail("carlos@prefeitura.gov.br");
        request.setDataNascimento(
                LocalDate.of(1990, 5, 15)
        );
        request.setSecretariaId(1L);

        Secretaria secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Secretaria Municipal de Saúde");
        secretaria.setSigla("SMS");
        secretaria.setAtivo(true);

        Servidor servidorSalvo = new Servidor();
        servidorSalvo.setId(10L);
        servidorSalvo.setNome("Carlos Oliveira");
        servidorSalvo.setEmail("carlos@prefeitura.gov.br");
        servidorSalvo.setDataNascimento(
                LocalDate.of(1990, 5, 15)
        );
        servidorSalvo.setSecretaria(secretaria);
        servidorSalvo.setAtivo(true);

        when(servidorRepository.existsByEmailIgnoreCase(
                "carlos@prefeitura.gov.br"
        )).thenReturn(false);

        when(secretariaRepository.findById(1L))
                .thenReturn(java.util.Optional.of(secretaria));

        when(servidorRepository.save(any(Servidor.class)))
                .thenReturn(servidorSalvo);

        ServidorResponse response =
                servidorService.criar(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(
                "Carlos Oliveira",
                response.getNome()
        );
        assertEquals(
                "carlos@prefeitura.gov.br",
                response.getEmail()
        );
        assertTrue(response.getAtivo());

        verify(servidorRepository)
                .save(any(Servidor.class));

        verify(servidorHistoricoRepository)
                .save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado() {

        ServidorCreateRequest request = new ServidorCreateRequest();
        request.setNome("Carlos Oliveira");
        request.setEmail("carlos@prefeitura.gov.br");
        request.setDataNascimento(
                LocalDate.of(1990, 5, 15)
        );
        request.setSecretariaId(1L);

        when(servidorRepository.existsByEmailIgnoreCase(
                "carlos@prefeitura.gov.br"
        )).thenReturn(true);

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> servidorService.criar(request)
                );

        assertEquals(
                "Já existe um servidor cadastrado com o e-mail: carlos@prefeitura.gov.br",
                exception.getMessage()
        );

        verify(servidorRepository, never())
                .save(any(Servidor.class));
    }

    @Test
    void deveLancarExcecaoQuandoSecretariaNaoExistir() {

        ServidorCreateRequest request = new ServidorCreateRequest();
        request.setNome("Carlos Oliveira");
        request.setEmail("carlos@prefeitura.gov.br");
        request.setDataNascimento(
                LocalDate.of(1990, 5, 15)
        );
        request.setSecretariaId(999L);

        when(servidorRepository.existsByEmailIgnoreCase(
                "carlos@prefeitura.gov.br"
        )).thenReturn(false);

        when(secretariaRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> servidorService.criar(request)
                );

        assertEquals(
                "Secretaria não encontrada: 999",
                exception.getMessage()
        );

        verify(servidorRepository, never())
                .save(any(Servidor.class));
    }

    @Test
    void deveImpedirTransferenciaDeServidorDesligado() {

        Secretaria secretariaOrigem = new Secretaria();
        secretariaOrigem.setId(1L);
        secretariaOrigem.setNome("Secretaria Municipal de Saúde");
        secretariaOrigem.setSigla("SMS");
        secretariaOrigem.setAtivo(true);

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setNome("Carlos Oliveira");
        servidor.setEmail("carlos@prefeitura.gov.br");
        servidor.setDataNascimento(
                LocalDate.of(1990, 5, 15)
        );
        servidor.setSecretaria(secretariaOrigem);
        servidor.setAtivo(false);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> servidorService.transferir(10L, 2L)
                );

        assertEquals(
                "Não é possível transferir um servidor desligado.",
                exception.getMessage()
        );

        verify(servidorRepository, never())
                .save(any(Servidor.class));

        verify(secretariaRepository, never())
                .findById(anyLong());
    }

    @Test
    void deveImpedirTransferenciaParaMesmaSecretaria() {

        Secretaria secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Secretaria Municipal de Saúde");
        secretaria.setSigla("SMS");
        secretaria.setAtivo(true);

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setNome("Carlos Oliveira");
        servidor.setEmail("carlos@prefeitura.gov.br");
        servidor.setDataNascimento(
                LocalDate.of(1990, 5, 15)
        );
        servidor.setSecretaria(secretaria);
        servidor.setAtivo(true);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        when(secretariaRepository.findById(1L))
                .thenReturn(java.util.Optional.of(secretaria));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> servidorService.transferir(10L, 1L)
                );

        assertEquals(
                "O servidor já pertence a essa secretaria.",
                exception.getMessage()
        );

        verify(servidorRepository, never())
                .save(any(Servidor.class));

        verify(servidorHistoricoRepository, never())
                .save(any());
    }

    @Test
    void deveDesligarServidorAtivo() {

        Secretaria secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Secretaria Municipal de Saúde");
        secretaria.setSigla("SMS");
        secretaria.setAtivo(true);

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setNome("Carlos Oliveira");
        servidor.setEmail("carlos@prefeitura.gov.br");
        servidor.setDataNascimento(
                LocalDate.of(1990, 5, 15)
        );
        servidor.setSecretaria(secretaria);
        servidor.setAtivo(true);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        when(servidorRepository.save(any(Servidor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServidorResponse response =
                servidorService.desligar(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertFalse(response.getAtivo());

        verify(servidorRepository)
                .save(any(Servidor.class));

        verify(servidorHistoricoRepository)
                .save(any());
    }

    @Test
    void deveImpedirDesligamentoDeServidorJaDesligado() {

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setAtivo(false);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> servidorService.desligar(10L)
                );

        assertEquals(
                "O servidor já está desligado.",
                exception.getMessage()
        );

        verify(servidorRepository, never())
                .save(any(Servidor.class));
    }

    @Test
    void deveReativarServidorDesligado() {

        Secretaria secretaria = new Secretaria();
        secretaria.setId(2L);
        secretaria.setNome("Secretaria Municipal de Educação");
        secretaria.setSigla("SME");
        secretaria.setAtivo(true);

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setNome("Carlos Oliveira");
        servidor.setEmail("carlos@prefeitura.gov.br");
        servidor.setDataNascimento(LocalDate.of(1990, 5, 15));
        servidor.setAtivo(false);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        when(secretariaRepository.findById(2L))
                .thenReturn(java.util.Optional.of(secretaria));

        when(servidorRepository.save(any(Servidor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServidorResponse response =
                servidorService.reativar(10L, 2L);

        assertNotNull(response);
        assertTrue(response.getAtivo());
        assertEquals(2L, response.getSecretaria().getId());

        verify(servidorRepository)
                .save(any(Servidor.class));

        verify(servidorHistoricoRepository)
                .save(any());
    }

    @Test
    void deveImpedirReativacaoDeServidorJaAtivo() {

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setAtivo(true);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> servidorService.reativar(10L, 2L)
                );

        assertEquals(
                "O servidor já está ativo.",
                exception.getMessage()
        );

        verify(servidorRepository, never())
                .save(any(Servidor.class));
    }

    @Test
    void deveTransferirServidorParaOutraSecretaria() {

        Secretaria origem = new Secretaria();
        origem.setId(1L);
        origem.setNome("Secretaria Municipal de Saúde");
        origem.setSigla("SMS");
        origem.setAtivo(true);

        Secretaria destino = new Secretaria();
        destino.setId(2L);
        destino.setNome("Secretaria Municipal de Educação");
        destino.setSigla("SME");
        destino.setAtivo(true);

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setNome("Carlos Oliveira");
        servidor.setEmail("carlos@prefeitura.gov.br");
        servidor.setDataNascimento(LocalDate.of(1990, 5, 15));
        servidor.setSecretaria(origem);
        servidor.setAtivo(true);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        when(secretariaRepository.findById(2L))
                .thenReturn(java.util.Optional.of(destino));

        when(servidorRepository.save(any(Servidor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServidorResponse response =
                servidorService.transferir(10L, 2L);

        assertNotNull(response);
        assertEquals(2L, response.getSecretaria().getId());
        assertEquals("SME", response.getSecretaria().getSigla());

        verify(servidorRepository)
                .save(any(Servidor.class));

        verify(servidorHistoricoRepository)
                .save(any());
    }

    @Test
    void deveLancarExcecaoAoBuscarServidorInexistente() {

        when(servidorRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> servidorService.buscarPorId(999L)
                );

        assertEquals(
                "Servidor não encontrado com o ID: 999",
                exception.getMessage()
        );
    }

    @Test
    void deveListarHistoricoDoServidor() {

        Secretaria secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Secretaria Municipal de Saúde");
        secretaria.setSigla("SMS");
        secretaria.setAtivo(true);

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setNome("Carlos Oliveira");
        servidor.setEmail("carlos@prefeitura.gov.br");
        servidor.setDataNascimento(LocalDate.of(1990, 5, 15));
        servidor.setSecretaria(secretaria);
        servidor.setAtivo(true);

        when(servidorRepository.findById(10L))
                .thenReturn(java.util.Optional.of(servidor));

        when(servidorHistoricoRepository
                .findByServidorIdOrderBySequencialAsc(10L))
                .thenReturn(java.util.List.of());

        var response =
                servidorService.listarHistorico(10L);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(servidorHistoricoRepository)
                .findByServidorIdOrderBySequencialAsc(10L);
    }

    @Test
    void deveAtualizarServidor() {
        Long servidorId = 1L;

        Servidor servidor = new Servidor();
        servidor.setId(servidorId);
        servidor.setNome("João");
        servidor.setEmail("joao@email.com");
        servidor.setDataNascimento(LocalDate.of(1990, 1, 10));
        servidor.setAtivo(true);

        Secretaria secretaria = new Secretaria();
        secretaria.setId(2L);
        secretaria.setNome("Secretaria de Educação");
        secretaria.setSigla("EDU");
        secretaria.setAtivo(true);

        servidor.setSecretaria(secretaria);

        ServidorUpdateRequest request = new ServidorUpdateRequest();
        request.setNome("João da Silva");
        request.setEmail("joao.silva@email.com");
        request.setDataNascimento(LocalDate.of(1990, 1, 10));
        request.setSecretariaId(2L);

        when(servidorRepository.findById(servidorId))
                .thenReturn(Optional.of(servidor));

        when(servidorRepository.existsByEmailIgnoreCase("joao.silva@email.com"))
                .thenReturn(false);

        when(secretariaRepository.findById(2L))
                .thenReturn(Optional.of(secretaria));

        when(servidorRepository.save(servidor))
                .thenReturn(servidor);

        ServidorResponse response = servidorService.atualizar(
                servidorId,
                request
        );

        assertEquals("João da Silva", servidor.getNome());
        assertEquals("joao.silva@email.com", servidor.getEmail());
        assertEquals("João da Silva", response.getNome());

        verify(servidorRepository).save(servidor);
    }
}