package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;

import java.util.ArrayList;

public interface IDataUpdater {
    void updateDailyData();

}
