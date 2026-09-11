package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Visita;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.enums.StatusVisita;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitaRepository extends JpaRepository<Visita, UUID> {

    boolean existsByJovemId(UUID encontristaId);

    List<Visita> findByStatusVisita(StatusVisita statusVisita);

    List<Visita> findByOrdemRotaIsNotNullOrderByOrdemRotaAsc();
}
