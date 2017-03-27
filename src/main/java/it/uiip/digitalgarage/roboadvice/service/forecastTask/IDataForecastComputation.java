package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;

import java.time.LocalDate;
import java.util.List;

public interface IDataForecastComputation {

    List<AssetClassDTO> computeForecast(Iterable<Data> data, LocalDate to);
}
