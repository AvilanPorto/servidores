package com.example.servidores_api.controller;

import com.example.servidores_api.dto.SecretariaResponse;
import com.example.servidores_api.dto.SecretariaUpdateRequest;
import com.example.servidores_api.dto.SecretariaCreateRequest;
import com.example.servidores_api.service.SecretariaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secretarias")
public class SecretariaController {

    private final SecretariaService secretariaService;

    public SecretariaController(SecretariaService secretariaService) {
        this.secretariaService = secretariaService;
    }

    @PostMapping
    public ResponseEntity<SecretariaResponse> criar(
            @Valid @RequestBody SecretariaCreateRequest request) {

        SecretariaResponse response =
                secretariaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<SecretariaResponse>> listar() {

        return ResponseEntity.ok(
                secretariaService.listarAtivas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecretariaResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                secretariaService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SecretariaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SecretariaUpdateRequest request) {

        return ResponseEntity.ok(
                secretariaService.atualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(
            @PathVariable Long id) {

        secretariaService.inativar(id);

        return ResponseEntity.noContent().build();
    }
}