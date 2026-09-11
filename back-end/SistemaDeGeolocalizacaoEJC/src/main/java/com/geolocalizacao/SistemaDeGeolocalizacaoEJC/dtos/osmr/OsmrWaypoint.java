package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.osmr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OsmrWaypoint(

        @JsonProperty("waypoint_index")
        Integer waypointIndex,

        Double distance,
        String nome,
        List<Double> location
) {
}
