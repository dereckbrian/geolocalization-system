# GeoField Tracker

Sistema web para registro de localizações geográficas e planejamento de rotas de visitas em campo, desenvolvido com Spring Boot, Angular e PostgreSQL/PostGIS.

A aplicação permite cadastrar coordenadas diretamente pelo dispositivo móvel, armazená-las como dados geoespaciais e posteriormente gerar uma sequência otimizada de visitas utilizando a malha viária do OpenStreetMap.

O projeto surgiu da necessidade de substituir processos manuais de coleta de endereços e definição de rotas, centralizando o cadastro, armazenamento e organização das visitas em uma única aplicação.

---

## Funcionalidades

### Registro de localização

A aplicação permite capturar a posição atual do dispositivo utilizando a Geolocation API do navegador.

Antes do registro, o usuário seleciona os responsáveis e a pessoa previamente cadastrada associada à localização.

As coordenadas são enviadas ao backend e armazenadas como uma geometria `Point` com SRID 4326.

---

### Armazenamento geoespacial

Latitude e longitude são convertidas para objetos geográficos utilizando JTS/Hibernate Spatial e persistidas no PostgreSQL com PostGIS.

Isso permite que as localizações sejam tratadas como dados espaciais em vez de apenas valores numéricos.

---

### Geração de rota de visitas

As visitas pendentes podem ser organizadas automaticamente em uma sequência otimizada.

O backend envia as coordenadas das visitas para o OSRM, utilizando um ponto de origem fixo e a malha viária do OpenStreetMap.

O OSRM retorna uma ordem otimizada de visita considerando o deslocamento pela rede de ruas.
