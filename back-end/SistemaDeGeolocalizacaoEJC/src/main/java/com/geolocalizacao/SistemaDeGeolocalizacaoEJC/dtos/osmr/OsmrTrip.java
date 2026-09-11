package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OsmrTrip(
        Double duration,
        Double distance
) {
}
