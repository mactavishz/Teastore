FROM nginx:latest

ARG DB_SIZE

RUN mkdir -p /data/images/tools.descartes.teastore.image
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY generated_images/db-${DB_SIZE} /data/images/tools.descartes.teastore.image

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]