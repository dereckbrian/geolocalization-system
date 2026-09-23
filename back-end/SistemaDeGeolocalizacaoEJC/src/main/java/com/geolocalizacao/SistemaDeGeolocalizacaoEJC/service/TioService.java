package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.CadastroTioDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.TioResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.TioHasExistException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.TioMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.TioRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TioService {

    private final TioRespository tioRespository;
    private final TioMapper tioMapper;

    public List<TioResponseDTO> acharTio(){
        return tioRespository.findAll()
                .stream()
                .map(tioMapper::toResponseDTO)
                .toList();
    }

    public TioResponseDTO cadastrarTio(CadastroTioDTO body){

        if (tioRespository.existsByCpf(body.cpf())){
            throw new TioHasExistException();
        }

        Tio tioParaSalvar =  tioMapper.dtoToEntity(body);
        Tio tioSalvo = tioRespository.save(tioParaSalvar);

        return tioMapper.toResponseDTO(tioSalvo);
    }


}
