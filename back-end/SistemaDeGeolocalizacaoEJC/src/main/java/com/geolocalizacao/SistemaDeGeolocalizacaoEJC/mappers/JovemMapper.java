package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.JovemResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JovemMapper {

    JovemResponseDTO toResponseDTO(Jovem jovem);

}
