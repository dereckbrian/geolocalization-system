package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.JovemResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.JovemMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.JovemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JovemServiceTest {

    @Mock
    private JovemRepository jovemRepository;

    @Mock
    private JovemMapper jovemMapper;

    @InjectMocks
    private JovemService jovemService;

    @Test
    @DisplayName("Should return jovens from tio")
    void retornarJovensComSucesso() {
        UUID tioId = UUID.randomUUID();

        Jovem jovem1 = mock(Jovem.class);
        Jovem jovem2 = mock(Jovem.class);

        JovemResponseDTO dto1 = mock(JovemResponseDTO.class);
        JovemResponseDTO dto2 = mock(JovemResponseDTO.class);

        when(jovemRepository.findByTioId(tioId)).thenReturn(List.of(jovem1, jovem2));
        when(jovemMapper.toResponseDTO(jovem1)).thenReturn(dto1);
        when(jovemMapper.toResponseDTO(jovem2)).thenReturn(dto2);

        List<JovemResponseDTO> resultado = jovemService.retornarJovem(tioId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(dto1, resultado.get(0));
        assertEquals(dto2, resultado.get(1));

        verify(jovemRepository).findByTioId(tioId);
        verify(jovemMapper).toResponseDTO(jovem1);
        verify(jovemMapper).toResponseDTO(jovem2);
    }

    @Test
    @DisplayName("Should return empty list when tio has no jovens")
    void retornarListaVaziaQuandoTioNaoPossuiJovens() {
        UUID tioId = UUID.randomUUID();

        when(jovemRepository.findByTioId(tioId)).thenReturn(List.of());

        List<JovemResponseDTO> resultado = jovemService.retornarJovem(tioId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(jovemRepository).findByTioId(tioId);
        verifyNoInteractions(jovemMapper);
    }
}