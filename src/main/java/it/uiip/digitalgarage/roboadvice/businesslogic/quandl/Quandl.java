package it.uiip.digitalgarage.roboadvice.businesslogic.quandl;

import com.jimmoores.quandl.*;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.threeten.bp.*;

import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * Created by Simone on 01/03/2017.
 */
public class Quandl {

    //api key: ydS_4tVLnsPS_bxo3uvd

    public static void callQuandl(String quandlKey,int quandlColumn,int startYear, int startMonth, int startDay) {

        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");

        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest.Builder
                        .of(
                                QuandlCodeRequest.singleColumn(quandlKey, quandlColumn),
                                QuandlCodeRequest.allColumns("DOE/RWTC")
                        )
                        .withStartDate(LocalDate.of(startYear,startMonth,startDay))
                        .withFrequency(Frequency.MONTHLY)
                        .build());
        System.out.println("Header definition: " + tabularResultMulti.getHeaderDefinition());
        ArrayList<Data> res = new ArrayList<>();
        for (final Row row : tabularResultMulti) {
            LocalDate date = row.getLocalDate("Date");
            Double value = row.getDouble("DOE/RWTC - Value");
            System.out.println("Value on date " + date + " was " + value);
            //res.add();
        }
    }

    public static void main(String [] args)
    {
        callQuandl("WIKI/AAPL",0,2015,10,11);
    }



}
