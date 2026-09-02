#  GeoField Tracker - Sistema de Gestão e Roteamento de Visitas de Campo

Um sistema de geolocalização com interface web focada no mobile (Progressive Web App), desenvolvido para otimizar a logística de equipes de pesquisa e assistência em campo. A aplicação descentraliza a coleta de coordenadas espaciais e resolve problemas de ineficiência de deslocamento, criando rotas inteligentes e automatizadas para os agentes a partir de uma base operacional.

## Funcionalidades

* **Registro de Visitas em Tempo Real:** Durante o trabalho em campo, os agentes acessam o sistema via smartphone, inserem seus dados e os do assistido/entrevistado, anexam uma comprovação fotográfica e salvam a localização exata (GPS) com um único clique.
* **Descentralização de Dados Espaciais:** Elimina o gargalo operacional de depender de um único analista para inserir dados geográficos manualmente. Cada equipe alimenta o banco de dados diretamente do campo de forma autônoma.
* **Roteamento Inteligente:** Ao iniciar um ciclo de visitas, o sistema calcula e retorna as 5 localizações mais próximas à Sede/Base, expandindo o raio de busca gradativamente. Isso evita rotas ineficientes, ziguezagues e economiza tempo de deslocamento da frota.
* **Processamento Geoespacial:** O back-end recebe e trata os dados de latitude/longitude convertendo-os em objetos de geometria (`Point`), armazenando as coordenadas de forma otimizada para cálculos de distância.
* **Integração com QGIS:** Permite a exportação e visualização avançada de todos os pontos de visitação diretamente no software QGIS para análise de densidade e cobertura territorial.
* **Confiabilidade e Qualidade de Código:** O tratamento de dados e as regras de negócio geográficas no back-end possuem cobertura de testes unitários utilizando Mocks, garantindo a estabilidade da aplicação em produção.

## Tecnologias Utilizadas

O projeto foi construído utilizando o padrão de arquitetura de API RESTful com as seguintes tecnologias para garantir a eficiência na coleta e processamento de dados espaciais:

### Front-end
* **Angular** (Construção da interface SPA)
* **Design Mobile-First / PWA** (Foco total na usabilidade via smartphones em ambiente externo)

### Back-end
* **Java**
* **Spring Boot** (Criação da API)
* **Hibernate Spatial** (Tratamento de coordenadas e cálculos espaciais via `Point`)
* **JUnit & Mockito** (Testes unitários automatizados)

### Banco de Dados & Ferramentas
* **PostgreSQL** (com extensão **PostGIS** para armazenamento de dados geoespaciais)
* **QGIS** (Visualização e análise do mapeamento dos dados)
* **Postman** (Testes e documentação de endpoints)
* **Git & GitHub** (Versionamento)

---
