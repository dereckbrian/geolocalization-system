package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CadastroVisitaDTO(
        @NotNull
        UUID jovemID,

        @NotNull
        UUID tioID,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude
) {
}
