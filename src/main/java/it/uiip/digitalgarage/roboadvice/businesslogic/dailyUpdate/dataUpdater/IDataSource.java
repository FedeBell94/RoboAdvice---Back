package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;

import java.sql.Date;
import java.util.ArrayList;

public interface IDataSource {

    Data getData(Asset asset, Date date);

    ArrayList<Data> getHistoricalData(Asset asset, int startYear, int startMonth, int startDay);
}
