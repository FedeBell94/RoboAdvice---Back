package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.sql.Date;
import java.util.Calendar;

import static org.junit.Assert.assertTrue;

/**
 * Created by Simone on 20/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class CustomDateTests {

    private CustomDate customDate;

    private Calendar calendar;

    @Before
    public void before(){

        calendar = Calendar.getInstance();

    }

    @Test
    public void testLocalDate() {

        customDate = new CustomDate(new Date(calendar.getTime().getTime()).toLocalDate());

        Boolean check1 = customDate.getDateLocalDate().getClass().toString().equals("class java.time.LocalDate");

        Boolean check2 = customDate.getDateUtils().getClass().toString().equals("class java.util.Date");

        Boolean check3 = customDate.getDateSql().getClass().toString().equals("class java.sql.Date");

        Boolean check4 = customDate.getDateBpLocalDate().getClass().toString().equals("class org.threeten.bp.LocalDate");

        assertTrue(check1);

        assertTrue(check2);

        assertTrue(check3);

        assertTrue(check4);

        calendar.add(Calendar.DATE, -1);

        Boolean check5 = customDate.getYesterdayLocalDate().equals(new Date(calendar.getTime().getTime()).toLocalDate());

        assertTrue(check5);

    }

    @Test
    public void testUtilDate(){

        customDate = new CustomDate(calendar.getTime());

        Boolean check1 = customDate.getDateLocalDate().getClass().toString().equals("class java.time.LocalDate");

        Boolean check2 = customDate.getDateUtils().getClass().toString().equals("class java.util.Date");

        Boolean check3 = customDate.getDateSql().getClass().toString().equals("class java.sql.Date");

        Boolean check4 = customDate.getDateBpLocalDate().getClass().toString().equals("class org.threeten.bp.LocalDate");

        assertTrue(check1);

        assertTrue(check2);

        assertTrue(check3);

        assertTrue(check4);

        calendar.add(Calendar.DATE, -1);

        Boolean check5 = customDate.getYesterdayUtils().getDate() == calendar.getTime().getDate();

        assertTrue(check5);

    }

    @Test
    public void testSqlDate(){

        customDate = new CustomDate(new java.sql.Date(calendar.getTime().getTime()));

        Boolean check1 = customDate.getDateLocalDate().getClass().toString().equals("class java.time.LocalDate");

        Boolean check2 = customDate.getDateUtils().getClass().toString().equals("class java.util.Date");

        Boolean check3 = customDate.getDateSql().getClass().toString().equals("class java.sql.Date");

        Boolean check4 = customDate.getDateBpLocalDate().getClass().toString().equals("class org.threeten.bp.LocalDate");

        assertTrue(check1);

        assertTrue(check2);

        assertTrue(check3);

        assertTrue(check4);

        calendar.add(Calendar.DATE, -1);

        Boolean check5 = customDate.getYesterdaySql().toString().equals(new java.sql.Date(calendar.getTime().getTime()).toString());

        assertTrue(check5);

    }

    @Test
    public void testParsing(){

        customDate = new CustomDate("2017-03-20");

        Boolean check1 = customDate.getDateLocalDate().getClass().toString().equals("class java.time.LocalDate");

        Boolean check2 = customDate.getDateUtils().getClass().toString().equals("class java.util.Date");

        Boolean check3 = customDate.getDateSql().getClass().toString().equals("class java.sql.Date");

        Boolean check4 = customDate.getDateBpLocalDate().getClass().toString().equals("class org.threeten.bp.LocalDate");

        assertTrue(check1);

        assertTrue(check2);

        assertTrue(check3);

        assertTrue(check4);

    }

}
