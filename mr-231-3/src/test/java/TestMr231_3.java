import org.example.searadar.mr231_3.convert.Mr231_3Converter;
import org.example.searadar.mr231_3.station.Mr231_3StationType;
import org.junit.jupiter.api.Test;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMr231_3 {

    /**
     * Тестирует создание и преобразование сообщения типа TTM.
     */
    @Test
    void TestTTM() {
        // Setup
        TrackedTargetMessage ttm = new TrackedTargetMessage();
        ttm.setMsgTime(457362L);
        ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        ttm.setTargetNumber(66);
        ttm.setDistance(28.71);
        ttm.setBearing(341.1);
        ttm.setSpeed(57.6);
        ttm.setCourse(24.5);
        ttm.setStatus(TargetStatus.LOST);
        ttm.setIff(IFF.FRIEND);
        ttm.setType(TargetType.UNKNOWN);

        String mr231_3_TTM = "$RATTM,66,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,А*42";

        // Execution
        Mr231_3StationType mr231_3 = new Mr231_3StationType();
        Mr231_3Converter converter_231_3 = mr231_3.createConverter();
        List<SearadarStationMessage> searadarMessages_mr231_3_TTM = converter_231_3.convert(mr231_3_TTM);

        // Assertion
        TrackedTargetMessage resultMessage = (TrackedTargetMessage) searadarMessages_mr231_3_TTM.get(0);
        assertEquals(ttm.getMsgTime(), resultMessage.getMsgTime());
        long difference = Math.abs(ttm.getMsgRecTime().getTime() - resultMessage.getMsgRecTime().getTime());
        assertTrue(difference < 1000, "Разница в миллисекундах слишком большая: " + difference);
        assertEquals(ttm.getTargetNumber(), resultMessage.getTargetNumber());
        assertEquals(ttm.getDistance(), resultMessage.getDistance());
        assertEquals(ttm.getBearing(), resultMessage.getBearing());
        assertEquals(ttm.getSpeed(), resultMessage.getSpeed());
        assertEquals(ttm.getCourse(), resultMessage.getCourse());
        assertEquals(ttm.getStatus(), resultMessage.getStatus());
        assertEquals(ttm.getIff(), resultMessage.getIff());
        assertEquals(ttm.getType(), resultMessage.getType());
    }

    /**
     * Тестирует создание и преобразование сообщения типа RSD.
     */
    @Test
    void TestRSFD() {
        // Setup
        RadarSystemDataMessage rsd = new RadarSystemDataMessage();
        rsd.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        rsd.setInitialDistance(50.5);
        rsd.setInitialBearing(309.9);
        rsd.setMovingCircleOfDistance(64.8);
        rsd.setBearing(132.3);
        rsd.setDistanceFromShip(52.6);
        rsd.setBearing2(155.0);
        rsd.setDistanceScale(48.0);
        rsd.setDistanceUnit("K");
        rsd.setDisplayOrientation("N");
        rsd.setWorkingMode("S");

        String mr231_3_RSD = "$RARSD,50.5,309.9,64.8,132.3,,,,,52.6,155.0,48.0,K,N,S*28";

        // Execution
        Mr231_3StationType mr231_3 = new Mr231_3StationType();
        Mr231_3Converter converter_231_3 = mr231_3.createConverter();
        List<SearadarStationMessage> searadarMessages_mr231_3RSD = converter_231_3.convert(mr231_3_RSD);

        // Assertion
        RadarSystemDataMessage resultMessage = (RadarSystemDataMessage) searadarMessages_mr231_3RSD.get(0);


        long difference = Math.abs(rsd.getMsgRecTime().getTime() - resultMessage.getMsgRecTime().getTime());
        assertTrue(difference < 1000, "Разница в миллисекундах слишком большая: " + difference);
        assertEquals(rsd.getInitialDistance(), resultMessage.getInitialDistance());
        assertEquals(rsd.getInitialBearing(), resultMessage.getInitialBearing());assertEquals(rsd.getMovingCircleOfDistance(), resultMessage.getMovingCircleOfDistance());
        assertEquals(rsd.getBearing(), resultMessage.getBearing());
        assertEquals(rsd.getDistanceFromShip(), resultMessage.getDistanceFromShip());
        assertEquals(rsd.getBearing2(), resultMessage.getBearing2());
        assertEquals(rsd.getDistanceScale(), resultMessage.getDistanceScale());
        assertEquals(rsd.getDistanceUnit(), resultMessage.getDistanceUnit());
        assertEquals(rsd.getDisplayOrientation(), resultMessage.getDisplayOrientation());
        assertEquals(rsd.getWorkingMode(), resultMessage.getWorkingMode());
    }
}