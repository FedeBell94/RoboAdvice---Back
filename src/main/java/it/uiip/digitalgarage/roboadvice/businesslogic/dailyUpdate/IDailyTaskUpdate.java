package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;

import java.util.List;

public interface IDailyTaskUpdate {

    void executeUpdateTask(DateProvider dateProvider, List<User> users);
}
