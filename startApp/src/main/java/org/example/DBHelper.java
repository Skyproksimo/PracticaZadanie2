package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс для работы с базой данных PostgreSQL.
 */
public class DBHelper {

    private static final String URL = "jdbc:postgresql://" + System.getenv("DB_HOST") + ":" + System.getenv("DB_PORT") + "/" + System.getenv("DB_NAME");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает соединение с базой данных PostgreSQL.
     *
     * @return объект {@link Connection}, представляющий соединение с базой данных PostgreSQL
     * @throws SQLException если происходит ошибка доступа к базе данных
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Получает список названий форматов из базы данных.
     *
     * @return список названий форматов
     */
    public static List<String> getFormats() {
        List<String> formats = new ArrayList<>();
        String query = "SELECT format_name FROM formats"; // Предполагается, что есть таблица 'formats' и колонка 'format_name'

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                formats.add(resultSet.getString("format_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return formats;
    }

    /**
     * Получает список вторых опций из базы данных.
     *
     * @return список вторых опций
     */
    public static List<String> getSecondOptions() {
        List<String> options = new ArrayList<>();
        String query = "SELECT option_name FROM options"; // Предполагается, что есть таблица 'options' и колонка 'option_name'

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                options.add(resultSet.getString("option_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return options;
    }
}