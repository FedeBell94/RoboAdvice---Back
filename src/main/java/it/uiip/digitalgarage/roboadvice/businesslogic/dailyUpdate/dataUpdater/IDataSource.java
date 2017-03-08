package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;

import java.sql.Date;

public interface IDataSource {

    Data getData(Asset asset, Date date);
}
