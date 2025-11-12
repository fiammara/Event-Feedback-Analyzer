# Stage 1: Build React app
FROM node:20 AS build
WORKDIR /app

# Accept build-time argument
ARG VITE_API_BASE
ENV VITE_API_BASE=$VITE_API_BASE

COPY package*.json ./
RUN npm install
COPY . .

# Build React app with the build-time env
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]



