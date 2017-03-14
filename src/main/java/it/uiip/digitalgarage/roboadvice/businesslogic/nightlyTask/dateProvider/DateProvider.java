package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider;

import java.sql.Date;
import java.util.Calendar;

public class DateProvider {

    public Date getToday(){
        return new Date(Calendar.getInstance().getTime().getTime());
    }

    public Date getYesterday(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new Date(calendar.getTime().getTime());
    }

    public Date getDayFromToday(int amount){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount);
        return new Date(calendar.getTime().getTime());
    }

    public Date getDayBeforeToday(int amount){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -amount);
        return new Date(calendar.getTime().getTime());
    }
}
