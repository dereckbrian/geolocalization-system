# GeoField Tracker

Sistema de gestão e geolocalização de visitas de campo, desenvolvido com
Spring Boot, Angular e PostgreSQL/PostGIS.

A aplicação permite que equipes registrem coordenadas geográficas diretamente
em campo por meio de uma PWA mobile-first. Os pontos são armazenados como
geometrias espaciais no PostGIS e podem posteriormente ser utilizados em
consultas de proximidade, análise territorial e otimização de rotas.

O objetivo é reduzir a dependência de endereços imprecisos e processos
manuais de georreferenciamento, centralizando a coleta e o processamento
de dados espaciais em uma única aplicação.

## Funcionalidades

* **Registro de Localização:** captura da posição geográfica do dispositivo
  utilizando a Geolocation API do navegador.

* **Vinculação de Equipes e Visitados:** seleção de responsáveis e pessoas
  previamente cadastradas antes do registro da localização.

* **Armazenamento Geoespacial:** conversão de latitude e longitude para
  geometria `Point` com SRID 4326 e persistência utilizando PostgreSQL/PostGIS.

* **Consultas por Proximidade:** estrutura preparada para localizar pontos
  próximos a uma determinada origem utilizando operações espaciais.

* **Planejamento de Visitas:** desenvolvimento de estratégias para ordenar
  e agrupar pontos de visita visando reduzir deslocamentos desnecessários.

* **Integração com QGIS:** possibilidade de utilizar os dados geográficos
  armazenados no PostGIS para visualização e análise espacial no QGIS.

* **Testes Automatizados:** testes unitários das principais regras de negócio
  utilizando JUnit, Mockito e MockMvc.

## Tecnologias Utilizadas

O projeto foi construído utilizando o padrão de arquitetura de API RESTful com as seguintes tecnologias para garantir a eficiência na coleta e processamento de dados espaciais:

### Front-end

* Angular
* TypeScript
* Tailwind CSS
* Progressive Web App (PWA)
* Geolocation API

### Back-end

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate Spatial
* MapStruct
* JUnit
* Mockito
* MockMvc

### Banco de Dados e Ferramentas

* PostgreSQL
* PostGIS
* QGIS
* Postman
* Git / GitHub
* **Postman** (Testes e documentação de endpoints)
* **Git & GitHub** (Versionamento)

## Roadmap

- [x] Cadastro e consulta de responsáveis
- [x] Consulta de visitados por responsável
- [x] Captura de coordenadas pelo navegador
- [x] Persistência geoespacial com PostGIS
- [ ] Consulta de pontos por proximidade
- [ ] Otimização da sequência de visitas
- [ ] Visualização dos pontos em mapa
- [ ] Autenticação e controle de acesso

---
