package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Simone on 27/03/2017.
 */
public class WekaForecastImpl implements IDataForecastComputation {
    @Override public List<AssetClassDTO> computeForecast(Iterable<Data> data, LocalDate to) {
        return null;
    }
}
