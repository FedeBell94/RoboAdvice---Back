package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.DateProvider.DateProvider;

public interface IDailyTaskUpdate {

    void executeUpdateTask(DateProvider dateProvider);
}
