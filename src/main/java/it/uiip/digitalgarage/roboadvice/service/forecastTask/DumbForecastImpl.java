package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Dumb forecast used to test the program.
 *
 * Created by feder on 27/03/2017.
 */
public class DumbForecastImpl implements IDataForecastComputation {

    @Override
    public Map<Date, BigDecimal> computeForecast(Iterable<Data> data, LocalDate to) {
        Iterator<Data> it = data.iterator();
        Data last = null;
        while(it.hasNext()){
            last = it.next();
        }

        BigDecimal value = last.getValue();

        Map<Date, BigDecimal> returnMap = new HashMap<>();
        CustomDate customDate = CustomDate.getToday();
        do{
            Double random = ThreadLocalRandom.current().nextInt(-9, 10) / 10.0;
            value = value.add(BigDecimal.valueOf(random));
            returnMap.put(customDate.getDateSql(), value);
        } while(customDate.moveOneDayForward().compareTo(to) < 0);

        return returnMap;
    }

}
