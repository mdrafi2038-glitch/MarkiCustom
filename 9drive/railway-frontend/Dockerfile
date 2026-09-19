FROM node:20-alpine AS build
WORKDIR /app
RUN apk add --no-cache git
RUN git clone --depth 1 https://github.com/zenhosta/9drive.git /src
WORKDIR /src/frontend
RUN npm ci
RUN npm run build

FROM nginx:1.27-alpine
COPY 9drive-railway-frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /src/frontend/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx","-g","daemon off;"]
