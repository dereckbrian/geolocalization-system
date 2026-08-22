package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.controller;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.CadastroVisitaDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.VisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service.VisitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/visita")
public class VisitaController {

    private final VisitaService visitaService;

    public VisitaController(VisitaService visitaService){
        this.visitaService = visitaService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<VisitaResponseDTO> cadastrarVisita(@RequestBody @Valid CadastroVisitaDTO dto){
        VisitaResponseDTO visitaResponseDTO = visitaService.cadastrarVisita(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(visitaResponseDTO);
    }

    @GetMapping("/buscarRota")
    public ResponseEntity<List<Visita>> buscarRotaDia(@RequestParam(defaultValue = "5") int quantidade){
        List<Visita> rota = visitaService.gerarRotaDoDia(quantidade);
        return ResponseEntity.status(HttpStatus.OK).body(rota);
    }

    @PatchMapping("/{id}/sucesso")
    public ResponseEntity<VisitaResponseDTO> marcarComoVisitado(@PathVariable("id") UUID id) {
        VisitaResponseDTO dto = visitaService.registrarSucesso(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PatchMapping("/{id}/falta")
    public ResponseEntity<VisitaResponseDTO> marcarFalta(@PathVariable("id") UUID id) {
        VisitaResponseDTO dto = visitaService.registrarFalta(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
