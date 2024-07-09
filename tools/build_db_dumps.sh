#!/bin/bash

JAR_FILE="../utilities/tools.descartes.teastore.dbgenerator/target/tools.descartes.teastore.dbgenerator-jar-with-dependencies.jar"
SETUP_SQL="../utilities/tools.descartes.teastore.database/setup.sql"
# get the absolute path of the jar file and the setup sql file
JAR_FILE=$(realpath $JAR_FILE)
SETUP_SQL=$(realpath $SETUP_SQL)
DB_NAME="teadb"
MYSQL_VER="8.4.0"

mkdir -p ./dumps/mysql
rm -rf ./dumps/mysql/*
mkdir -p ./dumps/sqlite
rm -rf ./dumps/sqlite/*
DB_SIZES=("small" "mid" "large")

docker network create dbgenerator-network
echo "Generating MySQL database dumps..."
for size in "${DB_SIZES[@]}"
do
  echo "Removing the database container if it exists..."
  docker rm -f dbgenerator
  echo "Starting the database container..."
  docker run --name dbgenerator --network dbgenerator-network -p 3306:3306 -e MYSQL_ROOT_PASSWORD="rootpassword" -d mysql:$MYSQL_VER
  echo "Waiting for the database to be ready..."
  until docker exec dbgenerator sh -c 'timeout 1 bash -c "< /dev/tcp/localhost/3306"'; do
    echo "Database connection is not ready ..."
    sleep 1
  done
  echo "Database is accepting connections!"
  echo "Database is ready!"
  docker exec -i -e MYSQL_PWD="rootpassword" dbgenerator sh -c "exec mysql -u root" < "$SETUP_SQL"
  echo "Generating data for the $size database..."
  # use a java 17 docker container to run the jar file to generate the data for the database with different arguments: small, mid, large
  docker run --network dbgenerator-network --rm \
    -e DB_HOST=dbgenerator -e DB_PORT=3306 \
    -e DB_USER="root" \
    -e DB_PASSWORD="rootpassword" \
    -v "$JAR_FILE":/app.jar openjdk:17 java -jar /app.jar "$size"
  echo "Dumping the $size database..."
  docker exec -it -e MYSQL_PWD="rootpassword" dbgenerator /usr/bin/mysqldump --user root --databases teadb > ./dumps/mysql/teadb_"$size".sql
  echo "MySQL dump for $size database is generated successfully."
  echo "Stopping the database container..."
  docker rm -f dbgenerator
done

docker network rm dbgenerator-network

# use https://github.com/mysql2sqlite/mysql2sqlite to convert the mysql dump to sqlite
# Convert the dumps to SQLite using a Debian container
for size in "${DB_SIZES[@]}"
do
  docker run --rm \
    -v "$(pwd)/dumps:/dumps" \
    -w /dumps \
    debian:bullseye-slim \
    bash -c '
      apt-get update && \
      apt-get install -y wget sqlite3 && \
      wget https://raw.githubusercontent.com/mysql2sqlite/mysql2sqlite/master/mysql2sqlite && \
      chmod +x mysql2sqlite && \
      ./mysql2sqlite "./mysql/teadb_'"$size"'.sql" | sqlite3 "./sqlite/teadb_'"$size"'.db" && \
      rm mysql2sqlite
    '
done

echo "Database dumps are generated successfully."
echo "Database dumps are available in the following directories:"
echo "MySQL dumps: ./dumps/mysql"
echo "SQLite dumps: ./dumps/sqlite"

mkdir -p ../utilities/tools.descartes.teastore.database/db-dumps
mkdir -p ../utilities/tools.descartes.teastore.imagegenerator/src/main/resources/db-dumps
for size in "${DB_SIZES[@]}"
do
  # move the mysql dump to the db-dumps folder in side the database module
  mv ./dumps/mysql/teadb_$size.sql ../utilities/tools.descartes.teastore.database/db-dumps/teadb_$size.sql
  # move the sqlite dump to the db-dumps folder in side the image module
  mv ./dumps/sqlite/teadb_$size.db ../utilities/tools.descartes.teastore.imagegenerator/src/main/resources/db-dumps/teadb_$size.db
done

echo "Database dumps are copied to the following directories:"
echo "MySQL dumps: ../utilities/tools.descartes.teastore.database/db-dumps"
echo "SQLite dumps: ../utilities/tools.descartes.teastore.imagegenerator/src/main/resources/db-dumps"
echo "Cleaning up..."
rm -rf ./dumps