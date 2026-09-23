package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.jovem;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;

import java.util.UUID;

public record CadastroJovemDTO(String nome,
                               UUID tioTd) {
}
