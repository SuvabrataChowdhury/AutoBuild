# Run backend containers

## App stack only

From project root, build and start the application and database:
```
docker build -t pipeline-app .
docker compose --env-file .env.local up -d
```

## App stack + monitoring

Starts the app stack together with the full monitoring stack (Grafana, Loki, Prometheus, Alloy). All services start in dependency order automatically:
```
docker build -t pipeline-app .
docker compose --env-file .env.local \
  -f docker-compose.yml \
  -f monitoring/docker-compose-monitoring.yml \
  -f monitoring/docker-compose-monitoring-integration.yml \
  up -d
```

Monitoring UIs available at:
- Grafana: http://localhost:3000
- Alloy: http://localhost:12345
- Prometheus: http://localhost:9090

## Stopping

```
docker compose down
```

To include the monitoring stack when tearing down:
```
docker compose \
  -f docker-compose.yml \
  -f monitoring/docker-compose-monitoring.yml \
  -f monitoring/docker-compose-monitoring-integration.yml \
  down
```

## Fresh instance

```
docker compose down -v
```
> Note: this will delete all persisted volumes including database data, Grafana dashboards, Loki logs, and Prometheus metrics.
