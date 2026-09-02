package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.repository;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.entity.Tio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TioRespository extends JpaRepository<Tio, UUID> {
}
