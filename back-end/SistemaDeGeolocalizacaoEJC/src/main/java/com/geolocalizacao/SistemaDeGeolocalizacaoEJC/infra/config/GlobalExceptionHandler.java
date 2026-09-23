package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.infra.config;

import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.JovemHasExistException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.TioHasExistException;
import com.geolocalizacao.SistemaDeGeolocalizacaoEJC.exceptions.VisitaNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(JovemHasExistException.class)
    public ResponseEntity<ProblemDetail> handleJovemHasExistException(JovemHasExistException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Jovem já existe"
        );
        problemDetail.setTitle("Jovem Existe");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(VisitaNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleVisitaNotFoundException(VisitaNotFoundException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                "Visita não encontrada"
        );
        problemDetail.setTitle("Visita não encontrada");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(TioHasExistException.class)
    public ResponseEntity<ProblemDetail> handleTioHasExistException(TioHasExistException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Tio já existe"
        );
        problemDetail.setTitle("Tio Existe");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }
}
