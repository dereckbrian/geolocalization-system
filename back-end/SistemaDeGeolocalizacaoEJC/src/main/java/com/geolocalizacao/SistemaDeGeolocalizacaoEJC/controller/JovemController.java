package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.controller;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.CadastroJovemDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.JovemResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service.JovemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jovem")
@RequiredArgsConstructor
public class JovemController {

    private final JovemService jovemService;

    @GetMapping("/achar/{id}")
    public ResponseEntity<List<JovemResponseDTO>> acharJovens(@PathVariable("id") UUID id){
        List<JovemResponseDTO> jovemResponseDTO = jovemService.retornarJovem(id);
        return ResponseEntity.status(HttpStatus.OK).body(jovemResponseDTO);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<JovemResponseDTO> cadastrarJovem(@RequestBody CadastroJovemDTO body){
        JovemResponseDTO reponse = jovemService.cadastroJovem(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}
