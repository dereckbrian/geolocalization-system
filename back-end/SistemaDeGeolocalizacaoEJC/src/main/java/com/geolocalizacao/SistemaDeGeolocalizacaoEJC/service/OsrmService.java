package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr.OsrmTripResponse;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class OsrmService {

    private static final Double LATITUDE_IGREJA = -15.549093;
    private static final Double LONGITUDE_IGREJA = -47.330434;

    private final RestClient restClient;
    private final String osrmUrl;

    public OsrmService(@Value("${osrm.url}") String osrmUrl){
        this.osrmUrl = osrmUrl;
        this.restClient = RestClient.create();
    }
    public OsrmTripResponse calcularRota(List<Visita> visitas){
        String coodenadas = montarCoordenadas(visitas);

        String url = osrmUrl + "/trip/v1/driving/" + coodenadas + "?source=first" + "&roundtrip=false" + "&overview=false";

        OsrmTripResponse response = restClient.get().uri(URI.create(url)).retrieve().body(OsrmTripResponse.class);

        if (response == null || !"Ok".equals(response.code())){
            throw new IllegalArgumentException("Não foi possível calcular a rota");
        }

        return response;
    }

    public String montarCoordenadas(List<Visita> visitas){
        List<String> coordenadas = new ArrayList<>();

        coordenadas.add(LONGITUDE_IGREJA + "," + LATITUDE_IGREJA);

        for (Visita visita: visitas){
            Point ponto = visita.getCoordenada();

            coordenadas.add(ponto.getX() + "," + ponto.getY());
        }

        return String.join(";", coordenadas);

    }
}
