package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.service;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.CadastroJovemDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem.JovemResponseDTO;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.mappers.JovemMapper;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.JovemRepository;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository.TioRespository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JovemService {

    private final JovemRepository jovemRepository;
    private final JovemMapper jovemMapper;
    private final TioRespository tioRespository;

    public List<JovemResponseDTO> retornarJovem(UUID id){

        return jovemRepository.findByTioId(id)
                .stream()
                .map(jovemMapper::toResponseDTO)
                .toList();
    }

    public JovemResponseDTO cadastroJovem(CadastroJovemDTO body){

        Tio tio = tioRespository.getReferenceById(body.tioTd());

        Jovem jovemSalvar = Jovem.builder()
                .nome(body.nome())
                .tio(tio)
                .build();

        Jovem jovemSalvo = jovemRepository.save(jovemSalvar);

        return jovemMapper.toResponseDTO(jovemSalvo);

    }


}
