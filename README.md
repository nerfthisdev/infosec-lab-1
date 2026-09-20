# infosec-lab-1
[![CI](https://github.com/nerfthisdev/infosec-lab-1/actions/workflows/ci.yml/badge.svg)](https://github.com/nerfthisdev/infosec-lab-1/actions/workflows/ci.yml)

## Локальный запуск PostgreSQL и E2E

Запустить PostgreSQL:

```bash
docker compose up -d
```

Запустить приложение:

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
./mvnw spring-boot:run
```

После установки [Hurl](https://hurl.dev/) выполнить E2E-проверки:

```bash
  hurl --test \
    --verbose \
    --include \
    --pretty \
    --variable host=localhost:8080 \
    --variable username="hurl-user-$(date +%s)" \
    e2e/api.hurl
```

Сценарий проверяет отказ без JWT, регистрацию, вход, создание и чтение данных.
Для полного удаления локальной базы данных используйте `docker compose down -v`.
