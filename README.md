# Информационно-справочная система Пенсионного фонда РФ

**Автор:** Волков Никита, группа ТРПО23-3

Кросс-платформенное приложение для учета и управления данными пенсионеров и пенсионных выплат.

## Структура проекта

Проект состоит из трех модулей:
1. `common` - общие модели данных
2. `server` - серверная часть (REST API)
3. `client` - клиентская часть (JavaFX приложение)

## Требования

- Java 17 или выше
- Maven 3.6 или выше
- PostgreSQL 12 или выше

## Настройка базы данных

1. Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE pension_fund;
```

2. Настройте подключение к базе данных в файле `server/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pension_fund
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Сборка и запуск

### Сборка проекта

```bash
mvn clean install
```

### Запуск сервера

```bash
cd server
mvn spring-boot:run
```

### Запуск клиента

```bash
cd client
mvn javafx:run
```

## Функциональность

### Управление пенсионерами
- Просмотр списка пенсионеров
- Добавление новых пенсионеров
- Редактирование данных пенсионеров
- Удаление пенсионеров
- Поиск пенсионеров по различным критериям

### Управление выплатами
- Просмотр списка выплат
- Добавление новых выплат
- Редактирование данных выплат
- Удаление выплат
- Фильтрация выплат по периоду

### Статистика
- Общее количество пенсионеров
- Средний размер пенсии
- Максимальный размер пенсии
- Минимальный размер пенсии

## API Endpoints

### Пенсионеры
- `GET /api/pensioners` - получить список всех пенсионеров
- `GET /api/pensioners/{id}` - получить данные пенсионера по ID
- `POST /api/pensioners` - создать нового пенсионера
- `PUT /api/pensioners/{id}` - обновить данные пенсионера
- `DELETE /api/pensioners/{id}` - удалить пенсионера
- `GET /api/pensioners/search` - поиск пенсионеров
- `GET /api/pensioners/statistics` - получить статистику

### Выплаты
- `GET /api/payments` - получить список всех выплат
- `GET /api/payments/{id}` - получить данные выплаты по ID
- `POST /api/payments` - создать новую выплату
- `PUT /api/payments/{id}` - обновить данные выплаты
- `DELETE /api/payments/{id}` - удалить выплату
- `GET /api/payments/period` - получить выплаты за период
- `GET /api/payments/pensioner/{pensionerId}/total` - получить общую сумму выплат пенсионера

## Лицензия

MIT 