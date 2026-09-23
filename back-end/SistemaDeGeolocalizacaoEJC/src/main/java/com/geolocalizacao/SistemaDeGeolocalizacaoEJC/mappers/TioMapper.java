package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.CadastroTioDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio.TioResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TioMapper {

    TioResponseDTO toResponseDTO(Tio tio);

    Tio dtoToEntity(CadastroTioDTO cadastroTioDTO);
}
