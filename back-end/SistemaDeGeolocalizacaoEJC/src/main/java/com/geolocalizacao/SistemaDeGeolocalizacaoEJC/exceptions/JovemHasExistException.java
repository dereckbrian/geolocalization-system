package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions;

public class JovemHasExistException extends RuntimeException{

    public JovemHasExistException(){super("Jovem já cadastrado");}

    public JovemHasExistException(String mesagen){super(mesagen);}
}
