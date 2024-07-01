FROM node:22.3.0
WORKDIR /web

COPY . .
RUN npm install --verbose

EXPOSE 8080
EXPOSE 3010

CMD npm run dev