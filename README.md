# [7.2][Практика] Авторизация в Битве Покемонов

Проект автотестов.

## Cтек
- **Java 17+**
- **Selenium WebDriver**
- **JUnit 5** (тестовый раннер)
- **AssertJ** (проверки)
- **Maven** (сборка и зависимости)

## Структура проекта
```
src/test/
    ├── java/ru/pokemonbattle/
    │       ├── framework/       # Инфраструктура: BrowserFactory, Config, BaseTest, Environment
    │       ├── tests/           # Тестовые сценарии 
    │       └── TestData.java    # Хранилище тестовых данных
    └── resources/
            └── config.properties    # Общие настройки
  
```

## Конфигурация
Параметры читаются в порядке приоритета
1. Аргументы командной строки JVM (флаги `-D`).
2. Файл `local.properties`.
3. Файл `config.properties`.
4. Значения по умолчанию в коде.

**Основные параметры:**
- `env` — окружение: `prod` (по умолч.), `stage`.
- `browser` — браузер: `chrome` (по умолч.), `firefox`, `edge`.
- `headless` — запуск без окна: `false` (по умолч.), `true`.
- `test.login` / `test.password` — учетные данные.

## Запуск
Можно запускать через maven
```bash
# Запуск по умолчанию (Chrome, Prod)
mvn test

# Запуск на Stage в Firefox в фоновом режиме
mvn test -Denv=stage -Dbrowser=firefox -Dheadless=true

# Запуск с конкретными данными
mvn test -Dtest.login=my_user -Dtest.password=my_password
