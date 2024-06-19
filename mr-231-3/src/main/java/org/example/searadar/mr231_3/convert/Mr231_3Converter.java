package org.example.searadar.mr231_3.convert;

import org.apache.camel.Exchange;
import ru.oogis.searadar.api.convert.SearadarExchangeConverter;
import ru.oogis.searadar.api.message.*;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Конвертер для сообщений станции Mr231_3.
 */
public class Mr231_3Converter implements SearadarExchangeConverter {

    private static final Double[] DISTANCE_SCALE = {0.125, 0.25, 0.5, 1.5, 3.0, 6.0, 12.0, 24.0, 48.0, 96.0};

    private String[] fields;
    private String msgType;

    /**
     * Конвертирует входное сообщение обмена в список объектов {@link SearadarStationMessage}.
     *
     * @param exchange обмен, содержащий строковое сообщение
     * @return список объектов типа {@link SearadarStationMessage}, созданных на основе входного сообщения
     */
    @Override
    public List<SearadarStationMessage> convert(Exchange exchange) {
        return convert(exchange.getIn().getBody(String.class));
    }

    /**
     * Конвертирует строковое сообщение в список объектов типа {@link SearadarStationMessage}.
     *
     * @param message строковое сообщение, содержащее данные для конвертации
     * @return список объектов типа {@link SearadarStationMessage}, созданных на основе входного сообщения
     */
    public List<SearadarStationMessage> convert(String message) {

        List<SearadarStationMessage> msgList = new ArrayList<>();


        readFields(message);


        switch (msgType) {
            case "TTM":

                msgList.add(getTTM());
                break;
            case "VHW":

                msgList.add(getVHW());
                break;
            case "RSD":

                RadarSystemDataMessage rsd = getRSD();

                InvalidMessage invalidMessage = checkRSD(rsd);


                if (invalidMessage != null) {
                    msgList.add(invalidMessage);
                } else {

                    msgList.add(rsd);
                }
                break;
        }


        return msgList;
    }

    /**
     * Читает поля из входного сообщения и определяет тип сообщения.
     *
     * @param msg строковое сообщение для обработки
     */
    private void readFields(String msg) {
        String temp = msg.substring(3, msg.indexOf("*")).trim();
        fields = temp.split(Pattern.quote(","));
        msgType = fields[0];
    }

    /**
     * Создает и возвращает сообщение типа TTM на основе ранее прочитанных полей.
     *
     * @return сообщение типа {@link TrackedTargetMessage}
     */
    private TrackedTargetMessage getTTM() {
        TrackedTargetMessage ttm = new TrackedTargetMessage();

        TargetStatus status = TargetStatus.UNRELIABLE_DATA;
        IFF iff = IFF.UNKNOWN;
        TargetType type = TargetType.UNKNOWN;

        switch (fields[12]) {
            case "L":
                status = TargetStatus.LOST;
                break;
            case "Q":
                status = TargetStatus.UNRELIABLE_DATA;
                break;
            case "T":
                status = TargetStatus.TRACKED;
                break;
        }

        switch (fields[11]) {
            case "b":
                iff = IFF.FRIEND;
                break;
            case "p":
                iff = IFF.FOE;
                break;
            case "d":iff = IFF.UNKNOWN;
                break;
        }

        ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        ttm.setTargetNumber(Integer.parseInt(fields[1]));
        ttm.setDistance(Double.parseDouble(fields[2]));
        ttm.setBearing(Double.parseDouble(fields[3]));
        ttm.setSpeed(Double.parseDouble(fields[5]));
        ttm.setCourse(Double.parseDouble(fields[6]));
        ttm.setStatus(status);
        ttm.setIff(iff);
        ttm.setMsgTime(Long.valueOf(fields[14]));
        ttm.setType(type);

        return ttm;
    }

    /**
     * Создает и возвращает сообщение типа VHW на основе ранее прочитанных полей.
     *
     * @return сообщение типа {@link WaterSpeedHeadingMessage}
     */
    private WaterSpeedHeadingMessage getVHW() {
        WaterSpeedHeadingMessage vhw = new WaterSpeedHeadingMessage();

        vhw.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        vhw.setCourse(Double.parseDouble(fields[1]));
        vhw.setCourseAttr(fields[2]);
        vhw.setSpeed(Double.parseDouble(fields[5]));
        vhw.setSpeedUnit(fields[6]);

        return vhw;
    }

    /**
     * Создает и возвращает сообщение типа RSD на основе ранее прочитанных полей.
     *
     * @return сообщение типа {@link RadarSystemDataMessage}
     */
    private RadarSystemDataMessage getRSD() {
        RadarSystemDataMessage rsd = new RadarSystemDataMessage();

        rsd.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        rsd.setInitialDistance(Double.parseDouble(fields[1]));
        rsd.setInitialBearing(Double.parseDouble(fields[2]));
        rsd.setMovingCircleOfDistance(Double.parseDouble(fields[3]));
        rsd.setBearing(Double.parseDouble(fields[4]));
        rsd.setDistanceFromShip(Double.parseDouble(fields[9]));
        rsd.setBearing2(Double.parseDouble(fields[10]));
        rsd.setDistanceScale(Double.parseDouble(fields[11]));
        rsd.setDistanceUnit(fields[12]);
        rsd.setDisplayOrientation(fields[13]);
        rsd.setWorkingMode(fields[14]);

        return rsd;
    }

    /**
     * Проверяет сообщение типа RSD на корректность и возвращает {@link InvalidMessage} в случае ошибки.
     *
     * @param rsd сообщение типа {@link RadarSystemDataMessage} для проверки
     * @return объект типа {@link InvalidMessage}, если сообщение некорректное, иначе {@code null}
     */
    private InvalidMessage checkRSD(RadarSystemDataMessage rsd) {
        InvalidMessage invalidMessage = new InvalidMessage();
        String infoMsg = "";

        if (!Arrays.asList(DISTANCE_SCALE).contains(rsd.getDistanceScale())) {
            infoMsg = "RSD message. Wrong distance scale value: " + rsd.getDistanceScale();
            invalidMessage.setInfoMsg(infoMsg);
            return invalidMessage;
        }

        return null;
    }
}