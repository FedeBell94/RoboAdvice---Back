package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.DateProvider.DateProvider;

public interface IDailyTaskUpdate {

    void executeUpdateTask(DateProvider dateProvider);
}
