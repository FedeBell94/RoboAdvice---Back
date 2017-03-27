package it.uiip.digitalgarage.roboadvice.utils;

import java.math.BigDecimal;

/**
 * This class holds the global constants of the project.
 */
public class RoboAdviceConstant {

    /**
     * Default start worth of a new user.
     */
    public static final BigDecimal DEFAULT_START_WORTH = BigDecimal.valueOf(10000);

    /**
     * Default start of the data - this particular date is chosen because before this date we do NOT have the values for
     * the asset BitCoin, from Quandl data source. Because it makes no sense go previously of this data, this is the
     * oldest data you can reach.
     */
    public static final CustomDate STARTING_DATA = new CustomDate("2014-04-30");

    /**
     * This value represents the boundaries after that a re-balancing of the portfolio is performed.
     */
    public static final BigDecimal RE_BALANCING_BOUNDARIES = BigDecimal.valueOf(0.01);

    /**
     * Default days to compute the forecast of the data.
     */
    public static final Integer FORECAST_DAYS = 180;

    private RoboAdviceConstant() {
    }

}
