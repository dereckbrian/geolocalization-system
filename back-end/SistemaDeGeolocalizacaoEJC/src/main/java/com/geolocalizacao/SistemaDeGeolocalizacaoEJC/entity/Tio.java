package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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

    @Column(name = "nome_tios", length = 100, nullable = false)
    private String nomeTios;

    @OneToMany(mappedBy = "tio")
    private List<Jovem> jovem;
}
