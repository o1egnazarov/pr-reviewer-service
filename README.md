# PR Reviewer Assignment Service
**Test Task — Backend Internship, Fall 2025**

---

## Пошаговая инструкция запуска проекта

### Установка и запуск

1. **Соберите проект и создайте Docker-образ:**
   ```bash
   make build
   ```

2. **Запустите контейнеры:**
   ```bash
   make up
   ```

3. **Проверьте доступность Swagger UI:**
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

4. **Для остановки контейнеров:**
   ```bash
   make down
   ```

5. **Для остановки и удаления томов:**
   ```bash
   make down VOLUMES=true
   ```

6. **Для пересборки образа и перезапуска приложения:**
   ```bash
   make rebuild
   ```

---

## Команды Makefile

| Команда              | Описание                                              |
|----------------------|-------------------------------------------------------|
| `make build`         | Собирает приложение и создаёт Docker-образ           |
| `make up`            | Запускает контейнеры через docker-compose            |
| `make down`          | Останавливает контейнеры и удаляет их                |
| `make down VOLUMES=true` | Останавливает контейнеры и удаляет связанные тома  |
| `make rebuild`       | Пересобирает образ, перезапускает контейнеры         |


### Что сделал?
- Описал сущности и создал репозитории (Jpa/Hibernate + Spring Data Jpa)
- Написал бизнес логику (покрыл unit и e2e тестами)
- Сгенерировал с помощью OpenApi контроллеры и модели (использовал openApi-maven-plugin)
- Добавил мапперы для преобразования моделей в сущности (использовал Mapstruct)
- Добавил линтер и автоформатирование кода по Google code style (использовал Checkstyle + Spotless)

### Дополнительные задания (выполнены)
- Эндпоинт статистики
- Массовая деактивация + безопасное переназначение PR
- E2E тесты 
- Линтер (Checkstyle + Spotless)
---

## PS
Выполнил задание на Java, так как на этом языке могу продемонстрировать максимально качественный и уверенный уровень.
С Go также знаком и могу работать с ним при необходимости; планирую развивать его как один из основных инструментов.

Также я вносил изменения в openapi.yaml для добавления dto в дополнительных заданиях. Также нашел опечатку:
```yaml
/pullRequest/reassign:
  post:
    operationId: reassignReviewer
    tags: [ PullRequests ]
    summary: Переназначить конкретного ревьювера на другого из его команды
    security:
      - AdminToken: [ ]
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [ pull_request_id, old_user_id ]
            properties:
              pull_request_id: { type: string }
              old_user_id: { type: string }
            example:
              pull_request_id: pr-1001
              old_reviewer_id: u2
```

В required - прописаны  `pull_request_id`, `old_user_id` а в example `pull_request_id` , `old_reviewer_id`