package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@Getter
@Entity
@Table(name = "tios")
public class Tio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_tios", length = 100, unique = true, nullable = false)
    private String nomeTios;
}
