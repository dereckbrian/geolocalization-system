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

    //recebe entidades como: Pedro, Lucas e Mateus
    public OsrmTripResponse calcularRota(List<Visita> visitas){

        //função responsalvel por separar as coordenadas da entidade visisita, retornando algo como: -47.330434,-15.549093;
        String coodenadas = montarCoordenadas(visitas);

        //cria a url com as coordenasdas retornadas pelo metodo montarCoordenadas.
        String url = osrmUrl + "/trip/v1/driving/" + coodenadas + "?source=first" + "&roundtrip=false" + "&overview=false";

        //Faz a chamada da API do OSRM para retornar a melhor rota para cada coordenada
        OsrmTripResponse response = restClient.get().uri(URI.create(url)).retrieve().body(OsrmTripResponse.class);

        //Só uma veitificação pra evitar dados nulos
        if (response == null || !"Ok".equals(response.code())){
            throw new IllegalArgumentException("Não foi possível calcular a rota");
        }

        return response;
    }

    //Recebe a entidade para ser separada dela a coodenada e retornar pra função principal
    public String montarCoordenadas(List<Visita> visitas){

        //cria um array vazio pra colocar as coordenas
        List<String> coordenadas = new ArrayList<>();

        //seta a corrdenada[0] como a da igreja
        coordenadas.add(LONGITUDE_IGREJA + "," + LATITUDE_IGREJA);

        //para cada objeto de visita, pegue a cordenada x e y e adicione dentro do array coordenadas
        for (Visita visita: visitas){
            Point ponto = visita.getCoordenada();

            coordenadas.add(ponto.getX() + "," + ponto.getY());
        }

        //retorna um join das coordenasa, algo como: -47.330434,-15.549093; -47.332848,-15.568466;. Que é o retorno lá encima
        return String.join(";", coordenadas);

    }
}
