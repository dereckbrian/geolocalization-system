package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.JovemResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.JovemMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.JovemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JovemService {

    private final JovemRepository jovemRepository;
    private final JovemMapper jovemMapper;

    public List<JovemResponseDTO> retornarJovem(UUID id){

        return jovemRepository.findByTioId(id)
                .stream()
                .map(jovemMapper::toResponseDTO)
                .toList();
    }
}
