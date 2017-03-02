package it.uiip.digitalgarage.roboadvice.businesslogic.quandl;

import com.jimmoores.quandl.*;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.threeten.bp.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Simone on 01/03/2017.
 */
public class Quandl {

    //api key: ydS_4tVLnsPS_bxo3uvd
    @Autowired
    private static DataRepository dataRepository;

    public static ArrayList<Data> callQuandl(Asset asset, int startYear, int startMonth, int startDay) {

        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");

        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest.Builder
                        .of(
                                QuandlCodeRequest.singleColumn(asset.getQuandlKey(), asset.getQuandlId()),
                                QuandlCodeRequest.allColumns("DOE/RWTC")
                        )
                        .withStartDate(LocalDate.of(startYear, startMonth, startDay))
                        .withFrequency(Frequency.DAILY)
                        .build());
        System.out.println("Header definition: " + tabularResultMulti.getHeaderDefinition());
        ArrayList<Data> res = new ArrayList<>();
        for (final Row row : tabularResultMulti) {
            LocalDate date = row.getLocalDate("Date");
            Double value = row.getDouble("DOE/RWTC - Value");
            System.out.println("Value on date " + date + " was " + value);

            if (value != null) {
                Data d = Data.builder().date(new Date(date.getYear(), date.getMonthValue(), date.getDayOfMonth())).value(BigDecimal.valueOf(value)).asset(asset).build();
                res.add(d);
                dataRepository.save(d);
            }

        }

        return res;
    }

    public static Data callDailyQuandl(Asset asset) {

        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");

        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest.Builder
                        .of(
                                QuandlCodeRequest.singleColumn(asset.getQuandlKey(), asset.getQuandlId()),
                                QuandlCodeRequest.allColumns("DOE/RWTC")
                        )
                        .build());
        System.out.println("Header definition: " + tabularResultMulti.getHeaderDefinition());

        for (final Row row : tabularResultMulti) {
            LocalDate date = row.getLocalDate("Date");
            Double value = row.getDouble("DOE/RWTC - Value");
            System.out.println("Value on date " + date + " was " + value);

            if (value != null) {
                Data d = Data.builder().date(new Date(Calendar.getInstance().getTime().getTime())).value(BigDecimal.valueOf(value)).build();
                dataRepository.save(d);
                return d;
            }

        }
        return null;
    }

    /*public static void main(String[] args) {
        //callQuandl("WIKI/FB", 0, 2015, 5, 11);
        //callDailyQuandl("WIKI/FB",4);
        callQuandl();
    }*/


}
