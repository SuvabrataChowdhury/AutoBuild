# Backend setup

## Steps:
- From project root execute,
```
docker build -t pipeline-app .
docker compose up
```

to start application and db.
- To turn off server run,
```
docker compose down
```
- To get a fresh instance run,
```
docker compose down -v 
```
> Note: this will get rid of all persisted volumes.