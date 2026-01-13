# Search Service

Spring Boot service that provides for message searching using Elasticsearch.

## Prerequisites
- Java 21
- Maven 3.9+
- (Optional) Docker + Docker Compose

## Environment (.env)
Create `search-service/.env` with the required variables, as shown in .env.example.

Notes:
- `ELASTICSEARCH_HOST/PORT` defaults to `localhost:9200`.
- `SERVER_SERVICE_BASE_URL` is required for access control (checking channel membership).

## Run locally (Maven)
From `search-service/`:

```
./load-env.ps1
```

This script builds the project, loads `.env`, and starts the API module. 

The service starts on `http://localhost:8084`.

## Run with Docker
From `search-service/`:

```
docker network create chat-net
```

(Only needed once; `docker-compose.yml` expects this external network.) Then:

```
docker compose up --build
```

This starts:
- Search Service (port 8084)
- Elasticsearch container (port 9200)

## Useful endpoints
- Health check: `http://localhost:8084/actuator/health`
- OpenAPI: `http://localhost:8084/v3/api-docs`
- Swagger UI: `http://localhost:8084/swagger-ui`
- REST:
  - `GET /search/messages?query=...&userId=...`
  - `POST /search/index/message`
  - `POST /search/index/user`