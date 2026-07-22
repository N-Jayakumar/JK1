# JK1 Docker Deployment Guide

This document outlines the steps required to run the JK1 Spring Boot application using Docker locally or in production environments like Render or Railway.

## Local Development (Docker Desktop)

The `docker-compose.yml` file is configured to spin up the Spring Boot application and a MySQL 8 database. It automatically links them together and provisions a persistent volume for the database data.

### Prerequisites
- Docker Desktop installed and running.

### Commands

1. **Start the application & database:**
   ```bash
   docker compose up --build -d
   ```

2. **View Logs:**
   ```bash
   docker compose logs -f
   ```

3. **Stop the environment:**
   ```bash
   docker compose down
   ```

*(Note: Data is persisted in the `db_data` volume even if the container is removed.)*

---

## Production Deployment (Render, Railway, Linux Server)

The application provides a multi-stage `Dockerfile` optimized for production using Eclipse Temurin JDK 21 for building, and JRE 21 for the runtime environment to keep the image size small.

### Render Deployment Steps

Render can natively build and deploy this application by detecting the `Dockerfile`.

1. **Create a new Web Service** on Render and connect your GitHub repository.
2. Select **Docker** as the runtime environment.
3. Render will automatically detect the exposed port (or inject its own `PORT` environment variable which our `application-prod.properties` is configured to pick up via `server.port=${PORT:8080}`).
4. **Environment Variables:** Set the following environment variables in your Render Web Service settings:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `SPRING_DATASOURCE_URL` = `jdbc:mysql://<your_database_host>:3306/jk1?useSSL=false&serverTimezone=UTC`
   - `SPRING_DATASOURCE_USERNAME` = `jk1`
   - `SPRING_DATASOURCE_PASSWORD` = `<your_database_password>`

*(Note: If you are using Render's managed PostgreSQL instead of MySQL, you will need to add the PostgreSQL driver to your `pom.xml` and update the driver class name in `application-prod.properties`.)*

### General Linux Server (Docker Engine)

You can run the same Docker Compose setup on any Linux server:

1. Install `docker` and `docker-compose`.
2. Clone the repository.
3. Run `docker compose up --build -d`.
4. We strongly recommend changing the default MySQL passwords in `docker-compose.yml` before starting the service in a production environment.

## Validation

The application contains standard endpoints to verify health. If deploying on a cloud platform:
- The `Dockerfile` compiles the code internally via `./mvnw clean package -DskipTests`. If compilation fails, the Docker build will fail.
- Once running, the application binds to `$PORT` (default 8080).
- The `docker-compose.yml` file contains a health check for the MySQL database. The Spring Boot application depends on this health check and will wait until the database is ready before starting.
