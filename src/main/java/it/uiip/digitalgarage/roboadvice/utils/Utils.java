package it.uiip.digitalgarage.roboadvice.utils;

import java.sql.Date;
import java.util.Calendar;

public class Utils {

    private Utils(){}

    public static Date getToday(){
        return new Date(Calendar.getInstance().getTime().getTime());
    }

    public static Date getYesterday(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new Date(calendar.getTime().getTime());
    }
}
