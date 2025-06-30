# Stage 1: Build the app
FROM node:18.10.0 AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci
#RUN npx npm install -g

COPY . .
RUN npm run build --prod

# Stage 2: Serve with NGINX
FROM nginx:alpine

# Optional: copy custom nginx config (if needed)
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

# Copy the built app from previous stage
COPY --from=build /app/dist/book-network-ui/ /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
