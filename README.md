# GeoField Tracker

Sistema web para registro de localizações geográficas e planejamento de rotas de visitas em campo.

O projeto utiliza **Java 21, Spring Boot, Angular, PostgreSQL/PostGIS e OSRM** para realizar a coleta de coordenadas, armazenamento geoespacial e organização de visitas em uma sequência otimizada pela malha viária.

A aplicação surgiu de um problema real: diferentes pessoas precisam cadastrar previamente vários locais e, posteriormente, uma equipe precisa visitar esses pontos sem definir manualmente qual endereço deve ser atendido em seguida.

O sistema centraliza esse processo, desde a captura da localização até a geração e execução da rota.

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

Antes do registro, o usuário seleciona os responsáveis e a pessoa previamente cadastrada que será associada à localização.

As informações são enviadas ao backend por meio de uma API REST.

---

### Armazenamento geoespacial

Latitude e longitude são convertidas no backend para uma geometria `Point` utilizando JTS/Hibernate Spatial.

As coordenadas são armazenadas no PostgreSQL/PostGIS utilizando o **SRID 4326**, permitindo que sejam tratadas como dados geoespaciais.

Exemplo conceitual:

```text
latitude + longitude
        ↓
Spring Boot
        ↓
JTS Point (SRID 4326)
        ↓
PostgreSQL + PostGIS
```

---

### Geração de rota de visitas

As visitas com status `PENDENTE` podem ser organizadas automaticamente em uma sequência otimizada.

O Spring Boot envia as coordenadas para o **OSRM**, utilizando um ponto de origem fixo para iniciar a rota.

O OSRM utiliza a malha viária do **OpenStreetMap** para determinar uma sequência eficiente entre os destinos.

```text
Visitas pendentes
        ↓
Spring Boot
        ↓
OSRM
        ↓
OpenStreetMap
        ↓
Sequência otimizada
```

O backend interpreta a ordem retornada pelo OSRM e associa cada posição à respectiva visita.

---

### Persistência da ordem da rota

A ordem calculada pelo OSRM é persistida no próprio registro da visita.

Isso evita que a rota seja recalculada sempre que:

- a página for atualizada;
- uma visita for concluída;
- uma visita for marcada como ausente.

Enquanto houver uma rota em andamento, o backend recupera a sequência já salva no banco.

Uma nova rota somente é calculada quando o usuário seleciona explicitamente **Gerar nova rota**.

---

### Controle das visitas

Durante a execução da rota, cada parada pode ser marcada como:

- **Visitado**
- **Ausente**

Quando uma visita é concluída, ela deixa de fazer parte da rota atual.

No caso de ausência, o sistema incrementa o número de tentativas. Enquanto o limite definido não for atingido, a visita pode continuar com status `PENDENTE` e participar de uma futura rodada.

---

### Navegação com Google Maps e Waze

O projeto não implementa um navegador GPS próprio.

Para cada parada da rota, o frontend disponibiliza opções para abrir diretamente o destino no:

- Google Maps
- Waze

A divisão de responsabilidades fica:

```text
GeoField Tracker
→ determina qual deve ser a próxima visita

Google Maps / Waze
→ realiza a navegação até o destino
```

---

### Paginação da rota

A rota completa permanece disponível no frontend, mas as visitas são divididas em páginas para facilitar a utilização com uma quantidade maior de pontos.

A paginação altera apenas a visualização.

A ordem original calculada para a rota é preservada.

Exemplo:

```text
Página 1
1 - Visita A
2 - Visita B
...
10 - Visita J

Página 2
11 - Visita K
12 - Visita L
...
```

---

### Confirmação e controle de ações

A interface utiliza modais de confirmação antes de operações importantes, como:

- marcar uma visita como realizada;
- registrar uma ausência;
- gerar uma nova rota.

Os botões também são temporariamente bloqueados enquanto uma requisição está sendo processada, evitando múltiplos envios acidentais.

---

### Progressive Web App

O frontend foi desenvolvido com foco em dispositivos móveis e configurado como **Progressive Web App (PWA)**.

A aplicação possui fluxos separados para:

- registro de localização;
- execução da rota de visitas.

---

## Arquitetura

A aplicação segue uma arquitetura onde cada tecnologia possui uma responsabilidade específica:

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
               │              │
               │              ▼
               │        OpenStreetMap
               │
               ▼
      Dados das visitas
```

Para a navegação:

```text
Angular
   ↓
próxima visita
   ↓
