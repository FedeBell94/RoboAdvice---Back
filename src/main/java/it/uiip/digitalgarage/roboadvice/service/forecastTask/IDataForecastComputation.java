package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.persistence.model.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

public interface IDataForecastComputation {

    Map<Date, BigDecimal> computeForecast(Iterable<Data> data, LocalDate to);
}
