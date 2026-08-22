package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.CadastroVisitaDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.VisitaResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.JovemRepository;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.TioRespository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class VisitaMapper {

    private final JovemRepository jovemRepository;

    private final TioRespository tioRepository;

    public VisitaMapper (JovemRepository jovemRepository, TioRespository tioRespository){
        this.jovemRepository = jovemRepository;
        this.tioRepository = tioRespository;
    }

    @Mapping(target = "coordenada", ignore = true)
    @Mapping(target = "statusVisita", ignore = true)
    @Mapping(target = "jovem", source = "jovemId", qualifiedByName = "idToJovem")
    @Mapping(target = "tio", source = "tioId", qualifiedByName = "idToTio")
    public abstract Visita toEntity(CadastroVisitaDTO dto);

    public abstract VisitaResponseDTO toEntityToDTO(Visita visita);


    @Named("idToJovem")
    private Jovem mapJovem(UUID id) {
        if (id == null) return null;
        return jovemRepository.getReferenceById(id);
    }

    @Named("idToTios")
    private Tio mapTio(UUID id) {
        if (id == null) return null;
        return tioRepository.getReferenceById(id);
    }
}
