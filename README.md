# Работа №1. Защищённый REST API с интеграцией CI/CD

[![CI](https://github.com/nerfthisdev/infosec-lab-1/actions/workflows/ci.yml/badge.svg)](https://github.com/nerfthisdev/infosec-lab-1/actions/workflows/ci.yml)

Учебное REST API на Java 25 и Spring Boot 4. Приложение хранит пользователей и данные в PostgreSQL, использует JWT-аутентификацию и автоматически проверяется в GitHub Actions.

## Локальный запуск

Запустить PostgreSQL:

```bash
docker compose up -d
```

Сгенерировать JWT-секрет и запустить API:

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
./mvnw spring-boot:run
```

По умолчанию приложение подключается к PostgreSQL из `docker-compose.yml`. При необходимости используются переменные `DB_URL`, `DB_USERNAME` и `DB_PASSWORD`. Учётные данные по умолчанию предназначены только для локальной учебной среды.

## API

| Метод | Endpoint | Назначение | Доступ |
| --- | --- | --- | --- |
| POST | `/auth/register` | Регистрация пользователя | публичный |
| POST | `/auth/login` | Получение JWT по логину и паролю | публичный |
| GET | `/api/data` | Получение списка данных | Bearer JWT |
| POST | `/api/data` | Создание текстовой записи | Bearer JWT |

Пример регистрации:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"student01","password":"strong-password"}'
```

Полученный при `POST /auth/login` токен передаётся в заголовке:

```bash
curl http://localhost:8080/api/data \
  -H 'Authorization: Bearer <JWT_TOKEN>'
```

## Меры защиты

- **SQLi:** применяются Spring Data JPA и параметризованные запросы репозиториев; пользовательский ввод не конкатенируется с SQL.
- **Аутентификация:** пароли сохраняются только в виде BCrypt-хэшей. JWT подписывается алгоритмом HS256, содержит subject и срок действия; Spring Security проверяет токен на защищённых endpoint’ах.
- **XSS:** текст, возвращаемый в `DataResponse`, экранируется `HtmlUtils.htmlEscape`; пользовательские данные не возвращаются в виде JPA-сущностей.
- **Валидация:** запросы проверяются через `@Valid`, `@NotBlank`, `@Size` и allow-list для имени пользователя.
- **Секреты:** JWT-ключ передаётся исключительно через `JWT_SECRET` и не хранится в репозитории.

## Тестирование

```bash
./mvnw test
```

Интеграционные тесты используют Testcontainers и реальный PostgreSQL. Они проверяют BCrypt, выпуск и валидацию JWT, отказ без токена, SQLi, XSS, валидацию и создание данных.

После установки [Hurl](https://hurl.dev/) можно выполнить E2E-проверку:

```bash
hurl --test --verbose --include --pretty \
  --variable host=localhost:8080 \
  --variable username="hurl-user-$(date +%s)" \
  e2e/api.hurl
```

Для удаления локальной базы: `docker compose down -v`.

## CI/CD

Workflow [CI](.github/workflows/ci.yml) запускается при каждом `push` и `pull request`:

1. собирает проект и выполняет тесты;
2. запускает SAST-анализ SpotBugs и сохраняет XML-отчёт;
3. запускает SCA OWASP Dependency-Check с порогом CVSS 7;
4. публикует отчёты сканеров как artifacts workflow.

Файл `dependency-check-suppressions.xml` содержит документированное подавление проверенного CPE false positive для CVE-2018-1258: проект использует Spring Framework 7, тогда как уязвимость относится к Spring Framework 5.0.5.
