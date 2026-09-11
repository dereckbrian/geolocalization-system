# Sistema de Geolocalização e Roteamento de Visitas

Sistema web para registro de localizações geográficas e organização de rotas de visitas em campo.

A aplicação utiliza **Java 21, Spring Boot, Angular, PostgreSQL/PostGIS e OSRM** para realizar a coleta de coordenadas, armazenamento geoespacial e organização das visitas em uma sequência otimizada pela malha viária.

O projeto surgiu de um problema real: diferentes pessoas precisam cadastrar previamente vários locais e, posteriormente, uma equipe precisa visitar esses pontos sem definir manualmente qual endereço deve ser atendido em seguida.

O sistema centraliza esse processo, desde a captura da localização até a geração e execução da rota.

---

## Tecnologias

### Back-end

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate Spatial
- JTS
- MapStruct
- RestClient

### Front-end

- Angular
- TypeScript
- Tailwind CSS
- Progressive Web App (PWA)
- Geolocation API
- Angular Router
- HttpClient

### Banco de dados

- PostgreSQL
- PostGIS

### Roteamento

- OSRM
- OpenStreetMap
- Docker

### Testes

- JUnit
- Mockito
- MockMvc

### Ferramentas

- Git
- GitHub
- Postman
- QGIS

---

## Funcionalidades

### Registro de localização

A aplicação permite capturar a localização atual do dispositivo utilizando a **Geolocation API** do navegador.

Antes do registro, o usuário seleciona os responsáveis e a pessoa previamente cadastrada associada à localização.

As coordenadas são enviadas ao backend por meio de uma API REST.

### Armazenamento geoespacial

Latitude e longitude são convertidas no backend para uma geometria `Point` utilizando JTS e Hibernate Spatial.

As coordenadas são persistidas no PostgreSQL/PostGIS utilizando **SRID 4326**, permitindo que sejam tratadas como dados geoespaciais.

### Geração de rota

As visitas com status `PENDENTE` podem ser organizadas automaticamente em uma sequência otimizada.

O Spring Boot envia as coordenadas para o **OSRM**, utilizando um ponto de origem fixo. O OSRM utiliza a malha viária do **OpenStreetMap** para calcular uma sequência eficiente entre os destinos.

A ordem calculada é persistida no banco, evitando que a sequência restante seja recalculada quando a página é atualizada ou uma visita é concluída.

Uma nova rota é gerada somente quando solicitada pelo usuário.

### Controle das visitas

Durante a execução da rota, cada parada pode ser marcada como:

- **Visitado**
- **Ausente**

Ao registrar uma ausência, o sistema incrementa o número de tentativas.

Enquanto o limite definido não é atingido, a visita permanece com status `PENDENTE` e pode participar de uma nova rodada de visitas.

### Navegação com Google Maps e Waze

Para cada parada da rota, o frontend disponibiliza opções para abrir diretamente o destino no:

- Google Maps
- Waze

A aplicação é responsável por definir a **sequência das visitas**, enquanto Google Maps e Waze ficam responsáveis pela navegação até cada destino.

### Paginação da rota

A rota completa é mantida no frontend, mas as visitas são divididas em páginas para facilitar a utilização com uma quantidade maior de destinos.

A paginação altera apenas a visualização e preserva a posição original de cada visita na rota.

### Confirmação de ações

A interface utiliza modais de confirmação antes de operações que alteram o estado das visitas, como:

- marcar uma visita como realizada;
- registrar uma ausência;
- gerar uma nova rota.

Durante as requisições, os botões são temporariamente bloqueados para evitar múltiplos envios.

### Progressive Web App

O frontend foi desenvolvido com foco em dispositivos móveis e configurado como **Progressive Web App (PWA)**.

A aplicação possui dois fluxos principais:

- registro de localização;
- execução da rota de visitas.

---

## Arquitetura

```text
                 Angular PWA
                      │
                      │ HTTP / REST
                      ▼
                 Spring Boot
                 /          \
                /            \
               ▼              ▼
      PostgreSQL/PostGIS     OSRM
                              │
                              ▼
                        OpenStreetMap
```

Para a navegação até cada destino:

```text
Angular
   ↓
próxima visita
   ↓
Google Maps / Waze
```

### Responsabilidades

**Angular**

- interface da aplicação;
- captura da localização;
- exibição da rota;
- paginação;
- interação com o usuário.

**Spring Boot**

- regras de negócio;
- gerenciamento das visitas;
- controle de tentativas;
- persistência da ordem da rota;
- integração com o banco de dados;
- integração com o OSRM.

**PostgreSQL/PostGIS**

- persistência dos dados;
- armazenamento geoespacial das coordenadas.

**OSRM**

- análise da malha viária;
- cálculo da sequência das visitas.

**OpenStreetMap**

- fornecimento dos dados da rede viária utilizados pelo OSRM.

**Google Maps / Waze**

- navegação entre a localização atual da equipe e o próximo destino.

---

## Decisões técnicas

O roteamento foi inicialmente explorado utilizando PostGIS, pgRouting e algoritmos de caminho mínimo.

A responsabilidade pelo cálculo da sequência de visitas foi posteriormente transferida para o **OSRM**, permitindo manter o PostGIS responsável pelo armazenamento geoespacial e utilizar um motor especializado para roteamento sobre a malha do OpenStreetMap.

A ordem retornada pelo OSRM é persistida na própria visita durante a execução da rota.

Dessa forma, concluir ou remover uma parada não provoca um novo cálculo e não altera a sequência restante.

Essa separação mantém as responsabilidades da aplicação bem definidas:

```text
PostGIS
→ armazenamento geoespacial

OSRM
→ cálculo e otimização da sequência

Spring Boot
→ regras de negócio e integração

Angular
→ interface

Google Maps / Waze
→ navegação
```

---

## Estrutura do projeto

```text
geolocalization-system/
│
├── back-end/
│   └── Spring Boot
│
├── front-end/
│   └── Angular
│
└── osrm/
    └── dados utilizados pelo motor de roteamento
```

---

## Testes

O projeto utiliza as seguintes tecnologias para testes automatizados do backend:

- JUnit
- Mockito
- MockMvc

Os testes são voltados para regras de negócio, serviços e endpoints da aplicação.

---

## Fluxo da aplicação

```text
Captura da localização
        ↓
Angular
        ↓
API REST
        ↓
Spring Boot
        ↓
PostgreSQL + PostGIS
        ↓
Visitas pendentes
        ↓
OSRM
        ↓
Sequência otimizada
        ↓
Angular
        ↓
Google Maps / Waze
        ↓
Visitado / Ausente
```

---

## Status do projeto

O sistema foi concluído dentro do escopo proposto e possui o fluxo principal de utilização funcional.

A aplicação permite:

- cadastrar e consultar localizações;
- capturar coordenadas diretamente pelo navegador;
- armazenar dados geoespaciais com PostGIS;
- gerar uma sequência otimizada de visitas com OSRM;
- preservar a ordem da rota durante sua execução;
- abrir os destinos no Google Maps e Waze;
- registrar visitas realizadas e ausências;
- controlar tentativas de visita;
- gerar novas rodadas manualmente;
- paginar as visitas no frontend;
- confirmar ações antes de alterações;
- evitar múltiplos envios durante requisições.

A arquitetura foi estruturada para execução em ambiente de produção.
