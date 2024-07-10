FROM nginx:latest

ARG DB_SIZE
ARG SERVICE_PORT
ARG SERVICE_PORT_SSL

RUN mkdir -p /data/images/tools.descartes.teastore.image
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY generated_images/db-${DB_SIZE} /data/images/tools.descartes.teastore.image

EXPOSE ${SERVICE_PORT} ${SERVICE_PORT_SSL}

CMD ["nginx", "-g", "daemon off;"]