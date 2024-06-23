package org.example;

import org.example.dao.MessageFormatDAO;
import org.example.dao.TTMMessageDAO;
import org.example.model.MessageFormat;
import org.example.model.TTMMessage;
import org.example.searadar.mr231.convert.Mr231Converter;
import org.example.searadar.mr231_3.convert.Mr231_3Converter;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Класс главной формы приложения.
 */
public class MainForm extends JFrame {

    private DefaultTableModel messagesTableModel;
    private JTextField messageTypeField;
    private JTextArea messageContentArea;
    private JComboBox<MessageFormat> messageFormatComboBox;
    private JTable messagesTable;
    private JList<String> decryptionList;
    private DefaultListModel<String> decryptionListModel;

    /**
     * Конструктор главной формы.
     */
    public MainForm() {
        setTitle("Главная форма");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadMessages();
    }

    /**
     * Инициализация элементов пользовательского интерфейса.
     */
    public void initializeUI() {
        JPanel panel = new JPanel(new BorderLayout());
        getContentPane().add(panel);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        JLabel messageTypeLabel = new JLabel("Тип сообщения:");
        messageTypeField = new JTextField(10);
        inputPanel.add(messageTypeLabel);
        inputPanel.add(messageTypeField);

        JLabel messageContentLabel = new JLabel("Текст сообщения:");
        messageContentArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(messageContentArea);
        inputPanel.add(messageContentLabel);
        inputPanel.add(scrollPane);

        JLabel messageFormatLabel = new JLabel("Формат сообщения:");
        messageFormatComboBox = new JComboBox<>();
        inputPanel.add(messageFormatLabel);
        inputPanel.add(messageFormatComboBox);

        JButton saveButton = new JButton("Сохранить");
        JButton loadButton = new JButton("Загрузить");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        inputPanel.add(buttonPanel);

        panel.add(inputPanel, BorderLayout.NORTH);

        messagesTableModel = new DefaultTableModel(new Object[]{"ID", "Сообщение"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        messagesTable = new JTable(messagesTableModel);
        JScrollPane messagesScrollPane = new JScrollPane(messagesTable);
        panel.add(messagesScrollPane, BorderLayout.WEST);

        TableColumn idColumn = messagesTable.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setPreferredWidth(0);

        decryptionListModel = new DefaultListModel<>();
        decryptionList = new JList<>(decryptionListModel);
        JScrollPane decryptionScrollPane = new JScrollPane(decryptionList);
        panel.add(decryptionScrollPane, BorderLayout.CENTER);

        messagesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = messagesTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int messageId = (int) messagesTableModel.getValueAt(selectedRow, 0);
                        displayDecryption(messageId); // Отображение расшифровки выбранного сообщения
                    }}
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMessage();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMessages();
            }
        });

        loadMessageFormats();
    }

    /**
     * Загружает форматы сообщений из базы данных и добавляет их в комбобокс.
     */
    private void loadMessageFormats() {
        MessageFormatDAO messageFormatDAO = new MessageFormatDAO();
        List<MessageFormat> formats = messageFormatDAO.getAllMessageFormats();
        for (MessageFormat format : formats) {
            messageFormatComboBox.addItem(format);
        }
    }

    /**
     * Сохраняет новое сообщение в базе данных.
     */
    private void saveMessage() {
        String messageType = messageTypeField.getText();
        String messageContent = messageContentArea.getText();
        MessageFormat selectedFormat = (MessageFormat) messageFormatComboBox.getSelectedItem();
        if (selectedFormat != null) {
            int formatId = selectedFormat.getId();

            TTMMessage message = new TTMMessage();
            message.setMessageType(messageType);
            message.setMessageContent(messageContent);
            message.setFormatId(formatId);

            TTMMessageDAO ttmMessageDAO = new TTMMessageDAO();
            ttmMessageDAO.saveTTMMessage(message);

            messageTypeField.setText("");
            messageContentArea.setText("");
            messageFormatComboBox.setSelectedIndex(0);
        }
    }

    /**
     * Отображает расшифровку выбранного сообщения.
     *
     * @param messageId идентификатор сообщения
     */
    private void displayDecryption(int messageId) {
        TTMMessageDAO ttmMessageDAO = new TTMMessageDAO();
        TTMMessage message = ttmMessageDAO.getTTMMessageById(messageId);

        if (message != null) {
            decryptionListModel.clear();
            String decryptedMessage = convertMessage(message.getMessageContent(), message.getFormatId());
            decryptionListModel.addElement(decryptedMessage);
        }
    }

    /**
     * Конвертирует зашифрованное сообщение в читаемый формат.
     *
     * @param encryptedMessage зашифрованное сообщение
     * @param formatId идентификатор формата
     * @return расшифрованное сообщение
     */
    private String convertMessage(String encryptedMessage, int formatId) {
        List<SearadarStationMessage> convertedMessages;

        if (formatId == 1) {
            Mr231Converter converter = new Mr231Converter();
            convertedMessages = converter.convert(encryptedMessage);
        } else if (formatId == 2) {
            Mr231_3Converter converter = new Mr231_3Converter();
            convertedMessages = converter.convert(encryptedMessage);
        } else {
            return "Неоднозначный формат";
        }

        if (!convertedMessages.isEmpty()) {
            return convertedMessages.get(0).toString();
        }
        return "Ошибка при расшифровке сообщения";
    }

    /**
     * Загружает сообщения из базы данных и отображает их в таблице.
     */
    private void loadMessages() {
        TTMMessageDAO ttmMessageDAO = new TTMMessageDAO();
        MessageFormat selectedFormat = (MessageFormat) messageFormatComboBox.getSelectedItem();
        if (selectedFormat != null) {
            int formatId = selectedFormat.getId();
            List<TTMMessage> messages = ttmMessageDAO.getTTMMessagesByFormat(formatId);

            messagesTableModel.setRowCount(0);
            decryptionListModel.clear();

            for (TTMMessage message : messages) {
                messagesTableModel.addRow(new Object[]{message.getId(), message.getMessageContent()});
            }
        }
    }

/**
 * Точка входа в приложение.
 *
 * @param args аргументы командной строки
 */public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        MainForm form = new MainForm();
        form.setVisible(true);
    });
}
}