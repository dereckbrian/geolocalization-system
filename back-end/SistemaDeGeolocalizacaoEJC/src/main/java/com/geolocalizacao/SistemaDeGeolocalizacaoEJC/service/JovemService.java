package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.JovemRepository;
import org.springframework.stereotype.Service;

@Service
public class JovemService {

    private final JovemRepository jovemRepository;

    public JovemService(JovemRepository jovemRepository){
        this.jovemRepository = jovemRepository;
    }
}
