package com.example.servidores_api.service;

import com.example.servidores_api.dto.SecretariaCreateRequest;
import com.example.servidores_api.dto.SecretariaResponse;
import com.example.servidores_api.dto.SecretariaUpdateRequest;
import com.example.servidores_api.entity.Secretaria;
import com.example.servidores_api.exception.BusinessRuleException;
import com.example.servidores_api.exception.ResourceNotFoundException;
import com.example.servidores_api.repository.SecretariaRepository;
import com.example.servidores_api.repository.ServidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecretariaServiceTest {

    @Mock
    private SecretariaRepository secretariaRepository;

    @Mock
    private ServidorRepository servidorRepository;

    @InjectMocks
    private SecretariaService secretariaService;

    @Test
    void deveCriarSecretariaComDadosValidos() {

        SecretariaCreateRequest request = new SecretariaCreateRequest();
        request.setNome("Secretaria Municipal de Saúde");
        request.setSigla("sms");

        Secretaria secretaria = new Secretaria();
        secretaria.setId(10L);
        secretaria.setNome("Secretaria Municipal de Saúde");
        secretaria.setSigla("SMS");
        secretaria.setAtivo(true);

        when(secretariaRepository.existsBySiglaIgnoreCase("SMS"))
                .thenReturn(false);

        when(secretariaRepository.save(any(Secretaria.class)))
                .thenReturn(secretaria);

        SecretariaResponse response =
                secretariaService.criar(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Secretaria Municipal de Saúde", response.getNome());
        assertEquals("SMS", response.getSigla());
        assertTrue(response.getAtivo());

        verify(secretariaRepository)
                .save(any(Secretaria.class));
    }

    @Test
    void deveImpedirCriacaoComSiglaDuplicada() {

        SecretariaCreateRequest request = new SecretariaCreateRequest();
        request.setNome("Outra Secretaria");
        request.setSigla("sms");

        when(secretariaRepository.existsBySiglaIgnoreCase("SMS"))
                .thenReturn(true);

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> secretariaService.criar(request)
                );

        assertEquals(
                "Já existe uma secretaria cadastrada com a sigla: SMS",
                exception.getMessage()
        );

        verify(secretariaRepository, never())
                .save(any(Secretaria.class));
    }

    @Test
    void deveImpedirInativacaoDeSecretariaComServidorAtivo() {

        Secretaria secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Secretaria Municipal de Saúde");
        secretaria.setSigla("SMS");
        secretaria.setAtivo(true);

        when(secretariaRepository.findById(1L))
                .thenReturn(Optional.of(secretaria));

        when(servidorRepository.existsBySecretariaIdAndAtivoTrue(1L))
                .thenReturn(true);

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> secretariaService.inativar(1L)
                );

        assertEquals(
                "Não é possível inativar a secretaria porque existem servidores ativos vinculados a ela.",
                exception.getMessage()
        );

        verify(secretariaRepository, never())
                .save(any(Secretaria.class));
    }

    @Test
    void deveInativarSecretariaSemServidoresAtivos() {

        Secretaria secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Secretaria Municipal de Saúde");
        secretaria.setSigla("SMS");
        secretaria.setAtivo(true);

        when(secretariaRepository.findById(1L))
                .thenReturn(Optional.of(secretaria));

        when(servidorRepository.existsBySecretariaIdAndAtivoTrue(1L))
                .thenReturn(false);

        secretariaService.inativar(1L);

        assertFalse(secretaria.getAtivo());

        verify(secretariaRepository)
                .save(secretaria);
    }

    @Test
    void deveImpedirInativacaoDeSecretariaJaInativa() {

        Secretaria secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setAtivo(false);

        when(secretariaRepository.findById(1L))
                .thenReturn(Optional.of(secretaria));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> secretariaService.inativar(1L)
                );

        assertEquals(
                "A secretaria já está inativa.",
                exception.getMessage()
        );

        verify(secretariaRepository, never())
                .save(any(Secretaria.class));
    }

    @Test
    void deveLancarExcecaoAoBuscarSecretariaInexistente() {

        when(secretariaRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> secretariaService.buscarPorId(999L)
                );

        assertEquals(
                "Secretaria não encontrada com o ID: 999",
                exception.getMessage()
        );
    }
}