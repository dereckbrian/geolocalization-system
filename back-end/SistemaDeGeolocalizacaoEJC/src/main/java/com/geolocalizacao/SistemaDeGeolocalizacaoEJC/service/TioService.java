package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.TioResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.TioMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.TioRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TioService {

    private final TioRespository tioRespository;
    private final TioMapper tioMapper;

    public List<TioResponseDTO> acharTio(){
        return tioRespository.findAll()
                .stream()
                .map(tioMapper::toResponseDTO)
                .toList();
    }


}
