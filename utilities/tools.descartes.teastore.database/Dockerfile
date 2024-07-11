FROM mysql:8.4.0

ARG DB_SIZE

COPY mysql.cnf /etc/mysql/conf.d/custom.cnf
# The setup.sql must be run before the data sql file. The container will execute the files in alphabetical order
COPY setup.sql /docker-entrypoint-initdb.d/setup_0.sql
COPY db-dumps/teadb_${DB_SIZE}.sql /docker-entrypoint-initdb.d/setup_1.sql

ENV MYSQL_ROOT_PASSWORD rootpassword

EXPOSE 3306
