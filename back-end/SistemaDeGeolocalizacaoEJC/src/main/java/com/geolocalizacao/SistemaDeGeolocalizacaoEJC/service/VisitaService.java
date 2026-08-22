package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.CadastroVisitaDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.VisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.enums.StatusVisita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.JovemHasExistException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.VisitaNotFoundException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.VisitaMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.VisitaRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final VisitaMapper visitaMapper;

    private static final Double LATITUDE_IGREJA = -15.549093;
    private static final Double LONGITUDE_IGREJA = -47.330434;

    public VisitaService(VisitaRepository visitaRepository, VisitaMapper visitaMapper){
        this.visitaRepository = visitaRepository;
        this.visitaMapper = visitaMapper;
    }

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public VisitaResponseDTO cadastrarVisita(CadastroVisitaDTO dto){

        if (visitaRepository.existsByJovemId(dto.jovenID())) {
            throw new JovemHasExistException();
        }

        Coordinate coordinate = new Coordinate(dto.longitude(), dto.latitude());
        Point casaJovem = geometryFactory.createPoint(coordinate);

        Visita visitaIncompleta = visitaMapper.toEntity(dto);

        Visita visitaProntaParaSalvar = visitaIncompleta.toBuilder()
                .coordenada(casaJovem)
                .statusVisita(StatusVisita.PENDENTE)
                .build();

        visitaRepository.save(visitaProntaParaSalvar);

        return visitaMapper.toEntityToDTO(visitaProntaParaSalvar);
    }

    public List<Visita> gerarRotaDoDia(int quantidadeDeCasas) {

        Coordinate coordIgreja = new Coordinate(LONGITUDE_IGREJA, LATITUDE_IGREJA);
        Point pontoIgreja = geometryFactory.createPoint(coordIgreja);

        PageRequest limitador = PageRequest.of(0, quantidadeDeCasas);

        return visitaRepository.buscarPendentesMaisProximas(pontoIgreja, limitador);
    }

    @Transactional
    public VisitaResponseDTO registrarSucesso(UUID visitaId) {
        Visita visita = visitaRepository.findById(visitaId)
                .orElseThrow(VisitaNotFoundException::new);

        Visita visitaAtualizada = visita.toBuilder()
                .statusVisita(StatusVisita.VISITADO)
                .build();

        return visitaMapper.toEntityToDTO(visitaRepository.save(visitaAtualizada));
    }

    @Transactional
    public VisitaResponseDTO registrarFalta(UUID visitaId) {
        Visita visita = visitaRepository.findById(visitaId)
                .orElseThrow(VisitaNotFoundException::new);

        int novasTentativas = visita.getTentativas() + 1;

        StatusVisita novoStatus = (novasTentativas >= 3) ? StatusVisita.FALTA : StatusVisita.PENDENTE;

        Visita visitaAtualizada = visita.toBuilder()
                .tentativas(novasTentativas)
                .statusVisita(novoStatus)
                .build();

        return visitaMapper.toEntityToDTO(visitaRepository.save(visitaAtualizada));
    }
}
