package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions;

public class TioHasExistException extends RuntimeException{

    public TioHasExistException(){super("Tio já existe");}

    public TioHasExistException(String mensagen){super(mensagen);}

}
