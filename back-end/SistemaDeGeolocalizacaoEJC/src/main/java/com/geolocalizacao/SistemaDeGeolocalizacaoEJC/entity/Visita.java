package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.enums.StatusVisita;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder(toBuilder = true)
@Entity
@Table(name = "localizacao")
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "encontrista_id", referencedColumnName = "id", nullable = false, unique = true)
    private Jovem jovem;

    @Column(name = "url_foto", nullable = false)
    private String urlFoto;

    @ManyToOne
    @JoinColumn(name = "tio_id", referencedColumnName = "id", nullable = false)
    private Tio tio;

    @Column(name = "status_visita")
    @Enumerated(EnumType.STRING)
    private StatusVisita statusVisita;

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point coordenada;

    @Column(nullable = false)
    @Builder.Default
    private Integer tentativas = 0;


}
