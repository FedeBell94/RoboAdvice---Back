package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;

import java.sql.Date;
import java.util.List;

public interface IDataSource {
    List<Data> getAllDataFrom(Asset asset, Date from);
}
