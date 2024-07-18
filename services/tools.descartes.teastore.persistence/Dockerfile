FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi

COPY --chown=1001:0 /src/main/liberty/config /config

RUN features.sh

COPY --chown=1001:0 target/*.war /config/apps

# Create the jdbc directory
RUN mkdir -p /config/jdbc

# Copy the MySQL Connector JAR
COPY --chown=1001:0 mysql-connector-java-5.1.44-bin.jar /config/jdbc/

RUN configure.sh
