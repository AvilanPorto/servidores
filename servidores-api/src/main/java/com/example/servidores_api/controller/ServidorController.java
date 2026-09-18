package com.example.servidores_api.controller;

import com.example.servidores_api.dto.ServidorCreateRequest;
import com.example.servidores_api.dto.ServidorHistoricoResponse;
import com.example.servidores_api.dto.ServidorResponse;
import com.example.servidores_api.service.ServidorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.servidores_api.dto.ServidorUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/servidores")
public class ServidorController {

    private final ServidorService servidorService;

    public ServidorController(ServidorService servidorService) {
        this.servidorService = servidorService;
    }

    @PostMapping
    public ResponseEntity<ServidorResponse> criar(
            @Valid @RequestBody ServidorCreateRequest request) {

        ServidorResponse response = servidorService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ServidorResponse>> listar() {

        List<ServidorResponse> servidores =
                servidorService.listarAtivos();

        return ResponseEntity.ok(servidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServidorResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                servidorService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}/transferencia")
    public ResponseEntity<ServidorResponse> transferir(
            @PathVariable Long id,
            @RequestParam Long secretariaDestinoId) {

        ServidorResponse response =
                servidorService.transferir(id, secretariaDestinoId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/desligamento")
    public ResponseEntity<ServidorResponse> desligar(
            @PathVariable Long id) {

        ServidorResponse response =
                servidorService.desligar(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reativacao")
    public ResponseEntity<ServidorResponse> reativar(
            @PathVariable Long id,
            @RequestParam Long secretariaId) {

        ServidorResponse response =
                servidorService.reativar(id, secretariaId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServidorResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServidorUpdateRequest request
    ) {
        return ResponseEntity.ok(
                servidorService.atualizar(id, request)
        );
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<ServidorHistoricoResponse>> listarHistorico(
            @PathVariable Long id) {

        List<ServidorHistoricoResponse> historico =
                servidorService.listarHistorico(id);

        return ResponseEntity.ok(historico);
    }

}