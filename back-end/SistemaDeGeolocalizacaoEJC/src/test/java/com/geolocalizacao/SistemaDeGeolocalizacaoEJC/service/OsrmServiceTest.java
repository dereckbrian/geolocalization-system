package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsrmServiceTest {

    private OsrmService osrmService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @BeforeEach
    void setUp() {
        osrmService = new OsrmService("http://localhost:5000");
    }

    @Test
    @DisplayName("Should mount coordinates starting from church")
    void montarCoordenadasComSucesso() {
        Point ponto1 = geometryFactory.createPoint(new Coordinate(-47.3100, -15.5100));

        Point ponto2 = geometryFactory.createPoint(new Coordinate(-47.3200, -15.5200));

        Visita visita1 = Visita.builder()
                .coordenada(ponto1)
                .build();

        Visita visita2 = Visita.builder()
                .coordenada(ponto2)
                .build();

        List<Visita> visitas = List.of(visita1, visita2);

        String resultado = osrmService.montarCoordenadas(visitas);

        String esperado = "-47.330434,-15.549093;" + "-47.31,-15.51;" + "-47.32,-15.52";

        assertEquals(esperado, resultado);
    }

    @Test
    @DisplayName("Should return only church coordinates when visits list is empty")
    void montarCoordenadasSemVisitas() {
        List<Visita> visitas = List.of();

        String resultado = osrmService.montarCoordenadas(visitas);

        assertEquals("-47.330434,-15.549093", resultado);
    }
}