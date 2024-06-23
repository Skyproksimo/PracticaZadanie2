package org.example.dao;

import org.example.model.MessageFormat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для доступа к данным форматов сообщений в базе данных PostgreSQL.
 */
public class MessageFormatDAO {

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
     * Получает список всех форматов сообщений из базы данных.
     *
     * @return список объектов {@link MessageFormat}, представляющих все форматы сообщений
     */
    public List<MessageFormat> getAllMessageFormats() {
        List<MessageFormat> formats = new ArrayList<>();
        String query = "SELECT * FROM message_formats";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                MessageFormat format = new MessageFormat();
                format.setId(resultSet.getInt("id"));
                format.setFormatName(resultSet.getString("format_name"));
                formats.add(format);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formats;
    }
}