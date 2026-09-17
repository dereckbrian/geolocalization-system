package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr.OsmrWaypoint;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr.OsrmTripResponse;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.RotaVisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.enums.StatusVisita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.VisitaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RotaServiceTest {

    @Mock
    private VisitaRepository visitaRepository;

    @Mock
    private OsrmService osrmService;

    @InjectMocks
    private RotaService rotaService;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    @DisplayName("Should return current route when route already exists")
    void gerarRotaQuandoJaExisteRota() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Visita visita1 = criarVisita(id1, "João", 1, -15.50, -47.30);
        Visita visita2 = criarVisita(id2, "Maria", 2, -15.60, -47.40);

        List<Visita> rotaAtual = List.of(visita1, visita2);

        when(visitaRepository.findByOrdemRotaIsNotNullOrderByOrdemRotaAsc()).thenReturn(rotaAtual);

        List<RotaVisitaResponseDTO> resultado = rotaService.gerarRota();

        assertEquals(2, resultado.size());
        assertEquals(new RotaVisitaResponseDTO(id1, "João", 1, -15.50, -47.30), resultado.get(0));
        assertEquals(new RotaVisitaResponseDTO(id2, "Maria", 2, -15.60, -47.40), resultado.get(1));

        verify(visitaRepository).findByOrdemRotaIsNotNullOrderByOrdemRotaAsc();
        verify(visitaRepository, never()).findByStatusVisita(any());
        verifyNoInteractions(osrmService);
        verify(visitaRepository, never()).saveAll(any());
    }


    @Test
    @DisplayName("Should return empty list when there are no pending visits")
    void gerarRotaQuandoNaoExistemVisitasPendentes() {

        when(visitaRepository.findByOrdemRotaIsNotNullOrderByOrdemRotaAsc()).thenReturn(List.of());
        when(visitaRepository.findByStatusVisita(StatusVisita.PENDENTE)).thenReturn(List.of());

        List<RotaVisitaResponseDTO> resultado = rotaService.gerarRota();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(visitaRepository).findByOrdemRotaIsNotNullOrderByOrdemRotaAsc();
        verify(visitaRepository).findByStatusVisita(StatusVisita.PENDENTE);
        verifyNoInteractions(osrmService);
        verify(visitaRepository, never()).saveAll(any());
    }


    @Test
    @DisplayName("Should generate, order and save a new route")
    void gerarNovaRotaComSucesso() {
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();
        UUID idC = UUID.randomUUID();

        Visita visitaA = criarVisita(idA, "João", null, -15.51, -47.31);
        Visita visitaB = criarVisita(idB, "Maria", null, -15.52, -47.32);
        Visita visitaC = criarVisita(idC, "Carlos", null, -15.53, -47.33);

        List<Visita> pendentes = List.of(visitaA, visitaB, visitaC);

        when(visitaRepository.findByOrdemRotaIsNotNullOrderByOrdemRotaAsc()).thenReturn(List.of());
        when(visitaRepository.findByStatusVisita(StatusVisita.PENDENTE)).thenReturn(pendentes);

        OsrmTripResponse osrmResponse = mock(OsrmTripResponse.class);
        OsmrWaypoint deposito = mock(OsmrWaypoint.class);
        OsmrWaypoint waypointA = mock(OsmrWaypoint.class);
        OsmrWaypoint waypointB = mock(OsmrWaypoint.class);
        OsmrWaypoint waypointC = mock(OsmrWaypoint.class);

        when(waypointA.waypointIndex()).thenReturn(2);
        when(waypointB.waypointIndex()).thenReturn(0);
        when(waypointC.waypointIndex()).thenReturn(1);
        when(osrmResponse.waypoints()).thenReturn(List.of(deposito, waypointA, waypointB, waypointC));

        when(osrmService.calcularRota(pendentes)).thenReturn(osrmResponse);

        List<RotaVisitaResponseDTO> resultado = rotaService.gerarRota();

        assertEquals(3, resultado.size());

        assertEquals(new RotaVisitaResponseDTO(idB, "Maria", 1, -15.52, -47.32), resultado.get(0));
        assertEquals(new RotaVisitaResponseDTO(idC, "Carlos", 2, -15.53, -47.33), resultado.get(1));
        assertEquals(new RotaVisitaResponseDTO(idA, "João", 3, -15.51, -47.31), resultado.get(2));

        verify(osrmService).calcularRota(pendentes);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Visita>> captor = ArgumentCaptor.forClass(List.class);

        verify(visitaRepository).saveAll(captor.capture());

        List<Visita> visitasSalvas = captor.getValue();

        assertEquals(3, visitasSalvas.size());

        assertEquals(idB, visitasSalvas.get(0).getId());
        assertEquals(1, visitasSalvas.get(0).getOrdemRota());

        assertEquals(idC, visitasSalvas.get(1).getId());
        assertEquals(2, visitasSalvas.get(1).getOrdemRota());

        assertEquals(idA, visitasSalvas.get(2).getId());
        assertEquals(3, visitasSalvas.get(2).getOrdemRota());
    }


    @Test
    @DisplayName("Should return current route")
    void buscarRotaAtual() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Visita visita1 = criarVisita(id1, "João", 1, -15.51, -47.31);
        Visita visita2 = criarVisita(id2, "Maria", 2, -15.52, -47.32);

        when(visitaRepository.findByOrdemRotaIsNotNullOrderByOrdemRotaAsc()).thenReturn(List.of(visita1, visita2));

        List<RotaVisitaResponseDTO> resultado = rotaService.buscarRotaAtual();

        assertEquals(2, resultado.size());
        assertEquals(new RotaVisitaResponseDTO(id1, "João", 1, -15.51, -47.31), resultado.get(0));
        assertEquals(new RotaVisitaResponseDTO(id2, "Maria", 2, -15.52, -47.32), resultado.get(1));

        verify(visitaRepository).findByOrdemRotaIsNotNullOrderByOrdemRotaAsc();
        verifyNoInteractions(osrmService);
    }


    private Visita criarVisita(UUID id, String nomeJovem, Integer ordem, double latitude, double longitude) {
        Jovem jovem = mock(Jovem.class);

        when(jovem.getNome()).thenReturn(nomeJovem);

        Point coordenada = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        return Visita.builder()
                .id(id)
                .jovem(jovem)
                .statusVisita(StatusVisita.PENDENTE)
                .ordemRota(ordem)
                .coordenada(coordenada)
                .build();
    }
}