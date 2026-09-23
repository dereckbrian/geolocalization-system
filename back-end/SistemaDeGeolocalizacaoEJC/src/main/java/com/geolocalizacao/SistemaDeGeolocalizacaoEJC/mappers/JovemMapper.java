package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.CadastroJovemDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.JovemResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JovemMapper {

    JovemResponseDTO toResponseDTO(Jovem jovem);

    @Mapping(target = "tio", ignore = true)
    Jovem toEntity(CadastroJovemDTO cadastroJovemDTO);

}
