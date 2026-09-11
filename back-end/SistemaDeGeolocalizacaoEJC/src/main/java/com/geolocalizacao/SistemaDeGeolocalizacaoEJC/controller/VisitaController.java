package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.controller;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.CadastroVisitaDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.RotaVisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.VisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service.RotaService;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service.VisitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/visita")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;
    private final RotaService rotaService;

    @PostMapping("/cadastrar")
    public ResponseEntity<VisitaResponseDTO> cadastrarVisita(@RequestBody @Valid CadastroVisitaDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(visitaService.cadastrarVisita(dto));
    }

    @PatchMapping("/{id}/sucesso")
    public ResponseEntity<VisitaResponseDTO> marcarComoVisitado(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(visitaService.registrarSucesso(id));
    }

    @PatchMapping("/{id}/falta")
    public ResponseEntity<VisitaResponseDTO> marcarFalta(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(visitaService.registrarFalta(id));
    }

    @GetMapping("/rota")
    public ResponseEntity<List<RotaVisitaResponseDTO>> gerarRota() {
        return ResponseEntity.status(HttpStatus.OK).body(rotaService.buscarRotaAtual());
    }

    @PostMapping("/rota/gerar")
    public ResponseEntity<List<RotaVisitaResponseDTO>> gerarNovaRota() {
        return ResponseEntity.status(HttpStatus.OK).body(rotaService.gerarRota());
    }
}
