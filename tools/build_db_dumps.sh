#!/bin/bash

jarfile="../utilities/tools.descartes.teastore.dbgenerator/target/tools.descartes.teastore.dbgenerator-jar-with-dependencies.jar"
# get the full path of the jar file
jarfile=$(realpath $jarfile)

mkdir -p ./dumps/mysql
mkdir -p ./dumps/sqlite
# fix this array
sizes=("small" "mid" "large")

docker compose -f "../docker-compose.yaml" down

# use a java 17 docker container to run the jar file to generate the data for the database with different arguments: small, mid, large
for size in "${sizes[@]}"
do
  # remove the container if it exists
  docker rm teastore-db
  # start the database container
  docker compose -f "../docker-compose.yaml" up db -d
  # generate data for the database
  docker run --network teastore-network -e DB_HOST=db -e DB_PORT=3306 --rm -v $jarfile:/app.jar openjdk:17 java -jar /app.jar "$size"
  # dump the database
  docker exec -it teastore-db /usr/bin/mysqldump --user root --password=rootpassword teadb > ./dumps/mysql/teadb_$size.sql
  # stop the database container
  docker compose -f "../docker-compose.yaml" down
done

# use https://github.com/mysql2sqlite/mysql2sqlite to convert the mysql dump to sqlite
# Convert the dumps to SQLite using a Debian container
for size in "${sizes[@]}"
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