docker compose down
mvn clean
mvn package
docker compose build --no-cache
docker compose up -d

wait_for_url() {
  local url=$1
  local service_name=$2
  local max_retries=60
  local retry=0
  local response_code

  echo "Ожидание запуска $service_name на $url"

  while [ $retry -lt $max_retries ]; do
    response_code=$(curl -k -s -o /dev/null -w "%{http_code}" "$url")
    if [[ "$response_code" -eq 200 ]]; then
      echo "$service_name доступен."
      return 0
    else
      echo "$service_name недоступен. Код ответа: $response_code. Повтор через 5 сек..."
      sleep 5
      retry=$((retry + 1))
    fi
  done

  echo "Ошибка: $service_name не стал доступным в течение $((max_retries * 5)) секунд."
  exit 1
}

# Ожидание обоих сервисов
wait_for_url "http://localhost:8080/actuator/health" "Сервис 8080"
wait_for_url "http://localhost:9090/actuator/health" "Сервис 9090"

echo "Все сервисы успешно запущены!"
