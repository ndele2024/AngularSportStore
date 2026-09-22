# --- Etape 1 : construction Angular ------------------------------------------
FROM node:22-alpine AS build
WORKDIR /build

# Les dependances sont installees avant la copie des sources : le cache Docker
# n'est invalide que lorsque package-lock.json change.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

# --- Etape 2 : service statique ----------------------------------------------
FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /build/dist/sport-store/browser /usr/share/nginx/html
EXPOSE 80
