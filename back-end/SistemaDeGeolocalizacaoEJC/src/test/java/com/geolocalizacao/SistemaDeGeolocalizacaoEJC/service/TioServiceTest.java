package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.CadastroTioDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.TioResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.TioHasExistException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.TioMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.TioRespository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TioServiceTest {

    @Mock
    private TioRespository tioRespository;

    @Mock
    private TioMapper tioMapper;

    @InjectMocks
    private TioService tioService;

    @Test
    @DisplayName("Should return all tios")
    void acharTiosComSucesso() {
        Tio tio1 = mock(Tio.class);
        Tio tio2 = mock(Tio.class);

        TioResponseDTO dto1 = mock(TioResponseDTO.class);
        TioResponseDTO dto2 = mock(TioResponseDTO.class);

        when(tioRespository.findAll()).thenReturn(List.of(tio1, tio2));
        when(tioMapper.toResponseDTO(tio1)).thenReturn(dto1);
        when(tioMapper.toResponseDTO(tio2)).thenReturn(dto2);

        List<TioResponseDTO> resultado = tioService.acharTio();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(dto1, resultado.get(0));
        assertEquals(dto2, resultado.get(1));

        verify(tioRespository).findAll();

        verify(tioMapper).toResponseDTO(tio1);
        verify(tioMapper).toResponseDTO(tio2);
    }

    @Test
    @DisplayName("Should return empty list when there are no tios")
    void acharTiosQuandoListaEstaVazia() {
        when(tioRespository.findAll()).thenReturn(List.of());

        List<TioResponseDTO> resultado = tioService.acharTio();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(tioRespository).findAll();
        verifyNoInteractions(tioMapper);
    }

    @Test
    @DisplayName("Should register sucess")
    void cadastrarTioSucesso(){
        CadastroTioDTO tioParaCadastrar = new CadastroTioDTO("Tio joao e alice", "123456", "12345678");
        Tio tioParaSalvar = new Tio();
        Tio tioSalvo = new Tio();
        TioResponseDTO tioResponse = new TioResponseDTO("Tio joao e alice");

        when(tioMapper.dtoToEntity(tioParaCadastrar)).thenReturn(tioParaSalvar);
        when(tioRespository.save(tioParaSalvar)).thenReturn(tioSalvo);
        when(tioRespository.existsByCpf("123456")).thenReturn(false);
        when(tioMapper.toResponseDTO(tioSalvo)).thenReturn(tioResponse);

        TioResponseDTO response = tioService.cadastrarTio(tioParaCadastrar);

        assertNotNull(response);
        assertEquals(tioResponse, response);

        verify(tioMapper).dtoToEntity(tioParaCadastrar);
        verify(tioRespository).save(tioParaSalvar);
        verify(tioMapper).toResponseDTO(tioSalvo);
    }

    @Test
    @DisplayName("Should register sucess")
    void cadastrarTioFaild(){
        CadastroTioDTO tioParaCadastrar = new CadastroTioDTO("Tio joao e alice", "123456", "12345678");

        when(tioRespository.existsByCpf("123456")).thenReturn(true);

        assertThrows(TioHasExistException.class, ()->tioService.cadastrarTio(tioParaCadastrar));

        verify(tioRespository).existsByCpf("123456");
        verify(tioRespository, never()).save(any());
    }
}