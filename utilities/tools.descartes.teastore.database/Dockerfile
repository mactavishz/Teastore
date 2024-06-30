FROM mysql:8.4.0

# The setup.sql must be run before the data sql file. The container will execute the files in alphabetical order
COPY setup.sql /docker-entrypoint-initdb.d/setup_0.sql
# Uncomment the following line if the teadb schema should come with a standard initialized data set
# COPY setupData.sql /docker-entrypoint-initdb.d/setup_1.sql

# Modify MySQL configuration
#RUN echo "max_connections = 2048" >> /etc/mysql/conf.d/mysql.cnf

ENV MYSQL_ROOT_PASSWORD rootpassword

EXPOSE 3306
