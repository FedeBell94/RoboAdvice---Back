package it.uiip.digitalgarage.roboadvice.core.forecastTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DumbForecastImpl implements IDataForecastComputation {

    public List<AssetClassDTO> computeForecast(Iterable<Data> data, LocalDate to){
        CustomDate customDate = CustomDate.getToday();

        List<AssetClassDTO> returnList = new ArrayList<>();
        do{
            returnList.add(AssetClassDTO.builder().date(customDate.getDateSql()).value(BigDecimal.TEN).build());
        }while(customDate.moveOneDayForward().compareTo(to) <= 0);

        return returnList;
    }
}
