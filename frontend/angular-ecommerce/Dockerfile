# ---------- Stage 1: Build ----------
FROM node:14-alpine AS build

WORKDIR /app

# copy only dependencies first
COPY package*.json ./

# install dependencies (optimized)
RUN npm install --legacy-peer-deps --no-audit --no-fund

# copy remaining code
COPY . .

# build angular app
RUN npm run build -- --configuration production


# ---------- Stage 2: Serve ----------
FROM nginx:alpine

# copy build output
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
