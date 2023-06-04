# Transactional Outbox

## Подготовка к запуску

Понадобится создать две базы данных Postgres для работы проекта:

```bash
sudo su - postgres
psql
CREATE DATABASE users;
CREATE DATABASE outbox;
ALTER USER postgres WITH PASSWORD 'password';
```

---

Выполнил Валерий Бергман.