Google Maps / Waze
```

### Responsabilidades

**Angular**
- interface;
- captura da localização;
- exibição da rota;
- interação com o usuário.

**Spring Boot**
- regras de negócio;
- gerenciamento das visitas;
- integração com o banco;
- integração com o OSRM.

**PostgreSQL/PostGIS**
- persistência;
- armazenamento geoespacial das coordenadas.

**OSRM**
- análise da malha viária;
- cálculo da sequência de visitas.

**OpenStreetMap**
- dados da rede de ruas utilizados pelo OSRM.

**Google Maps / Waze**
- navegação entre a localização atual da equipe e o próximo destino.

---

## Evolução da solução de roteamento

Durante o desenvolvimento, diferentes estratégias de roteamento foram estudadas.

Inicialmente, foi utilizada uma abordagem baseada em:

- PostGIS;
- pgRouting;
- grafos;
- vértices e arestas;
- algoritmo de Dijkstra;
- dados viários importados do OpenStreetMap.

Essa implementação permitiu compreender como uma rede viária pode ser representada como um grafo e como caminhos mínimos são calculados.

Entretanto, a solução exigia implementar manualmente recursos que já são tratados por motores especializados de roteamento.

Por isso, a arquitetura evoluiu para utilizar o **OSRM**.

A responsabilidade passou a ser dividida da seguinte forma:

```text
PostGIS
→ armazenamento geoespacial

OSRM
→ roteamento e ordenação das visitas

Spring Boot
→ regras de negócio e integração

Angular
→ interface

Google Maps / Waze
→ navegação
```

---

## Persistência da rota

Durante o desenvolvimento foi identificado outro problema: recalcular a rota após cada visita concluída poderia alterar completamente a sequência restante.

Exemplo:

```text
Rota original

1 - A
2 - B
3 - C
4 - D
```

Após concluir `A`, uma nova chamada ao algoritmo poderia retornar:

```text
1 - C
2 - D
3 - B
```

Para evitar esse comportamento, a ordem calculada é persistida no banco.

Exemplo:

```text
Visita A → ordemRota = 1
Visita B → ordemRota = 2
Visita C → ordemRota = 3
Visita D → ordemRota = 4
```

Ao concluir uma visita:

```text
ordemRota → null
```

As demais continuam com suas posições originais.

Dessa forma, atualizar a página não provoca um novo cálculo da rota.

---

## Integração com QGIS

O PostGIS também pode ser conectado ao **QGIS** durante o desenvolvimento.

Isso permite visualizar os pontos registrados e analisar os dados geográficos diretamente sobre um mapa.

O QGIS funciona como uma ferramenta auxiliar de desenvolvimento e análise e não faz parte da interface utilizada pelo usuário final.

---

## Testes

A estrutura de testes do backend utiliza:

- JUnit
- Mockito
- MockMvc

Os testes serão utilizados para validar principalmente:

- regras de negócio das visitas;
- geração e persistência da rota;
- comportamento quando uma rota já existe;
- controle de tentativas;
- endpoints REST;
- tratamento de cenários de erro.

---

## Estrutura geral do projeto

```text
geolocalizacao/
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

## Roadmap

### Concluído

- [x] Cadastro e consulta de localizações
- [x] Captura de coordenadas pelo navegador
- [x] Persistência geoespacial com PostGIS
- [x] Interface mobile-first
- [x] Configuração como PWA
- [x] Integração com OSRM
- [x] Utilização da malha do OpenStreetMap
- [x] Otimização da sequência de visitas
- [x] Persistência da ordem da rota
- [x] Controle de visitas realizadas
- [x] Controle de ausências e tentativas
- [x] Geração manual de novas rotas
- [x] Integração com Google Maps
- [x] Integração com Waze
- [x] Paginação da rota no frontend
- [x] Confirmação de ações
- [x] Proteção contra múltiplos envios

### Próximas etapas

- [ ] Ampliar cobertura de testes automatizados
- [ ] Testar o sistema com maior volume de localizações
- [ ] Configurar CI com GitHub Actions
- [ ] Deploy do frontend
- [ ] Deploy do backend
- [ ] Configuração do banco em ambiente de produção
- [ ] Deploy do serviço OSRM

---

## Deploy planejado

A arquitetura planejada para produção é:

```text
Vercel
→ Angular

Render
→ Spring Boot

Supabase
→ PostgreSQL + PostGIS

Serviço dedicado/container
→ OSRM
```

A infraestrutura poderá ser ajustada após os testes de consumo de recursos do OSRM.

---

## Status

O projeto está em desenvolvimento.

O fluxo principal já permite:

```text
Cadastrar localização
        ↓
Armazenar no PostGIS
        ↓
Gerar rota
        ↓
OSRM otimiza a sequência
        ↓
Executar visitas
        ↓
Abrir destino no Maps/Waze
        ↓
Registrar Visitado/Ausente
        ↓
Gerar uma nova rodada quando necessário
```

As próximas etapas estão concentradas em testes automatizados, validação com maior volume de dados, integração contínua e deploy.
