FROM ghcr.io/project-osrm/osrm-backend AS build

WORKDIR /data

COPY mapFormosa.osm .

RUN osrm-extract -p /opt/car.lua mapFormosa.osm \
    && osrm-partition mapFormosa.osrm \
    && osrm-customize mapFormosa.osrm


FROM ghcr.io/project-osrm/osrm-backend

WORKDIR /data

COPY --from=build /data/mapFormosa.osrm* /data/

EXPOSE 5000

CMD ["osrm-routed", "--algorithm", "mld", "/data/mapFormosa.osrm"]