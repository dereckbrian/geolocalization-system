package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CadastroVisitaDTO(
        @NotNull
        UUID jovenID,

        @NotNull
        UUID tioID,

        @NotBlank
        String urlFoto,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude
) {
}
