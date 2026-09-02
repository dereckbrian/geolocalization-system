package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.CadastroVisitaDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita.VisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitaMapper {

    @Mapping(target = "jovem", ignore = true)
    @Mapping(target = "tio", ignore = true)
    @Mapping(target = "coordenada", ignore = true)
    @Mapping(target = "statusVisita", ignore = true)
    Visita toEntity(CadastroVisitaDTO dto);

    VisitaResponseDTO toEntityToDTO(Visita visita);
}
