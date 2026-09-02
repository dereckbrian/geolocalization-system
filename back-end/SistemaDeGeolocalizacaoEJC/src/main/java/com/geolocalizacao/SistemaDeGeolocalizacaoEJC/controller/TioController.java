package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.controller;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.TioResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service.TioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tio")
@RequiredArgsConstructor
public class TioController {

    private final TioService tioService;

    @GetMapping("/buscar")
    public ResponseEntity<List<TioResponseDTO>> retornarTio(){
        List<TioResponseDTO> acharTio = tioService.acharTio();
        return ResponseEntity.status(HttpStatus.OK).body(acharTio);
    }
}
