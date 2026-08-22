package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.TioRespository;
import org.springframework.stereotype.Service;

@Service
public class TioService {

    private final TioRespository tioRespository;

    public TioService(TioRespository tioRespository){
        this.tioRespository = tioRespository;
    }
}
