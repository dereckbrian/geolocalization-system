package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder(toBuilder = true)
@Entity
@Table(name = "encontrista")
public class Jovem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20, unique = true, nullable = false)
    private String nome;

}
