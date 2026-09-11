package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OsrmTripResponse(
        String code,
        List<OsmrWaypoint> waypoints,
        List<OsmrTrip> trips
) {
}
