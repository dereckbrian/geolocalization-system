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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitaServiceTest {

    @Mock
    private VisitaRepository visitaRepository;

    @Mock
    private VisitaMapper visitaMapper;

    @Mock
    private JovemRepository jovemRepository;

    @Mock
    private TioRespository tioRespository;

    @InjectMocks
    private VisitaService visitaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should register successfully when everything is ok")
    void cadastrarVisitaComSucesso() {
        UUID jovemId = UUID.randomUUID();
        UUID tioId = UUID.randomUUID();

        CadastroVisitaDTO dto = mock(CadastroVisitaDTO.class);

        Jovem jovem = mock(Jovem.class);
        Tio tio = mock(Tio.class);

        Visita visitaMapeada = Visita.builder()
                .build();

        VisitaResponseDTO responseEsperado = mock(VisitaResponseDTO.class);

        when(dto.jovemID()).thenReturn(jovemId);
        when(dto.tioID()).thenReturn(tioId);
        when(dto.latitude()).thenReturn(-15.5377);
        when(dto.longitude()).thenReturn(-47.3344);

        when(visitaRepository.existsByJovemId(jovemId)).thenReturn(false);
        when(jovemRepository.getReferenceById(jovemId)).thenReturn(jovem);
        when(tioRespository.getReferenceById(tioId)).thenReturn(tio);
        when(visitaMapper.toEntity(dto)).thenReturn(visitaMapeada);
        when(visitaRepository.save(any(Visita.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(visitaMapper.toEntityToDTO(any(Visita.class))).thenReturn(responseEsperado);

        VisitaResponseDTO response = visitaService.cadastrarVisita(dto);

        assertNotNull(response);
        assertEquals(responseEsperado, response);

        ArgumentCaptor<Visita> visitaCaptor = ArgumentCaptor.forClass(Visita.class);

        verify(visitaRepository).save(visitaCaptor.capture());

        Visita visitaSalva = visitaCaptor.getValue();

        assertEquals(jovem, visitaSalva.getJovem());
        assertEquals(tio, visitaSalva.getTio());
        assertEquals(StatusVisita.PENDENTE, visitaSalva.getStatusVisita());

        assertNotNull(visitaSalva.getCoordenada());

        assertEquals(-47.3344, visitaSalva.getCoordenada().getX());
        assertEquals(-15.5377, visitaSalva.getCoordenada().getY());

        assertEquals(4326, visitaSalva.getCoordenada().getSRID());

        verify(visitaRepository).existsByJovemId(jovemId);
        verify(jovemRepository).getReferenceById(jovemId);
        verify(tioRespository).getReferenceById(tioId);
        verify(visitaMapper).toEntity(dto);
    }

    @Test
    @DisplayName("Should throw exception when jovem already has visita")
    void cadastrarVisitaQuandoJovemJaPossuiVisita() {

        UUID jovemId = UUID.randomUUID();

        CadastroVisitaDTO dto = mock(CadastroVisitaDTO.class);

        when(dto.jovemID()).thenReturn(jovemId);

        when(visitaRepository.existsByJovemId(jovemId)).thenReturn(true);

        assertThrows(JovemHasExistException.class, () -> visitaService.cadastrarVisita(dto));

        verify(visitaRepository).existsByJovemId(jovemId);

        verify(jovemRepository, never()).getReferenceById(any());

        verify(tioRespository, never()).getReferenceById(any());

        verify(visitaRepository, never()).save(any());

        verify(visitaMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("Should register visit as successfully visited")
    void registrarSucesso() {

        UUID visitaId = UUID.randomUUID();

        Visita visita = Visita.builder()
                .statusVisita(StatusVisita.PENDENTE)
                .ordemRota(1)
                .build();

        VisitaResponseDTO responseEsperado = mock(VisitaResponseDTO.class);

        when(visitaRepository.findById(visitaId)).thenReturn(Optional.of(visita));

        when(visitaRepository.save(any(Visita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(visitaMapper.toEntityToDTO(any(Visita.class))).thenReturn(responseEsperado);

        VisitaResponseDTO response = visitaService.registrarSucesso(visitaId);

        assertEquals(responseEsperado, response);

        ArgumentCaptor<Visita> captor = ArgumentCaptor.forClass(Visita.class);

        verify(visitaRepository).save(captor.capture());

        Visita visitaAtualizada = captor.getValue();

        assertEquals(StatusVisita.VISITADO, visitaAtualizada.getStatusVisita());

        assertNull(visitaAtualizada.getOrdemRota());

        verify(visitaRepository).findById(visitaId);
        verify(visitaMapper).toEntityToDTO(visitaAtualizada);
    }

    @Test
    @DisplayName("Should throw exception when visita is not found on success")
    void registrarSucessoVisitaNaoEncontrada() {

        UUID visitaId = UUID.randomUUID();

        when(visitaRepository.findById(visitaId)).thenReturn(Optional.empty());

        assertThrows(VisitaNotFoundException.class, () -> visitaService.registrarSucesso(visitaId));

        verify(visitaRepository).findById(visitaId);

        verify(visitaRepository, never()).save(any());

        verify(visitaMapper, never()).toEntityToDTO(any());
    }

    @Test
    @DisplayName("Should keep visita pending when attempts are less than three")
    void registrarFaltaMenorQueTresTentativas() {

        UUID visitaId = UUID.randomUUID();

        Visita visita = Visita.builder()
                .tentativas(1)
                .statusVisita(StatusVisita.PENDENTE)
                .ordemRota(2)
                .build();

        VisitaResponseDTO responseEsperado = mock(VisitaResponseDTO.class);

        when(visitaRepository.findById(visitaId)).thenReturn(Optional.of(visita));

        when(visitaRepository.save(any(Visita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(visitaMapper.toEntityToDTO(any(Visita.class))).thenReturn(responseEsperado);

        VisitaResponseDTO response = visitaService.registrarFalta(visitaId);

        assertEquals(responseEsperado, response);

        ArgumentCaptor<Visita> captor = ArgumentCaptor.forClass(Visita.class);

        verify(visitaRepository).save(captor.capture());

        Visita visitaAtualizada = captor.getValue();

        assertEquals(2, visitaAtualizada.getTentativas());

        assertEquals(StatusVisita.PENDENTE, visitaAtualizada.getStatusVisita());

        assertNull(visitaAtualizada.getOrdemRota());
    }

    @Test
    @DisplayName("Should mark visita as FALTA on third failed attempt")
    void registrarTerceiraFalta() {

        UUID visitaId = UUID.randomUUID();

        Visita visita = Visita.builder()
                .tentativas(2)
                .statusVisita(StatusVisita.PENDENTE)
                .ordemRota(3)
                .build();

        VisitaResponseDTO responseEsperado = mock(VisitaResponseDTO.class);

        when(visitaRepository.findById(visitaId)).thenReturn(Optional.of(visita));

        when(visitaRepository.save(any(Visita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(visitaMapper.toEntityToDTO(any(Visita.class))).thenReturn(responseEsperado);

        VisitaResponseDTO response = visitaService.registrarFalta(visitaId);

        assertEquals(responseEsperado, response);

        ArgumentCaptor<Visita> captor = ArgumentCaptor.forClass(Visita.class);

        verify(visitaRepository).save(captor.capture());

        Visita visitaAtualizada = captor.getValue();

        assertEquals(3, visitaAtualizada.getTentativas());

        assertEquals(StatusVisita.FALTA, visitaAtualizada.getStatusVisita());

        assertNull(visitaAtualizada.getOrdemRota());
    }

    @Test
    @DisplayName("Should throw exception when visita is not found on failed attempt")
    void registrarFaltaVisitaNaoEncontrada() {

        UUID visitaId = UUID.randomUUID();

        when(visitaRepository.findById(visitaId)).thenReturn(Optional.empty());

        assertThrows(VisitaNotFoundException.class, () -> visitaService.registrarFalta(visitaId));

        verify(visitaRepository).findById(visitaId);

        verify(visitaRepository, never()).save(any());

        verify(visitaMapper, never()).toEntityToDTO(any());
    }
}