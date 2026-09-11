package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr.OsmrWaypoint;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr.OsrmTripResponse;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.RotaVisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.enums.StatusVisita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.VisitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class RotaService {
    private final VisitaRepository visitaRepository;
    private final OsrmService osrmService;

    @Transactional
    public List<RotaVisitaResponseDTO> gerarRota() {

        List<Visita> rotaAtual = visitaRepository.findByOrdemRotaIsNotNullOrderByOrdemRotaAsc();

        if (!rotaAtual.isEmpty()) {
            return converterRotaParaDTO(rotaAtual);
        }

        List<Visita> visitasPendentes = visitaRepository.findByStatusVisita(StatusVisita.PENDENTE);

        if (visitasPendentes.isEmpty()) {
            return List.of();
        }

        OsrmTripResponse response = osrmService.calcularRota(visitasPendentes);

        List<Visita> visitasOrdenadas = ordenarVisita(visitasPendentes, response);

        List<Visita> visitasComOrdem = salvarOrdem(visitasOrdenadas);

        return converterRotaParaDTO(visitasComOrdem);
    }

    @Transactional(readOnly = true)
    public List<RotaVisitaResponseDTO> buscarRotaAtual() {

        List<Visita> rotaAtual = visitaRepository.findByOrdemRotaIsNotNullOrderByOrdemRotaAsc();

        return converterRotaParaDTO(rotaAtual);
    }

    private List<Visita> salvarOrdem(List<Visita> visitasOrdenadas) {

        List<Visita> visitasComOrdem = new ArrayList<>();

        for (int i = 0; i < visitasOrdenadas.size(); i++) {

            Visita visita = visitasOrdenadas.get(i);

            Visita visitaComOrdem = visita.toBuilder()
                    .ordemRota(i + 1)
                    .build();

            visitasComOrdem.add(visitaComOrdem);
        }

        visitaRepository.saveAll(visitasComOrdem);

        return visitasComOrdem;
    }

    private List<RotaVisitaResponseDTO> converterRotaParaDTO(List<Visita> visitas) {
        return visitas
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                Visita::getOrdemRota
                        )
                )
                .map(visita ->
                        new RotaVisitaResponseDTO(
                                visita.getId(),
                                visita.getJovem().getNome(),
                                visita.getOrdemRota(),
                                visita.getCoordenada().getY(),
                                visita.getCoordenada().getX()
                        )
                )
                .toList();
    }

    private List<Visita> ordenarVisita(List<Visita> visitasPendentes, OsrmTripResponse response) {
        Map<UUID, Integer> ordemPorVisita = new HashMap<>();

        for (int i = 0; i < visitasPendentes.size(); i++) {
            Visita visita = visitasPendentes.get(i);

            OsmrWaypoint waypoint = response.waypoints().get(i + 1);

            ordemPorVisita.put(visita.getId(), waypoint.waypointIndex());
        }

        return visitasPendentes
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                visita ->
                                        ordemPorVisita.get(visita.getId())
                        )
                )
                .toList();
    }
}