package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Jovem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JovemRepository extends JpaRepository<Jovem, UUID> {

    List<Jovem> findByTioId(UUID tioId);
}
