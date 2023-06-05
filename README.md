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

## Тест

Перейдите в консоли в корневую папку проекта и запустите брокер сообщений RabbitMQ:

```bash
docker compose up
```

Теперь запустите Worker с помощью WorkerMain. Переходите к тесту WriteToOutboxTest.

Тест запросит с помощью REST ProducerController. Тот запишет действия, которые хочет выполнить в таблицу outbox. Через некоторое время включится Scheduler, который увидит невыполненный план и попробует записать пользователя в БД и отправить сообщение в брокер. Пришедшее в брокер сообщение в классе TaskReciever будет проверено. Так как оно не выполнялось раньше, в логах Worker вы увидите, что пользователю был отправлен e-mail.

Тесты для прочих ситуаций написаны не были, но можно поэкспериментировать. Далее приведены варианты.

1. **Отключение брокера.** Можно не выполнять `docker compose up`. Тогда в логах вы увидите, как Scheduler пытается установить соединение с Rabbit, но у него не выходит. Поэтому он пробует выполнить это задание ещё раз в следующем запуске. Тест длится 12 секунд, за это время Scheduler запускается два раза. После того, как увидите, что первая попытка подключения провалилась, запустите Rabbit (возможно, стоит запустить его чуть раньше момента первого запуска Scheduler, чтобы он успел проснуться ко второму). Тогда в следующем запуске создание пользователя должно завершиться.

2. **Дедупликация.** Закомментируйте 65-ую строчку файла Writer.java:

```java
plan.setIsWrittenToBroker(true);
```

Теперь Scheduler при повторном запуске будет думать, что не отправил сообщение в Rabbit. В логах Worker вы увидите, что к нему пришло ранее обработанное сообщение:

```
2023-06-05T01:05:23.460+03:00  INFO 10891 --- [           main] com.mipt.worker.WorkerMain               : Started WorkerMain in 1.909 seconds (process running for 2.208)
2023-06-05T01:05:38.259+03:00  INFO 10891 --- [ntContainer#0-1] com.mipt.worker.service.TasksReceiver    : Get message from Email query. User(login=User) from Plan#1
2023-06-05T01:05:38.259+03:00  INFO 10891 --- [ntContainer#0-1] com.mipt.worker.service.TasksReceiver    : Send email to User(login=User) by Plan#1
2023-06-05T01:05:43.251+03:00  INFO 10891 --- [ntContainer#0-1] com.mipt.worker.service.TasksReceiver    : Get message from Email query. User(login=User) from Plan#1
2023-06-05T01:05:43.251+03:00  INFO 10891 --- [ntContainer#0-1] com.mipt.worker.service.TasksReceiver    : Already sent message to this user
```

---

Выполнил Валерий Бергман.

