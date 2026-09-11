package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.visita;

import java.util.UUID;

public record RotaVisitaResponseDTO(
        UUID visitaId,
        String jovem,
        Integer ordem,
        Double latitude,
        Double longitude
) {
}
