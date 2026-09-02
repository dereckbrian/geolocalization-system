package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.CadastroVisitaDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.VisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.enums.StatusVisita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.JovemHasExistException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.VisitaNotFoundException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.VisitaMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.JovemRepository;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.TioRespository;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.VisitaRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final VisitaMapper visitaMapper;
    private final JovemRepository jovemRepository;
    private final TioRespository tioRespository;

    private static final Double LATITUDE_IGREJA = -15.549093;
    private static final Double LONGITUDE_IGREJA = -47.330434;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public VisitaResponseDTO cadastrarVisita(CadastroVisitaDTO dto) {

        if (visitaRepository.existsByJovemId(dto.jovemID())) {
            throw new JovemHasExistException();
        }

        Jovem jovem = jovemRepository.getReferenceById(dto.jovemID());
        Tio tio = tioRespository.getReferenceById(dto.tioID());

        Coordinate coordinate =
                new Coordinate(dto.longitude(), dto.latitude());

        Point casaJovem =
                geometryFactory.createPoint(coordinate);

        Visita visita = visitaMapper.toEntity(dto)
                .toBuilder()
                .jovem(jovem)
                .tio(tio)
                .coordenada(casaJovem)
                .statusVisita(StatusVisita.PENDENTE)
                .build();

        Visita visitaSalva = visitaRepository.save(visita);

        return visitaMapper.toEntityToDTO(visitaSalva);
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
