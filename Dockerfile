# Используем официальный образ maven для сборки проекта
FROM maven:3.6.3-jdk-8 AS build

# Устанавливаем рабочую директорию для сборки
WORKDIR /app/build

# Копируем все файлы проекта в контейнер
COPY . /app

# Выполняем команду Maven для очистки и сборки проекта
RUN mvn clean package

# Используем официальный образ OpenJDK для запуска приложения
FROM openjdk:8-jre

# Обновляем списки пакетов и устанавливаем необходимые зависимости
RUN apt-get update && \
    apt-get install -y xvfb libxrender1 libxtst6 libxi6 postgresql-client x11-xkb-utils xclip && \
    apt-get clean

# Устанавливаем рабочую директорию для приложения
WORKDIR /app

# Копируем собранный jar файл из предыдущего слоя сборки
COPY --from=build /app/startApp/target/startApp-1.0-SNAPSHOT-jar-with-dependencies.jar /app/startApp.jar

# Устанавливаем переменные окружения для подключения к базе данных
ENV DB_HOST=db
ENV DB_PORT=5432
ENV DB_NAME=proect_mr
ENV DB_USER=postgres
ENV DB_PASSWORD=123

# Устанавливаем переменную окружения для дисплея
ENV DISPLAY=host.docker.internal:0

# Команда для запуска виртуального фреймбуфера, настройки раскладки клавиатуры и запуска Java приложения
CMD Xvfb host.docker.internal:0 -screen 0 1024x768x16 -ac & \
    setxkbmap us,ru -option 'grp:alt_shift_toggle' && \
    java -jar /app/startApp.jar