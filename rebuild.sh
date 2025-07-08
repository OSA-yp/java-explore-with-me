docker compose down
mvn clean
mvn package
docker compose build --no-cache
docker compose up -d
