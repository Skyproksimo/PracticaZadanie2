package org.example;

import org.example.searadar.mr231.convert.Mr231Converter;
import org.example.searadar.mr231_3.convert.Mr231_3Converter;
import org.example.searadar.mr231.station.Mr231StationType;
import org.example.searadar.mr231_3.station.Mr231_3StationType;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import java.util.List;

/**
 * Практическое задание направлено на проверку умения создавать функциональные модули и работать с технической
 * документацией.
 * Задача написать парсер сообщений для протокола МР-231-3 на основе парсера МР-231.
 * Приложение к заданию файлы:
 * - Протокол МР-231.docx
 * - Протокол МР-231-3.docx
 * <p>
 * Требования:
 * 1. Перенести контрольный пример из "Протокола МР-231.docx" в метод main, по образцу в методе main;
 * 2. Проверить правильность работы конвертера путём вывода контрольного примера в консоль, по образцу в методе main;
 * 3. Создать модуль с именем mr-231-3 и добавить его в данный проект <module>../mr-231-3</module> и реализовать его
 * функционал в соответствии с "Протоколом МР-231-3.docx" на подобии модуля mr-231;
 * 4. Создать для модуля контрольный пример и проверить правильность работы конвертера путём вывода контрольного
 * примера в консоль
 *
 * <p>
 *     Примечание:
 *     1. Данные в пакете ru.oogis не изменять!!!
 *     2. Весь код должен быть покрыт JavaDoc
 */

public class App {
    public static void main(String[] args) {
        // Контрольный пример для МР-231
        String mr231_TTM = "$RATTM,66,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,А*42";
        String mr231_VHW = "$RAVHW,356.7,T,,,50.4,N,,*76";
        String mr231_RSD = "$RARSD,50.5,309.9,64.8,132.3,,,,,52.6,155.0,48.0,K,N,S*28";

        // Проверка работы конвертера
        Mr231StationType mr231 = new Mr231StationType();
        Mr231Converter converter = mr231.createConverter();
        List<SearadarStationMessage> searadarMessages = converter.convert(mr231_TTM);
        searadarMessages.forEach(System.out::println);
        searadarMessages = converter.convert(mr231_VHW);
        searadarMessages.forEach(System.out::println);
        searadarMessages = converter.convert(mr231_RSD);
        searadarMessages.forEach(System.out::println);


        // Контрольный пример для МР-231-3
        String mr231_3_TTM = "$RATTM,66,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,А*42";
        String mr231_3_RSD = "$RARSD,50.5,309.9,64.8,132.3,,,,,52.6,155.0,48.0,K,N,S*28";

        // Проверка работы конвертера МР-231-3
        Mr231_3StationType mr231_3 = new Mr231_3StationType();
        Mr231_3Converter converter_231_3 = mr231_3.createConverter();
        List<SearadarStationMessage> searadarMessages_mr231_3 = converter_231_3.convert(mr231_3_TTM);
        searadarMessages_mr231_3.forEach(System.out::println);
        searadarMessages_mr231_3 = converter_231_3.convert(mr231_3_RSD);
        searadarMessages_mr231_3.forEach(System.out::println);

        // Запуск основной формы
        java.awt.EventQueue.invokeLater(() -> {
            new MainForm().initializeUI();
        });
    }
}