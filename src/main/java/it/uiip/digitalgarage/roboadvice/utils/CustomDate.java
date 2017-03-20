package it.uiip.digitalgarage.roboadvice.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomDate {

    private LocalDate localDate;

    /*
     *    CONSTRUCTORS
     */
    public CustomDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public CustomDate(Date date) {
        this.localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public CustomDate(java.sql.Date date) {
        this.localDate = date.toLocalDate();
    }

    public CustomDate(String s) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.localDate = LocalDate.parse(s, dateTimeFormatter);
    }


    /*
     *    GETTERS
     */
    public LocalDate getDateLocalDate() {
        return toLocalDate(this.localDate);
    }

    public Date getDateUtils() {
        return toDateUtils(this.localDate);
    }

    public java.sql.Date getDateSql() {
        return toDateSql(this.localDate);
    }

    public org.threeten.bp.LocalDate getDateBpLocalDate() {
        return org.threeten.bp.LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    public LocalDate getYesterdayLocalDate() {
        return getDayFromLocalDate(-1);
    }

    public Date getYesterdayUtils() {
        return getDayFromUtils(-1);
    }

    public java.sql.Date getYesterdaySql() {
        return getDayFromSql(-1);
    }

    public LocalDate getDayFromLocalDate(int days) {
        if (days > 0) {
            return this.localDate.plusDays(days);
        }
        return this.localDate.minusDays(-days);
    }

    public Date getDayFromUtils(int days) {
        return toDateUtils(getDayFromLocalDate(days));
    }

    public java.sql.Date getDayFromSql(int days) {
        return toDateSql(getDayFromLocalDate(days));
    }

    public int getYear() {
        return this.localDate.getYear();
    }

    public int getMonth() {
        return this.localDate.getMonthValue();
    }

    public int getDay() {
        return this.localDate.getDayOfMonth();
    }

    /*
     *    UTILITIES
     */
    public CustomDate moveOneDayForward() {
        return moveForward(1);
    }

    public CustomDate moveForward(int days) {
        this.localDate = this.localDate.plusDays(days);
        return this;
    }

    public int compareTo(LocalDate ld) {
        return this.localDate.compareTo(ld);
    }

    public int compareTo(Date date) {
        return compareTo(fromUtil(date));
    }

    public int compareTo(java.sql.Date date) {
        return compareTo(fromSql(date));
    }

    public int compareTo(CustomDate customDate) {
        return compareTo(customDate.getDateLocalDate());
    }

    /*
     *    CONVERSION UTILS
     */
    private LocalDate toLocalDate(LocalDate ld) {
        return LocalDate.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth());
    }

    private Date toDateUtils(LocalDate ld) {
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private java.sql.Date toDateSql(LocalDate ld) {
        return java.sql.Date.valueOf(ld);
    }

    private LocalDate fromSql(java.sql.Date date) {
        return date.toLocalDate();
    }

    private LocalDate fromUtil(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /*
     *    STATIC METHOD UTIL
     */
    public static CustomDate getToday() {
        return new CustomDate(LocalDate.now());
    }
}
