package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions;

public class VisitaNotFoundException extends RuntimeException{
    public VisitaNotFoundException(){super("Visita não encontrada");}

    public VisitaNotFoundException(String mesage){super(mesage);}
}
