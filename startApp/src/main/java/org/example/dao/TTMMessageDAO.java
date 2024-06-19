package org.example.dao;

import org.example.model.TTMMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TTMMessageDAO {

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

    public void saveTTMMessage(TTMMessage message) {
        String query = "INSERT INTO searadar_messages (message_type, message_content, format_id) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, message.getMessageType());
            statement.setString(2, message.getMessageContent());
            statement.setInt(3, message.getFormatId());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public TTMMessage getTTMMessageById(int id) {
        TTMMessage message = null;
        String query = "SELECT * FROM searadar_messages WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    message = new TTMMessage();
                    message.setId(resultSet.getInt("id"));
                    message.setMessageType(resultSet.getString("message_type"));
                    message.setMessageContent(resultSet.getString("message_content"));
                    message.setFormatId(resultSet.getInt("format_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }


    public List<TTMMessage> getTTMMessagesByFormat(int formatId) {
        List<TTMMessage> messages = new ArrayList<>();
        String query = "SELECT * FROM searadar_messages WHERE format_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, formatId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    TTMMessage message = new TTMMessage();
                    message.setId(resultSet.getInt("id"));
                    message.setMessageType(resultSet.getString("message_type"));
                    message.setMessageContent(resultSet.getString("message_content"));
                    message.setFormatId(resultSet.getInt("format_id"));
                    messages.add(message);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}