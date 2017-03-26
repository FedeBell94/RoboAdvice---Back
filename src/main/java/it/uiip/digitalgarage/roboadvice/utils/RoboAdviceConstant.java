package it.uiip.digitalgarage.roboadvice.utils;

import java.math.BigDecimal;

public class RoboAdviceConstant {

    public static final BigDecimal DEFAULT_START_WORTH = BigDecimal.valueOf(10000);

    public static final CustomDate STARTING_DATA = new CustomDate("2014-04-30");

    public static final BigDecimal RE_BALANCING_BOUNDARIES = BigDecimal.valueOf(0.01);

    public static final Integer FORECAST_DAYS = 180;

    private RoboAdviceConstant(){}

}
