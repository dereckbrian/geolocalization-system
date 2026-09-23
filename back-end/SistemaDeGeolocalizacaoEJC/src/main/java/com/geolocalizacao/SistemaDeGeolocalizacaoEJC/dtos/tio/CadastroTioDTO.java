package com.geolocalizacao.SistemaDeGeolocalizacaoEJC.dtos.tio;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record CadastroTioDTO(
                             @NotBlank
                             String nomeTios,
                             @CPF
                             @NotBlank
                             String cpf,
                             @NotBlank
                             String senha) {
}
