package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.DateProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LiarDateProvider extends DateProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(LiarDateProvider.class);
    private Calendar currCalendar = Calendar.getInstance();

    public LiarDateProvider(String date) {
        SimpleDateFormat parsed = new SimpleDateFormat("yyyy-MM-dd");
        try {
            currCalendar.setTime(parsed.parse(date));
        } catch (ParseException e) {
            LOGGER.error("Date malformed error. Correct format is yyyy-MM-dd ");
        }
    }

    @Override
    public Date getToday(){
        return new Date(currCalendar.getTime().getTime());
    }

    @Override
    public Date getYesterday(){
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(currCalendar.getTime());
        yesterday.add(Calendar.DATE, -1);
        return new Date(yesterday.getTime().getTime());
    }

    public void goNextDay(){
        currCalendar.add(Calendar.DATE, 1);
    }
}
